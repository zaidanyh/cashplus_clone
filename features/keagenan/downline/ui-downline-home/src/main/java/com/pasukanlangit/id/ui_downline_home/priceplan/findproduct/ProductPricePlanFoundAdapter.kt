package com.pasukanlangit.id.ui_downline_home.priceplan.findproduct

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.core.extensions.onDone
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.domain_downline.model.MarkupProductResponse
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.ItemProductMarkupBinding
import com.pasukanlangit.id.ui_downline_home.utils.SummaryProduct

class ProductPricePlanFoundAdapter: RecyclerView.Adapter<ProductPricePlanFoundAdapter.ProductFoundViewHolder>() {

    private val searchProducts = mutableListOf<MarkupProductResponse>()
    private val summaryProduct = mutableListOf<SummaryProduct>()
    private lateinit var onItemClickListener: OnItemClickListener

    fun setProducts(searchProducts: List<MarkupProductResponse>?) {
        if (searchProducts.isNullOrEmpty()) return
        this.searchProducts.clear()
        this.searchProducts.addAll(searchProducts)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setIsAddingProduct(summaryProduct: List<SummaryProduct>) {
        this.summaryProduct.clear()
        if (summaryProduct.isNotEmpty())
            this.summaryProduct.addAll(summaryProduct)
    }

    inner class ProductFoundViewHolder(val binding: ItemProductMarkupBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(product: MarkupProductResponse) {
            with(binding) {
                tvCategory.text = product.category
                tvCodeProvider.text = product.kodeProvider
                tvProductCode.text = product.kodeProduct
                tvCapitalPrice.text = getNumberRupiah(product.price.toIntOrNull() ?: 0)
                val outletPrice = product.outlet_price.toIntOrNull()?.plus(product.markup.toIntOrNull() ?: product.mainMarkup.toIntOrNull() ?: 0)
                tvOutletPrice.text = getNumberRupiah(outletPrice)
                tvMarkup.text = getNumberRupiah(product.markup.toIntOrNull() ?: product.mainMarkup.toIntOrNull() ?: 0)
                txtAlreadyAdded.isVisible = product.markup.isNotEmpty()
                btnChangeCancel.setOnClickListener {
                    onItemClickListener.onCancelClicked(product.kodeProduct)
                }
                with(tvStateIsActive) {
                    if (product.isActive == "1") {
                        text = root.context.getString(R.string.active)
                        setBackgroundResource(R.drawable.bg_green_light_rounded)
                        setTextColor(Color.parseColor("#0C965A"))
                    }
                    else {
                        text = root.context.getString(R.string.non_active)
                        setBackgroundResource(R.drawable.bg_red50_rounded_12)
                        setTextColor(Color.parseColor("#FF3822"))
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductFoundViewHolder {
        return ProductFoundViewHolder(ItemProductMarkupBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = searchProducts.size

    override fun onBindViewHolder(holder: ProductFoundViewHolder, position: Int) {
        holder.bind(searchProducts[position])
        with(holder.binding) {
            val context = root.context
            btnChangeMarkup.setOnClickListener {
                onItemClickListener.onAddClicked(
                    searchProducts[holder.bindingAdapterPosition].kodeProduct,
                    searchProducts[holder.bindingAdapterPosition].category,
                    holder.bindingAdapterPosition
                )
            }
            val summary = summaryProduct.find { item ->
                item.codeProduct == searchProducts[position].kodeProduct && item.positionIndex == position
            }
            btnChangeMarkup.isInvisible = summary != null || searchProducts[position].markup.isNotEmpty()
            wrapperChangeMarkup.isVisible = summary != null && searchProducts[position].markup.isEmpty()
            if (summary != null) {
                edtChangeMarkup.setText(summary.markup)
                edtChangeMarkup.setOnClickListener {
                    edtChangeMarkup.isFocusable = true
                    edtChangeMarkup.isFocusableInTouchMode = true
                    KeyboardUtil.openSoftKeyboard(context, edtChangeMarkup)
                }
                edtChangeMarkup.onDone {
                    val newMarkup = edtChangeMarkup.text.toString().trim()
                    edtChangeMarkup.isFocusable = false
                    if (newMarkup.isEmpty()) {
                        Toast.makeText(context, context.getString(R.string.markup_value_required), Toast.LENGTH_SHORT).show()
                        return@onDone
                    }
                    onItemClickListener.onTextChangeListener(summary.codeProduct, newMarkup)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onAddClicked(productCode: String, category: String, position: Int)
        fun onCancelClicked(productCode: String)
        fun onTextChangeListener(productCode: String, newMarkup: String)
    }
}