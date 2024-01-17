package com.pasukanlangit.id.ui_core.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.pasukanlangit.id.ui_core.R
import com.pasukanlangit.id.ui_core.databinding.CardGrayTitleDescriptionBinding
import com.pasukanlangit.id.core.utils.getRawDimensionInDp

@SuppressLint("CustomViewStyleable")
class CardGrayTitleDescriptionView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    private val binding = CardGrayTitleDescriptionBinding.inflate(LayoutInflater.from(context), this)

    init {
        init()
    }

    private fun init() {
        orientation = VERTICAL
        val paddingValue = context.resources.getRawDimensionInDp(R.dimen.dimen_24)
        setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
        background = ContextCompat.getDrawable(context, R.drawable.bg_gray_rounded)

        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.CardGrayTitleDescriptionView)

        try {
            val label = typeArrayStyle.getString(R.styleable.CardGrayTitleDescriptionView_title)
            val price = typeArrayStyle.getString(R.styleable.CardGrayTitleDescriptionView_description)

            with(binding){
                title.text = label
                description.text = price
            }

        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            typeArrayStyle.recycle()
        }
    }
}