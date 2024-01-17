package com.pasukanlangit.cash_topup.presentation

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cash_topup.databinding.ItemNominalBinding
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiahWithoutRp

class NominalTopUpAdapter(
    private val onClickNominal: (String) -> Unit
): RecyclerView.Adapter<NominalTopUpAdapter.NominalViewHolder>() {

    private val listOfNominal = mutableListOf("50000", "100000", "200000", "500000", "1000000", "2000000")

    inner class NominalViewHolder(val binding: ItemNominalBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(value: String) {
            with(binding) {
                tvNominal.text = getNumberRupiahWithoutRp(value.toIntOrNull())

                val animationOnClick = ObjectAnimator.ofFloat(
                    binding.root, View.TRANSLATION_X, -20F, 20F
                ).apply {
                    duration = 200
                    repeatCount = 4
                    repeatMode = ObjectAnimator.REVERSE
                }
                animationOnClick.doOnEnd {
                    root.animate().translationX(0F)
                }

                root.setOnClickListener {
                    animationOnClick.start()
                    onClickNominal(value)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NominalViewHolder {
        return NominalViewHolder(ItemNominalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = listOfNominal.size

    override fun onBindViewHolder(holder: NominalViewHolder, position: Int) {
        holder.bind(listOfNominal[position])
    }
}