package com.pasukanlangit.cashplus.ui.omni.packageomni

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityPackageOmniBinding
import com.pasukanlangit.cashplus.domain.model.OmniMenuResponse
import com.pasukanlangit.cashplus.domain.model.OmniPackageListResponse
import com.pasukanlangit.cashplus.domain.model.ResponsePackage
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.EnterPinDialogFragment
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.library_core.domain.model.NotifType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PackageOmniActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPackageOmniBinding
    private val viewModel: PackageOmniViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private lateinit var menuAdapter: PackageMenuAdapter
    private lateinit var categoryAdapter: PackageCategoryAdapter
    private lateinit var packagesAdapter: PackagesAdapter

    private var phoneDest: String? = null
    private var uuid: String? = null
    private var accessToken: String? = null
    private var paymentCode: String? = null
    private var balance: Double? = null
    private var mlid: String? = null

    private var packageSelected: String? = null
    private lateinit var packaged: ResponsePackage

    val enterPIN = EnterPinDialogFragment(
        onEnterPinCompleted = {
            viewModel.onTriggersEvent(
                PackageOmniEvents.PackageTransaction(
                    uuid = uuid ?: "", codeProduct = "PAYOMNICHANNEL", dest = "$paymentCode-$phoneDest",
                    pin = it, accessToken = accessToken ?: ""
                )
            )
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPackageOmniBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        phoneDest = intent.getStringExtra(PHONE_DEST_KEY)
        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()

        if (!uuid.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
            setRecyclerView()
            viewModel.onTriggersEvent(
                PackageOmniEvents.FetchMenu(
                    uuid = uuid ?: "", dest = phoneDest ?: "", accessToken =  accessToken ?: ""
                )
            )
            with(binding) {
                imgBack.setOnClickListener { finish() }
                swipeRefreshOmni.setOnRefreshListener {
                    if (mlid.isNullOrEmpty())
                        viewModel.onTriggersEvent(
                            PackageOmniEvents.FetchMenu(uuid = uuid ?: "", dest = phoneDest ?: "", accessToken = accessToken ?: "")
                        )
                    else
                        viewModel.onTriggersEvent(
                            PackageOmniEvents.FetchPackage(uuid = uuid ?: "", dest = phoneDest ?: "", mlId = mlid.toString(), accessToken = accessToken ?: "")
                        )
                    swipeRefreshOmni.isRefreshing = false
                }
            }
        }
    }

    private fun setRecyclerView() {
        menuAdapter = PackageMenuAdapter()
        categoryAdapter = PackageCategoryAdapter()
        packagesAdapter = PackagesAdapter()
        with(binding.rvMenuOmni) {
            layoutManager = LinearLayoutManager(this@PackageOmniActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = menuAdapter
            addItemDecoration(CashplusItemDecoration(10))
        }
        menuAdapter.setOnItemMenuClickListener(object: PackageMenuAdapter.OnItemMenuClickListener {
            override fun onItemMenuClicked(mlId: String) {
                this@PackageOmniActivity.mlid = mlId
                if (::menuAdapter.isInitialized) menuAdapter.setOnItemMenuSelected(mlId)
                viewModel.onTriggersEvent(
                    PackageOmniEvents.FetchPackage(uuid = uuid ?: "", dest = phoneDest ?: "", mlId, accessToken ?: "")
                )
            }
        })

        with(binding.rvCategoryOmniPackage) {
            layoutManager = LinearLayoutManager(this@PackageOmniActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
            addItemDecoration(CashplusItemDecoration(10))
        }
        categoryAdapter.setOnItemClickListener(object: PackageCategoryAdapter.OnItemClickListener {
            override fun onItemClicked(category: String) {
                if (::packagesAdapter.isInitialized) packagesAdapter.filter.filter(category)
            }
        })

        with(binding.rvOmniPackage) {
            layoutManager = GridLayoutManager(this@PackageOmniActivity, 2)
            adapter = packagesAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
        packagesAdapter.setPackageClickListener(object: PackagesAdapter.OnPackageClickListener {
            override fun onPackageClicked(packaged: ResponsePackage) {
                packageSelected = packaged.name
                this@PackageOmniActivity.packaged = packaged
                if (balance == null) {
                    GenericModalDialogCashplus.Builder()
                        .title(getString(R.string.something_wrong))
                        .image(R.raw.cashplus_gagal, true)
                        .description(getString(R.string.get_balance_error))
                        .buttonNegative(
                            NegativeButtonAction(
                                btnLabel = getString(R.string.close),
                                setClickOnDismiss = true
                            )
                        ).show(supportFragmentManager)
                    return
                }
                viewModel.onTriggersEvent(
                    PackageOmniEvents.PackageOrder(
                        uuid = uuid ?: "", dest = phoneDest.toString(), packageId = packaged.id,
                        accessToken = accessToken ?: ""
                    )
                )
            }
        })

        collectData()
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // GET BALANCE
                launch {
                    viewModel.loadingBalance.collectLatest { binding.progressBarPackage.isVisible = it }
                }
                launch {
                    viewModel.balance.collectLatest { response ->
                        if (response != null) balance = response.balance!!
                    }
                }
                launch {
                    viewModel.errorBalance.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@PackageOmniActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggersEvent(PackageOmniEvents.RemoveBalanceMessage)
                        }
                    }
                }

                // GET MENU
                launch {
                    viewModel.loadingMenu.collectLatest { binding.progressBarPackage.isVisible = it }
                }
                launch {
                    viewModel.menus.collectLatest { response -> response?.let { bindToMenu(it) } }
                }
                launch {
                    viewModel.errorMenu.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@PackageOmniActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggersEvent(PackageOmniEvents.RemoveFetchMenuError)
                        }
                    }
                }

                // PACKAGE LIST
                launch {
                    viewModel.loadingPackage.collectLatest { binding.progressBarPackage.isVisible = it }
                }
                launch {
                    viewModel.packages.collectLatest { response -> response?.let { bindToView(it) } }
                }
                launch {
                    viewModel.errorPackage.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@PackageOmniActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggersEvent(PackageOmniEvents.RemovePackageMessage)
                        }
                    }
                }

                // PACKAGE ORDER
                launch {
                    viewModel.orderLoading.collectLatest {
                        binding.progressBarPackage.isVisible = it
                    }
                }
                launch {
                    viewModel.packageOrder.collectLatest { response ->
                        if (response != null) {
                            this@PackageOmniActivity.paymentCode = response.paymentCode
                            DetailPackageFragment.newInstance(
                                packaged = packaged, tag = response.transaction, balance = balance!!
                            ).show(supportFragmentManager, "Detail Package Omni")
                        }
                    }
                }
                launch {
                    viewModel.orderError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@PackageOmniActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggersEvent(PackageOmniEvents.RemovePackageOrderMessage)
                        }
                    }
                }

                // TRANSACTION "PAY"
                launch {
                    viewModel.trxLoading.collectLatest { enterPIN.setLoading(it) }
                }
                launch {
                    viewModel.trxResponse.collectLatest {
                        if (it != null) {
                            enterPIN.dismiss()
                            delay(300)
                            MyUtils.goToMainAndSendMessage(
                                this@PackageOmniActivity,
                                getString(R.string.omni_buy_successfully, packageSelected),
                                NotifType.NOTIF_SUCCESS
                            )
                        }
                    }
                }
                launch {
                    viewModel.trxError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.contains("pin", ignoreCase = true)) enterPIN.clearTextOnDialog()
                            else enterPIN.dismiss()

                            val toast = Toast.makeText(
                                this@PackageOmniActivity,
                                if (info.contains("pin", ignoreCase = true)) getString(com.pasukanlangit.id.ui_downline_transfersaldo.R.string.state_wrong_pin)
                                else info,
                                Toast.LENGTH_SHORT
                            )
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggersEvent(PackageOmniEvents.RemovePackageTransactionMessage)
                        }
                    }
                }
            }
        }
    }

    private fun bindToMenu(menus: OmniMenuResponse) {
        with(binding) {
            rvMenuOmni.isVisible = !menus.menuTitle.isNullOrEmpty() || menus.error.isNullOrEmpty()
            lineGap.isVisible = !menus.menuTitle.isNullOrEmpty() || menus.error.isNullOrEmpty()
            wrapperIsEmpty.isVisible = menus.menuTitle.isNullOrEmpty() || !menus.error.isNullOrEmpty()
            tvStateEmpty.text = getString(R.string.unavailable_package)

            if (!menus.menuTitle.isNullOrEmpty() || menus.error.isNullOrEmpty()) {
                menuAdapter.setMenuOmni(menus.menuTitle)
                menuAdapter.notifyDataSetChanged()
                menuAdapter.setOnItemMenuSelected(menus.menuTitle?.first()?.mlid.toString())
            }
        }
    }

    private fun bindToView(packagesOmni: OmniPackageListResponse) {
        with(binding) {
            wrapperIsEmpty.isVisible = packagesOmni.subcategory.isNullOrEmpty() && packagesOmni.packages.isNullOrEmpty()
            rvCategoryOmniPackage.isVisible = !packagesOmni.subcategory.isNullOrEmpty()
            rvOmniPackage.isVisible = !packagesOmni.packages.isNullOrEmpty()
            tvStateEmpty.text = if (packagesOmni.error.isNullOrEmpty() || packagesOmni.error.trim() == "") getString(R.string.unavailable_package)
            else packagesOmni.error
            if (!packagesOmni.packages.isNullOrEmpty() && !packagesOmni.subcategory.isNullOrEmpty()) {
                categoryAdapter.setCategory(packagesOmni.subcategory)
                categoryAdapter.notifyDataSetChanged()
                packagesAdapter.setPackages(packagesOmni.packages)
                packagesAdapter.notifyDataSetChanged()
                categoryAdapter.setCategoryChoosed(packagesOmni.subcategory.first())
                packagesAdapter.filter.filter(packagesOmni.subcategory.first())
            }
        }
    }

    companion object {
        const val PHONE_DEST_KEY = "phone_dest_key"
    }
}