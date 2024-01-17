package com.pasukanlangit.id.ui_core.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.Button
import androidx.core.content.ContextCompat
import com.pasukanlangit.id.ui_core.R

@SuppressLint("AppCompatCustomView")
class ButtonCashGoldPrimaryView @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : Button(context, attrs) {

    init {
        init()
    }

    private fun init() {
        background = ContextCompat.getDrawable(context, R.drawable.selector_button_gold_primary)

        isAllCaps = false
        setTextColor(Color.WHITE)
    }
}