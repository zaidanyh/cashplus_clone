package com.pasukanlangit.id.cash_transfer.presentation.local.transfer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.core.databinding.ItemNominalTransferBinding

class NominalTransferAdapter: RecyclerView.Adapter<NominalTransferAdapter.NominalViewHolder>() {

    private val nominalList = ArrayList<String>()
    private lateinit var onItemClickListener: OnItemClickListener
    private var isSelected: String? = null

    fun setNominalList(nominalList: List<String>) {
        this.nominalList.clear()
        this.nominalList.addAll(nominalList)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class NominalViewHolder(val binding: ItemNominalTransferBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            with(binding) {
                tvNominal.text = item
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NominalViewHolder {
        return NominalViewHolder(ItemNominalTransferBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NominalViewHolder, position: Int) {
        holder.bind(nominalList[position])
        holder.binding.root.setOnClickListener {
            onItemClickListener.onItemClicked(nominalList[holder.bindingAdapterPosition])
            setOnItemClickSelected(nominalList[position])
        }
        if (isSelected == nominalList[position])
            holder.binding.root.setBackgroundResource(R.drawable.bg_transparent_border_primary_rounded_10)
        else holder.binding.root.setBackgroundResource(R.drawable.bg_transparent_border_slate200_rounded_10)
    }

    override fun getItemCount(): Int = nominalList.size

    fun setOnItemClickSelected(position: String) {
        isSelected = position
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClicked(item: String)
    }
}