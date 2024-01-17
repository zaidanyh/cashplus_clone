package com.pasukanlangit.cashplus.ui.pembayarancart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemProductPengirimanBinding
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.cashplus.utils.MyUtils

class PembayaranCartAdapter(private val products: List<ProductStoreDataItem>): RecyclerView.Adapter<PembayaranCartAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ItemProductPengirimanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemProductPengirimanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentProduct = products[position]

        with(holder.binding){
            Glide.with(root.context)
                .load(currentProduct.image1)
                .thumbnail(
                    Glide.with(root.context)
                        .load(R.raw.image_loading_state)
                )
                .error(R.drawable.ic_empty)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(ivProduct)
            tvProductNo.text = "Pesanan ${position.inc()}"
            tvNameProduct.text = currentProduct.productName
            tvCountAndHeightProduct.text = "${currentProduct.qty} Barang (${currentProduct.weight} gram)"
            tvPriceProduct.text = MyUtils.getNumberRupiah(currentProduct.price.toInt())
            tvNote.isVisible = currentProduct.note.isNotEmpty()
            tvNote.text = currentProduct.note
        }
    }

    override fun getItemCount(): Int = products.size
}