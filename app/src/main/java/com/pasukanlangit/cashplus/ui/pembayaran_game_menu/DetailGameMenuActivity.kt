package com.pasukanlangit.cashplus.ui.pembayaran_game_menu

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.MotionEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.adapter.DataProductAdapter
import com.pasukanlangit.cashplus.databinding.ActivityDetailGameMenuBinding
import com.pasukanlangit.cashplus.model.request_body.OprNameRequest
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.model.response.ServerIdDataItem
import com.pasukanlangit.cashplus.ui.all_menus.DetailMenusViewModel
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.pages.home.KEY_GAME_DEEPLINK
import com.pasukanlangit.cashplus.utils.CategoryProduct
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.*
import com.pasukanlangit.id.core.utils.InputUtil.inputCharNumberFilter
import com.pasukanlangit.id.library_core.domain.model.NotifType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailGameMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailGameMenuBinding
    private val viewModel: DetailMenusViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private val saintSaiyaProvider = "SAINT_SEIYA"
    private val listGameNeedsServerName = listOf(
        saintSaiyaProvider, "GENSHIN_IMPACT", "LIFEAFTER", "BLEACH_MOBILE"
    )

    private var serverNameList: List<ServerIdDataItem> = listOf()
    private var serverIdSelected: ServerIdDataItem? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailGameMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = intent.data
        val providerCode = uri?.getQueryParameter(KEY_GAME_DEEPLINK) ?: intent.getStringExtra(PROVIDER_CODE_KEY)

        val uuid = sharedPref.getUUID()
        val token = sharedPref.getAccessToken()

        setupProductGame()
        with(binding) {
            val isNeedServerName = listGameNeedsServerName.contains(providerCode?.uppercase())
            txtServerName.isVisible = isNeedServerName
            spinnerServer.isVisible = isNeedServerName
            txtZoneId.isVisible = providerCode.equals("MOBILE_LEGENDS", true)
            edtZoneId.isVisible = providerCode.equals("MOBILE_LEGENDS", true)
            txtUsername.isVisible = providerCode.equals(saintSaiyaProvider, true)
            edtUsername.isVisible = providerCode.equals(saintSaiyaProvider, true)

            if (isNeedServerName) viewModel.getServerGamesId(
                OprNameRequest(providerCode ?: "", uuid ?: ""),
                token ?: ""
            )
            viewModel.getProduct(
                ProductRequest(
                    uuid = uuid ?: "",
                    category = CategoryProduct.GAMES.value,
                    kode_provider = providerCode ?: "",
                    is_faktur = "",
                    is_active = "1"
                ),
                accessToken = token ?: ""
            )

            appBar.setTitle(CategoryProduct.GAMES.title)
            appBar.setOnBackPressed { finish() }
            edtCustomerId.filters = arrayOf(InputFilter.LengthFilter(24), inputCharNumberFilter)
            spinnerServer.setOnTouchListener { _, event ->
                if (event.action== MotionEvent.ACTION_UP) {
                    KeyboardUtil.hideSoftKeyboard(this@DetailGameMenuActivity)
                }
                false
            }
        }

        observeServerList()
        observeProduct()
    }

    private fun setupProductGame() {
        with(binding.rvGameVoucher) {
            layoutManager = LinearLayoutManager(this@DetailGameMenuActivity)
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    private fun observeServerList() {
        viewModel.serverName.observe(this) {
            if (it.status == Status.SUCCESS) {
                serverNameList = it.data?.data ?: listOf()

                val serverList = arrayListOf<String>()
                serverList.add(getString(R.string.input_server))

                serverNameList.forEach { server ->
                    serverList.add(server.serverName)
                }
                val serverAdapter = setDropDownView(this, serverList)
                serverAdapter.setDropDownViewResource(R.layout.item_spinner_small)
                binding.spinnerServer.adapter = serverAdapter
            }
        }
    }

    private fun observeProduct() {
        viewModel.product.observe(this) { response ->
            when(response.status) {
                Status.LOADING -> {
                    with(binding) {
                        rvGameVoucherShimmer.isVisible = true
                        rvGameVoucherShimmer.startShimmer()
                        rvGameVoucher.isVisible = false
                    }
                }
                Status.SUCCESS -> {
                    with(binding) {
                        rvGameVoucherShimmer.isVisible = false
                        rvGameVoucherShimmer.stopShimmer()
                        rvGameVoucher.isVisible = true

                        if (response.data != null) {
                            rvGameVoucher.adapter = response.data.data?.let { data ->
                                DataProductAdapter(data) { product ->
                                    val inputIdGame = edtCustomerId.text.toString().trim()
                                    val zoneId = edtZoneId.text.toString().trim()
                                    val username = edtUsername.text.toString().trim()

                                    if (inputIdGame.isNotEmpty()) {
                                        if(spinnerServer.isVisible) {
                                            val serverName = spinnerServer.selectedItem.toString()
                                            serverIdSelected = serverNameList.singleOrNull { server -> server.serverName.lowercase() == serverName.lowercase() }
                                            if (serverIdSelected == null) {
                                                GenericModalDialogCashplus.Builder()
                                                    .title(getString(R.string.something_wrong))
                                                    .image(R.drawable.illustration_empty)
                                                    .description(getString(R.string.any_not_found, getString(R.string.server_name)))
                                                    .buttonNegative(
                                                        NegativeButtonAction(
                                                            btnLabel = getString(R.string.close),
                                                            backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                                            buttonTextColor = Color.parseColor("#0A57FF"),
                                                            setClickOnDismiss = true
                                                        )
                                                    )
                                                    .show(supportFragmentManager, "server_id_is_wrong")
                                                return@DataProductAdapter
                                            }
                                        }

                                        if(edtZoneId.isVisible && zoneId.isEmpty()) {
                                            GenericModalDialogCashplus.Builder()
                                                .title(getString(R.string.something_wrong))
                                                .image(R.drawable.illustration_empty)
                                                .description(getString(R.string.input_zone_id_first))
                                                .buttonNegative(
                                                    NegativeButtonAction(
                                                        btnLabel = getString(R.string.close),
                                                        backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                                        buttonTextColor = Color.parseColor("#0A57FF"),
                                                        setClickOnDismiss = true
                                                    )
                                                )
                                                .show(supportFragmentManager, "server_id_is_wrong")
                                            return@DataProductAdapter
                                        }

                                        if(edtUsername.isVisible && username.isEmpty()) {
                                            GenericModalDialogCashplus.Builder()
                                                .title(getString(R.string.something_wrong))
                                                .image(R.drawable.illustration_empty)
                                                .description(getString(R.string.input_username_first))
                                                .buttonNegative(
                                                    NegativeButtonAction(
                                                        btnLabel = getString(R.string.close),
                                                        backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                                        buttonTextColor = Color.parseColor("#0A57FF"),
                                                        setClickOnDismiss = true
                                                    )
                                                )
                                                .show(supportFragmentManager, "server_id_is_wrong")
                                            return@DataProductAdapter
                                        }

                                        DetailTopUpGameFragment.newInstance(
                                            productModel = product,
                                            customerId = "${inputIdGame}${if(spinnerServer.isVisible) "-${serverIdSelected?.serverId}" else ""}${if(edtZoneId.isVisible) "$zoneId" else ""}${if(edtUsername.isVisible) "-$username" else ""}"
                                        ).show(supportFragmentManager, "Detail Top Up Game")
                                        return@DataProductAdapter
                                    }

                                    GenericModalDialogCashplus.Builder()
                                        .title(getString(R.string.something_wrong))
                                        .image(R.drawable.illustration_empty)
                                        .description(getString(R.string.input_id_first))
                                        .buttonNegative(
                                            NegativeButtonAction(
                                                btnLabel = getString(R.string.close),
                                                backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                                buttonTextColor = Color.parseColor("#0A57FF"),
                                                setClickOnDismiss = true
                                            )
                                        )
                                        .show(supportFragmentManager, "CustomerId_is_empty")
                                }
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    with(binding) {
                        rvGameVoucherShimmer.isVisible = false
                        rvGameVoucherShimmer.stopShimmer()
                        rvGameVoucher.isVisible = true
                    }

                    val menusAllFragment = ButtomSheetNotif(response.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                }
            }
        }
    }

    companion object {
        const val PROVIDER_CODE_KEY = "provider_code_key"
    }
}