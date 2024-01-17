package com.pasukanlangit.cashplus.ui.all_menus

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemMenuProductEWalletBinding
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.MyUtils.getAvaImagePlaceholder
import com.pasukanlangit.cashplus.utils.MyUtils.httpToHttps
import com.pasukanlangit.cashplus.utils.CategoryProduct

class MenuProductAllAdapter(
    private val menus: List<ProductModel>,
    val listener: (String) -> Unit
): RecyclerView.Adapter<MenuProductAllAdapter.MenuViewHolder>() {

    var offsetAnimation : Long = 40

    inner class MenuViewHolder(val binding: ItemMenuProductEWalletBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: ProductModel) {
            var imgUrl = menu.img_url
            with(binding) {
                if(imgUrl.isEmpty()) imgUrl =
                    getAvaImagePlaceholder(menu.kode_provider.replace("_", " "))

                if (menu.category == CategoryProduct.GAMES.value)
                    Glide.with(root.context)
                        .load(imgUrl.httpToHttps())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .into(ivLogoMenu)
                else
                    Glide.with(root.context)
                        .load(imgUrl.httpToHttps())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivLogoMenu)

                tvMenu.text = menu.kode_provider.replace("_", " ")
                startAnimationOnLoad(root.context, binding)
                root.setOnClickListener {
                    listener(menu.kode_provider)
                }
            }
        }

        private fun startAnimationOnLoad(context: Context, item: ItemMenuProductEWalletBinding) {
            val animation = AnimationUtils.loadAnimation(context,R.anim.translate_fade_in_anim)
            animation.startOffset = offsetAnimation

            item.ivLogoMenu.startAnimation(animation)
            item.tvMenu.startAnimation(animation)

            offsetAnimation += 100
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuViewHolder {
        return MenuViewHolder(ItemMenuProductEWalletBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menus[position])
    }

    override fun getItemViewType(position: Int): Int = position
    override fun getItemCount(): Int = menus.size
}