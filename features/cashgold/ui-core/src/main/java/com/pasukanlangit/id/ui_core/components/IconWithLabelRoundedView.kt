package com.pasukanlangit.id.ui_core.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.pasukanlangit.id.ui_core.R
import com.pasukanlangit.id.ui_core.databinding.CardLogoWithLabelBinding
import com.pasukanlangit.id.core.utils.getRawDimensionInDp


@SuppressLint("CustomViewStyleable")
class IconWithLabelRoundedView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    init {
        init()
    }

    private fun init() {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        val paddingValue = context.resources.getRawDimensionInDp(R.dimen.dimen_16)
        setPadding(paddingValue, paddingValue, paddingValue, paddingValue)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = CardLogoWithLabelBinding.inflate(inflater, this)
        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.IconWithLabelRoundedView)

        try {
            val label = typeArrayStyle.getString(R.styleable.IconWithLabelRoundedView_label)
            val imageIcon = typeArrayStyle.getDrawable(R.styleable.IconWithLabelRoundedView_icon)
            val background = typeArrayStyle.getDrawable(R.styleable.IconWithLabelRoundedView_backgroundRounded)

            with(binding){
                root.background = background
                icon.setImageDrawable(imageIcon)
                tvLabel.text = label
            }
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            typeArrayStyle.recycle()
        }
    }
}