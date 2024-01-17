package com.pasukanlangit.cashplus.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemPreviewBinding

class PreviewAdapter(
    private val contents: List<String>
): RecyclerView.Adapter<PreviewAdapter.PreviewViewHolder>() {

    inner class PreviewViewHolder(val binding: ItemPreviewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(content: String) {
            val contentSplit = content.split(":")
            with(binding) {
                tvLabel.text = contentSplit.first().trim()
                tvValue.text = contentSplit.last().trim().replace("[R]", "")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewViewHolder {
        return PreviewViewHolder(ItemPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = contents.size

    override fun onBindViewHolder(holder: PreviewViewHolder, position: Int) {
        holder.bind(contents[position])
    }
}