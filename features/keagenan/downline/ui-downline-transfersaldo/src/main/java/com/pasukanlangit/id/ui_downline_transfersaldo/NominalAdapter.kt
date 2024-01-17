package com.pasukanlangit.id.ui_downline_transfersaldo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.core.databinding.ItemNominalTransferBinding
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiahWithoutRp

class NominalAdapter(private val onClickNominal: (String) -> Unit): RecyclerView.Adapter<NominalAdapter.ViewHolder>() {

    private val nominals = listOf(
        "50000", "100000", "200000", "500000", "1000000", "2000000"
    )

    private var isSelected: String? = null

    inner class ViewHolder(val binding: ItemNominalTransferBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(nominal: String) {
            with(binding){
                tvNominal.text = getNumberRupiahWithoutRp(nominal.toInt())

                root.setOnClickListener {
                    onClickNominal(nominal)
                    setOnItemClickSelected(nominal)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NominalAdapter.ViewHolder {
        return ViewHolder(ItemNominalTransferBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NominalAdapter.ViewHolder, position: Int) {
        holder.bind(nominals[position])

        if (isSelected == nominals[position])
            holder.binding.root.setBackgroundResource(R.drawable.bg_transparent_border_primary_rounded_10)
        else holder.binding.root.setBackgroundResource(R.drawable.bg_transparent_border_slate200_rounded_10)
    }

    override fun getItemCount(): Int = nominals.size

    fun setOnItemClickSelected(position: String) {
        isSelected = position
        notifyDataSetChanged()
    }
}