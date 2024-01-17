package com.pasukanlangit.id.ui_downline_mutasi_summary_detail.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.pasukanlangit.id.core.presentation.BaseDatePickerInputView
import com.pasukanlangit.id.ui_downline_mutasi_summary_detail.R
import com.pasukanlangit.id.ui_downline_mutasi_summary_detail.databinding.LayoutDatepickerInputSummaryBinding


class DatePickerInputViewSummaryView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): BaseDatePickerInputView(context, attrs, defStyleAttr) {

    private val binding= LayoutDatepickerInputSummaryBinding.inflate(
        LayoutInflater.from(context), this)

    init {
        init()
    }

    private fun init(){
        bindEventDateListener(
            triggerView = binding.inputDate,
            outputTextView = binding.inputDate
        )
        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.DatePickerInputViewSummaryView)
        try {
            val label = typeArrayStyle.getString(R.styleable.DatePickerInputViewSummaryView_label)
            binding.tvLabel.text = label
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            typeArrayStyle.recycle()
        }
    }

    override fun getValue(): String  = formatValue(
        input = binding.inputDate.text.toString()
    )
}