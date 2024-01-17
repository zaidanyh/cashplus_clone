package com.pasukanlangit.id.ui_downline_mutasi_summary_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.domain_downline.model.SummaryTransferResponse
import com.pasukanlangit.id.ui_downline_mutasi_summary_detail.databinding.ItemSummaryTransferBinding

class SummaryTransferAdapter(private val summaries: List<SummaryTransferResponse>): RecyclerView.Adapter<SummaryTransferAdapter.OnViewHolder>() {
    class OnViewHolder(private val binding: ItemSummaryTransferBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(summary: SummaryTransferResponse) {
            with(binding){
                tvDownlineName.text = summary.downlineName
                tvPrice.text = summary.totalTransferRupiah
                tvUsername.text = summary.username
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnViewHolder {
       val view = ItemSummaryTransferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnViewHolder, position: Int) {
        holder.bind(summaries[position])
    }

    override fun getItemCount(): Int = summaries.size
}