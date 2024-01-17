package com.pasukanlangit.id.nobu.presentation.history

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.core.utils.setDropDownView
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.ActivityHistoryNobuBinding
import com.pasukanlangit.id.nobu.domain.model.DataTrx
import com.pasukanlangit.id.nobu.domain.temp.FilteringHistory
import com.pasukanlangit.id.nobu.domain.temp.NobuDataDummy
import com.pasukanlangit.id.nobu.presentation.scan.QrScanActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryNobuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryNobuBinding
    private val viewModel: HistoryNobuViewModel by viewModel()
    private lateinit var historyNobuAdapter: HistoryNobuAdapter
    private lateinit var typeFilter: List<FilteringHistory>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryNobuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        with(binding) {
            appBar.setOnBackPressed { finish() }
            swipeRefresh.setOnRefreshListener {
                viewModel.onTriggerEvent(HistoryNobuEvent.FilterByDateHistory())
                viewModel.setFilterTitle()
                swipeRefresh.isRefreshing = false
                with(spinnerType) {
                    setSelection(0)
                    setBackgroundResource(R.drawable.bg_white_spinner_border_slate200_rounded_10)
                }
            }
            filterByDate.setOnClickListener {
                FilterByDateFragment().show(supportFragmentManager, "Filter By Date")
            }
            btnCreateNow.setOnClickListener {
                startActivity(Intent(this@HistoryNobuActivity, QrScanActivity::class.java))
                finish()
            }
            spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position != 0) {
                        val value = typeFilter[position].value
                        if (!value.isNullOrEmpty()) {
                            spinnerType.setBackgroundResource(R.drawable.bg_spinner_primary)
                            (view as TextView).setTextColor(Color.parseColor("#FFFFFF"))
                            historyNobuAdapter.filter.filter(value)
                        }
                    } else {
                        historyNobuAdapter.filter.filter("")
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
        collectHistoryTrx()
    }

    private fun setupRecyclerView() {
        historyNobuAdapter = HistoryNobuAdapter()
        with(binding.rvHistoryNobu) {
            layoutManager = LinearLayoutManager(this@HistoryNobuActivity)
            adapter = historyNobuAdapter
        }
    }

    private fun observeAdapter() {
        historyNobuAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIsEmpty()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                checkIsEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkIsEmpty()
            }

            private fun checkIsEmpty() {
                val isLoading = viewModel.stateLoading.value
                binding.wrapperEmptyHistory.isVisible = !isLoading && historyNobuAdapter.itemCount == 0
            }
        })
    }

    private fun collectHistoryTrx() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateLoading.collectLatest {
                        with(binding) {
                            shimmerHistory.isVisible = it
                            shimmerHistory.startShimmer()
                            wrapperFilter.isVisible = !it
                            wrapperContent.isVisible = !it
                        }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        if (message.isNotEmpty()) {
                            binding.shimmerHistory.stopShimmer()

                            message.peek()?.let { info ->
                                val toast =
                                    Toast.makeText(this@HistoryNobuActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvent(HistoryNobuEvent.RemoveHeadMessage)
                            }
                        }
                    }
                }
                launch {
                    viewModel.nobuHistory.collectLatest { response ->
                        val typeList = arrayListOf<String>()
                        typeFilter = NobuDataDummy.allFilterType()

                        typeFilter.forEach { type -> typeList.add(type.name) }

                        val typeAdapter = setDropDownView(this@HistoryNobuActivity, typeList)
                        typeAdapter.setDropDownViewResource(R.layout.item_spinner_small)
                        binding.spinnerType.adapter = typeAdapter
                        binding.shimmerHistory.stopShimmer()

                        bindHistoryNobu(response?.listData)
                    }
                }
                launch {
                    viewModel.filterTitle.collectLatest {
                        with(binding) {
                            with(filterByDate) {
                                text = if (it.isNullOrEmpty()) getString(R.string.date) else it
                                setTextColor(if (it.isNullOrEmpty()) Color.parseColor("#334155") else Color.parseColor("#FFFFFF"))
                                setBackgroundResource(
                                    if (it.isNullOrEmpty()) R.drawable.bg_white_spinner_border_slate200_rounded_10
                                    else R.drawable.bg_spinner_primary
                                )
                            }
                            with(spinnerType) {
                                setSelection(0)
                                setBackgroundResource(R.drawable.bg_white_spinner_border_slate200_rounded_10)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun bindHistoryNobu(history: List<DataTrx>?) {
        with(binding) {
            if (history.isNullOrEmpty()) {
                wrapperEmptyHistory.isVisible = true
                wrapperFilter.isVisible = false
                rvHistoryNobu.isVisible = false
            } else {
                wrapperEmptyHistory.isVisible = false
                wrapperFilter.isVisible = true
                rvHistoryNobu.isVisible = true
                historyNobuAdapter.setDataTrx(history)
                historyNobuAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(HistoryNobuEvent.FilterByDateHistory())
        observeAdapter()
    }
}