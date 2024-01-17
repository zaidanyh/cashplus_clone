package com.pasukanlangit.cashplus.ui.ewallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.pasukanlangit.cashplus.databinding.ActivityEwalletListBinding
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.TopUpGameViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.utils.CategoryProduct
import com.pasukanlangit.id.core.utils.GridSpacingItemDecoration
import com.pasukanlangit.id.library_core.domain.model.NotifType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EwalletListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEwalletListBinding
    private val viewModel: TopUpGameViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEwalletListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()

        if (!uuid.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
            val productRequest = ProductRequest(
                uuid = uuid ?: "",
                category = CategoryProduct.E_WALLET.value,
                kode_provider = "",
                is_faktur = "",
                is_active = "1"
            )
            viewModel.getMenu(productRequest, accessToken ?: "")
        }
        binding.appBar.setOnBackPressed { finish() }

        observeMenu()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        with(binding.rvCategoryEWallet) {
            layoutManager = GridLayoutManager(this@EwalletListActivity,2)
            addItemDecoration(GridSpacingItemDecoration(24))
        }
    }

    private fun observeMenu() {
        viewModel.menuGames.observe(this) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    with(binding) {
                        rvCategoryEWalletShimmer.isVisible = false
                        rvCategoryEWalletShimmer.stopShimmer()
                        rvCategoryEWallet.isVisible = true

                        val uniqueProduct = response.data?.data?.distinctBy { prod -> prod.kode_provider }
                        uniqueProduct?.let { menus ->
                            binding.rvCategoryEWallet.adapter = EWalletListAdapter(menus) { eWallet ->
                                val productPurchase = response.data.data.find { product ->
                                    product.product_type == "U" && product.kode_provider == eWallet.kode_provider
                                }
                                if (productPurchase != null) {
                                    ChooseEwalletMethodFragment.newInstance(
                                        product = productPurchase, providerCode = eWallet.kode_provider
                                    ).show(supportFragmentManager, null)
                                    return@EWalletListAdapter
                                }
//                                if (eWallet.kode_provider == "DANA") {
//                                    val productPurchase = response.data.data.find { product ->
//                                        product.product_type == "U"
//                                    }
//                                    ChooseEwalletMethodFragment.newInstance(
//                                        product = productPurchase, providerCode = eWallet.kode_provider
//                                    ).show(supportFragmentManager, null)
//                                    return@EWalletListAdapter
//                                }
                                startActivity(
                                    Intent(this@EwalletListActivity, EWalletActivity::class.java).apply {
                                        putExtra(EWalletActivity.KEY_PROVIDER_CODE, eWallet.kode_provider)
                                    }
                                )
                            }
                        }
                    }
                }
                Status.LOADING -> {
                    with(binding) {
                        rvCategoryEWalletShimmer.isVisible = true
                        rvCategoryEWalletShimmer.startShimmer()
                        rvCategoryEWallet.isVisible = false
                    }
                }
                Status.ERROR -> {
                    with(binding) {
                        rvCategoryEWalletShimmer.isVisible = false
                        rvCategoryEWalletShimmer.stopShimmer()
                        rvCategoryEWallet.isVisible = true
                    }

                    val menusAllFragment = ButtomSheetNotif(response.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                }
            }
        }
    }
}