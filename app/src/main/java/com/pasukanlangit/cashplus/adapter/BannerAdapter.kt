package com.pasukanlangit.cashplus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.pasukanlangit.cashplus.databinding.ItemBannerHomeBinding
import com.pasukanlangit.cashplus.ui.pages.home.BannerDummy

class BannerAdapter(
    val listener : (BannerDummy, Array<Pair<View, String>>) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    private var listBanner = ArrayList<BannerDummy>()
    private lateinit var viewPager2 : ViewPager2

    fun setDataBanner(listBanner: List<BannerDummy>?, viewPager2: ViewPager2) {
        if (listBanner == null) return
        this.listBanner.addAll(listBanner)
        this.viewPager2 = viewPager2
    }

    inner class BannerViewHolder(val binding: ItemBannerHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bannerDummy: BannerDummy) {
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

    override fun getItemCount(): Int = listBanner.size

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(listBanner[position])
        if (position == listBanner.size - 2) {
            viewPager2.post(runnable)
        }
    }

    private val runnable = Runnable {
        listBanner.addAll(listBanner)
        notifyDataSetChanged()
    }
}