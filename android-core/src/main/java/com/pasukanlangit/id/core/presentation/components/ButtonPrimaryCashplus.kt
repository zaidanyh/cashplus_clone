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
class ButtonPrimaryCashplus @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : Button(context, attrs) {

    init {
        init()
    }

    private fun init() {
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            R.dimen.dimen_42
        )

        layoutParams = params
        background = ContextCompat.getDrawable(context, R.drawable.bg_primary_rounded_12)

        isAllCaps = false

        setFont()
        setTextColor(Color.WHITE)
    }

    private fun setFont() {
        val typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
        setTypeface(typeface)
    }
}