package com.pasukanlangit.cashplus.ui.all_menus

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemMenuProductallBinding

class MenuTagihanAdapter(private val menus: List<DummyTagihan>, val listener: (Int) -> Unit): RecyclerView.Adapter<MenuTagihanAdapter.MenuViewHolder>() {

    var offsetAnimation : Long = 40

    inner class MenuViewHolder(val binding: ItemMenuProductallBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: DummyTagihan) {
            with(binding) {
                ivLogoMenu.setImageResource(menu.image)
                tvMenu.text = menu.title
                startAnimationOnLoad(root.context, binding)

                root.setOnClickListener {
                    listener(menu.direction)
                }
            }
        }

        private fun startAnimationOnLoad(context: Context, item: ItemMenuProductallBinding) {
            val animation = AnimationUtils.loadAnimation(context,R.anim.translate_fade_in_anim)
            animation.startOffset = offsetAnimation

            item.ivLogoMenu.startAnimation(animation)
            item.tvMenu.startAnimation(animation)

            offsetAnimation += 60
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuViewHolder {
        return MenuViewHolder(ItemMenuProductallBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menus[position])
    }

    override fun getItemViewType(position: Int): Int = position
    override fun getItemCount(): Int = menus.size
}