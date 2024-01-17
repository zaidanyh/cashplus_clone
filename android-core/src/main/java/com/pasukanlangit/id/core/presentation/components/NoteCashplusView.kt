package com.pasukanlangit.id.core.presentation.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.pasukanlangit.id.core.R
import com.pasukanlangit.id.core.databinding.NoteCashplusViewBinding
import com.pasukanlangit.id.core.utils.getRawDimensionInDp

@SuppressLint("CustomViewStyleable")
class NoteCashplusView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    private val binding = NoteCashplusViewBinding.inflate(LayoutInflater.from(context), this)
    init {
        init()
    }

    fun setOnButtonClicked(func: () -> Unit){
        binding.btnAction.setOnClickListener {
            func()
        }
    }

    private fun init() {
        orientation = VERTICAL
        val paddingValue = context.resources.getRawDimensionInDp(R.dimen.dimen_16)
        setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
        background = ContextCompat.getDrawable(context, R.drawable.bg_blue50_rounded_6)

        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.NoteCashplusView)

        try {
            val title = typeArrayStyle.getString(R.styleable.NoteCashplusView_title)
            val description = typeArrayStyle.getString(R.styleable.NoteCashplusView_description)
            val btnActionLabel = typeArrayStyle.getString(R.styleable.NoteCashplusView_button_action_title)

            with(binding){
                tvTitle.text = title
                tvDescription.text = description

                btnAction.isVisible = btnActionLabel != null
                btnAction.text = btnActionLabel
            }

        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            typeArrayStyle.recycle()
        }
    }
}