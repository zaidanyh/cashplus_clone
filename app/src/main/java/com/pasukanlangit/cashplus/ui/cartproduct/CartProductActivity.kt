package com.pasukanlangit.cashplus.ui.cartproduct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityCartProductBinding
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.cashplus.ui.pembayarancart.PembayaranCartActivity
import com.pasukanlangit.cashplus.utils.MyUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartProductActivity : AppCompatActivity(), CartAdapter.CartEvent {

    private lateinit var binding: ActivityCartProductBinding
    private val viewModel: CartViewModel by viewModel()
    private lateinit var cartAdapter: CartAdapter
    private var countQty = 0
    private var price = 0
    private var weightGram = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerCart()

        with(binding) {
            btnBackCart.setOnClickListener { finish() }
            viewModel.getAllProductCarts()
            viewModel.productCarts.observe(this@CartProductActivity) { carts ->
                cartAdapter.submitList(carts.toMutableList())
                if (!rvCart.isComputingLayout)
                {
                    rvCart.adapter?.notifyDataSetChanged()
                }

                val countProductChecked = carts.count { it.isChecked }
                cbSelectAll.isChecked =  countProductChecked == carts.size

                btnBuy.isEnabled = carts.any { it.isChecked }

                countQty = 0
                price = 0
                weightGram = 0
                carts.forEach { cart ->
                    if (cart.isChecked) {
                        price += cart.qty.times(cart.price.toInt())
                        countQty += cart.qty
                        weightGram += cart.weight.toInt().times(cart.qty)
                    }
                }
                btnBuy.text = getString(R.string.buy_count, countQty) //"Beli ($countQty)"
                tvPrice.text = MyUtils.getNumberRupiah(price)
            }

            cbSelectAll.setOnClickListener {
                viewModel.updateCartsIsChecked(cbSelectAll.isChecked)
                cartAdapter.allProductIsChecked(cbSelectAll.isChecked)
            }

            btnBuy.setOnClickListener {
                with(Intent(this@CartProductActivity, PembayaranCartActivity::class.java)){
                    putParcelableArrayListExtra(PembayaranCartActivity.KEY_PRODUCT_ORDERED, ArrayList(cartAdapter.getProductChecked()))
                    putExtra(PembayaranCartActivity.KEY_TOTAL_PRODUCT_PRICE, price)
                    putExtra(PembayaranCartActivity.KEY_TOTAL_QTY, countQty)
                    putExtra(PembayaranCartActivity.KEY_TOTAL_WEIGHT, weightGram)
                    startActivity(this)
                }
            }
        }
    }

    private fun setUpRecyclerCart() {
        cartAdapter = CartAdapter(this)
        with(binding.rvCart) {
            layoutManager = LinearLayoutManager(this@CartProductActivity)
            adapter = cartAdapter
        }
    }

    override fun updateCart(productStoreDataItem: ProductStoreDataItem) {
        viewModel.updateCartDb(productStoreDataItem)
    }

    override fun deleteCart(productStoreDataItem: ProductStoreDataItem) {
        viewModel.deleteProductFromCart(productStoreDataItem)
    }

}