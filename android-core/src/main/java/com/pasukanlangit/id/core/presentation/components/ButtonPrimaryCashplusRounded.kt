package com.pasukanlangit.id.core.presentation.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.Button
import androidx.core.content.ContextCompat
import com.pasukanlangit.id.core.R
import androidx.core.content.res.ResourcesCompat
import android.view.ViewGroup


@SuppressLint("AppCompatCustomView")
class ButtonPrimaryCashplusRounded @JvmOverloads
constructor(
    context: Context,
    val attrs: AttributeSet? = null,
) : Button(context, attrs) {

    init {
        init()
    }

    private fun init() {
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            48
        )

        layoutParams = params
        background = ContextCompat.getDrawable(context, R.drawable.selector_button_primary_cashplus_rounded)

        val textColor = attrs?.getAttributeValue("http://schemas.android.com/apk/res/android", "textColor")
        setTextColor(
            if(textColor != null) Color.parseColor(textColor) else Color.WHITE
        )

        isAllCaps = false

        setFont()
//        setTextColor(Color.WHITE)
    }

    private fun setFont() {
        val typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
        setTypeface(typeface)
    }
}