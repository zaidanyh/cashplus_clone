package com.pasukanlangit.id.core.presentation.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.pasukanlangit.id.core.R
import com.pasukanlangit.id.core.databinding.LayoutAppbarWithBackBinding


@SuppressLint("CustomViewStyleable")
class AppBarWithIconBackView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutAppbarWithBackBinding.inflate(LayoutInflater.from(context), this)

    init {
        init()
    }

    fun setOnBackPressed(onBackPressed: () -> Unit){
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    fun setTitle(title: String){
        binding.tvTitle.text = title
    }

    private fun init() {
        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.AppBarWithIconBackView)

        val paddingHorizontal = context.resources.getDimensionPixelSize(R.dimen.dimen_16)
        val paddingVertical = context.resources.getDimensionPixelSize(R.dimen.dimen_8)
        setBackgroundColor(Color.parseColor("#0A57FF"))
        setPadding(paddingHorizontal,paddingVertical, paddingHorizontal, paddingVertical)

        try {
            val title = typeArrayStyle.getString(R.styleable.AppBarWithIconBackView_title)
            val showBackButton = typeArrayStyle.getBoolean(R.styleable.AppBarWithIconBackView_showBackButton, true)

            with(binding){
                btnBack.isVisible = showBackButton
                tvTitle.text = title
            }

        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            typeArrayStyle.recycle()
        }
    }
}