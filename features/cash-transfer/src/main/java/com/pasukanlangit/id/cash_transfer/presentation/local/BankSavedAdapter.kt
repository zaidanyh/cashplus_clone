package com.pasukanlangit.id.cash_transfer.presentation.local

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasukanlangit.id.cash_transfer.databinding.ItemBankSavedBinding
import com.pasukanlangit.id.cash_transfer.domain.model.LocalBankSavedResponse

class BankSavedAdapter(
    private val listener: (LocalBankSavedResponse, isDelete: Boolean) -> Unit
): RecyclerView.Adapter<BankSavedAdapter.BankSavedViewHolder>(), Filterable {

    private var banksSaved = listOf<LocalBankSavedResponse>()
    private var banksSavedFiltered = banksSaved

    fun setBankSavedList(banksSaved: List<LocalBankSavedResponse>) {
        this.banksSaved = banksSaved
        banksSavedFiltered = banksSaved
        notifyDataSetChanged()
    }

    inner class BankSavedViewHolder(val binding: ItemBankSavedBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(bankSaved: LocalBankSavedResponse) {
            with(binding) {
                Glide.with(root.context)
                    .load(bankSaved.bank_img)
                    .into(imgBankSaved)
                accountBankName.text = bankSaved.bank_acc_name
                accountBankNum.text = bankSaved.bank_acc_num
                root.setOnClickListener {
                    listener(bankSaved, false)
                }
                btnDeleteBank.setOnClickListener { listener(bankSaved, true) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankSavedViewHolder {
        return BankSavedViewHolder(ItemBankSavedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BankSavedViewHolder, position: Int) {
        holder.bind(banksSavedFiltered[position])
    }

    override fun getItemCount(): Int = banksSavedFiltered.size

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(char: CharSequence?): FilterResults {
                val query = char.toString()
                banksSavedFiltered = if (query.isEmpty()) {
                    banksSaved
                } else {
                    val filteredList: MutableList<LocalBankSavedResponse> = ArrayList()
                    for (i in banksSaved) {
                        if (
                            """(?i)($query)""".toRegex().containsMatchIn(i.bank_acc_name) ||
                            """(?i)($query)""".toRegex().containsMatchIn(i.bank_acc_num)
                        )
                            filteredList.add(i)
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = banksSavedFiltered
                return filterResults
            }

            override fun publishResults(char: CharSequence?, result: FilterResults?) {
                banksSavedFiltered = result?.values as List<LocalBankSavedResponse>
                notifyDataSetChanged()
            }
        }
    }
}