package com.pasukanlangit.cashplus.ui.omni.packageomni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemDetailPackageOmniBinding
import com.pasukanlangit.cashplus.domain.model.BonusPackage

class DetailPackageAdapter: RecyclerView.Adapter<DetailPackageAdapter.DetailPackageViewHolder>() {

    private val bonuses = mutableListOf<BonusPackage>()

    fun setBonuses(bonuses: List<BonusPackage>?) {
        if (bonuses.isNullOrEmpty()) return
        this.bonuses.clear()
        this.bonuses.addAll(bonuses)
        notifyDataSetChanged()
    }

    inner class DetailPackageViewHolder(val binding: ItemDetailPackageOmniBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(bonus: BonusPackage) {
            with(binding) {
                tvLabel.text = bonus.name
                tvValue.text = bonus.quota
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailPackageViewHolder {
        return DetailPackageViewHolder(ItemDetailPackageOmniBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = bonuses.size

    override fun onBindViewHolder(holder: DetailPackageViewHolder, position: Int) {
        holder.bind(bonuses[position])
    }
}