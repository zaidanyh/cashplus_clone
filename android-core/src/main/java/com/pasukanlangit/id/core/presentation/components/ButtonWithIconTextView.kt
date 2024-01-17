package com.pasukanlangit.id.core.presentation.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.pasukanlangit.id.core.R
import com.pasukanlangit.id.core.databinding.LayoutButtonIconTextBinding

@SuppressLint("CustomViewStyleable")
class ButtonWithIconTextView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutButtonIconTextBinding.inflate(LayoutInflater.from(context), this)

    init {
        init()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        orientation = VERTICAL
        setBackgroundResource(R.drawable.bg_blue50_rounded_12)
        elevation = 1f
        gravity = Gravity.CENTER
        isClickable = true
        isFocusable = true

        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.ButtonWithIconTextView)
        try {
            val text = typeArrayStyle.getString(R.styleable.ButtonWithIconTextView_android_text)
            val backgroundTint = typeArrayStyle.getColorStateList(R.styleable.ButtonWithIconTextView_android_backgroundTint)
            val drawableStart = typeArrayStyle.getDrawable(R.styleable.ButtonWithIconTextView_android_drawableStart)
            val drawableEnd = typeArrayStyle.getDrawable(R.styleable.ButtonWithIconTextView_android_drawableEnd)
            val textColor = typeArrayStyle.getColorStateList(R.styleable.ButtonWithIconTextView_android_textColor)

            with(binding){
                tvLabel.text = text
                tvLabel.setTextColor(textColor)
                tvLabel.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, drawableEnd, null)
                backgroundTintList = backgroundTint
            }


        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            typeArrayStyle.recycle()
        }
    }
}