package com.pasukanlangit.cashplus.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.cashplus.databinding.ItemPulsaBinding
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.MyUtils.convertToNormalNumber
import com.pasukanlangit.cashplus.utils.MyUtils.getNumberRupiah
import com.pasukanlangit.cashplus.utils.MyUtils.httpToHttps
import com.pasukanlangit.cashplus.utils.MyUtils.startZoomInAnim
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiahWithoutRp

class PulsaAdapter(
    val productModel: List<ProductModel>,
    val listener: (ProductModel) -> Unit
): RecyclerView.Adapter<PulsaAdapter.PulsaViewHolder>() {

    inner class PulsaViewHolder(val binding: ItemPulsaBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(productModel: ProductModel){
            with(binding) {
                val context = root.context
                val imgUrl = productModel.img_url_2.ifEmpty { productModel.img_url }.httpToHttps()
                Glide.with(context)
                    .load(imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(ivOperator)
                tvDescPulsa.text = productModel.dsc
                tvIsPromo.isVisible = productModel.dsc.contains("promo", ignoreCase = true)
                tvPriceBuy.text = getNumberRupiah(productModel.price.toIntOrNull())

                val priceValueSplit = productModel.dsc.split(" ")
                val priceValue = priceValueSplit[priceValueSplit.size - 1]

                if(productModel.category == "#PULSA") {
                    val nominalBuy = convertToNormalNumber(productModel.short_dsc).toInt()
                    tvPriceValue.text = getNumberRupiahWithoutRp(nominalBuy)
                } else {
                    tvPriceValue.text = priceValue
                }
                root.setOnClickListener {
                    listener(productModel)
                }
                //animation
                startZoomInAnim(context, root)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PulsaViewHolder {
       return PulsaViewHolder(ItemPulsaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int  = productModel.size

    override fun getItemViewType(position: Int): Int = position

    override fun onBindViewHolder(holder: PulsaViewHolder, position: Int) = holder.bind(productModel[position])
}