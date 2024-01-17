package com.pasukanlangit.id.travelling.train.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.travelling.domain.model.train.TrainItemSchedule
import com.pasukanlangit.id.travelling.train.R
import com.pasukanlangit.id.travelling.train.databinding.ItemTrainScheduleBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TrainScheduleAdapter(
    private val listener: (TrainItemSchedule) -> Unit
): ListAdapter<TrainItemSchedule, TrainScheduleAdapter.ScheduleViewHolder>(DIFF_UTIL) {

    inner class ScheduleViewHolder(val binding: ItemTrainScheduleBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return ScheduleViewHolder(ItemTrainScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = getItem(position)
        val time = schedule.trainDateTime.split("-")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val departureDate = (sdf.parse(schedule.timeDeparture) as Date).time
        val arrivalDate = (sdf.parse(schedule.timeArrival) as Date).time
        val timeDestination = countTimeDestination(departureDate, arrivalDate)
        with(holder.binding) {
            tvNameTrain.text = schedule.name
            tvDepartureCode.text = schedule.from
            tvDestinationCode.text = schedule.to
            tvTimeDeparture.text = time.first()
            tvTimeDestination.text = time.last()
            tvTime.text = timeDestination
            tvTrainClass.text = schedule.trainClass
            tvTrainPrice.text = getNumberRupiah(schedule.price)
            btnChooseTrain.text = if (schedule.available.toInt() > 0) root.context.getString(R.string.choose)
                else root.context.getString(R.string.sold_out_ticket)
            btnChooseTrain.isEnabled = schedule.available.toInt() > 0
            btnChooseTrain.setOnClickListener { listener(schedule) }
        }
    }

    private fun countTimeDestination(departureDate: Long, arrivalDate: Long): String {
        val timeDestination = arrivalDate - departureDate
        val hours = TimeUnit.MILLISECONDS.toHours(timeDestination) % 24
        val minute = TimeUnit.MILLISECONDS.toMinutes(timeDestination) % 60
        return String.format("%d Jam %d Menit", hours, minute)
    }

    companion object {
        val DIFF_UTIL = object: DiffUtil.ItemCallback<TrainItemSchedule>() {
            override fun areItemsTheSame(
                oldItem: TrainItemSchedule,
                newItem: TrainItemSchedule
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: TrainItemSchedule,
                newItem: TrainItemSchedule
            ): Boolean = oldItem == newItem
        }
    }
}