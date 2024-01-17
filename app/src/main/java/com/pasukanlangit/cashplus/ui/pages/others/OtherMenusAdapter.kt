package com.pasukanlangit.cashplus.ui.pages.others

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemOtherMenusBinding

class OtherMenusAdapter(
    private val otherMenus: List<Menu>,
    val listener: (Int) -> Unit
): RecyclerView.Adapter<OtherMenusAdapter.OtherMenusViewHolder>() {

    inner class OtherMenusViewHolder(val binding: ItemOtherMenusBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: Menu) {
            with(binding) {
                ivLogoMenu.setImageResource(menu.icon)
                ivLogoMenu.setBackgroundResource(R.drawable.bg_primary_rounded_8)
                ivLogoMenu.backgroundTintList = root.resources.getColorStateList(menu.backgroundTint, null)
                tvTitleMenu.text = menu.nameMenu
                root.setOnClickListener {
                    listener(menu.position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherMenusViewHolder {
        return OtherMenusViewHolder(ItemOtherMenusBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = otherMenus.size

    override fun onBindViewHolder(holder: OtherMenusViewHolder, position: Int) {
        holder.bind(otherMenus[position])
    }

    override fun getItemViewType(position: Int): Int = position
}