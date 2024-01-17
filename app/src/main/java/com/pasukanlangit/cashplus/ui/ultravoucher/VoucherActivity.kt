package com.pasukanlangit.cashplus.ui.ultravoucher

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.adapter.DataAdapter
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.ActivityVoucherBinding
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.TopUpGameViewModel
import com.pasukanlangit.cashplus.utils.CategoryProduct
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class VoucherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVoucherBinding
    private val viewModel: TopUpGameViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private lateinit var mAdapter: DataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uuid = sharedPref.getUUID()
        val token = sharedPref.getAccessToken()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnCancel.setOnClickListener { finish() }

        setUpRvVoucher()
        observeProductList()
        eventOnSearch()

        if (uuid != null && token != null) {
            val productRequest = ProductRequest(
                uuid = uuid,
                category = CategoryProduct.VOUCHER.value,
                kode_provider = "",
                is_faktur = "",
                is_active = "1"
            )

            viewModel.getProduct(productRequest, token)
        }
    }

    private fun eventOnSearch() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(::mAdapter.isInitialized)  mAdapter.filter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun setUpRvVoucher() {
        binding.rvVoucher.layoutManager = LinearLayoutManager(this)
    }

    private fun observeProductList() {
        viewModel.productList.observe(this){
            when (it.status) {
                Status.SUCCESS -> {
                    with(binding) {
                        rvVoucherShimmer.visibility = View.GONE
                        rvVoucherShimmer.stopShimmer()
                        rvVoucher.visibility = View.VISIBLE

                        if (it.data != null) {
                            mAdapter = DataAdapter(it.data) { product ->
                                InputVoucherDataFragment(product).show(supportFragmentManager, null)
//                            val inputIdGame = input_nomer_game.text.toString()
//
//                            if (inputIdGame.isNotEmpty()) {
//                                val intent = Intent(this, PembayaranActivity::class.java)
//                                intent.putExtra(PembayaranActivity.KEY_PRODUCT, gameModel)
//                                intent.putExtra(
//                                    PembayaranActivity.KEY_NUMBER, inputIdGame
//                                )
////                                intent.putExtra(
////                                    PembayaranActivity.KEY_LOGO_PRODUCT,
////                                    imgLogo
////                                )
//                                startActivity(intent)
//                            } else {
//                                val menusAllFragment = ButtomSheetNotif(
//                                    "Masukkan ID Pelanggan terlebih dahulu",
//                                    NotifType.NOTIF_INPUT_UNCOMPLETED
//                                )
//                                menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
//
//                            }
                            }
                            rvVoucher.adapter = mAdapter
                        }
                    }

                }
                Status.LOADING -> {
                    with(binding) {
                        rvVoucherShimmer.visibility = View.VISIBLE
                        rvVoucherShimmer.startShimmer()
                        rvVoucher.visibility = View.GONE
                    }
                }
                Status.ERROR -> {
                    val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)

                    with(binding) {
                        rvVoucherShimmer.visibility = View.GONE
                        rvVoucherShimmer.stopShimmer()
                        rvVoucher.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}