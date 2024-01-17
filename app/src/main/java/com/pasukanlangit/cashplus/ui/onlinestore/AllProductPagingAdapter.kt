package com.pasukanlangit.cashplus.ui.onlinestore

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemProdukHomeBinding
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class AllProductPagingAdapter(private val productStoreEvent: ProductStoreEvent): PagingDataAdapter<ProductStoreDataItem, AllProductPagingAdapter.ViewHolder>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ProductStoreDataItem>(){
            override fun areItemsTheSame(
                oldItem: ProductStoreDataItem,
                newItem: ProductStoreDataItem
            ): Boolean {
                return oldItem.productId == newItem.productId
            }

            override fun areContentsTheSame(
                oldItem: ProductStoreDataItem,
                newItem: ProductStoreDataItem
            ): Boolean {
               return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(val binding: ItemProdukHomeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductStoreDataItem?) {
            with(binding) {
                item?.let { productStoreDataItem ->
                    Glide.with(root.context)
                        .load(productStoreDataItem.image1)
                        .thumbnail(
                            Glide.with(root.context)
                                .load(R.raw.image_loading_state)
                        )
                        .error(R.drawable.ic_empty)
                        .transition(DrawableTransitionOptions.withCrossFade(300))
                        .into(ivProdukItem)
                    tvTitleItemProduct.text = productStoreDataItem.productName

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

                    //animation
                    MyUtils.startZoomInAnim(root.context, root)

                    root.setOnClickListener {
                        productStoreEvent.onClickRoot(productStoreDataItem)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemProdukHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface ProductStoreEvent {
        fun onClickRoot(productStoreDataItem: ProductStoreDataItem)
    }
}