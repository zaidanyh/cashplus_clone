package com.pasukanlangit.cashplus.ui.onlinestore

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityProductDetailBinding
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.cashplus.ui.cartproduct.CartProductActivity
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.onlinestore.image.ImagePhotoViewActivity
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.cashplus.ui.pembayarancart.PembayaranCartLangsungActivity
import com.pasukanlangit.cashplus.utils.MyUtils.getNumberRupiah
import com.pasukanlangit.id.core.utils.parcelable
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val viewModel: ProductDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product : ProductStoreDataItem?= intent.parcelable(KEY_IMG_PRODUCT)

        viewModel.getAllProductCarts()
        with(binding) {
            if(product != null){
                val imgSlider = ImageProductSliderAdapter(
                    this@ProductDetailActivity,
                    arrayListOf(product.image1, product.image2, product.image3, product.image4)
                )
                pagerImage.adapter = imgSlider
                indicator.setViewPager(pagerImage)
                imgSlider.setOnImageClickListener(object: ImageProductSliderAdapter.OnImageClickListener {
                    override fun onImageClicked(item: ArrayList<String>, position: Int) {
                        val intent = Intent(this@ProductDetailActivity, ImagePhotoViewActivity::class.java).apply {
                            putExtra(ImagePhotoViewActivity.ITEM_IMAGE_KEY, item)
                            putExtra(ImagePhotoViewActivity.ITEM_POSITION_KEY, position)
                        }
                        startActivity(intent)
                    }
                })
                tvTitleDetailProduct.text = product.productName

                var price = product.price.toInt()
                if (product.discount != "0") {
                    price =  product.price.toInt() - product.discount.toInt()
                    val calculateDiscount = 100 * (product.price.toDouble() - price) / product.price.toDouble()
                    tvDiscountProduct.text = root.context.getString(R.string.price_discounts, calculateDiscount)
                }
                tvDiscountProduct.isVisible = product.discount != "0"
                tvPriceProduct.isVisible = product.discount != "0"
                tvPriceProduct.text = getNumberRupiah(product.price.toInt())
                tvPriceProduct.paintFlags = tvPriceProduct.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                tvPriceDetailProduct.text = getNumberRupiah(price)
                tvSellDetailProduct.text = getString(R.string.product_viewer, product.viewer)
                tvStarDetailProduct.text = getString(R.string.product_rate_per_viewer, product.rateAverage, product.viewer)
                tvWeightProductDetail.text = getString(R.string.product_weight, product.weight)
                tvConditionProductDetail.text = product.productCondition
                tvCategoriProductDetail.text = product.productCategoryName
                tvDescriptionProductDetail.text = product.description

                btnCart.setOnClickListener {
                    if((product.stock.toIntOrNull() ?: 0) > 0) {
                        Toast.makeText(this@ProductDetailActivity, getString(R.string.successfully_added_to_cart), Toast.LENGTH_SHORT).show()
                        viewModel.insertProductToCart(product)
                    }else{
                        Toast.makeText(this@ProductDetailActivity,getString(R.string.empty_product_stock), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            btnLivebuy.setOnClickListener {
                with(Intent(this@ProductDetailActivity, PembayaranCartLangsungActivity::class.java)){
                    putExtra(PembayaranCartLangsungActivity.KEY_PRODUCT, product)
                    startActivity(this)
                }
            }

            btnBackProduckAll.setOnClickListener { onBackPressed() }
        }

        observeCartProduct()
    }

    private fun observeCartProduct() {
        viewModel.productsInCart.observe(this) { carts ->
            with(binding) {
                tvQty.isVisible = !carts.isNullOrEmpty()
                tvQty.text = carts.size.toString()

                ivCart.setOnClickListener {
                    if(carts.isNullOrEmpty()){
                        val menusAllFragment = ButtomSheetNotif(getString(R.string.empty_cart), NotifType.NOTIF_INPUT_UNCOMPLETED)
                        menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                    }else{
                        startActivity(Intent(this@ProductDetailActivity, CartProductActivity::class.java))
                    }
                }
            }
        }
    }


    companion object {
        const val KEY_IMG_PRODUCT = "KEY_IMG_PRODUCT"
    }
}