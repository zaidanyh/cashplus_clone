package com.pasukanlangit.cashplus.ui.onlinestore.image

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemImagePhotoViewBinding

class ImageAdapter(private val context: Context, private val items: List<String>): PagerAdapter() {

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getCount(): Int = items.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemImagePhotoViewBinding.inflate(LayoutInflater.from(context), container, false)

        Glide.with(context)
            .load(items[position])
            .error(R.drawable.ic_empty)
            .into(binding.ivPhotoView)
        container.addView(binding.root, 0)

        return binding.root
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}