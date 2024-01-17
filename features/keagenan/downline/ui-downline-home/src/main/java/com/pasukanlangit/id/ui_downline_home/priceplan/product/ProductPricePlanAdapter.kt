package com.pasukanlangit.id.ui_downline_home.priceplan.product

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.domain_downline.model.MarkupProductResponse
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.ItemProductMarkupBinding

class ProductPricePlanAdapter: RecyclerView.Adapter<ProductPricePlanAdapter.ProductPricePlanViewHolder>(), Filterable {

    private val productPricePlan = ArrayList<MarkupProductResponse>()
    private var productPricePlanFiltered: List<MarkupProductResponse> = productPricePlan
    private lateinit var onItemClickListener: SetOnItemClickListener

    fun setProductPricePlan(productPricePlan: List<MarkupProductResponse>) {
        this.productPricePlan.clear()
        this.productPricePlan.addAll(productPricePlan)
        notifyDataSetChanged()
    }

    fun setOnClickListener(onItemClickListener: SetOnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class ProductPricePlanViewHolder(val binding: ItemProductMarkupBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(product: MarkupProductResponse) {
            with(binding) {
                val context = root.context
                btnSetMarkup.isVisible = true
                tvCategory.text = product.category
                tvCodeProvider.text = product.kodeProvider
                tvProductCode.text = product.kodeProduct
                tvCapitalPrice.text = getNumberRupiah(product.price.toIntOrNull() ?: 0)
                val outletPrice = product.outlet_price.toIntOrNull()?.plus(product.markup.toIntOrNull() ?: 0)
                tvOutletPrice.text = getNumberRupiah(outletPrice)
                tvMarkup.text = getNumberRupiah(product.markup.toIntOrNull() ?: 0)
                btnChangeMarkup.text = context.getString(R.string.delete)
                btnChangeMarkup.setBackgroundResource(R.drawable.bg_red600_rounded_8)
                with(tvStateIsActive) {
                    if (product.isActive == "1") {
                        text = root.context.getString(R.string.active)
                        setBackgroundResource(R.drawable.bg_green_light_rounded)
                        setTextColor(android.graphics.Color.parseColor("#0C965A"))
                    }
                    else {
                        text = root.context.getString(R.string.non_active)
                        setBackgroundResource(R.drawable.bg_red50_rounded_12)
                        setTextColor(android.graphics.Color.parseColor("#FF3822"))
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductPricePlanViewHolder {
        return ProductPricePlanViewHolder(ItemProductMarkupBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = productPricePlanFiltered.size

    override fun onBindViewHolder(holder: ProductPricePlanViewHolder, position: Int) {
        holder.bind(productPricePlanFiltered[position])
        holder.binding.btnChangeMarkup.setOnClickListener {
            onItemClickListener.onItemDeleteClicked(
                productPricePlanFiltered[holder.bindingAdapterPosition]
            )
        }
        holder.binding.btnSetMarkup.setOnClickListener {
            onItemClickListener.onItemSetMarkupClick(
                productPricePlanFiltered[holder.bindingAdapterPosition].kodeProduct
            )
        }
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(char: CharSequence?): FilterResults {
                val query = char.toString()
                productPricePlanFiltered = if (query.isEmpty()) productPricePlan
                else {
                    val filteredList: MutableList<MarkupProductResponse> = ArrayList()
                    for (i in productPricePlan) {
                        if ("""(?i)($query)""".toRegex().containsMatchIn(i.kodeProvider))
                            filteredList.add(i)
                    }
                    filteredList
                }
                val filterResult = FilterResults()
                filterResult.values = productPricePlanFiltered
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productPricePlanFiltered = results?.values as List<MarkupProductResponse>
                notifyDataSetChanged()
            }
        }
    }

    interface SetOnItemClickListener {
        fun onItemDeleteClicked(product: MarkupProductResponse)
        fun onItemSetMarkupClick(codeProduct: String)
    }
}