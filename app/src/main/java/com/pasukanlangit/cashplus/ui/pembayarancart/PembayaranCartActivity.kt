package com.pasukanlangit.cashplus.ui.pembayarancart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.ActivityPembayaranCartBinding
import com.pasukanlangit.cashplus.model.request_body.Buyer
import com.pasukanlangit.cashplus.model.request_body.OnlineStoreOrderCourier
import com.pasukanlangit.cashplus.model.request_body.OnlineStoreOrderRequest
import com.pasukanlangit.cashplus.model.request_body.ProductsItemOrder
import com.pasukanlangit.cashplus.model.response.AddressBookData
import com.pasukanlangit.cashplus.model.response.CostsItem
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.cashplus.ui.addressbook.AddressBookActivity
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.cashplus.ui.pembayarancart.courir.ChooseCourierFragment
import com.pasukanlangit.cashplus.ui.pembayarancart.pay.PayOnlineStoreActivity
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.utils.parcelableArrayList
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PembayaranCartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranCartBinding
    private val viewModel: PembayaranCartViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null
    private var priceProducts: Int = 0

    private var addressBookData: AddressBookData? = null
    private var courierChoosen: CostsItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()
        
        priceProducts = intent.getIntExtra(KEY_TOTAL_PRODUCT_PRICE, 0)
        val qtyProducts = intent.getIntExtra(KEY_TOTAL_QTY, 0)
        val weightProducts = intent.getIntExtra(KEY_TOTAL_WEIGHT, 0)
        val productsChecked = intent.parcelableArrayList<ProductStoreDataItem>(KEY_PRODUCT_ORDERED)
        
        viewModel.setWeight(weightProducts)
        
        with(binding){
            tvTotalProduct.text = "Total Harga ($qtyProducts Barang)"
            tvPriceTotal.text = MyUtils.getNumberRupiah(priceProducts)
            productsChecked?.let { products ->
                with(rvProductOrdered) {
                    layoutManager = LinearLayoutManager(this@PembayaranCartActivity)
                    adapter = PembayaranCartAdapter(products)
                }
            }

            btnBack.setOnClickListener { finish() }

            btnChoseAddress.setOnClickListener {
                startActivity(Intent(this@PembayaranCartActivity, AddressBookActivity::class.java))
            }

            wrapperShipping.setOnClickListener {
                if (addressBookData != null) {
                    ChooseCourierFragment().show(supportFragmentManager, null)
                } else {
                    Toast.makeText(this@PembayaranCartActivity, "Pilih alamat pengiriman terlebih dahulu", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            viewModel.allRequirementCompleted.observe(this@PembayaranCartActivity) {
                btnCheckout.isEnabled = it
            }

            btnCheckout.setUpToProgressButton(this@PembayaranCartActivity)


            btnCheckout.setOnClickListener {
                if (!uuid.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
                    if (addressBookData != null || courierChoosen != null || productsChecked != null) {
                        val couirer =
                            OnlineStoreOrderCourier(courierChoosen!!.prefix.lowercase(), courierChoosen!!.service)
                        val productsOrdered = mutableListOf<ProductsItemOrder>()
                        productsChecked?.forEach { productCart ->
                            productsOrdered.add(
                                ProductsItemOrder(
                                    productCart.note,
                                    productCart.productId,
                                    productCart.qty.toString(), ""
                                )
                            )
                        }
                        val orderRequest = OnlineStoreOrderRequest(
                            courier = couirer,
                            voucherCode = "",
                            uuid = uuid!!,
                            "",
                            products = productsOrdered,
                            buyer = Buyer(bookAddressId = addressBookData?.bookAddressId ?: "")
                        )

                        viewModel.order(orderRequest, accessToken!!)
                    } else {
                        Toast.makeText(
                            this@PembayaranCartActivity,
                            "Pilih alamat pengiriman dan courier terlebih dahulu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        observeMainBook()
        observeCourierChoosen()
        observeOrderResponse()
    }

    private fun observeOrderResponse() {
        viewModel.order.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.btnCheckout.hideProgress("Checkout")
                    it.data?.getContentIfNotHandled()?.let { dataOrder ->
                        with(Intent(this, PayOnlineStoreActivity::class.java)) {
                            putExtra(PayOnlineStoreActivity.KEY_ORDER_RESPONSE, dataOrder)
                            startActivity(this)
                        }
                    }
                }
                Status.LOADING -> {
                    binding.btnCheckout.showLoading()
                }
                Status.ERROR -> {
                    binding.btnCheckout.hideProgress("Checkout")
                    val menusAllFragment = ButtomSheetNotif(
                        it.message,
                        NotifType.NOTIF_ERROR
                    )
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeCourierChoosen() {
        with(binding){
            viewModel.courierChoosen.observe(this@PembayaranCartActivity) {
                tvWarningCourir.isVisible = it == null
                tvDescShipping.isVisible = it != null
                courierChoosen = it

                if (it != null) {
                    tvLabelShipping.text =
                        "${it.prefix} ${it.service} (${MyUtils.getNumberRupiah(it.cost[0].value)})"
                    tvPriceShipping.text = MyUtils.getNumberRupiah(it.cost[0].value)
                    tvDescShipping.text =
                        "Estimasi tiba ${it.cost[0].etd.replace("hari", "", true)} hari"

                    val priceTotal = priceProducts + it.cost[0].value
                    tvPrice.text = MyUtils.getNumberRupiah(priceTotal)
                } else {
                    tvPrice.text = "-"
                    tvLabelShipping.text = "Pilih Pengiriman"
                    tvPriceShipping.text = "-"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeMainBook() {
        with(binding){
            viewModel.addressMainBookAddress.observe(this@PembayaranCartActivity) {
                if (it.status == Status.SUCCESS) {
                    addressBookData = it.data
                    if (it.data != null) {
                        val addressBook = it.data

                        tvWarningAddress.isVisible = false
                        wrapperAddressBook.isVisible = true
                        tvTypeAddress.text = addressBook.addressType
                        labelMainAddress.isVisible = addressBook.isMainAddress == "1"
                        tvDescAddress.text =
                            "${addressBook.receiverName} (${addressBook.phoneNumber}) \n${addressBook.address}"
                    } else {
                        tvWarningAddress.isVisible = true
                        wrapperAddressBook.isVisible = false
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!uuid.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
            viewModel.getMainBookAddress(uuid!!, accessToken!!)
        }

    }

    companion object {
        const val KEY_PRODUCT_ORDERED = "KEY_PRODUCT_ORDERED"
        const val KEY_TOTAL_PRODUCT_PRICE = "KEY_TOTAL_PRODUCT_PRICE"
        const val KEY_TOTAL_QTY = "KEY_TOTAL_QTY"
        const val KEY_TOTAL_WEIGHT = "KEY_TOTAL_WEIGHT"
    }
}