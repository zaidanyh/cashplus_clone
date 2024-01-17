package com.pasukanlangit.id.core.presentation

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.pasukanlangit.id.core.R
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.*

/**
 * note when implement this class view parent must [ConstraintLayout]
 **/
abstract class BaseDatePickerInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr){

    private lateinit var datePickerDialog: DatePickerDialog
    private val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val sdfOutput = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    private var calender: Calendar = Calendar.getInstance()
    private var currentDay: Int = calender.get(Calendar.DAY_OF_MONTH)
    private var currentMonth: Int = calender.get(Calendar.MONTH)
    private var currentYear: Int = calender.get(Calendar.YEAR)
    protected var selectedDate = MutableStateFlow<String>(sdf.format(Date()))

    init {
        initView()
        setUpCalender()
    }

    private fun initView() {
        setBackgroundColor(Color.TRANSPARENT)
    }

    private fun setUpCalender() {
        datePickerDialog = DatePickerDialog(
            context, R.style.DatePickerCustom,
            null, currentYear, currentMonth, currentDay
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    }

    protected fun formatValue(input: String): String{
        val currentDate = sdf.parse(input)
        return sdfOutput.format(currentDate ?: Date()).toString()
    }

    /**
     * Call this on your block init when implement [BaseDatePickerInputView] class.
     *
     * this function is to show dialog date picker when the [triggerView] is onClick, and init default date value (current date) to [outputTextView]
     *
     * @param triggerView is view to trigger popup dialog date picker
     * @param outputTextView is TextView to set text result of dialog date picker
     */
    protected fun bindEventDateListener(triggerView: View, outputTextView: TextView){
        initDefaultDate(outputTextView)

        triggerView.setOnClickListener {
            datePickerDialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                "Pilih"
            ) { _, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    val datepicker = datePickerDialog.datePicker

                    currentDay = datepicker.dayOfMonth
                    currentMonth = datepicker.month
                    currentYear = datepicker.year

                    calender.set(currentYear, currentMonth, currentDay)
                    datePickerDialog.updateDate(currentYear,currentMonth, currentDay)

                    val formatDate = sdf.format(calender.time)
                    selectedDate.value = formatDate
                    outputTextView.text = formatDate
                }
            }
            datePickerDialog.show()
        }
    }

    private fun initDefaultDate(outputTextView: TextView) {
        outputTextView.text = sdf.format(Date())
    }

    /**
     * Get value from input edittext and the implementation should call [BaseDatePickerInputView.formatValue]
     * */
    abstract fun getValue(): String
}