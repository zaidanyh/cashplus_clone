package com.pasukanlangit.id.cash_transfer.presentation.global.find

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.ItemCountriesBinding
import com.pasukanlangit.id.cash_transfer.domain.model.FetchCountryResponse

class FindCountryAdapter: RecyclerView.Adapter<FindCountryAdapter.FindCountryViewHolder>(), Filterable {

    private val countries = mutableListOf<FetchCountryResponse>()
    private var countriesFiltered: List<FetchCountryResponse> = countries
    private lateinit var onItemClickListener: OnItemClickListener

    fun setCountries(countries: List<FetchCountryResponse>?) {
        if (countries.isNullOrEmpty()) return
        this.countries.clear()
        this.countries.addAll(countries)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class FindCountryViewHolder(val binding: ItemCountriesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FetchCountryResponse) {
            with(binding) {
                val context = root.context
                Glide.with(context)
                    .load(data.imgUrl)
                    .error(R.drawable.ic_image_default)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imgCountriesFlag)
                tvNameCountry.text = data.nameCountry
                tvCurrencyCode.text = data.currency
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindCountryViewHolder {
        return FindCountryViewHolder(ItemCountriesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = countriesFiltered.size

    override fun onBindViewHolder(holder: FindCountryViewHolder, position: Int) {
        holder.bind(countriesFiltered[position])
        holder.binding.root.setOnClickListener {
            onItemClickListener.onItemClicked(countriesFiltered[holder.bindingAdapterPosition])
        }
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(char: CharSequence?): FilterResults {
                val query = char.toString()
                countriesFiltered = if (query.isEmpty()) countries
                else {
                    val filtered = mutableListOf<FetchCountryResponse>()
                    for (i in countries) {
                        if ("""(?i)($query)""".toRegex().containsMatchIn(i.nameCountry))
                            filtered.add(i)
                    }
                    filtered
                }
                val result = FilterResults()
                result.values = countriesFiltered
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countriesFiltered = results?.values as List<FetchCountryResponse>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(item: FetchCountryResponse)
    }
}