package com.pasukanlangit.id.ui_downline_home.priceplan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.ItemPricePlanBinding
import com.pasukanlangit.id.ui_downline_home.utils.MarkupPlanParcelable

class PricePlanAdapter: RecyclerView.Adapter<PricePlanAdapter.PricePlanViewHolder>() {

    private val pricePlans = mutableListOf<MarkupPlanParcelable>()
    private lateinit var markupPlan: String
    private lateinit var onItemClickListener: OnItemClickListener

    fun setPricePlanList(pricePlans: List<MarkupPlanParcelable>, markupPlan: String) {
        this.pricePlans.clear()
        this.pricePlans.addAll(pricePlans)
        this.markupPlan = markupPlan
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class PricePlanViewHolder(val binding: ItemPricePlanBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MarkupPlanParcelable) {
            with(binding) {
                val context = root.context
                tvNamePricePlan.text = item.codeMarkupPlan.replace("_", " ")
                tvDescPricePlan.text = item.description
                btnChoosePricePlan.setBackgroundResource(
                    if (markupPlan == item.codeMarkupPlan) R.drawable.bg_red600_rounded_8
                    else R.drawable.bg_primary_rounded_8
                )
                btnChoosePricePlan.text = if (markupPlan == item.codeMarkupPlan)
                    context.getString(R.string.unapply)
                else context.getString(R.string.submit)
                btnChoosePricePlan.setOnClickListener {
                    onItemClickListener.onApplyClicked(item.codeMarkupPlan)
                }
                btnDeleteMarkup.setOnClickListener {
                    onItemClickListener.onDeleteClicked(item.codeMarkupPlan)
                }
                btnEditMarkup.setOnClickListener {
                    onItemClickListener.onEditClicked(item)
                }
                btnSetMarkupProduct.setOnClickListener {
                    onItemClickListener.onSetMarkupProductClicked(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PricePlanViewHolder {
        return PricePlanViewHolder(ItemPricePlanBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = pricePlans.size

    override fun onBindViewHolder(holder: PricePlanViewHolder, position: Int) {
        holder.bind(pricePlans[position])
    }

    interface OnItemClickListener {
        fun onApplyClicked(codeMarkup: String)
        fun onDeleteClicked(codeMarkup: String)
        fun onEditClicked(markupPlan: MarkupPlanParcelable)
        fun onSetMarkupProductClicked(markupPlan: MarkupPlanParcelable)
    }
}