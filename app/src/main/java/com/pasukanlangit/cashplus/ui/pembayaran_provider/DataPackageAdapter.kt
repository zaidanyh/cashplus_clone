package com.pasukanlangit.cashplus.ui.pembayaran_provider

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils.loadAnimation
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemDataBinding
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.MyUtils.httpToHttps
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class DataPackageAdapter(
    val productModel: List<ProductModel>,
    val listener: (ProductModel) -> Unit
): RecyclerView.Adapter<DataPackageAdapter.DataViewHolder>() {

    inner class DataViewHolder(val binding: ItemDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(productModel: ProductModel) {
            with(binding) {
                val context = root.context
                val imgUrl = productModel.img_url_2.ifEmpty { productModel.img_url }.httpToHttps()
                Glide.with(context)
                    .load(imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(ivOperator)
                tvShortDesc.text = productModel.short_dsc
                tvDesc.text = productModel.dsc
                tvDataPromo.isVisible = productModel.opr_name.contains("promo", ignoreCase = true)
                tvPriceData.text = getNumberRupiah(productModel.price.toIntOrNull())

                val animation = loadAnimation(context, R.anim.zoom_in_anim)
                root.startAnimation(animation)
                root.setOnClickListener { listener(productModel) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(ItemDataBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(productModel[position])
    }

    override fun getItemCount(): Int = productModel.size

    override fun getItemViewType(position: Int): Int = position
}