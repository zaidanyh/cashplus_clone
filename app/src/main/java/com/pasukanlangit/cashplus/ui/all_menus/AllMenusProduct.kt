package com.pasukanlangit.cashplus.ui.all_menus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.GridLayoutManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityAllMenusBinding
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.ewallet.ChooseEwalletMethodFragment
import com.pasukanlangit.cashplus.ui.ewallet.EWalletActivity
import com.pasukanlangit.cashplus.ui.omni.EnterNumOmniActivity
import com.pasukanlangit.cashplus.ui.pembayaran_bulanan.PembayaranBulananActivity
import com.pasukanlangit.cashplus.ui.pembayaran_game_menu.DetailGameMenuActivity
import com.pasukanlangit.cashplus.ui.pembayaran_pln.TopUpPlnActivity
import com.pasukanlangit.cashplus.ui.pembayaran_provider.TopUpProviderActivity
import com.pasukanlangit.cashplus.ui.ultravoucher.VoucherActivity
import com.pasukanlangit.cashplus.utils.MyUtils.showCoomingSon
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.*
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.utils.CategoryProduct
import com.pasukanlangit.id.library_core.domain.model.NotifType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllMenusProduct : AppCompatActivity() {

    private lateinit var binding: ActivityAllMenusBinding
    private val allMenusViewModel: AllMenusViewModel by viewModel()
    private val sharedPref : SharedPrefDataSource by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllMenusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimationService()

        binding.appBar.setOnBackPressed { finish() }

        val token = sharedPref.getAccessToken()
        val uuid = sharedPref.getUUID()

        if(uuid != null && token != null){
            val productRequest = ProductRequest(
                uuid = uuid,
                category = CategoryProduct.GAMES.value,
                kode_provider = "",
                is_faktur = "",
                is_active = "1"
            )

            allMenusViewModel.getMenuGame(productRequest, token)
            allMenusViewModel.getMenuEmoney(productRequest.copy(category = CategoryProduct.E_WALLET.value), token)
        }

        setUpRecyclerGame()
        setUpRecyclerEmoney()
        setUpRecyclerTagihan()

        allMenusViewModel.menuGames.observe(this) {
            when(it.status){
                Status.SUCCESS -> {
                    with(binding) {
                        val uniqueProduct = it.data?.data?.distinctBy { prod -> prod.kode_provider }
                        uniqueProduct?.let { menus ->
                            rvGameAll.adapter = MenuProductAllAdapter(menus) { providerCode ->
                                startActivity(
                                    Intent(this@AllMenusProduct, DetailGameMenuActivity::class.java).apply {
                                        putExtra(DetailGameMenuActivity.PROVIDER_CODE_KEY, providerCode)
                                    }
                                )
                            }
                        }
                    }
                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                }
            }
        }

        allMenusViewModel.menuEmoney.observe(this) { response ->
            when(response.status) {
                Status.SUCCESS -> {
                    with(binding) {
                        rvEmoneyAllShimmer.visibility = View.INVISIBLE
                        rvEmoneyAllShimmer.stopShimmer()
                        rvEmoneyAll.visibility = View.VISIBLE

                        val uniqueProduct = response.data?.data?.distinctBy { prod -> prod.kode_provider }
                        uniqueProduct?.let { menus ->
                            rvEmoneyAll.adapter = MenuProductAllAdapter(menus) { codeProvider ->
                                if (codeProvider == "DANA") {
                                    val productPurchase = response.data.data.find { product ->
                                        product.product_type == "U"
                                    }
                                    ChooseEwalletMethodFragment.newInstance(
                                        product = productPurchase, providerCode = codeProvider
                                    ).show(supportFragmentManager, null)
                                    return@MenuProductAllAdapter
                                }
                                startActivity(
                                    Intent(this@AllMenusProduct, EWalletActivity::class.java).apply {
                                        putExtra(EWalletActivity.KEY_PROVIDER_CODE, codeProvider)
                                    }
                                )
                            }
                        }
                    }
                }
                Status.LOADING -> {
                    with(binding) {
                        rvEmoneyAllShimmer.visibility = View.VISIBLE
                        rvEmoneyAllShimmer.startShimmer()
                        rvEmoneyAll.visibility = View.INVISIBLE
                    }
                }
                Status.ERROR -> {
                    val menusAllFragment = ButtomSheetNotif(response.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)

                    with(binding) {
                        rvEmoneyAllShimmer.visibility = View.INVISIBLE
                        rvEmoneyAllShimmer.stopShimmer()
                        rvEmoneyAll.visibility = View.INVISIBLE
                    }
                }
            }
        }

        with(binding) {
            btnMenusAllPulsa.setOnClickListener {
                startActivity(Intent(this@AllMenusProduct, TopUpProviderActivity::class.java))
            }
            btnMenusAllData.setOnClickListener {
                startActivity(
                    Intent(this@AllMenusProduct, TopUpProviderActivity::class.java).apply {
                        putExtra(TopUpProviderActivity.KEY_ACTIVE_VIEWPAGER, 1)
                    }
                )
            }
            btnMenusAllTelephone.setOnClickListener {
                startActivity(
                    Intent(this@AllMenusProduct, TopUpProviderActivity::class.java).apply {
                        putExtra(TopUpProviderActivity.KEY_ACTIVE_VIEWPAGER, 2)
                    }
                )
            }
            btnMenusAllSms.setOnClickListener {
                startActivity(
                    Intent(this@AllMenusProduct, TopUpProviderActivity::class.java).apply {
                        putExtra(TopUpProviderActivity.KEY_ACTIVE_VIEWPAGER, 3)
                    }
                )
            }

            btnMenusAllHotel.setOnClickListener {
                startActivity(
                    ModuleRoute.internalIntent(this@AllMenusProduct, HOTEL_PATH)
                )
            }
            btnMenusAllBioskop.setOnClickListener {
                showCoomingSon(supportFragmentManager)
            }
            btnMenusVoucher.setOnClickListener {
                startActivity(Intent(this@AllMenusProduct, VoucherActivity::class.java))
            }

            btnMenusAllPlane.setOnClickListener {
                startActivity(
                    ModuleRoute.internalIntent(this@AllMenusProduct, FLIGHT_PATH)
                )
            }
            btnMenusAllKai.setOnClickListener {
                startActivity(
                    ModuleRoute.internalIntent(this@AllMenusProduct, TRAIN_PATH)
                )
            }
            btnMenusAllShip.setOnClickListener {
                startActivity(
                    ModuleRoute.internalIntent(this@AllMenusProduct, SHIP_PATH)
                )
            }
        }
    }

    private fun setUpRecyclerTagihan() {
        with(binding.rvTagihanAll) {
            layoutManager = GridLayoutManager(this@AllMenusProduct,5)
            adapter = MenuTagihanAdapter(getDummyTagihanMenu()) {
                lateinit var intent: Intent
                when (it) {
                    310 -> intent = Intent(this@AllMenusProduct, EnterNumOmniActivity::class.java)
                    PositionPagePembayaranBulanan.TAGIHAN_PLN.position -> {
                        intent = Intent(this@AllMenusProduct, TopUpPlnActivity::class.java)
                        intent.putExtra(TopUpPlnActivity.KEY_PAGE_POSITION,0)
                    }
                    else -> {
                        intent = Intent(this@AllMenusProduct, PembayaranBulananActivity::class.java)
                        intent.putExtra(PembayaranBulananActivity.KEY_POSITION, it)
                    }
                }
                startActivity(intent)
            }
        }
    }

    private fun setUpRecyclerGame() {
        with(binding.rvGameAll) {
            layoutManager = GridLayoutManager(this@AllMenusProduct, 5)
        }
    }

    private fun setUpRecyclerEmoney() {
        with(binding.rvEmoneyAll) {
            layoutManager = GridLayoutManager(this@AllMenusProduct,5)
        }
    }


    private fun startAnimationService(){
        var offsetAnimation : Long = 0
        with(binding) {
            arrayOf(
                txtTopUp, txtDescTopUp,
                btnMenusAllPulsa, btnMenusAllData, btnMenusAllTelephone, btnMenusAllSms,
                txtBill, txtDescBill,
                txtEntertainment, txtDescEntertainment, btnMenusAllHotel, btnMenusAllBioskop, btnMenusVoucher,
                txtTravel, txtDescTravel, btnMenusAllPlane, btnMenusAllKai, btnMenusAllShip,
                txtEWallet, txtDescEWallet,
                txtGame, txtDescGame
            ).forEach { btn ->
                val animation = AnimationUtils.loadAnimation(this@AllMenusProduct,R.anim.translate_fade_in_anim)
                animation.startOffset = offsetAnimation
                btn.startAnimation(animation)
                offsetAnimation += 30
            }
        }
    }
}