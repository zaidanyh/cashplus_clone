package com.pasukanlangit.cashplus.ui.pages.history.transaction

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemHistoryTrxBinding
import com.pasukanlangit.cashplus.model.response.TransactionItem
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.id.library_core.utils.TrxStatus
import com.pasukanlangit.id.library_core.utils.TrxUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryTrxAdapter(
    private val listener: (TransactionItem) -> Unit
) : RecyclerView.Adapter<HistoryTrxAdapter.ActivityViewHolder>() {

    private val transactions = mutableListOf<TransactionItem>()

    fun setTransactionHistory(transactions: List<TransactionItem>?) {
        if (transactions.isNullOrEmpty()) return
        this.transactions.clear()
        this.transactions.addAll(transactions)
    }

    inner class ActivityViewHolder(val binding: ItemHistoryTrxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transactionItem: TransactionItem) {
            with(binding) {
                val context = root.context
                tvTrxHistoryTitle.text = transactionItem.dsc
                tvTrxHistoryDestinationAndCategory.text = root.context.getString(R.string.destinasi_riwayat_transaksi, transactionItem.noHp, transactionItem.category)
                tvTrxHistoryPrice.text = MyUtils.getNumberRupiah(transactionItem.value?.toInt()!!)

                val inputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                val dateTime = LocalDateTime.parse(transactionItem.trxStart, inputFormat)

                val outputFormat = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale(
                    context.getString(R.string.locale_language), context.getString(R.string.locale_country))
                )
                val newDateTime = outputFormat.format(dateTime)
                tvTrxHistoryDate.text = newDateTime

                when(TrxUtil.getTrxStatusByCode(transactionItem.transStat)) {
                    TrxStatus.PENDING -> {
                        tvTrxHistoryStatus.setBackgroundResource(R.drawable.bg_yellow50_rounded_12)
                        tvTrxHistoryStatus.setTextColor(Color.parseColor("#FFBE0B"))
                        tvTrxHistoryStatus.text = context.getString(R.string.pending)
                    }
                    TrxStatus.SUCCESS -> {
                        tvTrxHistoryStatus.setBackgroundResource(R.drawable.bg_green50_rounded_12)
                        tvTrxHistoryStatus.setTextColor(Color.parseColor("#0C965A"))
                        tvTrxHistoryStatus.text = context.getString(R.string.success)
                    }
                    TrxStatus.FAILED -> {
                        tvTrxHistoryStatus.setBackgroundResource(R.drawable.bg_red50_rounded_12)
                        tvTrxHistoryStatus.setTextColor(Color.parseColor("#FF3822"))
                        tvTrxHistoryStatus.text = context.getString(R.string.failed)
                    }
                }

                root.setOnClickListener {
                    listener(transactionItem)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        return ActivityViewHolder(ItemHistoryTrxBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) = holder.bind(transactions[position])
}