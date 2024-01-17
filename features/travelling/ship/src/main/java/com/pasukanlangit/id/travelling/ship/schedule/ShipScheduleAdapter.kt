package com.pasukanlangit.id.travelling.ship.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.travelling.domain.model.ship.HarborSchedules
import com.pasukanlangit.id.travelling.ship.R
import com.pasukanlangit.id.travelling.ship.databinding.ItemShipScheduleBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ShipScheduleAdapter: ListAdapter<HarborSchedules, ShipScheduleAdapter.ShipScheduleViewHolder>(DIFF_UTIL) {

    private lateinit var onButtonClickListener: OnButtonClickListener

    fun setOnClickListener(onButtonClickListener: OnButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener
    }

    inner class ShipScheduleViewHolder(val binding: ItemShipScheduleBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipScheduleViewHolder {
        return ShipScheduleViewHolder(ItemShipScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ShipScheduleViewHolder, position: Int) {
        val schedule = getItem(position)
        val shipFrom = schedule.from.split(", ")
        val shipTo = schedule.to.split(", ")
        val estimation = schedule.infoDateTime.split("-")
        val departureTime = estimation.first().split(" ").last()
        val destinationTime = estimation.last().split(" ").last()

        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        val departureDate = (sdf.parse(estimation.first()) as Date).time
        val arrivalDate = (sdf.parse(estimation.last()) as Date).time
        val estimationDay = countTimeDestination(holder.binding.root.context, departureDate, arrivalDate)
        with(holder.binding) {
            tvNameShip.text = schedule.name
            tvNumberShip.text = schedule.number
            tvClassShip.text = schedule.shipClass
            tvHarborDepartureName.text = shipFrom.first()
            tvHarborDepartureCity.text = shipFrom.last()
            tvHarborArrivalName.text = shipTo.first()
            tvHarborArrivalCity.text = shipTo.last()
            tvTimeDeparture.text = departureTime
            tvTimeArrival.text = destinationTime
            tvTravelDestination.text = estimationDay
            tvPriceShip.text = getNumberRupiah(schedule.basicFare)
            root.setOnClickListener {
                onButtonClickListener.onClickHarborSchedule(schedule)
            }
            btnChooseShip.setOnClickListener {
                onButtonClickListener.onChooseHarborSchedule(schedule)
            }
        }
    }

    private fun countTimeDestination(context: Context, departureDate: Long, arrivalDate: Long): String {
        val timeDestination = arrivalDate - departureDate
        val days = TimeUnit.MILLISECONDS.toDays(timeDestination)
        val hour = TimeUnit.MILLISECONDS.toHours(timeDestination) % 24
        val minute = TimeUnit.MILLISECONDS.toMinutes(timeDestination) % 60
        return if (days > 0) context.getString(R.string.format_estimation_full, days, hour, minute)
        else context.getString(R.string.format_estimation_hours, hour, minute)
    }

    interface OnButtonClickListener {
        fun onClickHarborSchedule(harbor: HarborSchedules)
        fun onChooseHarborSchedule(harbor: HarborSchedules)
    }

    companion object {
        val DIFF_UTIL = object: DiffUtil.ItemCallback<HarborSchedules>() {
            override fun areItemsTheSame(
                oldItem: HarborSchedules,
                newItem: HarborSchedules
            ): Boolean = oldItem.code == newItem.code

            override fun areContentsTheSame(
                oldItem: HarborSchedules,
                newItem: HarborSchedules
            ): Boolean = oldItem == newItem
        }
    }
}