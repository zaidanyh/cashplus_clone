package com.pasukanlangit.cashplus.ui.pages.history.topup

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemHistoryTopupBinding
import com.pasukanlangit.cashplus.model.response.HistoryTopUpDataItem
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.id.library_core.utils.TrxStatus
import com.pasukanlangit.id.library_core.utils.TrxUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TopUpPagingAdapter: PagingDataAdapter<HistoryTopUpDataItem, TopUpPagingAdapter.TopUpViewHolder>(DIFF_UTIL) {

    private lateinit var onButtonClickListener: OnButtonClickListener

    fun setOnButtonClickListener(onButtonClickListener: OnButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener
    }

    inner class TopUpViewHolder(val binding: ItemHistoryTopupBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(topUpItem: HistoryTopUpDataItem?) {
            with(binding) {
                val context = root.context
                if (topUpItem?.paymentMethod?.contains("tiket", ignoreCase = true) == true) {
                    tvTitle.text = context.getString(R.string.transfer_topUp_format, topUpItem.paymentMethod.replace("tiket", "", ignoreCase = true))
                    tvTypeTopUp.text = context.getString(R.string.bank_account_number)
                } else if (topUpItem?.paymentMethod?.contains("va", ignoreCase = true) == true) {
                    tvTitle.text = context.getString(R.string.topUp_format, topUpItem.paymentMethod.replace("_", " "))
                    tvTypeTopUp.text = context.getString(R.string.virtual_account_number)
                } else if (topUpItem?.paymentMethod?.contains("cvs", ignoreCase = true) == true) {
                    tvTitle.text = context.getString(R.string.topUp_alfamart)
                    tvTypeTopUp.text = context.getString(R.string.payment_number)
                } else if (topUpItem?.paymentMethod?.contains("ewallet", ignoreCase = true) == true) {
                    tvTitle.text = context.getString(R.string.topUp_format, topUpItem.paymentMethod.replace("ewallet", "Via", ignoreCase = true).replace("_", " "))
                } else if (topUpItem?.transStat == "700") {
                    tvTitle.text = context.getString(R.string.commission_recipient)
                }
                else tvTitle.text = context.getString(R.string.deposit_via_format, topUpItem?.paymentMethod?.replace("_", " "))

                when(TrxUtil.getTrxStatusByCode(topUpItem?.transStat)) {
                    TrxStatus.PENDING -> {
                        tvTrxHistoryStatus.setBackgroundResource(R.drawable.bg_yellow50_rounded_12)
                        tvTrxHistoryStatus.setTextColor(Color.parseColor("#FFBE0B"))
                        tvTrxHistoryStatus.text = context.getString(R.string.process)
                    }
                    TrxStatus.SUCCESS -> {
                        tvTrxHistoryStatus.setBackgroundResource(R.drawable.bg_green50_rounded_12)
                        tvTrxHistoryStatus.setTextColor(Color.parseColor("#0C965A"))
                        tvTrxHistoryStatus.text = context.getString(R.string.success)
                        wrapperContent.isVisible = false
                        btnCopiedNominal.isVisible = false
                    }
                    TrxStatus.FAILED -> {
                        tvTrxHistoryStatus.setBackgroundResource(R.drawable.bg_red50_rounded_12)
                        tvTrxHistoryStatus.setTextColor(Color.parseColor("#FF3822"))
                        tvTrxHistoryStatus.text = context.getString(R.string.failed)
                        wrapperContent.isVisible = false
                        btnCopiedNominal.isVisible = false
                    }
                }

                tvNoRek.text = topUpItem?.noRek
                btnCopyNorek.setOnClickListener {
                    onButtonClickListener.onCopiedRek(topUpItem?.noRek ?: "")
                }
                tvReceiverName.text = topUpItem?.bank_acc_name

                val inputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                val dateTime = LocalDateTime.parse(topUpItem?.trxDate, inputFormat)

                val outputFormat = DateTimeFormatter.ofPattern(
                    "EEEE, dd MMMM yyyy",
                    Locale(context.getString(R.string.locale_language), context.getString(R.string.locale_country))
                )
                val newDateTime = outputFormat.format(dateTime)

                tvTrxHistoryDate.text = newDateTime
                tvHistoryTopupPrice.text = MyUtils.getNumberRupiah(topUpItem?.value?.toIntOrNull())
                btnCopiedNominal.setOnClickListener {
                    onButtonClickListener.onCopiedNominal(topUpItem?.value.toString())
                }
            }
        }
    }

    override fun onBindViewHolder(holder: TopUpViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopUpViewHolder {
        return TopUpViewHolder(ItemHistoryTopupBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface OnButtonClickListener {
        fun onCopiedRek(rekening: String)
        fun onCopiedNominal(nominal: String)
    }

    companion object {
        val DIFF_UTIL = object: DiffUtil.ItemCallback<HistoryTopUpDataItem>() {
            override fun areItemsTheSame(
                oldItem: HistoryTopUpDataItem,
                newItem: HistoryTopUpDataItem
            ): Boolean = oldItem.rowNum == newItem.rowNum

            override fun areContentsTheSame(
                oldItem: HistoryTopUpDataItem,
                newItem: HistoryTopUpDataItem
            ): Boolean = oldItem == newItem
        }
    }
}