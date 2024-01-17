package com.pasukanlangit.id.rebate.component

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.pasukanlangit.id.core.presentation.BaseDatePickerInputView
import com.pasukanlangit.id.rebate.R
import com.pasukanlangit.id.rebate.databinding.LayoutDatePickerWithLabelBinding


@SuppressLint("CustomViewStyleable")
class DatePickerWithLabel @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): BaseDatePickerInputView(context, attrs, defStyleAttr) {

    private val binding = LayoutDatePickerWithLabelBinding.inflate(LayoutInflater.from(context), this)

    init {
        init()
    }

    override fun getValue(): String = formatValue(binding.tvDate.text.toString())

    private fun init() {
        bindEventDateListener(
            triggerView = binding.tvDate,
            outputTextView = binding.tvDate
        )
        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.DatePickerWithLabel)

        try {
            val label = typeArrayStyle.getString(R.styleable.DatePickerWithLabel_label)
            binding.tvLabel.text = label
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            typeArrayStyle.recycle()
        }
    }

}