package com.pasukanlangit.id.ui_downline_transfersaldo.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasukanlangit.id.core.utils.InputUtil.toCapitalize
import com.pasukanlangit.id.domain_downline.model.DownLineItem
import com.pasukanlangit.id.ui_downline_transfersaldo.databinding.ItemListPhoneDownlineBinding

class DownlinePagingAdapter(
    private val onClick: (DownLineItem) -> Unit
): PagingDataAdapter<DownLineItem, DownlinePagingAdapter.DownlineViewHolder>(DIFF_UTIL) {

    inner class DownlineViewHolder(val binding: ItemListPhoneDownlineBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(downLineItem: DownLineItem?) {
            with(binding) {
                downLineItem?.let { item ->
                    val imageUrl = item.img_url.ifEmpty {
                        "https://ui-avatars.com/api/?name=${item.name}&size=300&rounded=true&background=0A57FF&color=ffffff&bold=true"
                    }
                    Glide.with(root.context)
                        .load(imageUrl)
                        .circleCrop()
                        .into(imgDownline)
                    tvNameDownline.text = item.name.toCapitalize()
                    tvPhoneDownline.text = item.phoneActive

                    root.setOnClickListener {
                        onClick(item)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: DownlineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownlineViewHolder {
        return DownlineViewHolder(ItemListPhoneDownlineBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<DownLineItem>() {
            override fun areItemsTheSame(
                oldItem: DownLineItem,
                newItem: DownLineItem
            ): Boolean {
                return oldItem.phoneActive == newItem.phoneActive
            }

            override fun areContentsTheSame(
                oldItem: DownLineItem,
                newItem: DownLineItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}