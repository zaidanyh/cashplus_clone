package com.pasukanlangit.cashplus.ui.ewallet

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemEWalletListBinding
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.MyUtils.httpToHttps

class EWalletListAdapter(
    private val category: List<ProductModel>, val listener: (ProductModel) -> Unit
): RecyclerView.Adapter<EWalletListAdapter.EWalletListViewHolder>() {

    inner class EWalletListViewHolder(val binding: ItemEWalletListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: ProductModel) {
            var imgUrl = menu.img_url
            with(binding) {
                val context = root.context
                if (imgUrl.isEmpty()) imgUrl =
                    "https://ui-avatars.com/api/?name=${menu.kode_provider}"
                Glide.with(context)
                    .load(imgUrl.httpToHttps())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_empty)
                    .into(ivLogoEWaller)

                tvNameEWallet.text = menu.kode_provider.replace("_", " ")

                root.setOnClickListener {
                    listener(menu)
                }
                //animation
                val animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in_anim)
                root.startAnimation(animation)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EWalletListViewHolder {
        return EWalletListViewHolder(ItemEWalletListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: EWalletListViewHolder, position: Int) {
        holder.bind(category[position])
    }

    override fun getItemCount(): Int = category.size
    override fun getItemViewType(position: Int): Int = position
}