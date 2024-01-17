package com.pasukanlangit.cashplus.ui.omni.packageomni

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.id.core.databinding.CashplusItemProductCategoryBinding

class PackageCategoryAdapter: RecyclerView.Adapter<PackageCategoryAdapter.PackageCategoryViewHolder>() {

    private val categories = mutableListOf<String>()
    private lateinit var onItemClickListener: OnItemClickListener
    private lateinit var categoryChoosed: String

    fun setCategory(categories: List<String>?) {
        if (categories.isNullOrEmpty()) return
        this.categories.clear()
        this.categories.addAll(categories)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setCategoryChoosed(category: String) {
        categoryChoosed = category
        notifyDataSetChanged()
    }

    inner class PackageCategoryViewHolder(val binding: CashplusItemProductCategoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageCategoryViewHolder {
        return PackageCategoryViewHolder(
            CashplusItemProductCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: PackageCategoryViewHolder, position: Int) {
        val category = categories[position]
        with(holder.binding) {
            root.setBackgroundResource(
                if (categoryChoosed == categories[position]) R.drawable.bg_primary_rounded_12
                else R.drawable.bg_transparent_border_slate200_rounded_12
            )
            root.text = category
            root.setTextColor(
                if (categoryChoosed == categories[position]) Color.parseColor("#FFFFFF")
                else Color.parseColor("#64748B")
            )
            root.setOnClickListener {
                onItemClickListener.onItemClicked(categories[holder.bindingAdapterPosition])
                setCategoryChoosed(categories[position])
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(category: String)
    }
}