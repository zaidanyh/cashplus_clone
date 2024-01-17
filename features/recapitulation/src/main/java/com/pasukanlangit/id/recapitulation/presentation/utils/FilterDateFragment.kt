package com.pasukanlangit.id.recapitulation.presentation.utils

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.recapitulation.R
import com.pasukanlangit.id.recapitulation.databinding.FragmentFilterDateBinding
import java.text.SimpleDateFormat
import java.util.*

class FilterDateFragment(
    private val event: SetOnSubmitListener
) : BottomSheetDialogFragment() {

    private var _binding: FragmentFilterDateBinding? = null
    private val binding get() = _binding!!

    private lateinit var sdfForShow: SimpleDateFormat
    private lateinit var now: List<String>
    private lateinit var lastWeek: List<String>
    private lateinit var twoWeekAgo: List<String>

    private var dateStartLimit: Date? = null
    private var dateEndLimit: Date? = null

    private var dateStartReq: String? = null
    private var dateEndReq: String? = null

    private val calendarStart = Calendar.getInstance()
    private val calendarEnd = Calendar.getInstance()
    private val dateFormatReq = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    private var isDateStart = true
    private var dayInitState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateValue = arguments?.parcelable<DateObjectClass>(ARG_DATE_FORMAT_OBJECT_CLASS)
        init(dateValue?.dateStart, dateValue?.dateEnd)
        with(binding) {
            wrapperFilterLastWeek.setOnClickListener {
                rbLastWeek.isChecked = true
                rb14LastDays.isChecked = false
                rbByDate.isChecked = false

                val dateStartLastWeek = sdfForShow.parse(lastWeek.first())
                val dateEndLastWeek = sdfForShow.parse(lastWeek.last())
                dateStartReq = dateFormatReq.format(dateStartLastWeek as Date).replace(" ", "")
                dateEndReq = dateFormatReq.format(dateEndLastWeek as Date).replace(" ", "")
                btnSubmit.isEnabled = true
            }
            wrapperFilter14LastDays.setOnClickListener {
                rb14LastDays.isChecked = true
                rbLastWeek.isChecked = false
                rbByDate.isChecked = false

                val dateStart14Days = sdfForShow.parse(twoWeekAgo.first())
                val dateEnd14Days = sdfForShow.parse(twoWeekAgo.last())
                dateStartReq = dateFormatReq.format(dateStart14Days as Date)
                dateEndReq = dateFormatReq.format(dateEnd14Days as Date)
                btnSubmit.isEnabled = true
            }
            rbByDate.setOnClickListener {
                rbByDate.isChecked = true
                rbLastWeek.isChecked = false
                rb14LastDays.isChecked = false

                if (isDateStart) {
                    openDatePicker()
                    return@setOnClickListener
                }
                openDatePicker(isDateTo = true)
            }
            setDateFrom.setOnClickListener {
                rbByDate.isChecked = true
                rbLastWeek.isChecked = false
                rb14LastDays.isChecked = false
                openDatePicker()
            }
            setDateTo.setOnClickListener {
                rbByDate.isChecked = true
                rbLastWeek.isChecked = false
                rb14LastDays.isChecked = false
                openDatePicker(isDateTo = true)
            }
            btnSubmit.setOnClickListener {
                val dateFromStr = tvDateFrom.text.toString()
                val dateToStr = tvDateTo.text.toString()
                val dateStart = if (rbByDate.isChecked) {
                    val dateStartParse = sdfForShow.parse(dateFromStr)
                    dateFormatReq.format(dateStartParse as Date)
                } else dateStartReq

                val dateEnd = if (rbByDate.isChecked) {
                    val dateEndParse = sdfForShow.parse(dateToStr)
                    dateFormatReq.format(dateEndParse as Date)
                } else dateEndReq

                val label = if (rbLastWeek.isChecked) getString(R.string.last_week).lowercase()
                else if (rb14LastDays.isChecked) getString(R.string.last_14_days).lowercase()
                else if (rbByDate.isChecked) {
                    if (dateFromStr == dateToStr) dateFromStr
                    else {
                        val sdfLabel = SimpleDateFormat("dd MMM", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
                        val dateFrom = sdfForShow.parse(dateFromStr)
                        "${sdfLabel.format(dateFrom as Date)} \u2014 $dateToStr"
                    }
                } else dateValue?.label.toString()

                event.onSubmitEvent(
                    date = DateObjectClass(
                        dateStart = dateStart, dateEnd = dateEnd,
                        label = label
                    )
                )
                dismiss()
            }
        }
    }

    private fun init(dateStart: String?, dateEnd: String?) {
        sdfForShow = SimpleDateFormat("dd MMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
        now = if (dateStart.isNullOrEmpty() && dateEnd.isNullOrEmpty()) {
            dayInitState = true
            filterByDate(1)
        }
        else {
            val dateStartFormat = dateFormatReq.parse(dateStart.toString())
            val dateEndFormat = dateFormatReq.parse(dateEnd.toString())
            val dateStartNew = sdfForShow.format(dateStartFormat as Date)
            val dateEndNew = sdfForShow.format(dateEndFormat as Date)
            calendarStart.time = dateStartFormat
            calendarEnd.time = dateEndFormat
            listOf(dateStartNew, dateEndNew)
        }

        lastWeek = filterByDate(7)
        twoWeekAgo = filterByDate(14)
        dateStartLimit = sdfForShow.parse(now.first().toString())
        dateEndLimit = sdfForShow.parse(now.last().toString())

        with(binding) {
            dateLastWeek.text = lastWeek.first()
            dateNow1.text = lastWeek.last()
            date14LastDays.text = twoWeekAgo.first()
            dateNow2.text = twoWeekAgo.last()
            tvDateFrom.text = now.first()
            tvDateTo.text = now.last()
        }
    }

    private fun filterByDate(countDay: Int) : List<String> {
        val startOfLastMonth = getDayTimeBefore(countDay)
        val endOfLastMonth = Date()

        return listOf(
            sdfForShow.format(startOfLastMonth),
            sdfForShow.format(endOfLastMonth)
        )
    }

    private fun getDayTimeBefore(countDay: Int): Long {
        var currentDateTime = Date().time
        for(i in 0 until countDay){
            currentDateTime -= 86400000
        }
        return currentDateTime
    }

    private fun openDatePicker(isDateTo: Boolean = false) {
        val year: Int
        val month: Int
        val day: Int
        if (isDateTo) {
            year = calendarEnd.get(Calendar.YEAR)
            month = calendarEnd.get(Calendar.MONTH)
            day = calendarEnd.get(Calendar.DAY_OF_MONTH)
        } else {
            year = calendarStart.get(Calendar.YEAR)
            month = calendarStart.get(Calendar.MONTH)
            day = if (dayInitState) calendarStart.get(Calendar.DAY_OF_MONTH)-1
            else calendarStart.get(Calendar.DAY_OF_MONTH)
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(), R.style.DatePickerCustom,
            { _, _, _, _ -> }, year, month, day
        )
        datePickerDialog.datePicker.maxDate = dateEndLimit!!.time
        if (isDateTo) {
            datePickerDialog.datePicker.minDate = dateStartLimit!!.time
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        }

        datePickerDialog.setButton(
            DialogInterface.BUTTON_POSITIVE,
            getString(R.string.choose)
        ) { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                val datePicker = datePickerDialog.datePicker

                val dayPicked = datePicker.dayOfMonth
                val monthPicked = datePicker.month
                val yearPicked = datePicker.year

                val formatForShow = if (isDateTo) {
                    calendarEnd.set(yearPicked, monthPicked, dayPicked)
                    sdfForShow.format(calendarEnd.time)
                } else {
                    calendarStart.set(yearPicked, monthPicked, dayPicked)
                    sdfForShow.format(calendarStart.time)
                }

                val sdfDummy = SimpleDateFormat("yyyy M d", Locale.getDefault())
                val formatting = "$yearPicked ${monthPicked+1} $dayPicked"
                if (isDateTo) {
                    dateEndLimit = sdfDummy.parse(formatting)
                    binding.tvDateTo.text = formatForShow
                    isDateStart = true
                } else {
                    dayInitState = false
                    dateStartLimit = sdfDummy.parse(formatting)
                    binding.tvDateFrom.text = formatForShow
                    isDateStart = false
                }
                binding.btnSubmit.isEnabled = true
            }
        }
        datePickerDialog.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.cancel)
        ) { _, which ->
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                if (!isDateTo) {
                    binding.rbByDate.isChecked = !dayInitState
                }
                datePickerDialog.dismiss()
            }
        }
        datePickerDialog.show()
    }

    interface SetOnSubmitListener {
        fun onSubmitEvent(date: DateObjectClass)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ARG_DATE_FORMAT_OBJECT_CLASS = "arg_date_format_object_class"

        @JvmStatic
        fun newInstance(
            date: DateObjectClass?, event: SetOnSubmitListener
        ) = FilterDateFragment(event).apply {
            arguments = Bundle().apply {
                putParcelable(ARG_DATE_FORMAT_OBJECT_CLASS, date)
            }
        }
    }
}