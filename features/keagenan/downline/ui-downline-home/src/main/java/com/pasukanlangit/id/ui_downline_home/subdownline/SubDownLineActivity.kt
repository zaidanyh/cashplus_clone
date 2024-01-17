package com.pasukanlangit.id.ui_downline_home.subdownline

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker
import com.pasukanlangit.id.core.presentation.SublimePickerDialogFragment
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.ui_downline_home.databinding.ActivitySubDownLineBinding
import com.pasukanlangit.id.ui_downline_home.detail.DetailDownLineFragment
import com.pasukanlangit.id.ui_downline_home.detail.DownLineDetail
import com.pasukanlangit.id.ui_downline_home.paging.DownlineLoadStateAdapter
import com.pasukanlangit.id.ui_downline_home.paging.DownlinePagingAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class SubDownLineActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubDownLineBinding
    private val viewModel: SubDownLineViewModel by viewModel()

    private lateinit var pickerFrag: SublimePickerDialogFragment
    private lateinit var subDownLineAdapter: DownlinePagingAdapter

    private val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private var dateStart = sdf.format(Date())
    private var dateEnd = sdf.format(Date())
    private var parentNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubDownLineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subDownLineAdapter = DownlinePagingAdapter(true)
        subDownLineAdapter.setOnItemClickListener(object: DownlinePagingAdapter.OnItemClickListener {
            override fun onRootClicked(downlineDetail: DownLineDetail) {
                DetailDownLineFragment.newInstance(
                    downLineDetail = downlineDetail, isFromSubDownLine = true
                ).show(supportFragmentManager, "Detail SubDownLine")
            }
            override fun onSetMarkupClicked(phone: String, name: String, mainMarkup: String) { return }
            override fun onMoreClicked(downlineDetail: DownLineDetail) { return }
            override fun onMarkupPlanClicked(phone: String, name: String, markupPlan: String, mainMarkup: String) { return }
        })

        parentNumber = intent.getStringExtra(KEY_PHONE_PARENT)
        initDatePicker()
        setUpRvSubDownLine()
        with(binding) {
            appbar.setOnBackPressed { finish() }
            tvFilter.setOnClickListener {
                openDateRangePicker()
            }
            intent.getStringExtra(KEY_NAME_PARENT)?.let { nameParent ->
                tvNameParent.text = nameParent
            }
        }
        loadStateListener()
    }

    private fun collectSubDownLine() {
        lifecycleScope.launch {
            viewModel.getSubDownLineList(parentNumber.toString())
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collectLatest {
                    if (::subDownLineAdapter.isInitialized) subDownLineAdapter.submitData(it)
                }
        }
    }

    private fun setUpRvSubDownLine() {
        with(binding.rvSubdownline) {
            val headerAdapter = DownlineLoadStateAdapter()
            val footAdapter = DownlineLoadStateAdapter()
            val concatAdapter = subDownLineAdapter.withLoadStateHeaderAndFooter(
                header = headerAdapter, footer = footAdapter
            )
            layoutManager = LinearLayoutManager(this@SubDownLineActivity)
            adapter = concatAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    private fun openDateRangePicker() {
        pickerFrag.show(supportFragmentManager, "SUBLIME_PICKER")
        binding.loading.isVisible = true
    }

    private fun initDatePicker() {
        pickerFrag = SublimePickerDialogFragment()
        pickerFrag.setCallback(object : SublimePickerDialogFragment.Callback {
            override fun onCancelled() {
                binding.loading.isVisible = false
            }

            override fun onDateTimeRecurrenceSet(
                selectedDate: SelectedDate?, hourOfDay: Int, minute: Int,
                recurrenceOption: SublimeRecurrencePicker.RecurrenceOption?,
                recurrenceRule: String?
            ) {
                if (selectedDate == null) return
                dateStart = sdf.format(selectedDate.startDate.time)
                dateEnd = sdf.format(selectedDate.endDate.time)

                viewModel.onTriggerEvent(
                    SubDownLineEvent.SetDatePicker(
                        dateStart = dateStart,
                        dateEnd = dateEnd
                    )
                )
                collectSubDownLine()
            }
        })

        // ini configurasi agar library menggunakan method Date Range Picker
        val options = SublimeOptions()
        options.setDateRange(Long.MIN_VALUE, Date().time)
        options.setCanPickDateRange(true)
        options.pickerToShow = SublimeOptions.Picker.DATE_PICKER
        options.setDisplayOptions(SublimeOptions.ACTIVATE_DATE_PICKER)
        val bundle = Bundle()
        bundle.putParcelable("SUBLIME_OPTIONS", options)
        pickerFrag.arguments = bundle
        pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
    }

    private fun loadStateListener() {
        subDownLineAdapter.addLoadStateListener { loadState ->
            with(binding) {
                when (loadState.source.refresh) {
                    is LoadState.Loading -> {
                        emptyView.isVisible = false
                        rvSubdownline.isVisible = false
                        loading.isVisible = true
                        rvSubDownlineShimmer.isVisible = true
                        rvSubDownlineShimmer.startShimmer()
                    }
                    is LoadState.NotLoading -> {
                        emptyView.isVisible = subDownLineAdapter.itemCount == 0
                        rvSubdownline.isVisible = subDownLineAdapter.itemCount != 0
                        loading.isVisible = false
                        rvSubDownlineShimmer.isVisible = false
                        rvSubDownlineShimmer.stopShimmer()
                    }
                    is LoadState.Error -> {
                        rvSubdownline.isVisible = false
                        rvSubDownlineShimmer.isVisible = false
                        loading.isVisible = false
                        rvSubDownlineShimmer.stopShimmer()
                        handleError(loadState)
                    }
                }
            }
        }
    }

    private fun handleError(loadState: CombinedLoadStates) {
        val errorState = loadState.source.append as? LoadState.Error
            ?: loadState.source.prepend as? LoadState.Error
        errorState?.let {
            Toast.makeText(this, "${it.error}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(SubDownLineEvent.SetDatePicker(
            dateStart = dateStart,
            dateEnd = dateEnd
        ))
        collectSubDownLine()
    }

    companion object {
        const val KEY_PHONE_PARENT = "KEY_PHONE_PARENT"
        const val KEY_NAME_PARENT = "KEY_NAME_PARENT"
    }
}