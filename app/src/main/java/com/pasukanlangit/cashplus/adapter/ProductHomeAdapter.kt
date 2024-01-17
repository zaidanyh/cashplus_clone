package com.pasukanlangit.cashplus.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemProdukEcommerceHomeBinding
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class ProductHomeAdapter(private val products : List<ProductStoreDataItem>, val listener: (ProductStoreDataItem) -> Unit) : RecyclerView.Adapter<ProductHomeAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProdukEcommerceHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(productStoreDataItem: ProductStoreDataItem){
            with(binding) {
                val imgUrl = productStoreDataItem.image2.ifEmpty {
                    productStoreDataItem.image1
                }
                Glide.with(root.context)
                    .load(imgUrl)
                    .error(R.drawable.ic_empty)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivProdukItem)

                tvTitleProduct.text = productStoreDataItem.productName

                var price = productStoreDataItem.price.toInt()
                if (productStoreDataItem.discount != "0") {
                    price =  productStoreDataItem.price.toInt() - productStoreDataItem.discount.toInt()
                    val calculateDiscount = 100 * (productStoreDataItem.price.toDouble() - price) / productStoreDataItem.price.toDouble()
                    tvDiscontProduct.text = root.context.getString(R.string.price_discounts, calculateDiscount)
                }

                tvPriceAfterDiscountProduct.text = getNumberRupiah(price)
                tvPriceProduct.text = getNumberRupiah(productStoreDataItem.price.toInt())
                tvPriceProduct.paintFlags = tvPriceProduct.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvDiscontProduct.isVisible = productStoreDataItem.discount != "0"
                tvPriceProduct.isVisible = productStoreDataItem.discount != "0"

                ratingbarProduct.rating = productStoreDataItem.rateAverage.toFloat()
                ratingbarProduct.invalidate()

                root.setOnClickListener {
                    listener(productStoreDataItem)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
       return ProductViewHolder(ItemProdukEcommerceHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = products.size

    override fun getItemViewType(position: Int): Int = position

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
      holder.bind(products[position])
    }
}