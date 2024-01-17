package com.pasukanlangit.id.nobu.presentation.history

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.FragmentFilterByDateBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*

class FilterByDateFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFilterByDateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryNobuViewModel by sharedViewModel()

    private lateinit var now: List<String>
    private lateinit var lastWeek: List<String>
    private lateinit var lastMonth: List<String>
    private lateinit var lastQuarter: List<String>
    private lateinit var sdfForShow: SimpleDateFormat

    private val calendarStart = Calendar.getInstance()
    private val calendarEnd = Calendar.getInstance()
    private val dateFormatRequest = SimpleDateFormat("yyyy MM dd", Locale.getDefault())

    // for limit custom filter
    private var dateStartLimitMillis: Date? = null
    private var dateEndLimitMillis: Date? = null

    // for request to API
    private var dateStartReq: String? = null
    private var dateEndReq: String? = null

    private var filterTitle: String? = null
    private var stateDayInit = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterByDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        with(binding) {
            wrapperFilterLastWeek.setOnClickListener {
                rbLastWeek.isChecked = true
                rbLastMonth.isChecked = false
                rbLastQuarter.isChecked = false
                rbByDate.isChecked = false

                val dateStartLastWeek = sdfForShow.parse(lastWeek.first())
                val dateEndLastWeek = sdfForShow.parse(lastWeek.last())
                dateStartReq = dateFormatRequest.format(dateStartLastWeek as Date).replace(" ", "")
                dateEndReq = dateFormatRequest.format(dateEndLastWeek as Date).replace(" ", "")
                filterTitle = getString(R.string.last_week)
            }
            wrapperFilterLastMonth.setOnClickListener {
                rbLastMonth.isChecked = true
                rbLastWeek.isChecked = false
                rbLastQuarter.isChecked = false
                rbByDate.isChecked = false

                val dateStartLastMonth = sdfForShow.parse(lastMonth.first())
                val dateEndLastMonth = sdfForShow.parse(lastMonth.last())
                dateStartReq = dateFormatRequest.format(dateStartLastMonth as Date).replace(" ", "")
                dateEndReq = dateFormatRequest.format(dateEndLastMonth as Date).replace(" ", "")
                filterTitle = getString(R.string.last_month)
            }
            wrapperFilterLastQuarter.setOnClickListener {
                rbLastQuarter.isChecked = true
                rbLastWeek.isChecked = false
                rbLastMonth.isChecked = false
                rbByDate.isChecked = false

                val dateStartLastQuarter = sdfForShow.parse(lastQuarter.first())
                val dateEndLastQuarter = sdfForShow.parse(lastQuarter.last())
                dateStartReq = dateFormatRequest.format(dateStartLastQuarter as Date).replace(" ", "")
                dateEndReq = dateFormatRequest.format(dateEndLastQuarter as Date).replace(" ", "")
                filterTitle = getString(R.string.last_quarter)
            }

            rbByDate.setOnClickListener {
                rbByDate.isChecked = true
                rbLastWeek.isChecked = false
                rbLastMonth.isChecked = false
                rbLastQuarter.isChecked = false

                val dateStartNow = sdfForShow.parse(now.first())
                val dateEndNow = sdfForShow.parse(now.last())
                dateStartReq = dateFormatRequest.format(dateStartNow as Date).replace(" ", "")
                dateEndReq = dateFormatRequest.format(dateEndNow as Date).replace(" ", "")
                filterTitle = getString(R.string.set_date)
            }

            setDateFrom.setOnClickListener {
                rbByDate.isChecked = true
                rbLastWeek.isChecked = false
                rbLastMonth.isChecked = false
                rbLastQuarter.isChecked = false
                filterTitle = getString(R.string.set_date)
                openDatePicker()
            }
            setDateTo.setOnClickListener {
                rbByDate.isChecked = true
                rbLastWeek.isChecked = false
                rbLastMonth.isChecked = false
                rbLastQuarter.isChecked = false
                if (filterTitle.isNullOrEmpty()) filterTitle = getString(R.string.set_date)
                openDatePicker(isDateTo = true)
            }
            btnSubmit.setOnClickListener {
                viewModel.onTriggerEvent(HistoryNobuEvent.FilterByDateHistory(dateStartReq, dateEndReq))
                viewModel.setFilterTitle(filterTitle)
                this@FilterByDateFragment.dismiss()
            }
        }
    }

    private fun init() {
        sdfForShow = SimpleDateFormat("dd MMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
        now = filterByDate(1)
        dateStartLimitMillis = sdfForShow.parse(now.first().toString())
        dateEndLimitMillis = sdfForShow.parse(now.last().toString())

        lastWeek = filterByDate(7)
        lastMonth = filterByDate(30)
        lastQuarter = filterByDate(90)

        with(binding) {
            dateLastWeek.text = lastWeek.first()
            dateNow1.text = lastWeek.last()
            dateLastMonth.text = lastMonth.first()
            dateNow2.text = lastMonth.last()
            dateLastQuarter.text = lastQuarter.first()
            dateNow3.text = lastQuarter.last()
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
            if (stateDayInit) {
                day = calendarStart.get(Calendar.DAY_OF_MONTH)-1
                stateDayInit = false
            } else day = calendarStart.get(Calendar.DAY_OF_MONTH)
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(), R.style.DatePickerCustom,
            { _, _, _, _ -> }, year, month, day
        )
        datePickerDialog.datePicker.maxDate = dateEndLimitMillis!!.time
        if (isDateTo) {
            datePickerDialog.datePicker.minDate = dateStartLimitMillis!!.time
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
                    dateEndLimitMillis = sdfDummy.parse(formatting)
                    dateEndReq = dateFormatRequest.format(calendarEnd.time).replace(" ", "")
                    binding.tvDateTo.text = formatForShow
                } else {
                    dateStartLimitMillis = sdfDummy.parse(formatting)
                    dateStartReq = dateFormatRequest.format(calendarStart.time).replace(" ", "")
                    binding.tvDateFrom.text = formatForShow
                }
            }
        }
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}