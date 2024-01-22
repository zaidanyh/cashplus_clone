package com.pasukanlangit.cashplus.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.MainActivityNavComp
import com.pasukanlangit.cashplus.ui.compose.screen.SplashScreenView
import com.pasukanlangit.cashplus.ui.ewallet.EWalletActivity
import com.pasukanlangit.cashplus.ui.login.LoginActivity
import com.pasukanlangit.cashplus.ui.onboarding.OnBoardingActivity
import com.pasukanlangit.cashplus.ui.pembayaran_bulanan.PembayaranBulananActivity
import com.pasukanlangit.cashplus.ui.pembayaran_game_menu.DetailGameMenuActivity
import com.pasukanlangit.cashplus.ui.pembayaran_pln.TopUpPlnActivity
import com.pasukanlangit.cashplus.ui.pembayaran_provider.TopUpProviderActivity
import com.pasukanlangit.cashplus.ui.splashscreen.update.UpdateAppsActivity
import com.pasukanlangit.cashplus.utils.MyUtils.getPackageInfoCompat
import com.pasukanlangit.cashplus.utils.model.AppDataVersion
import com.pasukanlangit.id.core.HOTEL_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.scottyab.rootbeer.RootBeer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SplashScreen : AppCompatActivity() {

    private val sharedPrefDataSource : SharedPrefDataSource by inject()

    private var token: String? = null
    private var uuid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUi()
        setContent {
            SplashScreenView(modifier = Modifier.fillMaxSize())
        }

        uuid = sharedPrefDataSource.getUUID()
        token = sharedPrefDataSource.getAccessToken()

        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataJson = mFirebaseRemoteConfig.getString("data_version")
                val gson = GsonBuilder().create()
                val stringJson = gson.fromJson(dataJson, AppDataVersion::class.java)
                val appDataVersion = AppDataVersion(
                    appVersion = stringJson.appVersion, appCodeVersion = stringJson.appCodeVersion,
                    newFeatures = stringJson.newFeatures, improvements = stringJson.improvements,
                    bugFixings = stringJson.bugFixings
                )
                val versionApp = PackageInfoCompat.getLongVersionCode(packageManager.getPackageInfoCompat(packageName))
                val rangeUpdate = appDataVersion.appCodeVersion.minus(versionApp)
                if (rangeUpdate > 1) {
                    startActivity(
                        Intent(this, UpdateAppsActivity::class.java).apply {
                            putExtra(UpdateAppsActivity.KEY_VERSION_APP, appDataVersion)
                        }
                    )
                    return@addOnCompleteListener
                }
            }
            finishSplash()
        }
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
    }

    private fun finishSplash() {
        val isBoardingHasBeenShow = sharedPrefDataSource.getBoardingHasBeenShow()

        lifecycleScope.launch {
            delay(2000)
            finish()
            if (!isBoardingHasBeenShow) {
                startActivity(Intent(this@SplashScreen, OnBoardingActivity::class.java))
                return@launch
            }

            if (uuid.isNullOrEmpty() && token.isNullOrEmpty()) {
                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                return@launch
            }
            if (!checkNotification()) {
                startActivity(Intent(this@SplashScreen, MainActivityNavComp::class.java))
            }
        }
    }

    private fun checkNotification() : Boolean {
        val extras = intent.extras

        if (extras != null) {
            val direction: String? = extras.getString("direction")
            val indexTab : String? = extras.getString("index_tab")
            val inputPrefix: String? = extras.getString("input_prefix")
            val providerCode: String? =  extras.getString("provider_code")

            lateinit var intent: Intent
            when (direction) {
                "history" -> intent = Intent(this, MainActivityNavComp::class.java).apply {
                    putExtra(MainActivityNavComp.FORWARDING_TO_HISTORY, true)
                    putExtra(MainActivityNavComp.ADDITIONAL_INDEX_KEY, indexTab)
                }
                "provider" -> intent = Intent(this, TopUpProviderActivity::class.java).apply {
                    putExtra(TopUpProviderActivity.KEY_ACTIVE_VIEWPAGER, indexTab?.toInt())
                    putExtra(TopUpProviderActivity.KEY_PREFIX_NUMBER, inputPrefix)
                }
                "tagihan" -> intent = Intent(this, PembayaranBulananActivity::class.java).apply {
                    putExtra(PembayaranBulananActivity.KEY_POSITION, indexTab?.toInt())
                }
                "pln" -> intent = Intent(this, TopUpPlnActivity::class.java).apply {
                    putExtra(TopUpPlnActivity.KEY_PAGE_POSITION, indexTab?.toInt())
                }
                "ewallet" -> intent = Intent(this, EWalletActivity::class.java).apply {
                    putExtra(EWalletActivity.KEY_PROVIDER_CODE, providerCode)
                }
                "pdam" -> intent = Intent(this, PembayaranBulananActivity::class.java).apply {
                    putExtra(PembayaranBulananActivity.KEY_POSITION, 4)
                }
                "game" -> intent = Intent(this, DetailGameMenuActivity::class.java).apply {
                    putExtra(DetailGameMenuActivity.PROVIDER_CODE_KEY, providerCode)
                }
                "hotel" -> intent = ModuleRoute.internalIntent(this, HOTEL_PATH)
                else -> intent = Intent(this, MainActivityNavComp::class.java)
            }
            startActivity(intent)
            finish()
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        if (RootBeer(this).isRooted) {
            finishAffinity()
            startActivity(Intent(this, RootedActivity::class.java))
        }
    }
}
