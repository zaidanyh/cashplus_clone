package com.pasukanlangit.id.ui_core.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.pasukanlangit.id.ui_core.R
import com.pasukanlangit.id.ui_core.databinding.AppbarCashgoldBinding
import com.pasukanlangit.id.ui_core.utils.getPrimaryColor

//class CoinAppBar {
//}

@SuppressLint("CustomViewStyleable")
class CashGoldAppBarView @JvmOverloads constructor(
    context: Context,
    val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        init()
    }
    private var isHideButtonBack: Boolean = false
    private val binding = AppbarCashgoldBinding.inflate(LayoutInflater.from(context), this)
    private val btnBack get() = binding.btnBack

    fun setIconBackPressed(event: () -> Unit){
        if(isHideButtonBack) return
        btnBack.setOnClickListener { event() }
    }

    private fun init() {
        setBackgroundColor(context.getPrimaryColor())
        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.CashGoldAppBarView)
        isHideButtonBack = typeArrayStyle.getBoolean(R.styleable.CashGoldAppBarView_hideButtonBack, false)

        try {
            binding.btnBack.isVisible = !isHideButtonBack
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            typeArrayStyle.recycle()
        }
    }
}