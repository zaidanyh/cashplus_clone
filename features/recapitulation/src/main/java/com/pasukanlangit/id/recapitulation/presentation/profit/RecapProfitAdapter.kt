package com.pasukanlangit.id.recapitulation.presentation.profit

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.recapitulation.databinding.ItemRecapProfitBinding
import com.pasukanlangit.id.recapitulation.domain.model.ProfitByProductResponse

class RecapProfitAdapter: RecyclerView.Adapter<RecapProfitAdapter.RecapTransViewHolder>(), Filterable {

    private val items = mutableListOf<ProfitByProductResponse>()
    private var itemsFiltered: List<ProfitByProductResponse> = items

    fun setRecapTrans(items: List<ProfitByProductResponse>?) {
        if (items.isNullOrEmpty()) return
        this.items.clear()
        this.items.addAll(items)
    }

    inner class RecapTransViewHolder(val binding: ItemRecapProfitBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProfitByProductResponse) {
            with(binding) {
                tvShortDsc.text = item.shortDesc
                tvProductCode.text = item.productCode
                tvDescription.text = item.desc
                tvCapital.text = getNumberRupiah(item.modal.toBigIntegerOrNull())
                tvProfit.text = getNumberRupiah(item.profit.toBigIntegerOrNull())
                tvQty.text = item.qty
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecapTransViewHolder {
        return RecapTransViewHolder(ItemRecapProfitBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = itemsFiltered.size

    override fun getItemViewType(position: Int): Int = position

    override fun onBindViewHolder(holder: RecapTransViewHolder, position: Int) {
        holder.bind(itemsFiltered[position])
    }

    override fun getFilter(): Filter = object: Filter() {
        override fun performFiltering(char: CharSequence?): FilterResults {
            val query = char.toString()
            itemsFiltered = if (query.isEmpty()) items
            else {
                val filtered = mutableListOf<ProfitByProductResponse>()
                for (i in items) {
                    val regex = """(?i)($query)""".toRegex()
                    if (regex.containsMatchIn(i.shortDesc)) filtered.add(i)
                }
                filtered
            }
            val result = FilterResults()
            result.values = itemsFiltered
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            itemsFiltered = results?.values as List<ProfitByProductResponse>
            notifyDataSetChanged()
        }
    }
}