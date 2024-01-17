package com.pasukanlangit.id.ui_core.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.pasukanlangit.id.ui_core.R
import com.pasukanlangit.id.ui_core.databinding.CardInfoSellBuyBinding

@SuppressLint("CustomViewStyleable")
class CardInfoPriceGoldView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = CardInfoSellBuyBinding.inflate(LayoutInflater.from(context), this)

    init {
        init()
    }

    fun setPrice(value: String){
        binding.tvPrice.text = value
    }

    private fun init() {
        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.CardInfoPriceGoldView)

        val padding = context.resources.getDimensionPixelSize(R.dimen.dimen_16)
        background = ContextCompat.getDrawable(context, R.drawable.bg_white_rounded_4)
        setPadding(padding)

        try {
            val label = typeArrayStyle.getString(R.styleable.CardInfoPriceGoldView_label)
//            val returnValue = typeArrayStyle.getString(R.styleable.CardInfoPriceGoldView_returnValue)
            val price = typeArrayStyle.getString(R.styleable.CardInfoPriceGoldView_price)

            with(binding){
                tvLabel.text = label
//                tvReturnValue.text = returnValue
                tvPrice.text = price
            }

        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            typeArrayStyle.recycle()
        }
    }
}