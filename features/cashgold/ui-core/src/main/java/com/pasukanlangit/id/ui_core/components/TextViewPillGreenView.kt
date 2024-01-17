package com.pasukanlangit.id.ui_core.components

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.pasukanlangit.id.ui_core.R

class TextViewPillGreenView @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : AppCompatTextView(context, attrs) {

    init {
        init()
    }

    private fun init() {
        val paddingVertical = context.resources.getDimensionPixelSize(R.dimen.dimen_4)
        val paddingHorizontal = context.resources.getDimensionPixelSize(R.dimen.dimen_20)

        background = ContextCompat.getDrawable(context, R.drawable.green_pill_rounded)
        setPadding(paddingHorizontal,paddingVertical, paddingHorizontal, paddingVertical)
        setTextColor(Color.WHITE)
    }
}