package com.pasukanlangit.id.core.presentation.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.pasukanlangit.id.core.R

@SuppressLint("AppCompatCustomView")
class ButtonBorderPrimaryCashplus @JvmOverloads
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
            48
        )

        layoutParams = params
        background = ContextCompat.getDrawable(context, R.drawable.bg_transparent_border_primary_rounded_16)

        isAllCaps = false

        setFont()
        setTextColor(Color.parseColor("#0A57FF"))
    }

    private fun setFont() {
        setTextSize(TypedValue.COMPLEX_UNIT_SP,14f)
        val typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
        setTypeface(typeface)
    }
}