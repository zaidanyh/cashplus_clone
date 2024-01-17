package com.pasukanlangit.id.rebate.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.rebate.databinding.ItemDetailRebateBinding
import com.pasukanlangit.id.rebate.domain.model.RebatePerProductResponse

class RebateDetailProductAdapter(private val products: List<RebatePerProductResponse>): RecyclerView.Adapter<RebateDetailProductAdapter.OnViewHolder>() {
    class OnViewHolder(private val binding: ItemDetailRebateBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(product: RebatePerProductResponse) {
            with(binding){
                tvProductCode.text = product.productCode
                tvRebateTotal.text = product.totalRebateRupiah
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnViewHolder {
       return OnViewHolder(ItemDetailRebateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: OnViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}