package com.pasukanlangit.id.ui_downline_home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.ui_downline_home.databinding.ItemPaymentBinding

data class BalanceAgent(
    val label: String,
    val value: String
)
class BalanceAgentAdapter: ListAdapter<BalanceAgent, BalanceAgentAdapter.BalanceViewHolder>(DIFF_UTIL){

    inner class BalanceViewHolder(val binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceViewHolder {
        val binding = ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BalanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BalanceViewHolder, position: Int) {
        val balance = getItem(position)
        with(holder.binding){
            tvLabel.text = balance.label
            tvValue.text = balance.value
        }
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<BalanceAgent>() {
            override fun areItemsTheSame(
                oldItem: BalanceAgent,
                newItem: BalanceAgent
            ): Boolean = oldItem.label == newItem.label

            override fun areContentsTheSame(
                oldItem: BalanceAgent,
                newItem: BalanceAgent
            ): Boolean = oldItem == newItem
        }
    }
}