package com.pasukanlangit.cashplus.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemDataProductModelBinding
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.MyUtils.getAvaImagePlaceholder
import com.pasukanlangit.cashplus.utils.MyUtils.httpToHttps
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class DataProductAdapter(
    val productModel: List<ProductModel>,
    val listener: (ProductModel) -> Unit
): RecyclerView.Adapter<DataProductAdapter.DataProductViewHolder>() {

    inner class DataProductViewHolder(val binding: ItemDataProductModelBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(productModel: ProductModel) {
            with(binding) {
                val context = root.context
                var imgUrl = productModel.img_url_2.ifEmpty { productModel.img_url.httpToHttps() }
                if(imgUrl.isEmpty()) imgUrl =
                    getAvaImagePlaceholder(productModel.kode_provider.replace("_", " "))

                Glide.with(context)
                    .load(imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivOperator)
                tvShortDesc.text = productModel.short_dsc
                tvDesc.text = productModel.dsc
                tvPriceData.text = getNumberRupiah(productModel.price.toIntOrNull())

                val animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in_anim)
                root.startAnimation(animation)
                root.setOnClickListener { listener(productModel) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataProductViewHolder {
        return DataProductViewHolder(ItemDataProductModelBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DataProductViewHolder, position: Int) {
        holder.bind(productModel[position])
    }

    override fun getItemCount(): Int = productModel.size
    override fun getItemViewType(position: Int): Int = position
}