package com.pasukanlangit.cashplus.ui.pages.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemBalanceBinding
import com.pasukanlangit.cashplus.domain.model.ItemBalance
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiahWithoutRp

class BalanceAdapter: ListAdapter<ItemBalance, BalanceAdapter.BalanceViewHolder>(DIFF_UTIL) {

    private lateinit var bindingAccountClickListener: BindingAccountClickListener

    fun setOnButtonClickListener(bindingAccountClickListener: BindingAccountClickListener) {
        this.bindingAccountClickListener = bindingAccountClickListener
    }

    inner class BalanceViewHolder(val binding: ItemBalanceBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceViewHolder {
        return BalanceViewHolder(ItemBalanceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BalanceViewHolder, position: Int) {
        val balance = getItem(position)
        with(holder.binding) {
            tvLabel.text = balance.label
            tvValueBalance.text = getNumberRupiahWithoutRp(balance.balance)
            btnBindingAccount.setOnClickListener {
                bindingAccountClickListener.onBindingAcountClicked()
            }
            if (!balance.stateBalance) {
                wrapperBalance.isVisible = false
                btnBindingAccount.isVisible = true
            } else {
                wrapperBalance.isVisible = true
                btnBindingAccount.isVisible = false
            }

            if (position == (this@BalanceAdapter.currentList.size -1) && position == 1 && balance.stateBalance) {
                txtTapForDetail.isVisible = true
                iconTap.isVisible = true
                wrapperBalance.setOnClickListener {
                    bindingAccountClickListener.onDetailNobuFeatures()
                }
            } else {
                txtTapForDetail.isVisible = false
                iconTap.isVisible = false
            }
        }
    }

    interface BindingAccountClickListener {
        fun onDetailNobuFeatures()
        fun onBindingAcountClicked()
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<ItemBalance>() {
            override fun areItemsTheSame(
                oldItem: ItemBalance,
                newItem: ItemBalance
            ): Boolean = oldItem.stateBalance == newItem.stateBalance

            override fun areContentsTheSame(
                oldItem: ItemBalance,
                newItem: ItemBalance
            ): Boolean = oldItem == newItem
        }
    }
}