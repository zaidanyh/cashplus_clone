package com.pasukanlangit.cashplus

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.pasukanlangit.cashplus.databinding.ActivityMainNavCompBinding
import com.pasukanlangit.cashplus.model.request_body.KYCRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.view_model.MainViewModel
import com.pasukanlangit.id.core.MAIN_ACTIVITY_FORWARDING_TO_HELP
import com.pasukanlangit.id.core.MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.SCAN_PATH
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.getBase64FromUri
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.id.core.utils.getEnumExtra
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class PageMenu(val resourceNav: Int) {
    HOME(R.id.homeFragment),
    HISTORY(R.id.historyFragment),
    HELP(R.id.helpFragment),
    OTHERS(R.id.otherFragment)
}

class MainActivityNavComp : AppCompatActivity() {

    private lateinit var binding: ActivityMainNavCompBinding
    private val viewModel: MainViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()
    private lateinit var navController: NavController

    private var uuid: String? = null
    private var token: String? = null

    private var backPressedTime: Long = 0
    private var stateBinded = false
    private var isLoading = false
    var stateIsOpenEmailConfig: Boolean = false
    private var pageMenuOnSelected = PageMenu.HOME.resourceNav

    val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {

                    val kycRequest = KYCRequest(
                        base64Data = getBase64FromUri(data?.data, contentResolver),
                        uploadType = "profile_pic",
                        fileExt = "jpg",
                        idType = "",
                        uuid = uuid ?: ""
                    )
                    viewModel.uploadPhotoProfile(kycRequest, token ?: "")
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainNavCompBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onFinishActivity()
            }
        })

        uuid = sharedPref.getUUID()
        token = sharedPref.getAccessToken()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        MapsInitializer.initialize(this)
        triggerInAppMessage()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        val messageCallback = intent.getStringExtra(MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE)
        val additionalTokenMessage = intent.getStringExtra(ADDITIONAL_MESSAGE_TOKEN)
        val isForwardToHistory = intent.getBooleanExtra(FORWARDING_TO_HISTORY, false)
        val isForwardToHelp = intent.getBooleanExtra(MAIN_ACTIVITY_FORWARDING_TO_HELP, false)
        val isForwardToOther = intent.getBooleanExtra(FORWARDING_TO_OTHER, false)

        when {
            isForwardToHistory -> {
                val indexKey = intent.getStringExtra(ADDITIONAL_INDEX_KEY)
                if (!indexKey.isNullOrEmpty()) {
                    navigateToPage(PageMenu.HISTORY, indexKey)
                    return
                }
                navigateToPage(PageMenu.HISTORY)
            }
            isForwardToHelp -> navigateToPage(PageMenu.HELP)
            isForwardToOther -> navigateToPage(PageMenu.OTHERS)
        }

        if (!messageCallback.isNullOrEmpty()) {
            val typeMessage = intent.getEnumExtra() ?: NotifType.NOTIF_SUCCESS
            val menusAllFragment: ButtomSheetNotif = if (!additionalTokenMessage.isNullOrEmpty()) {
                ButtomSheetNotif(
                    messageCallback,
                    typeMessage,
                    additionalToken = additionalTokenMessage
                )
            } else {
                ButtomSheetNotif(messageCallback, typeMessage)
            }
            menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
        }
    }

    fun navigateToPage(pageMenu: PageMenu, indexPage: String? = null) {
        pageMenuOnSelected = pageMenu.resourceNav
        navController.navigate(
            pageMenu.resourceNav,
            if (!indexPage.isNullOrEmpty())
                Bundle().apply { putInt("index_tab_history", indexPage.toInt()) }
            else null
        )
    }

    private fun setUpBottomNavigation() {
        with(binding.btmNavMain) {
            this.setupWithNavController(navController)
            this.setOnItemSelectedListener { menuItem ->
                if(menuItem.itemId == R.id.paymentActivity) {
                    if (stateBinded) startActivity(ModuleRoute.internalIntent(this@MainActivityNavComp, SCAN_PATH))
                    else if (isLoading) Toast.makeText(this@MainActivityNavComp, getString(R.string.checking_connection), Toast.LENGTH_SHORT).show()
                    else Toast.makeText(this@MainActivityNavComp, getString(R.string.nobu_not_connected), Toast.LENGTH_SHORT).show()
                    return@setOnItemSelectedListener false
                }

                if (pageMenuOnSelected != menuItem.itemId) {
                    pageMenuOnSelected = menuItem.itemId
                    menuItem.onNavDestinationSelected(navController)
                }
                return@setOnItemSelectedListener false
            }
        }
    }

    private fun triggerInAppMessage() {
        try {
            FirebaseAnalytics.getInstance(this).logEvent("main_activity_ready", null)
            FirebaseInAppMessaging.getInstance().triggerEvent("main_activity_ready")
        }catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(RuntimeException("trigger in app message throw exceptions cause : ${e.message}"))
        }
    }

    fun setStateIsLoading(isLoading: Boolean) {
        this.isLoading = isLoading
    }

    fun setStateBinded(stateBinded: Boolean) {
        this.stateBinded = stateBinded
    }

    private fun onFinishActivity() {
        val time = System.currentTimeMillis()
        if (time - backPressedTime > 1700) {
            backPressedTime = time
            Toast.makeText(this, getString(R.string.press_again), Toast.LENGTH_SHORT).show()
        } else finish()
    }

    override fun onResume() {
        super.onResume()
        setUpBottomNavigation()
    }

    companion object {
//        const val CALLBACK_MESSAGE = "CALLBACK_MESSAGE"
        const val ADDITIONAL_INDEX_KEY = "ADDITIONAL_INDEX_KEY"
        const val ADDITIONAL_MESSAGE_TOKEN = "ADDITIONAL_MESSAGE_TOKEN"
        const val FORWARDING_TO_HISTORY = "FORWARDING_TO_HISTORY"
        const val FORWARDING_TO_OTHER = "FORWARDING_TO_OTHER"
    }
}