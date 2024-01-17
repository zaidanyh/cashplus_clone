package com.pasukanlangit.id.ui_downline_home.priceplan.findproduct

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.ui_downline_home.databinding.ItemSummaryAddProductBinding
import com.pasukanlangit.id.ui_downline_home.utils.SummaryProduct

class SummaryAddProductAdapter(
    private val summary: List<SummaryProduct>
): RecyclerView.Adapter<SummaryAddProductAdapter.SummaryViewHolder>() {

    inner class SummaryViewHolder(val binding: ItemSummaryAddProductBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SummaryProduct) {
            with(binding) {
                tvProductCode.text = item.codeProduct
                tvProductCategory.text = item.category
                tvMarkupValue.text = getNumberRupiah(item.markup.toIntOrNull())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        return SummaryViewHolder(ItemSummaryAddProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = summary.size

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        holder.bind(summary[position])
    }
}