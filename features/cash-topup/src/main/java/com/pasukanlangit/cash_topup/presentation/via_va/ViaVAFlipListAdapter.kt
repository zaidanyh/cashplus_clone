package com.pasukanlangit.cash_topup.presentation.via_va

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.ItemPaymentTopUpBinding
import com.pasukanlangit.cash_topup.domain.model.FlipAcceptListResponse

class ViaVAFlipListAdapter(
    private val data: List<FlipAcceptListResponse>,
    private val event: (FlipAcceptListResponse) -> Unit
): RecyclerView.Adapter<ViaVAFlipListAdapter.ViaVAFlipListViewHolder>() {

    inner class ViaVAFlipListViewHolder(val binding: ItemPaymentTopUpBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FlipAcceptListResponse) {
            with(binding) {
                val animation = AnimationUtils.loadAnimation(root.context, R.anim.zoom_in_anim)
                val animationShake = AnimationUtils.loadAnimation(root.context, R.anim.shake_anim)

                Glide.with(root.context)
                    .load(item.imgUrl)
                    .skipMemoryCache(true)
                    .into(ivTopUpVia)
                ivTopUpVia.startAnimation(animation)

                ivTopUpVia.setOnClickListener {
                    ivTopUpVia.startAnimation(animationShake)
                    event(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViaVAFlipListViewHolder {
        return ViaVAFlipListViewHolder(ItemPaymentTopUpBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViaVAFlipListViewHolder, position: Int) {
        holder.bind(data[position])
    }
}