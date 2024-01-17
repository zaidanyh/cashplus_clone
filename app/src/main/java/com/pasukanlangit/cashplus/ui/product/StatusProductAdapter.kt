package com.pasukanlangit.cashplus.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.cashplus.databinding.ItemStatusProductBinding
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.MyUtils.httpToHttps
import com.pasukanlangit.id.core.utils.InputUtil.toCapitalize
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class StatusProductAdapter: RecyclerView.Adapter<StatusProductAdapter.StatusProductViewHolder>(), Filterable {

    private val productStatus = ArrayList<ProductModel>()
    private var productStatusFiltered: List<ProductModel> = productStatus
    private lateinit var onItemClickListener: OnItemClickListener

    fun setProductStatus(productStatus: List<ProductModel>?) {
        if (productStatus.isNullOrEmpty()) return
        this.productStatus.clear()
        this.productStatus.addAll(productStatus)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class StatusProductViewHolder(val binding: ItemStatusProductBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductModel) {
            with(binding) {
                val imgUrl = product.img_url.ifEmpty { product.img_url_2 }
                val profit = product.outlet_price.toIntOrNull()?.minus(product.price.toInt())
                Glide.with(root.context)
                    .load(imgUrl.httpToHttps())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imgProduct)
                tvNameProduct.text = product.kode_provider.replace("_", " ").toCapitalize()
                tvDescProduct.text = product.dsc
                tvProductCode.text = product.kode_produk
                tvCapitalPrice.text = getNumberRupiah(product.price.toIntOrNull())
                tvSellingPrice.text = getNumberRupiah(product.outlet_price.toIntOrNull())
                tvProfit.text = getNumberRupiah(profit)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusProductViewHolder {
        return StatusProductViewHolder(ItemStatusProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = productStatusFiltered.size

    override fun onBindViewHolder(holder: StatusProductViewHolder, position: Int) {
        holder.bind(productStatusFiltered[position])
        holder.binding.btnEditPrice.setOnClickListener {
            onItemClickListener.onItemClicked(
                productStatusFiltered[holder.bindingAdapterPosition].kode_produk,
                productStatusFiltered[holder.bindingAdapterPosition].price,
                productStatusFiltered[holder.bindingAdapterPosition].outlet_price
            )
        }
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val keyword = constraint.toString()
                productStatusFiltered = if (keyword.isEmpty()) productStatus
                else {
                    val filteredList: MutableList<ProductModel> = ArrayList()
                    for (i in productStatus) {
                        if ("""(?i)($keyword)""".toRegex().containsMatchIn(i.short_dsc))
                            filteredList.add(i)
                    }
                    filteredList
                }

                val filterResult = FilterResults()
                filterResult.values = productStatusFiltered
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productStatusFiltered = results?.values as List<ProductModel>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(productCode: String, defaultPrice: String, outletPrice: String)
    }
}