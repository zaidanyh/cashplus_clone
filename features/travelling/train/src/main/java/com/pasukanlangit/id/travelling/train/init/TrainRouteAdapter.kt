package com.pasukanlangit.id.travelling.train.init

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.travelling.domain.model.train.Stations
import com.pasukanlangit.id.travelling.train.R
import com.pasukanlangit.id.travelling.train.databinding.ItemTrainRouteBinding

class TrainRouteAdapter(
    private val stations: List<Stations>,
    private val listener: (Stations) -> Unit
): RecyclerView.Adapter<TrainRouteAdapter.TrainRouteViewHolder>() {

    inner class TrainRouteViewHolder(val binding: ItemTrainRouteBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(station: Stations) {
            val city = station.location.split(",").last().trim()
            with(binding) {
                tvNameCity.text = city
                tvNameCityCode.text = root.context.getString(R.string.two_format_with_brackets, station.location, station.code)
                root.setOnClickListener { listener(station) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainRouteViewHolder {
        return TrainRouteViewHolder(ItemTrainRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TrainRouteViewHolder, position: Int) {
        holder.bind(stations[position])
    }

    override fun getItemCount(): Int = stations.size
}