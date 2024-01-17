package com.pasukanlangit.id.ui_core.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.pasukanlangit.id.ui_core.R
import com.pasukanlangit.id.ui_core.databinding.CoinWithLabelBinding
import android.view.LayoutInflater


@SuppressLint("CustomViewStyleable")
class CoinWithLabelView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding = CoinWithLabelBinding.inflate(LayoutInflater.from(context), this)

    init {
        init()
    }

    fun setValue(value: String){
        binding.tvValue.text = value
    }

    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.CoinWithLabelView)

        try {
            val coinValue = typeArrayStyle.getString(R.styleable.CoinWithLabelView_coinValue)
            binding.tvValue.text = coinValue
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            typeArrayStyle.recycle()
        }
    }
}