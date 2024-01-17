package com.pasukanlangit.cashplus.ui.pembayarancart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.ActivityPembayaranCartLangsungBinding
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
import com.pasukanlangit.cashplus.ui.pembayarancart.pay.EnterPinOnlineStoreActivity
import com.pasukanlangit.cashplus.ui.pembayarancart.pay.PayOnlineStoreActivity
import com.pasukanlangit.cashplus.ui.pembayarancart.pay.ProductCode
import com.pasukanlangit.cashplus.utils.*
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class PembayaranCartLangsungActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranCartLangsungBinding
    private val viewModel: PembayaranCartViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null

    private var currentQty = 1
    private var product: ProductStoreDataItem? = null
    private var addressBookData: AddressBookData? = null
    private var courierChoosen: CostsItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranCartLangsungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()

        val productIntent = intent.parcelable<ProductStoreDataItem>(KEY_PRODUCT)

        binding.btnBack.setOnClickListener { finish() }
        productIntent?.let { product ->
            this.product = product
            viewModel.setWeight(product.weight.toInt())

            binding.tvPriceTotal.text = MyUtils.getNumberRupiah(product.price.toInt())
            Glide.with(this)
                .load(product.image1)
                .thumbnail(
                    Glide.with(this)
                        .load(R.raw.image_loading_state)
                )
                .error(R.drawable.ic_empty)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(binding.ivProduct)

            binding.tvTitleProduct.text = product.productName
            binding.tvPriceProduct.text =
                MyUtils.getNumberRupiah(product.price.toDoubleOrNull() ?: 0.0)

            binding.edtQty.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    var qty = binding.edtQty.text.toString().toInt()
                    if (qty > product.stock.toInt()) {
                        qty = product.stock.toInt()
                    }
                    if (qty < 1) {
                        qty = 1
                    }
                    viewModel.setQtyProduct(qty)
                }
                false
            }
        }

        binding.btnChoseAddress.setOnClickListener {
            startActivity(Intent(this, AddressBookActivity::class.java))
        }

        binding.wrapperShipping.setOnClickListener {
            if (addressBookData != null) {
                ChooseCourierFragment().show(supportFragmentManager, null)
            } else {
                Toast.makeText(this, "Pilih alamat pengiriman terlebih dahulu", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnCheckout.apply {
            setUpToProgressButton(this@PembayaranCartLangsungActivity)
            setOnClickListener {
                if (!uuid.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
                    if (addressBookData != null || courierChoosen != null || product != null) {
                        val couirer = OnlineStoreOrderCourier(
                            courierChoosen!!.prefix.lowercase(Locale.getDefault()),
                            courierChoosen!!.service
                        )
                        val productsOrdered = listOf(
                            ProductsItemOrder(
                                binding.edtNote.text.toString().trim(),
                                product!!.productId,
                                currentQty.toString(), ""
                            )
                        )

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
                            this@PembayaranCartLangsungActivity,
                            "Pilih alamat pengiriman dan courier terlebih dahulu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        setUpBtnQty()
        observeQty()
        observeMainBook()
        observeCourierChoosen()
        observeBtnCheckout()
        observeOrderResponse()
    }

    private fun observeBtnCheckout() {
        viewModel.allRequirementCompleted.observe(this) {
            binding.btnCheckout.isEnabled = it
        }
    }

    private fun observeQty() {
        viewModel.qtyProduct.observe(this) {
            currentQty = it

            binding.edtQty.setText(it.toString())
            binding.tvTotalProduct.text = "Total Harga (${it} Barang)"
            binding.tvPriceTotal.text = MyUtils.getNumberRupiah(it * (product?.price?.toIntOrNull() ?: 0))

            product?.let { this_product ->
                binding.btnDecreaseQty.isEnabled = it > 1
                binding.btnIncreaseQty.isEnabled = it < (this_product.stock.toIntOrNull() ?: 0)

                if (courierChoosen != null) {
                    binding.tvPrice.text =
                        MyUtils.getNumberRupiah(
                            this_product.price.toIntOrNull()
                                ?: (0 * it + courierChoosen!!.cost[0].value)
                        )
                } else {
                    binding.tvPrice.text = "-"
                }
            }
        }
    }

    private fun setUpBtnQty() {
        binding.btnIncreaseQty.setOnClickListener {
            if (product != null) {
                val newQtyValue = currentQty.inc()
                if (newQtyValue <= product!!.stock.toInt()) {
                    viewModel.setQtyProduct(newQtyValue)
                }
            }
        }

        binding.btnDecreaseQty.setOnClickListener {
            if (product != null) {
                val newQtyValue = currentQty.dec()
                if (newQtyValue <= product!!.stock.toInt()) {
                    viewModel.setQtyProduct(newQtyValue)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeMainBook() {
        viewModel.addressMainBookAddress.observe(this@PembayaranCartLangsungActivity) {
            with(binding) {
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


    @SuppressLint("SetTextI18n")
    private fun observeCourierChoosen() {
        viewModel.courierChoosen.observe(this@PembayaranCartLangsungActivity) {
            with(binding) {
                tvWarningCourir.isVisible = it == null
                tvDescShipping.isVisible = it != null
                courierChoosen = it

                if (it != null) {
                    tvLabelShipping.text =
                        "${it.prefix} ${it.service} (${MyUtils.getNumberRupiah(it.cost[0].value)})"
                    tvPriceShipping.text = MyUtils.getNumberRupiah(it.cost[0].value)
                    tvDescShipping.text =
                        "Estimasi tiba ${it.cost[0].etd.replace("hari", "", true)} hari"

                    val priceTotal =
                        product?.price?.toInt()?.times(currentQty)?.plus(it.cost[0].value)
                    tvPrice.text = MyUtils.getNumberRupiah(priceTotal ?: 0)
                } else {
                    tvPrice.text = "-"
                    tvLabelShipping.text = "Pilih Pengiriman"
                    tvPriceShipping.text = "-"
                }
            }
        }
    }

    private fun observeOrderResponse() {
        viewModel.order.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.btnCheckout.hideProgress("Checkout")
                    it.data?.getContentIfNotHandled()?.let { dataOrder ->
                        with(Intent(this, PayOnlineStoreActivity::class.java)) {
                            putExtra(PayOnlineStoreActivity.KEY_ORDER_RESPONSE, dataOrder)
                            putExtra(
                                EnterPinOnlineStoreActivity.KEY_PRODUCT_CODE,
                                ProductCode.TOKO_ONLINE.value
                            )
                            putExtra(PayOnlineStoreActivity.KEY_IS_DIRECT_BUY, true)
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


    override fun onResume() {
        super.onResume()
        if (!uuid.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
            viewModel.getMainBookAddress(uuid!!, accessToken!!)
        }

    }

    companion object {
        const val KEY_PRODUCT = "KEY_PRODUCT"
    }
}