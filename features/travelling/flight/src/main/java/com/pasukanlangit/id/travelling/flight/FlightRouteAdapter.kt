package com.pasukanlangit.id.travelling.flight

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.travelling.domain.model.flight.Airports
import com.pasukanlangit.id.travelling.flight.databinding.ItemAirportRouteBinding

class FlightRouteAdapter(
    private val airports: List<Airports>,
    private val isDestination: Boolean = false,
    private val listener: (Airports) -> Unit
): RecyclerView.Adapter<FlightRouteAdapter.FlightRouteViewHolder>() {

    inner class FlightRouteViewHolder(val binding: ItemAirportRouteBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(airport: Airports) {
            with(binding) {
                tvNameAirport.text = airport.airport
                tvNameCity.text = airport.city
                if (isDestination) imgType.setImageResource(R.drawable.ic_plane_landing)
                else imgType.setImageResource(R.drawable.ic_plane_take_off)
                root.setOnClickListener { listener(airport) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightRouteViewHolder {
        return FlightRouteViewHolder(ItemAirportRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FlightRouteViewHolder, position: Int) = holder.bind(airports[position])

    override fun getItemCount(): Int = airports.size
}