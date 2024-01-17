package com.pasukanlangit.cashplus.ui.pembayarancart.courir

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemListCourierBinding

class CourierListAdapter(private val couriers: List<Courier>,private val listener: (Courier) -> Unit): RecyclerView.Adapter<CourierListAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val binding: ItemListCourierBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(courier: Courier) {
            with(binding){
                ivCourier.setImageResource(courier.image)
                root.setOnClickListener {
                    listener(courier)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
       val view = ItemListCourierBinding.inflate(LayoutInflater.from(parent.context), parent, false)
       return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(couriers[position])
    }

    override fun getItemCount(): Int = couriers.size
}