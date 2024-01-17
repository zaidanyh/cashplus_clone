package com.pasukanlangit.cashplus.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemBannerListBinding
import com.pasukanlangit.cashplus.ui.pages.home.BannerDummy

class BannerAdapterList(private val listBanner : List<BannerDummy>, val listener: (BannerDummy) -> Unit) : RecyclerView.Adapter<BannerAdapterList.BannerViewHolder>() {
    inner class BannerViewHolder(val binding: ItemBannerListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bannerDummy: BannerDummy){
            with(binding) {
                imgItemBannerList.setImageResource(bannerDummy.image)
                tvTitleBanner.text = bannerDummy.titleBanner
                tvDescriptionBanner.text = bannerDummy.descriptionShort

                root.setOnClickListener { listener(bannerDummy) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
       return BannerViewHolder(ItemBannerListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = listBanner.size


    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(listBanner[position])
    }

}