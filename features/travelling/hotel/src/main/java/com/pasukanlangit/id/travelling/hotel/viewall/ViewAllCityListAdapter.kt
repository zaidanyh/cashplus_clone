package com.pasukanlangit.id.travelling.hotel.viewall

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.travelling.hotel.R
import com.pasukanlangit.id.travelling.hotel.databinding.ItemCityListBinding
import com.pasukanlangit.id.travelling.hotel.temp.HotelsHardCode

class ViewAllCityListAdapter(
    private val cities: List<HotelsHardCode>,
    private val listener: (HotelsHardCode) -> Unit
): RecyclerView.Adapter<ViewAllCityListAdapter.ViewAllCityListViewHolder>() {

    inner class ViewAllCityListViewHolder(val binding: ItemCityListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(city: HotelsHardCode) {
            with(binding.root) {
                text = city.cityName

                if (city.isActive) {
                    setBackgroundResource(R.drawable.bg_primary_rounded_16)
                    setTextColor(Color.parseColor("#FFFFFF"))
                } else {
                    setBackgroundResource(R.drawable.bg_blue50_rounded_16)
                    setTextColor(Color.parseColor("#0A57FF"))
                }
                setOnClickListener {
                    listener(city)
                    setActiveCity(city.cityCode)
                }
            }
        }
    }

    private fun setActiveCity(code: String) {
        cities.forEach { city -> city.isActive = city.cityCode == code }
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAllCityListViewHolder {
        return ViewAllCityListViewHolder(ItemCityListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewAllCityListViewHolder, position: Int) {
        holder.bind(cities[position])
    }

    override fun getItemCount(): Int = cities.size

    override fun getItemViewType(position: Int): Int = position
}