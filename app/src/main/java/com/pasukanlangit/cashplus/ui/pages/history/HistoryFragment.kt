package com.pasukanlangit.cashplus.ui.pages.history

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment.STYLE_NO_TITLE
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker
import com.google.android.material.tabs.TabLayoutMediator
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentHistoryBinding
import com.pasukanlangit.cashplus.ui.pages.history.topup.HistoryTopUpFragment
import com.pasukanlangit.cashplus.ui.pages.history.transaction.HistoryTrxFragment
import com.pasukanlangit.cashplus.utils.DAY_IN_MILES
import com.pasukanlangit.id.core.presentation.SublimePickerDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by sharedViewModel()
    private lateinit var titles: List<String>
    private lateinit var sdf: SimpleDateFormat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titles = listOf(getString(R.string.transaction), getString(R.string.topUp))
        sdf = SimpleDateFormat("yyyyMMdd", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
        val indexTab = arguments?.getInt("index_tab_history", -1) ?: -1
        setPagerAdapter()
        setUpTabLayout()
        viewModel.setDate(DateHistory(getCurrentDate()))

        with(binding) {
            if (indexTab < titles.size && indexTab >= 0) pagerHistory.currentItem = indexTab
            rbRecent.setOnClickListener {
                viewModel.setDate(
                    DateHistory(getCurrentDate(), getCurrentDate(), getString(R.string.today))
                )
            }
            rbLastWeek.setOnClickListener {
                viewModel.setDate(
                    DateHistory(getLastWeek().first(), getLastWeek().last(), getString(R.string.seven_days_ago))
                )
            }
            rbByDate.setOnClickListener {
                loading.isVisible = true
                openDateRangePicker()
            }
        }
        collectSearchState()
    }

    private fun setPagerAdapter() {
        val historyPagerAdapter = HistoryPagerAdapter(requireActivity())
        binding.pagerHistory.adapter = historyPagerAdapter
        binding.pagerHistory.isSaveEnabled = false
    }

    private inner class HistoryPagerAdapter(fragmentActivity: FragmentActivity):
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return HistoryTrxFragment()
                1 -> return HistoryTopUpFragment()
            }
            return Fragment()
        }
    }

    private fun setUpTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.pagerHistory) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    private fun getCurrentDate() : String {
        return sdf.format(Date())
    }

    private fun getLastWeek() : List<String> {
        val startOfLastWeek = getDayTimeBefore()
        val endOfLastWeek = Date()

        return listOf(
            sdf.format(startOfLastWeek),
            sdf.format(endOfLastWeek)
        )
    }

    private fun openDateRangePicker() {
        val pickerFrag = SublimePickerDialogFragment()
        pickerFrag.setCallback(object : SublimePickerDialogFragment.Callback {
            override fun onCancelled() {
                binding.loading.isVisible = false
            }

            override fun onDateTimeRecurrenceSet(
                selectedDate: SelectedDate?, hourOfDay: Int, minute: Int,
                recurrenceOption: SublimeRecurrencePicker.RecurrenceOption?,
                recurrenceRule: String?
            ) {
                val formatDate = SimpleDateFormat("yyyyMMdd", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
                val formatDateLabel = SimpleDateFormat("d MMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
                val mDateStart = formatDate.format(selectedDate!!.startDate.time)
                val mDateEnd = formatDate.format(selectedDate.endDate.time)
                val mDateStartLabel = formatDateLabel.format(selectedDate.startDate.time)
                val mDateEndLabel = formatDateLabel.format(selectedDate.endDate.time)

                binding.loading.isVisible = false
                viewModel.setDate(DateHistory(mDateStart,mDateEnd,"$mDateStartLabel - $mDateEndLabel"))
            }
        })

        // ini configurasi agar library menggunakan method Date Range Picker
        val options = SublimeOptions()
        options.setDateRange(Long.MIN_VALUE, Date().time)
        options.setCanPickDateRange(true)
        options.pickerToShow = SublimeOptions.Picker.DATE_PICKER

        val bundle = Bundle()
        bundle.putParcelable("SUBLIME_OPTIONS", options)
        pickerFrag.arguments = bundle
        pickerFrag.setStyle(STYLE_NO_TITLE, 0)
        pickerFrag.show(childFragmentManager , "SUBLIME_PICKER")
    }


    //to avoid overflow
    private fun getDayTimeBefore(): Long {
        var currentDateTime = Date().time
        for(i in 0 until 7) {
            currentDateTime -= DAY_IN_MILES
        }
        return currentDateTime
    }

    private fun collectSearchState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isSearch.collectLatest {
                        if (it) binding.rgMenu.clearCheck()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}