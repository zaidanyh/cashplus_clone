package com.pasukanlangit.id.ui_cashgold_withdraw.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.model.WithDrawProduct
import com.pasukanlangit.id.ui_cashgold_withdraw.R
import com.pasukanlangit.id.ui_cashgold_withdraw.databinding.ItemProductWithdrawBinding

class WDProductAdapter(private val products: MutableList<WithDrawProduct>,private val onQtyUpdate: (WithDrawProduct?) -> Unit) : RecyclerView.Adapter<WDProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductWithdrawBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: WithDrawProduct){
            with(binding) {
                tvGram.text = product.denominationGram
                tvStock.text = this.root.context.getString(R.string.stock_format, product.amount)
                tvPrice.text = product.feeIdr
                btnAdd.isEnabled = product.amount > 0
                tvIsOutOfStock.isVisible = product.amount <= 0
                tvStockAvaibility.text = this.root.context.getString(R.string.total_limit_withdraw, product.withDrawDaily, product.withDrawLimit)

                binding.inputCart.isVisible = product.isStateInputActive
                binding.btnAdd.isVisible = !product.isStateInputActive


                binding.btnAdd.setOnClickListener {
                    setActiveProduct(product)
                }

                with(binding.inputCart){
                    val remainingDailyWithdraw = product.withDrawLimit - product.withDrawDaily
                    val maxAmount = if(product.amount < remainingDailyWithdraw){
                        product.amount
                    }else{
                        remainingDailyWithdraw
                    }
                    setMaxQty(maxAmount)
                    setCurrentQty(product.inputQty)

                    setEventReachMin {
                        deleteActiveState(product)
                    }
                    setEventReachMax {
                        Toast.makeText(this.context, "Out of Stok/Limit", Toast.LENGTH_SHORT).show()
                    }
                    setOnQtyUpdate {
                        updateQty(product, getQty())
                    }
                }
            }
        }
    }


    private fun updateQty(product: WithDrawProduct, newQty: Int) {
        val indexOfClicked = products.indexOfFirst { it.denominationRaw == product.denominationRaw }
        products[indexOfClicked].inputQty = newQty

        onQtyUpdate(products[indexOfClicked])
        notifyItemChanged(indexOfClicked)
    }

    private fun deleteActiveState(product: WithDrawProduct) {
        val indexOfClicked = products.indexOfFirst { it.denominationRaw == product.denominationRaw }
        products[indexOfClicked].isStateInputActive = false

        onQtyUpdate(null)
        notifyItemChanged(indexOfClicked)
    }

    private fun setActiveProduct(product: WithDrawProduct) {
        val productCurrentActive = products.singleOrNull { it.isStateInputActive }

        val indexOfCurrentActive = products.indexOfFirst { it.denominationRaw == productCurrentActive?.denominationRaw }
        val indexOfClicked = products.indexOfFirst { it.denominationRaw == product.denominationRaw }

        if(indexOfCurrentActive != -1){
            products[indexOfCurrentActive].isStateInputActive = false
            notifyItemChanged(indexOfCurrentActive)
        }
        products[indexOfClicked].isStateInputActive = true

        onQtyUpdate(product)
        notifyItemChanged(indexOfClicked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemProductWithdrawBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }
}