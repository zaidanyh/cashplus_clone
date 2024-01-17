package com.pasukanlangit.id.travelling.hotel.find

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.travelling.domain.model.hotel.RoomHotel
import com.pasukanlangit.id.travelling.hotel.R
import com.pasukanlangit.id.travelling.hotel.databinding.ItemFacilityBinding
import com.pasukanlangit.id.travelling.hotel.databinding.ItemHotelsRoomBinding
import com.pasukanlangit.id.travelling.hotel.find.util.FacilitiesUtils

class RoomHotelAdapter: ListAdapter<RoomHotel, RoomHotelAdapter.RoomHotelViewHolder>(DIFF_UTIL) {

    private lateinit var onButtonClickListener: OnButtonClickListener

    fun setOnClickListener(onButtonClickListener: OnButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener
    }

    inner class RoomHotelViewHolder(val binding: ItemHotelsRoomBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomHotelViewHolder {
        return RoomHotelViewHolder(ItemHotelsRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RoomHotelViewHolder, position: Int) {
        val room = getItem(position)
        with(holder.binding) {
            val context = root.context
            Glide.with(context)
                .load(room.image)
                .thumbnail(
                    Glide.with(root.context)
                        .load(R.raw.image_loading_state)
                )
                .error(R.drawable.ic_empty)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(imageHotelRoom)
            tvHotelName.text = room.name
            tvHotelPrice.text = getNumberRupiah(room.price)
            addLayout(context, room.boardName, holder.binding)
            btnOrder.setOnClickListener {
                onButtonClickListener.onOrderRoomListener(getItem(holder.bindingAdapterPosition))
            }
        }
    }

    private fun addLayout(context: Context, facilities: String, binding: ItemHotelsRoomBinding) {
        val facility = facilities.split(",")
        with(binding) {
            wrapperFacility.removeAllViews()
            val facilitiesLocal = FacilitiesUtils.getFacilities(context)
            for (i in facility.indices) {
                val view: ItemFacilityBinding = ItemFacilityBinding.inflate(LayoutInflater.from(context))
                val isFind = facilitiesLocal.find { local -> local.value == facility[i] }
                if (isFind != null) {
                    with(view.tvFacility) {
                        text = isFind.value
                        setCompoundDrawablesWithIntrinsicBounds(isFind.icon, 0, 0, 0)
                    }
                    wrapperFacility.addView(view.root.rootView)
                }
            }
        }
    }

    interface OnButtonClickListener {
        fun onOrderRoomListener(data: RoomHotel)
    }

    companion object {
        val DIFF_UTIL = object: DiffUtil.ItemCallback<RoomHotel>() {
            override fun areItemsTheSame(
                oldItem: RoomHotel,
                newItem: RoomHotel
            ): Boolean = oldItem.rateKey == newItem.rateKey

            override fun areContentsTheSame(
                oldItem: RoomHotel,
                newItem: RoomHotel
            ): Boolean = oldItem == newItem
        }
    }
}