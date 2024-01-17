package com.pasukanlangit.cashplus.ui.pages.history.detailtokoonline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemTrackingBinding
import com.pasukanlangit.cashplus.databinding.ItemTrackingTopBinding
import com.pasukanlangit.cashplus.model.response.ManifestItem
import java.text.SimpleDateFormat
import java.util.*

enum class TrackingType(val value: Int) {
    TRACKING_HEADER(0),
    TRACKING_ITEM(1)
}

class TrackingAdapter(private val manifests: List<ManifestItem>) : RecyclerView.Adapter<TrackingAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val manifest = manifests[position]

            val tvTime = itemView.findViewById<TextView>(R.id.tv_time)
            val tvDescription = itemView.findViewById<TextView>(R.id.tv_desc)
            val tvLine = itemView.findViewById<View>(R.id.line)

            val timeParser = SimpleDateFormat("yyyy-MM-dd").parse(manifest.manifestDate)
            val time = SimpleDateFormat("dd MMM", Locale.getDefault()).format(timeParser)

            tvTime.text = "$time\n${manifest.manifestTime}"
            tvDescription.text = "${manifest.manifestDescription} [${manifest.cityName}]"

            tvLine.isVisible = position != manifests.size - 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingAdapter.ViewHolder {
        val view = if(viewType == TrackingType.TRACKING_HEADER.value){
            ItemTrackingTopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }else{
            ItemTrackingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackingAdapter.ViewHolder, position: Int) {
       holder.bind(position)
    }

    override fun getItemViewType(position: Int): Int  =
        if(position == 0) TrackingType.TRACKING_HEADER.value
        else TrackingType.TRACKING_ITEM.value


    override fun getItemCount(): Int = manifests.size
}