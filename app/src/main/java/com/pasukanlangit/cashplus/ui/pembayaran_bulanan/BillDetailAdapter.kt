package com.pasukanlangit.cashplus.ui.pembayaran_bulanan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemBillDetailBinding

class BillDetailAdapter(
    private val infoDetails: List<String>
): RecyclerView.Adapter<BillDetailAdapter.BillDetailViewHolder>() {

    inner class BillDetailViewHolder(val binding: ItemBillDetailBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillDetailViewHolder {
        return BillDetailViewHolder(ItemBillDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BillDetailViewHolder, position: Int) {
        if (position == 0 || position == (infoDetails.size -1)) {
            holder.binding.tvLabel.isVisible = false
            holder.binding.tvValue.isVisible = false
        }
        else {
            val info = infoDetails[position]
            if(info.isNotEmpty()) {
                val infoSplitted = info.split(":", limit = 2)
                if(infoSplitted.size < 2) return

                with(holder.binding){
                    tvLabel.text = infoSplitted.first()
                    tvValue.text = infoSplitted.last()
                }
            }
        }
    }

    override fun getItemCount(): Int = infoDetails.size
}