package com.pasukanlangit.cashplus.ui.omni.packageomni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemMenuOmniBinding
import com.pasukanlangit.cashplus.domain.model.MenuTitle
import com.pasukanlangit.cashplus.utils.MyUtils.httpToHttps

class PackageMenuAdapter: RecyclerView.Adapter<PackageMenuAdapter.PackageMenuViewHolder>() {

    private val menus = mutableListOf<MenuTitle>()
    private lateinit var onItemMenuClickListener: OnItemMenuClickListener
    private var mlIdSelected: String? = null

    fun setMenuOmni(menus: List<MenuTitle>?) {
        if (menus == null) return
        this.menus.clear()
        this.menus.addAll(menus)
    }

    fun setOnItemMenuClickListener(onItemMenuClickListener: OnItemMenuClickListener) {
        this.onItemMenuClickListener = onItemMenuClickListener
    }

    fun setOnItemMenuSelected(mlId: String) {
        mlIdSelected = mlId
        notifyDataSetChanged()
    }

    inner class PackageMenuViewHolder(val binding: ItemMenuOmniBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: MenuTitle) {
            with(binding) {
                val context = root.context
                Glide.with(context)
                    .load(menu.icon.httpToHttps())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imgMenu)
                tvNameMenu.text = menu.title.id
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageMenuViewHolder {
        return PackageMenuViewHolder(ItemMenuOmniBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = menus.size

    override fun getItemViewType(position: Int): Int = position

    override fun onBindViewHolder(holder: PackageMenuViewHolder, position: Int) {
        holder.bind(menus[position])
        holder.binding.root.setOnClickListener {
            onItemMenuClickListener.onItemMenuClicked(menus[holder.bindingAdapterPosition].mlid)
            setOnItemMenuSelected(menus[position].mlid)
        }
        if (mlIdSelected == menus[position].mlid)
            holder.binding.root.setBackgroundResource(R.drawable.bg_blue50_border_blue100_rounded_12)
        else holder.binding.root.setBackgroundResource(R.drawable.bg_transparent_border_slate200_rounded_12)
    }

    interface OnItemMenuClickListener {
        fun onItemMenuClicked(mlId: String)
    }
}