package com.pasukanlangit.id.ui_downline_mutasi_summary_detail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.library_core.utils.TrxStatus
import com.pasukanlangit.id.library_core.utils.TrxUtil
import com.pasukanlangit.id.domain_downline.model.DetailTransferDownlineResponse
import com.pasukanlangit.id.ui_downline_mutasi_summary_detail.databinding.ItemDetailTransferBinding

class DetailTransferAdapter(private val items: List<DetailTransferDownlineResponse>): RecyclerView.Adapter<DetailTransferAdapter.OnViewHolder>() {
    class OnViewHolder(private val binding: ItemDetailTransferBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(transfer: DetailTransferDownlineResponse) {
            with(binding){
                tvPaymentMethod.text = transfer.paymentMethod
                tvDownlineName.text = transfer.downlineName
                tvNumberDownline.text = transfer.destinationNumber
                tvPrice.text = transfer.valueRupiah
                tvBalance.text = transfer.endingBalanceRupiah
                tvDate.text = transfer.date

                when(TrxUtil.getTrxStatusByCode(transfer.transtStat)){
                    TrxStatus.PENDING -> {
                        tvStatusTrx.setBackgroundResource(R.drawable.bg_yellow50_rounded_12)
                        tvStatusTrx.setTextColor(Color.parseColor("#ECB909"))
                        tvStatusTrx.text = root.context.getString(R.string.pending)
                    }
                    TrxStatus.SUCCESS -> {
                        tvStatusTrx.setBackgroundResource(R.drawable.bg_green50_rounded_12)
                        tvStatusTrx.setTextColor(Color.parseColor("#115E59"))
                        tvStatusTrx.text = root.context.getString(R.string.success)
                    }
                    TrxStatus.FAILED -> {
                        tvStatusTrx.setBackgroundResource(R.drawable.bg_red50_rounded_12)
                        tvStatusTrx.setTextColor(Color.parseColor("#DC2626"))
                        tvStatusTrx.text = root.context.getString(R.string.failed)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnViewHolder {
       val view = ItemDetailTransferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}