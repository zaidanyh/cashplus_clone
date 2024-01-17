package com.pasukanlangit.id.recapitulation.presentation.deposit.pagination

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.recapitulation.R
import com.pasukanlangit.id.recapitulation.databinding.ItemRecapDepositBinding
import com.pasukanlangit.id.recapitulation.domain.model.MutationDepositResponse
import com.pasukanlangit.id.recapitulation.presentation.utils.RecapUtils.changeDateFormat

class RecapSummaryDepositAdapter: PagingDataAdapter<MutationDepositResponse, RecapSummaryDepositAdapter.RecapSummaryDepositViewHolder>(DIFF_UTIL) {

    inner class RecapSummaryDepositViewHolder(val binding: ItemRecapDepositBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MutationDepositResponse?) {
            with(binding) {
                item?.let { item ->
                    val context = root.context

                    tvProductCode.text = item.productCode
                    tvDepositType.setBackgroundResource(
                        if (item.isDB) R.drawable.bg_orange50_rounded_12
                        else R.drawable.bg_green50_rounded_12
                    )
                    tvDepositType.text = if (item.isDB) "DB" else "CR"
                    tvDepositType.setTextColor(
                        if (item.isDB) Color.parseColor("#EA580C")
                        else Color.parseColor("#059669")
                    )
                    tvDesc.text = item.productDesc
                    tvProviderCode.text = item.providerCode
                    tvDestDeposit.text = item.dest
                    tvValueDeposit.text = getNumberRupiah(item.value.toIntOrNull())
                    tvEndingBalance.text = getNumberRupiah(item.endingBalance.toBigIntegerOrNull())
                    tvDateDepositMutation.text = item.trxDate.changeDateFormat(context)
                    tvMutationCategory.text = item.category.uppercase()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecapSummaryDepositViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecapSummaryDepositViewHolder =
        RecapSummaryDepositViewHolder(ItemRecapDepositBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<MutationDepositResponse>() {
            override fun areItemsTheSame(
                oldItem: MutationDepositResponse,
                newItem: MutationDepositResponse
            ): Boolean {
                return oldItem.rowNum == newItem.rowNum
            }

            override fun areContentsTheSame(
                oldItem: MutationDepositResponse,
                newItem: MutationDepositResponse
            ): Boolean = oldItem == newItem
        }
    }
}