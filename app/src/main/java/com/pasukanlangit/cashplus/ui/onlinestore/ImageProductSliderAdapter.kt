package com.pasukanlangit.cashplus.ui.onlinestore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ImageSlideBinding

class ImageProductSliderAdapter(
    private val context: Context,
    private val images: ArrayList<String>
    ) : PagerAdapter() {

    private lateinit var onImageClickListener: OnImageClickListener

    fun setOnImageClickListener(onImageClickListener: OnImageClickListener) {
        this.onImageClickListener = onImageClickListener
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getCount(): Int = images.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ImageSlideBinding.inflate(LayoutInflater.from(context), container, false)

        Glide.with(context)
            .load(images[position])
            .error(R.drawable.ic_empty)
            .into(binding.ivProductDetail)
        container.addView(binding.root, 0)
        binding.ivProductDetail.setOnClickListener {
            onImageClickListener.onImageClicked(images, position)
        }
        return binding.root
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    interface OnImageClickListener {
        fun onImageClicked(item: ArrayList<String>, position: Int)
    }
}