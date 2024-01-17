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
class ButtonSecondaryCashplusRounded @JvmOverloads
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
        background = ContextCompat.getDrawable(context, R.drawable.bg_blue50_rounded_16)

        isAllCaps = false

        setFont()
        setTextColor(Color.parseColor("#0A57FF"))
    }

    private fun setFont() {
        val typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
        setTypeface(typeface)
    }
}