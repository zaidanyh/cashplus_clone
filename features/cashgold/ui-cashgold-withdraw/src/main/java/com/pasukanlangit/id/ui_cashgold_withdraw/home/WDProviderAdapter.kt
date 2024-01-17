package com.pasukanlangit.id.ui_cashgold_withdraw.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasukanlangit.id.model.WithDrawProduct
import com.pasukanlangit.id.model.WithDrawProvider
import com.pasukanlangit.id.ui_cashgold_withdraw.R
import com.pasukanlangit.id.ui_cashgold_withdraw.databinding.ItemProviderWithdrawBinding
import com.pasukanlangit.id.ui_cashgold_withdraw.tag.ProductWithDraw

class WDProviderAdapter(private val providers: MutableList<WithDrawProvider>,private val listener: (List<WithDrawProduct>) -> Unit) : RecyclerView.Adapter<WDProviderAdapter.ProviderViewHolder>() {

    inner class ProviderViewHolder(private val binding: ItemProviderWithdrawBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(provider: WithDrawProvider){
            with(binding) {
                loadImageLogo(binding, provider)
                binding.tvLabel.text = provider.title

                if(provider.isStateUIActive){
                    binding.root.setBackgroundResource(R.drawable.bg_border_yellow_rounded)
                }else{
                    binding.root.setBackgroundColor(Color.WHITE)
                }

                binding.root.setOnClickListener {
                    listener(provider.product)
                    setActiveProvider(provider)
                }
            }
        }

        private fun loadImageLogo(binding: ItemProviderWithdrawBinding, provider: WithDrawProvider) {
            when (provider.id) {
                1 -> {
                    binding.ivLogo.setImageResource(R.drawable.antam)
                }
                5 -> {
                    binding.ivLogo.setImageResource(R.drawable.ubs)
                }
                else -> {
                    Glide.with(itemView.context)
                        .load(provider.img)
                        .error(R.drawable.ic_empty)
                        .into(binding.ivLogo)
                }
            }
        }
    }

    fun getProductSelected(): ProductWithDraw? {
        val provider = providers.singleOrNull { it.isStateUIActive }
        val product = provider?.product?.singleOrNull { it.isStateInputActive }

        if(provider == null || product == null || product.inputQty < 1) return null
        return ProductWithDraw(
            providerId = provider.id.toString(),
            productDomination = product.denominationGram,
            productDominationRaw = product.denominationRaw.toString(),
            fee = product.feeIdr,
            feeRaw = product.feeRaw,
            qty = product.inputQty.toString()
        )
    }

    private fun setActiveProvider(provider: WithDrawProvider) {
        val providerCurrentActive = providers.singleOrNull { it.isStateUIActive }

        val indexOfCurrentActive = providers.indexOfFirst { it.id == providerCurrentActive?.id }
        val indexOfClicked = providers.indexOfFirst { it.id == provider.id }

       if(indexOfCurrentActive != -1){
           providers[indexOfCurrentActive].isStateUIActive = false
           notifyItemChanged(indexOfCurrentActive)
       }
        providers[indexOfClicked].isStateUIActive = true
        notifyItemChanged(indexOfClicked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder {
        return ProviderViewHolder(ItemProviderWithdrawBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = providers.size

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) {
        holder.bind(providers[position])
    }
}