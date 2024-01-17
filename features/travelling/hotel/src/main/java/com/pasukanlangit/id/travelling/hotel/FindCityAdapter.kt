package com.pasukanlangit.id.travelling.hotel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.travelling.domain.model.hotel.HotelCityList
import com.pasukanlangit.id.travelling.hotel.databinding.ItemHotelCityListBinding

class FindCityAdapter(
    private val cities: List<HotelCityList>,
    private val listener: (HotelCityList) -> Unit
): RecyclerView.Adapter<FindCityAdapter.FindCityViewHolder>() {

    inner class FindCityViewHolder(val binding: ItemHotelCityListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(city: HotelCityList) {
            with(binding) {
                tvCityName.text = city.hotelCityName
                root.setOnClickListener { listener(city) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindCityViewHolder {
        return FindCityViewHolder(ItemHotelCityListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FindCityViewHolder, position: Int) = holder.bind(cities[position])

    override fun getItemCount(): Int = cities.size
}