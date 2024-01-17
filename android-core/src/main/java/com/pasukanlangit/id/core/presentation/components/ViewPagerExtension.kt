package com.pasukanlangit.id.core.presentation.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.*
import androidx.viewpager.widget.ViewPager

class ViewPagerExtension @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): ViewPager(context, attrs) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.onTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }
}