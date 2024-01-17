package com.pasukanlangit.cashplus.ui.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemDetailTrxBinding
import com.pasukanlangit.cashplus.utils.MyUtils.getValueFromComplexString
import com.pasukanlangit.id.core.utils.CoreUtils.copyClipboard

class DetailTrxAdapter: RecyclerView.Adapter<DetailTrxAdapter.TrxViewHolder>() {

    private var infoDetails = mutableListOf<String>()
    private var adminFee: Int = 0

    inner class TrxViewHolder(val binding: ItemDetailTrxBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(info : String) {
            if(info.isNotEmpty()) {
                val spitting = info.split(":", limit = 2)
                if(spitting.size < 2) return
                if (spitting.first().contains("admin", ignoreCase = true))
                    adminFee = spitting.last().getValueFromComplexString()
                with(binding){
                    tvLabel.text = spitting.first()
                    tvValue.text = spitting.last()
                    btnCopyIdTransaction.setOnClickListener {
                        copyClipboard(root.context, spitting.last())
                    }
                }
            }
        }
    }

    fun setInfoDetails(infoDetails: List<String>) {
        if (infoDetails.isEmpty()) return
        this.infoDetails.clear()
        this.infoDetails.addAll(infoDetails)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrxViewHolder {
        return TrxViewHolder(ItemDetailTrxBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: TrxViewHolder, position: Int) {
        holder.bind(infoDetails[position])
        holder.binding.btnCopyIdTransaction.isVisible = infoDetails[position] == infoDetails.first()
    }

    override fun getItemCount(): Int = infoDetails.size

    fun getAdminValue() = adminFee
}