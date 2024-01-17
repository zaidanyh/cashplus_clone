package com.pasukanlangit.cashplus.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemHotelHomeBinding
import com.pasukanlangit.cashplus.model.response.HotelListDataItem
import com.pasukanlangit.id.core.*
import com.pasukanlangit.id.travelling.hotel.temp.CitySelected
import com.pasukanlangit.id.travelling.hotel.temp.RoomVisitor

class HomeHotelAdapter(private val listHotel: List<HotelListDataItem>) : RecyclerView.Adapter<HomeHotelAdapter.HotelViewHolder>() {

    class HotelViewHolder(private val binding: ItemHotelHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hotelModel: HotelListDataItem){
            with(binding) {
                val context = root.context
                Glide.with(context)
                    .load(hotelModel.hotelImageUrl1?.ifEmpty { hotelModel.hotelImageUrl2 })
                    .thumbnail(
                        Glide.with(context)
                            .load(R.raw.image_loading_state)
                    )
                    .error(R.drawable.ic_empty)
                    .into(ivProdukItem)

                tvTitleItemHotel.text = hotelModel.hotelName
                tvLocation.text = hotelModel.hotelCityName

                root.setOnClickListener {
                    context.startActivity(
                        ModuleRoute.internalIntent(context, DETAIL_HOTEL_PATH).apply {
                            putExtra(HOTEL_CITY_SELECTED, CitySelected(hotelModel.hotelCityName, hotelModel.hotelCityCode))
                            putExtra(HOTEL_ROOM_VISITOR, RoomVisitor(1, 1, 0))
                            putExtra(HOTEL_CODE, hotelModel.hotelCode)
                            putExtra(IS_DESTINATION_EDIT, true)
                        }
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        return HotelViewHolder(ItemHotelHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = Integer.MAX_VALUE

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        holder.bind(listHotel[position % listHotel.size])
    }
}