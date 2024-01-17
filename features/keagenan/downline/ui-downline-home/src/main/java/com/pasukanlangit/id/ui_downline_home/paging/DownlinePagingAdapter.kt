package com.pasukanlangit.id.ui_downline_home.paging

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.domain_downline.model.DownLineItem
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.ItemDownlineLayoutBinding
import com.pasukanlangit.id.ui_downline_home.detail.DownLineDetail

class DownlinePagingAdapter(
    private val isSubDownline: Boolean = false
): PagingDataAdapter<DownLineItem, DownlinePagingAdapter.DownlineViewHolder>(DIFF_UTIL) {

    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class DownlineViewHolder(val binding: ItemDownlineLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(downLineItem: DownLineItem?) {
            with(binding) {
                val context = root.context
                downLineItem?.let { item ->
                    val downlineDetail = DownLineDetail(
                        accountName = item.name, phoneNumber = item.phoneActive, address = item.address,
                        balance = item.balanceRupiah, ownerName = item.ownerName, trxCount = item.trxCount,
                        markup = item.markup, downLineCount = item.downLineCount, isActive = item.isActive
                    )
                    tvName.text = item.name
                    tvNumber.text = item.userPhone
                    tvBalance.text = item.balanceRupiah
                    tvMarkup.text = getNumberRupiah(item.markup.toIntOrNull())
                    tvMarkupPlan.isVisible = !isSubDownline
                    tvMarkupPlan.text = item.markupCode.replace("_", " ").ifEmpty { root.context.getString(R.string.default_markup_plan) }
                    btnSetMarkup.isVisible = !isSubDownline
                    btnMore.isVisible = !isSubDownline
                    val isActive = item.isActive
                    bgNonActive.isVisible = !isActive || item.phoneActive.isEmpty()
                    if (isActive && item.phoneActive.isNotEmpty())
                        tvIsActive.apply {
                            setBackgroundResource(R.drawable.bg_green50_rounded_12)
                            text = context.getString(R.string.active)
                            setTextColor(Color.parseColor("#22C55E"))
                        }
                    else tvIsActive.apply {
                        setBackgroundResource(R.drawable.bg_red50_rounded_12)
                        text = context.getString(R.string.non_active)
                        setTextColor(Color.parseColor("#FF796A"))
                    }

                    root.setOnClickListener {
                        if (isActive && item.phoneActive.isNotEmpty()) {
                            onItemClickListener.onRootClicked(downlineDetail)
                            return@setOnClickListener
                        }
                        Toast.makeText(context, context.getString(R.string.downline_is_not_active, item.userPhone), Toast.LENGTH_SHORT).show()
                    }
                    btnSetMarkup.setOnClickListener {
                        if (isActive && item.phoneActive.isNotEmpty()) {
                            onItemClickListener.onSetMarkupClicked(item.phoneActive, item.name, item.markup)
                            return@setOnClickListener
                        }
                        Toast.makeText(context, context.getString(R.string.downline_is_not_active, item.userPhone), Toast.LENGTH_SHORT).show()
                    }
                    btnMore.setOnClickListener {
                        if (isActive && item.phoneActive.isNotEmpty()) {
                            onItemClickListener.onMoreClicked(downlineDetail)
                            return@setOnClickListener
                        }
                        Toast.makeText(context, context.getString(R.string.downline_is_not_active, item.userPhone), Toast.LENGTH_SHORT).show()
                    }
                    tvMarkupPlan.setOnClickListener {
                        if (isActive && item.phoneActive.isNotEmpty()) {
                            onItemClickListener.onMarkupPlanClicked(item.phoneActive, item.name, item.markupCode, item.markup)
                            return@setOnClickListener
                        }
                        Toast.makeText(context, context.getString(R.string.downline_is_not_active, item.userPhone), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: DownlineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownlineViewHolder {
        return DownlineViewHolder(ItemDownlineLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface OnItemClickListener {
        fun onRootClicked(downlineDetail: DownLineDetail)
        fun onSetMarkupClicked(phone: String, name: String, mainMarkup: String)
        fun onMoreClicked(downlineDetail: DownLineDetail)
        fun onMarkupPlanClicked(phone: String, name: String, markupPlan: String, mainMarkup: String)
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<DownLineItem>() {
            override fun areItemsTheSame(
                oldItem: DownLineItem,
                newItem: DownLineItem
            ): Boolean {
                return oldItem.userPhone == newItem.userPhone
            }

            override fun areContentsTheSame(
                oldItem: DownLineItem,
                newItem: DownLineItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}