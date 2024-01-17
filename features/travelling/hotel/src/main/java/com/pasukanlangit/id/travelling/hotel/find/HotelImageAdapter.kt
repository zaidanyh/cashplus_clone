package com.pasukanlangit.id.travelling.hotel.find

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pasukanlangit.id.travelling.hotel.R
import com.pasukanlangit.id.travelling.hotel.databinding.ItemImageHotelsBinding

class HotelImageAdapter: RecyclerView.Adapter<HotelImageAdapter.HotelImageViewHolder>() {

    private val images = ArrayList<String>()
    private lateinit var onItemClickListener: OnItemClickListener
    private var imagePosition: String? = null

    fun setImages(images: List<String>?) {
        if (images == null) return
        this.images.clear()
        this.images.addAll(images)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class HotelImageViewHolder(val binding: ItemImageHotelsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            with(binding) {
                Glide.with(root.context)
                    .load(image)
                    .thumbnail(
                        Glide.with(root.context)
                            .load(R.raw.image_loading_state)
                    )
                    .error(R.drawable.ic_empty)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(imageHotel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelImageViewHolder {
        return HotelImageViewHolder(ItemImageHotelsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HotelImageViewHolder, position: Int) {
        holder.bind(images[position])
        holder.binding.root.setOnClickListener {
            onItemClickListener.onItemClicked(images[holder.bindingAdapterPosition])
            setSelectedImage(images[position])
        }
        if (imagePosition == images[position]) {
            holder.binding.wrapperImage.setBackgroundResource(R.drawable.bg_transparent_border_primary_rounded_12)
        } else {
            holder.binding.wrapperImage.setBackgroundResource(R.drawable.bg_transparent_border_slate200_rounded_12)
        }
    }

    fun setSelectedImage(position: String) {
        imagePosition = position
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = images.size

    interface OnItemClickListener {
        fun onItemClicked(item: String)
    }
}