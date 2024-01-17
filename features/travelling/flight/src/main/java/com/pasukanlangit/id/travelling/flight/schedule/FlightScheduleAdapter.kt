package com.pasukanlangit.id.travelling.flight.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.travelling.domain.model.flight.FlightSchedule
import com.pasukanlangit.id.travelling.flight.R
import com.pasukanlangit.id.travelling.flight.databinding.ItemFlightScheduleBinding

class FlightScheduleAdapter(
    private val listener: (FlightSchedule) -> Unit
): ListAdapter<FlightSchedule, FlightScheduleAdapter.ScheduleViewHolder>(DIFF_UTIL) {

    inner class ScheduleViewHolder(val binding: ItemFlightScheduleBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return ScheduleViewHolder(ItemFlightScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = getItem(position)
        val dateTime = schedule.flightTime.split("-")
        with(holder.binding) {
            Glide.with(root.context)
                .load(schedule.flightImage)
                .thumbnail(
                    Glide.with(root.context)
                        .load(R.raw.image_loading_state)
                )
                .error(R.drawable.ic_empty)
                .into(imgFlight)
            tvFlightCode.text = schedule.flightCode.uppercase().replace(",","\n")
            tvNameFlight.text = schedule.flight
            tvDepartureCode.text = schedule.flightFrom
            tvDestinationCode.text = schedule.flightTo
            tvTimeDeparture.text = dateTime.first().trim()
            tvTimeDestination.text = dateTime.last().trim()
            tvTime.text = schedule.flightDuration
            tvPrice.text = root.context.getString(R.string.price_flight, getNumberRupiah(schedule.flightPublishFare))
            btnChooseFlight.isEnabled = schedule.flightSeatAvailable > 0
            btnChooseFlight.setOnClickListener { listener(schedule) }
        }
    }

    companion object {
        val DIFF_UTIL = object: DiffUtil.ItemCallback<FlightSchedule>() {
            override fun areItemsTheSame(
                oldItem: FlightSchedule,
                newItem: FlightSchedule
            ): Boolean = oldItem.flightId == newItem.flightId

            override fun areContentsTheSame(
                oldItem: FlightSchedule,
                newItem: FlightSchedule
            ): Boolean = oldItem == newItem
        }
    }
}