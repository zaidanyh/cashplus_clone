package com.pasukanlangit.cashplus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemBannerHomeBinding
import com.pasukanlangit.cashplus.ui.pages.home.BannerDummy

class GoToMakkahBannerAdapter(private val listBanner : List<BannerDummy>, val listener : (BannerDummy, Array<Pair<View, String>>) -> Unit): RecyclerView.Adapter<GoToMakkahBannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(val binding: ItemBannerHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bannerDummy: BannerDummy){
            with(binding) {
                imageItemBanner.setImageResource(bannerDummy.image)
                titleBanner.text = bannerDummy.titleBanner

                val animationView = arrayOf<Pair<View, String>>(
                    Pair(imageItemBanner, "image"),
                    Pair(titleBanner, "title")
                )
                root.setOnClickListener {
                    listener(bannerDummy, animationView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(ItemBannerHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = Integer.MAX_VALUE


    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(listBanner[position % listBanner.size])
    }
}