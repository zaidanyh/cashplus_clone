package com.pasukanlangit.cashplus.ui.omni.packageomni

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemOmniPackageBinding
import com.pasukanlangit.cashplus.domain.model.ResponsePackage
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class PackagesAdapter: RecyclerView.Adapter<PackagesAdapter.PackagesViewHolder>(), Filterable {

    private val packages = mutableListOf<ResponsePackage>()
    private var packagesFiltered: List<ResponsePackage> = packages
    private lateinit var onPackageClickListener: OnPackageClickListener

    fun setPackages(packages: List<ResponsePackage>?) {
        if (packages.isNullOrEmpty()) return
        this.packages.clear()
        this.packages.addAll(packages)
    }

    fun setPackageClickListener(onPackageClickListener: OnPackageClickListener) {
        this.onPackageClickListener = onPackageClickListener
    }

    inner class PackagesViewHolder(val binding: ItemOmniPackageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(packaged: ResponsePackage) {
            with(binding) {
                tvNamePackage.text = packaged.name
                tvDurationPackage.text = packaged.validity
                tvPricePackage.text = getNumberRupiah(packaged.price)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackagesViewHolder {
        return PackagesViewHolder(ItemOmniPackageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = packagesFiltered.size

    override fun onBindViewHolder(holder: PackagesViewHolder, position: Int) {
        holder.bind(packagesFiltered[position])
        holder.binding.root.setOnClickListener {
            onPackageClickListener.onPackageClicked(packagesFiltered[holder.bindingAdapterPosition])
        }
    }

    interface OnPackageClickListener {
        fun onPackageClicked(packaged: ResponsePackage)
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(char: CharSequence?): FilterResults {
                val keyword = char.toString()
                packagesFiltered = if (keyword.isEmpty()) packages
                else {
                    val filteredList: MutableList<ResponsePackage> = ArrayList()
                    for (i in packages) {
                        if ("""(?i)($keyword)""".toRegex().matches(i.subcategory)) {
                            filteredList.add(i)
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = packagesFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                packagesFiltered = results?.values as List<ResponsePackage>
                notifyDataSetChanged()
            }
        }
    }
}