package com.pasukanlangit.cashplus.ui.gotomekka

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemMekkaBinding

class MekkaAdapter(private val images: List<Int>, val listener: () -> Unit) : RecyclerView.Adapter<MekkaAdapter.MekkaViewHolder>() {

    inner class MekkaViewHolder(val binding: ItemMekkaBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(image: Int) {
            binding.ivItem.setImageResource(image)

            itemView.setOnClickListener {
                listener()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MekkaViewHolder {
        return MekkaViewHolder(ItemMekkaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MekkaViewHolder, position: Int) {
       holder.bind(images[position % images.size])
    }

    override fun getItemCount(): Int = images.size * 3
}