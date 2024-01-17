package com.pasukanlangit.id.core.presentation

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.appeaser.sublimepickerlibrary.SublimePicker
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker
import com.pasukanlangit.id.core.R
import com.pasukanlangit.id.core.utils.parcelable
import java.text.DateFormat
import java.util.*

class SublimePickerDialogFragment : DialogFragment() {
    var mDateFormatter: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    private var mTimeFormatter: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
    private var mSublimePicker: SublimePicker? = null
    var mCallback: Callback? = null

    init {
        mTimeFormatter.timeZone = TimeZone.getTimeZone("GMT+0")
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if(isShown) return
        super.show(manager, tag)
        isShown = true
    }
    private var mListener: SublimeListenerAdapter = object : SublimeListenerAdapter() {
        override fun onCancelled() {
            if (mCallback != null) {
                mCallback!!.onCancelled()
            }
            dismiss()
        }


        override fun onDateTimeRecurrenceSet(
            sublimeMaterialPicker: SublimePicker?,
            selectedDate: SelectedDate?,
            hourOfDay: Int,
            minute: Int,
            recurrenceOption: SublimeRecurrencePicker.RecurrenceOption?,
            recurrenceRule: String?
        ) {
            if (mCallback != null) {
                mCallback!!.onDateTimeRecurrenceSet(
                    selectedDate,
                    hourOfDay, minute, recurrenceOption, recurrenceRule
                )
            }
            dismiss()
        }
    }

    fun setCallback(callback: Callback?) {
        mCallback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mSublimePicker = activity?.layoutInflater?.inflate(R.layout.sublime_picker, container) as SublimePicker
        val arguments: Bundle? = arguments
        var options: SublimeOptions? = null
        if (arguments != null) {
            options = arguments.parcelable("SUBLIME_OPTIONS")
        }
        mSublimePicker?.initializePicker(options, mListener)
        return mSublimePicker
    }

    override fun onDismiss(dialog: DialogInterface) {
        isShown = false
        super.onDismiss(dialog)
    }
    interface Callback {
        fun onCancelled()
        fun onDateTimeRecurrenceSet(
            selectedDate: SelectedDate?,
            hourOfDay: Int, minute: Int,
            recurrenceOption: SublimeRecurrencePicker.RecurrenceOption?,
            recurrenceRule: String?
        )
    }

    companion object {
        var isShown: Boolean = false
    }
}


