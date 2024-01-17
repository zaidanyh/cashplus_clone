package com.pasukanlangit.id.cash_transfer.presentation.global

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.ItemGlobalBankSavedBinding
import com.pasukanlangit.id.cash_transfer.domain.model.GlobalBankSavedResponse

class GlobalBankSavedAdapter: RecyclerView.Adapter<GlobalBankSavedAdapter.GlobalBankSavedViewHolder>(), Filterable {

    private val savedBanks = mutableListOf<GlobalBankSavedResponse>()
    private var savedBanksFiltered: List<GlobalBankSavedResponse> = savedBanks
    private lateinit var onItemClickListener: OnItemClickListener

    fun setGlobalBankSaved(savedBanks: List<GlobalBankSavedResponse>?) {
        if (savedBanks.isNullOrEmpty()) return
        this.savedBanks.clear()
        this.savedBanks.addAll(savedBanks)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class GlobalBankSavedViewHolder(val binding: ItemGlobalBankSavedBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GlobalBankSavedResponse) {
            with(binding) {
                tvGlobalBankNameAccount.text = item.bankAccName
                tvGlobalBankName.text = item.bankName
                tvGlobalBankNumberAccount.text = item.bankAccNum
                tvCurrencyCode.text = item.currency
                Glide.with(root.context)
                    .load(item.countryImgUrl)
                    .error(R.drawable.ic_image_default)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imgCountry)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlobalBankSavedViewHolder {
        return GlobalBankSavedViewHolder(ItemGlobalBankSavedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = savedBanksFiltered.size

    override fun onBindViewHolder(holder: GlobalBankSavedViewHolder, position: Int) {
        holder.bind(savedBanksFiltered[position])
        holder.binding.root.setOnClickListener {
            onItemClickListener.onItemClicked(savedBanksFiltered[holder.bindingAdapterPosition])
        }
        holder.binding.btnDeleteGlobalBankSaved.setOnClickListener {
            onItemClickListener.onDeleteItemClicked(
                savedBanksFiltered[holder.bindingAdapterPosition].bankCode,
                savedBanksFiltered[holder.bindingAdapterPosition].bankAccNum
            )
        }
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(char: CharSequence?): FilterResults {
                val query = char.toString()
                savedBanksFiltered = if (query.isEmpty()) savedBanks
                else {
                    val filtered = mutableListOf<GlobalBankSavedResponse>()
                    for (i in savedBanks) {
                        val regex = """(?i)($query)""".toRegex()
                        if (regex.containsMatchIn(i.bankAccName) || regex.containsMatchIn(i.bankAccNum))
                            filtered.add(i)
                    }
                    filtered
                }
                val result = FilterResults()
                result.values = savedBanksFiltered
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                savedBanksFiltered = results?.values as List<GlobalBankSavedResponse>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(item: GlobalBankSavedResponse)
        fun onDeleteItemClicked(bankCode: String, bankAccNum: String)
    }
}