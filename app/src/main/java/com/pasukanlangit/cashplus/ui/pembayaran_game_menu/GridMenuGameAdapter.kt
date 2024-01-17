package com.pasukanlangit.cashplus.ui.pembayaran_game_menu

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemGameMenuBinding
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.MyUtils.httpToHttps

class GridMenuGameAdapter(private val menus: List<ProductModel>, val listener: (String) -> Unit): RecyclerView.Adapter<GridMenuGameAdapter.MenuViewHolder>() {
    inner class MenuViewHolder(val binding: ItemGameMenuBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: ProductModel) {
            var imgUrl = menu.img_url
            with(binding) {
                if (imgUrl.isEmpty()) imgUrl =
                    "https://ui-avatars.com/api/?name=${menu.kode_provider.replace("_", " ")}"

                Glide.with(root.context)
                    .load(imgUrl.httpToHttps())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_empty)
                    .into(ivLogoGame)

                tvTitleGame.text = menu.kode_provider.replace("_", " ")

                root.setOnClickListener {
                    listener(menu.kode_provider)
                }

                //animation
                val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.zoom_in_anim)
                root.startAnimation(animation)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuViewHolder {
        return MenuViewHolder(ItemGameMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menus[position])
    }

    override fun getItemViewType(position: Int): Int = position
    override fun getItemCount(): Int = menus.size
}