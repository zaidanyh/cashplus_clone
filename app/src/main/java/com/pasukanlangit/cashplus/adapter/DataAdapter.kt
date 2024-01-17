package com.pasukanlangit.cashplus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemDataTestBinding
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.model.response.ProductResultModel
import com.pasukanlangit.cashplus.utils.MyUtils.getNumberRupiah
import com.pasukanlangit.cashplus.utils.CategoryProduct

class DataAdapter(productResultModel: ProductResultModel, val listener: (ProductModel) -> Unit) : RecyclerView.Adapter<DataAdapter.DataViewHolder>(), Filterable {
    private val productModel: List<ProductModel> = productResultModel.data!!
    private var productFiltered: List<ProductModel> = productModel

    inner class DataViewHolder(val binding: ItemDataTestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(productModel: ProductModel){
            with(binding) {
//                tvPriceBuy.text = "harga: ${MyUtils.getNumberRupiah(productModel.price.toInt())}"
                tvPriceBuy.text = root.context.getString(R.string.price_with_label, getNumberRupiah(productModel.price.toInt()))
                tvDescData.text = productModel.dsc
                tvShortDesc.text = productModel.short_dsc

                ivItem.isVisible = productModel.img_url.isNotEmpty()
                if(productModel.img_url.isNotEmpty()) {
                    Glide.with(root.context)
                        .load(if (productModel.category == CategoryProduct.GAMES.value) productModel.img_url_2 else productModel.img_url)
                        .circleCrop()
                        .into(ivItem)
                }

                itemView.setOnClickListener {
                    listener(productModel)
                }

                //animation
                val animation = AnimationUtils.loadAnimation(root.context, R.anim.zoom_in_anim)
                root.startAnimation(animation)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(ItemDataTestBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = productFiltered.size

    override fun getItemViewType(position: Int): Int = position

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) = holder.bind(
        productFiltered[position]
    )

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val searchQuery: String = charSequence.toString()
                productFiltered = if (searchQuery.isEmpty()) {
                    productModel
                } else {
                    val filteredList: MutableList<ProductModel> = ArrayList()
                    for (row in productModel) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match

                        if ("""(?i)($searchQuery)""".toRegex().containsMatchIn(row.short_dsc)) {
                            filteredList.add(row)
                        }
//                        if(row.short_dsc.contains(searchQuery)){
//                            filteredList.add(row)
//                        }
                    }

                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = productFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productFiltered = results?.values as List<ProductModel>
                notifyDataSetChanged()
            }
        }
    }
}