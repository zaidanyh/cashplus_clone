package com.pasukanlangit.id.ui_cashgold_buy.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View

import android.widget.TextView

import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.pasukanlangit.id.ui_cashgold_buy.R


@SuppressLint("ViewConstructor")
class CustomMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById<View>(R.id.tv_value) as TextView
    private var mOffset: MPPointF? = null


    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Suppress("DEPRECATION")
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tvContent.text = if (Build.VERSION.SDK_INT >= 24)
            Html.fromHtml("${e?.data}", Html.FROM_HTML_MODE_LEGACY)
        else Html.fromHtml("${e?.data}")

    // set the entry-value as the display text
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
        }
        return mOffset as MPPointF
    }
}