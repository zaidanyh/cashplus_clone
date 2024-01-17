package com.pasukanlangit.id.travelling.ship.init

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.travelling.domain.model.ship.Harbors
import com.pasukanlangit.id.travelling.ship.databinding.ItemShipRouteBinding

class ShipRouteAdapter(
    private val harbors: List<Harbors>,
    private val listener: (Harbors) -> Unit
): RecyclerView.Adapter<ShipRouteAdapter.ShipRouteViewHolder>() {

    inner class ShipRouteViewHolder(val binding: ItemShipRouteBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(harbor: Harbors) {
            val city = harbor.name.split(",").last().trim()
            with(binding) {
                tvNameCity.text = city
                tvNameCityCode.text = harbor.name
                root.setOnClickListener { listener(harbor) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipRouteViewHolder {
        return ShipRouteViewHolder(ItemShipRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ShipRouteViewHolder, position: Int) {
        holder.bind(harbors[position])
    }

    override fun getItemCount(): Int = harbors.size
}