package com.pasukanlangit.cashplus.ui.splashscreen.update

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BulletSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemUpdateAppBinding

class UpdateListAdapter(private val listUpdate: List<String>): RecyclerView.Adapter<UpdateListAdapter.UpdateListViewHolder>() {

    inner class UpdateListViewHolder(val binding: ItemUpdateAppBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(value: String) {
            val spannableString = SpannableString(value)
            spannableString.setSpan(
                BulletSpan(20, Color.parseColor("#334155")),
                0, value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.tvUpdateDesc.text = spannableString
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateListViewHolder {
        return UpdateListViewHolder(ItemUpdateAppBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = listUpdate.size

    override fun onBindViewHolder(holder: UpdateListViewHolder, position: Int) {
        holder.bind(listUpdate[position])
    }
}