package com.pasukanlangit.cashplus.ui.gotomekka

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemMenuMekkaBinding

class MekkaMenuAdapter(private var menus: List<MekkaMenu>) : RecyclerView.Adapter<MekkaMenuAdapter.MekkaViewHolder>() {
    inner class MekkaViewHolder(val binding: ItemMenuMekkaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mekkaMenu: MekkaMenu) {
            with(binding) {
                tvMenu.text = mekkaMenu.name

                if(mekkaMenu.isActive)
                    root.setBackgroundResource(R.drawable.bank_transfer_bg_selected)
                else
                    root.setBackgroundColor(Color.parseColor("#FFFFFF"))

                itemView.setOnClickListener {
                    changeMenusActive(mekkaMenu)
                }
            }
        }
    }

    private fun changeMenusActive(mekkaMenu: MekkaMenu) {
        menus.forEach { menu ->
            menu.isActive = menu.name == mekkaMenu.name
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MekkaViewHolder {
        return MekkaViewHolder(ItemMenuMekkaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MekkaViewHolder, position: Int) {
        holder.bind(menus[position])
    }

    override fun getItemCount(): Int  = menus.size
}