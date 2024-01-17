package com.pasukanlangit.id.ui_downline_mutasi_summary_detail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.core.utils.DrawablePosition
import com.pasukanlangit.id.domain_downline.model.DbCrType
import com.pasukanlangit.id.domain_downline.model.DownLineMutationResponse
import com.pasukanlangit.id.library_core.utils.TrxStatus
import com.pasukanlangit.id.ui_downline_mutasi_summary_detail.databinding.ItemMutasiDownlineBinding

class MutasiDownlineAdapter(private val mutations: List<DownLineMutationResponse>): RecyclerView.Adapter<MutasiDownlineAdapter.OnViewHolder>() {
    class OnViewHolder(private val binding: ItemMutasiDownlineBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(mutation: DownLineMutationResponse) {
            with(binding){
                when(mutation.dbCrType){
                    DbCrType.DB -> iconDbCr.setImageResource(R.drawable.icon_db)
                    DbCrType.CR -> iconDbCr.setImageResource(R.drawable.icon_cr)
                    DbCrType.UNKNOWN -> iconDbCr.isVisible = false
                }

                when(mutation.trxStatus){
                    TrxStatus.PENDING -> {
                        tvStatusTrx.text = root.context.getString(R.string.pending)
                        tvStatusTrx.setTextColor(Color.parseColor("#FFBE0B"))
                        tvDesc.setTextColor(Color.parseColor("#FFBE0B"))
                        tvDesc.compoundDrawablesRelative[DrawablePosition.DRAWABLE_LEFT].setTint(Color.parseColor("#FFBE0B"))
                    }
                    TrxStatus.SUCCESS ->  {
                        tvStatusTrx.text = root.context.getString(R.string.success)
                        tvStatusTrx.setTextColor(Color.parseColor("#0EAD69"))
                        tvDesc.setTextColor(Color.parseColor("#0EAD69"))
                        tvDesc.compoundDrawablesRelative[DrawablePosition.DRAWABLE_LEFT].setTint(Color.parseColor("#0EAD69"))
                    }
                    TrxStatus.FAILED -> {
                        tvStatusTrx.text = root.context.getString(R.string.failed)
                        tvStatusTrx.setTextColor(Color.parseColor("#FF6150"))
                        tvDesc.setTextColor(Color.parseColor("#FF6150"))
                        tvDesc.compoundDrawablesRelative[DrawablePosition.DRAWABLE_LEFT].setTint(Color.parseColor("#FF6150"))
                    }
                }
                tvDesc.text = mutation.desc
                tvPrice.text = mutation.valueRupiah
                tvEndingBalance.text = mutation.endingBalanceRupiah
                tvDate.text = mutation.date
                tvProductCode.text = mutation.productCode.ifEmpty { "-" }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnViewHolder {
       val view = ItemMutasiDownlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnViewHolder, position: Int) {
        holder.bind(mutations[position])
    }

    override fun getItemCount(): Int = mutations.size
}