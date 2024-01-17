package com.pasukanlangit.cashplus.ui.pages.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemSplitSnBinding

class DetailSNAdapter(
    private val splitSN: List<String>?
): RecyclerView.Adapter<DetailSNAdapter.DetailSNViewHolder>() {

    inner class DetailSNViewHolder(val binding: ItemSplitSnBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailSNViewHolder {
        return DetailSNViewHolder(ItemSplitSnBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = splitSN?.size ?: 0

    override fun onBindViewHolder(holder: DetailSNViewHolder, position: Int) {
        val item = splitSN?.get(position)
        if (item.isNullOrEmpty()) return
        else {
            holder.binding.root.text = item
        }
    }
}