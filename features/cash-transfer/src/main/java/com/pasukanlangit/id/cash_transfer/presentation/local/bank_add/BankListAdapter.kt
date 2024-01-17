package com.pasukanlangit.id.cash_transfer.presentation.local.bank_add

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.id.cash_transfer.databinding.ItemListBankBinding
import com.pasukanlangit.id.cash_transfer.domain.model.LocalBankListResponse

class BankListAdapter(
    private val listener: (LocalBankListResponse) -> Unit
): RecyclerView.Adapter<BankListAdapter.BankListViewHolder>(), Filterable {

    private var banks = mutableListOf<LocalBankListResponse>()
    private var banksFiltered: List<LocalBankListResponse> = banks

    fun setBankList(banks: List<LocalBankListResponse>?) {
        if (banks.isNullOrEmpty()) return
        this.banks.clear()
        this.banks.addAll(banks)
    }

    inner class BankListViewHolder(val binding: ItemListBankBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(bank: LocalBankListResponse) {
            with(binding) {
                Glide.with(root.context)
                    .load(bank.imgBank)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imgBank)
                tvBankName.text = bank.bankName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankListViewHolder {
        return BankListViewHolder(ItemListBankBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BankListViewHolder, position: Int) {
        holder.bind(banksFiltered[position])
        holder.binding.root.setOnClickListener {
            listener(banksFiltered[holder.bindingAdapterPosition])
        }
    }

    override fun getItemCount(): Int = banksFiltered.size

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(char: CharSequence?): FilterResults {
                val query = char.toString()
                banksFiltered = if (query.isEmpty()) {
                    banks
                } else {
                    val filteredList: MutableList<LocalBankListResponse> = ArrayList()
                    for (i in banks) {
                        if ("""(?i)($query)""".toRegex().containsMatchIn(i.bankName))
                            filteredList.add(i)
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = banksFiltered
                return filterResults
            }

            override fun publishResults(char: CharSequence?, result: FilterResults?) {
                banksFiltered = result?.values as List<LocalBankListResponse>
                notifyDataSetChanged()
            }
        }
    }
}