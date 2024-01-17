package com.pasukanlangit.cashplus.ui.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.cashplus.databinding.ItemOnboardingBinding

class OnBoardingAdapter(private val context: Context, private val pageData : List<OnBoardingModel>) : PagerAdapter() {


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getCount(): Int = pageData.size

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val binding = ItemOnboardingBinding.inflate(LayoutInflater.from(context), container, false)
        //may this below cause memory leak in android 7. check crashlytics firebase for detail
//        binding.ivOnboarding.setImageResource(pageData[position].image)

        //try change load with bitmap
        Glide.with(context)
            .asBitmap()
            .load(pageData[position].image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.ivOnboarding)

        container.addView(binding.root,0)
        return binding.root
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}