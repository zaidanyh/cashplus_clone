package com.pasukanlangit.cashplus.ui.onlinestore.image

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pasukanlangit.cashplus.databinding.ActivityImagePhotoViewBinding

class ImagePhotoViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImagePhotoViewBinding

    private var item: List<String>? = null
    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        item = intent.getStringArrayListExtra(ITEM_IMAGE_KEY)
        position = intent.getIntExtra(ITEM_POSITION_KEY, 0)

        if (item != null) {
            val adapter = ImageAdapter(this, item as ArrayList<String>)
            with(binding) {
                btnClose.setOnClickListener { finish() }
                viewPagerPhotoView.adapter = adapter
                viewPagerPhotoView.currentItem = if (position != -1) position else 0
            }
        }
    }

    companion object {
        const val ITEM_IMAGE_KEY = "item_image_key"
        const val ITEM_POSITION_KEY = "item_position_key"
    }
}