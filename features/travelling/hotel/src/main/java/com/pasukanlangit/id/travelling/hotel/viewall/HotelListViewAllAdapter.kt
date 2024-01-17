package com.pasukanlangit.id.travelling.hotel.viewall

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.travelling.domain.model.hotel.Hotels
import com.pasukanlangit.id.travelling.hotel.R
import com.pasukanlangit.id.travelling.hotel.databinding.ItemHotelListBinding

class HotelListViewAllAdapter(
    private val hotels: List<Hotels>,
    private val listener: (Hotels) -> Unit
): RecyclerView.Adapter<HotelListViewAllAdapter.HotelListViewAllViewHolder>() {

    inner class HotelListViewAllViewHolder(val binding: ItemHotelListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(hotel: Hotels) {
            with(binding) {
                val context = root.context
                Glide.with(context)
                    .load(hotel.images?.first() ?: hotel.images?.last())
                    .thumbnail(
                        Glide.with(root.context)
                            .load(R.raw.image_loading_state)
                    )
                    .error(R.drawable.ic_empty)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(imgHotel)
                tvNameHotel.text = hotel.name
                ratingHotel.rating = hotel.rating?.toFloatOrNull() ?: (0.0).toFloat()
                tvHotelAddress.text = context.getString(R.string.hotel_address_format, hotel.address, hotel.city, hotel.country)
                tvHotelPrice.text = getNumberRupiah(hotel.room?.first()?.price ?: hotel.room?.last()?.price)
                root.isEnabled = (hotel.room?.first()?.price ?: hotel.room?.last()?.price) != null
                bgIsEnabled.isVisible = (hotel.room?.first()?.price ?: hotel.room?.last()?.price) == null
                root.setOnClickListener { listener(hotel) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelListViewAllViewHolder {
        return HotelListViewAllViewHolder(ItemHotelListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HotelListViewAllViewHolder, position: Int) {
        holder.bind(hotels[position])
    }

    override fun getItemCount(): Int = hotels.size
}