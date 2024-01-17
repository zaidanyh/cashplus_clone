package com.pasukanlangit.cash_topup.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.ItemPaymentTopUpBinding
import com.pasukanlangit.cash_topup.utils.MenuViaTopUpPayment

class MenuPaymentAdapter(
    private val menuPayments: List<MenuViaTopUpPayment>,
    private val event: (MenuViaTopUpPayment) -> Unit
): RecyclerView.Adapter<MenuPaymentAdapter.MenuPaymentViewHolder>() {

    inner class MenuPaymentViewHolder(val binding: ItemPaymentTopUpBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: MenuViaTopUpPayment) {
            with(binding) {
                val animation = AnimationUtils.loadAnimation(root.context, R.anim.zoom_in_anim)
                val animationShake = AnimationUtils.loadAnimation(root.context, R.anim.shake_anim)

                ivTopUpVia.setImageResource(menu.img)
                ivTopUpVia.startAnimation(animation)

                ivTopUpVia.setOnClickListener {
                    ivTopUpVia.startAnimation(animationShake)
                    event(menu)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuPaymentViewHolder {
        return MenuPaymentViewHolder(ItemPaymentTopUpBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = menuPayments.size

    override fun onBindViewHolder(holder: MenuPaymentViewHolder, position: Int) {
        holder.bind(menuPayments[position])
    }
}