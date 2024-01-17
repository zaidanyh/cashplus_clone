package com.pasukanlangit.cashplus.ui.onlinestore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemCategoryTokoonlineBinding

class CategoryProductAdapter(private val categories: List<String>,private val listener: (String) -> Unit): RecyclerView.Adapter<CategoryProductAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemCategoryTokoonlineBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String){
            with(binding){
                textCategory.text = category
                textCategory.setOnClickListener {
                    listener(category)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryTokoonlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}