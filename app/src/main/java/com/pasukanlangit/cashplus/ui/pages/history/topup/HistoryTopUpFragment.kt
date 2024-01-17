package com.pasukanlangit.cashplus.ui.pages.history.topup

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.FragmentHistoryTopUpBinding
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.pages.history.DateHistory
import com.pasukanlangit.cashplus.ui.pages.history.HistoryViewModel
import com.pasukanlangit.cashplus.ui.pages.history.transaction.HistoryLoadStateAdapter
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.CoreUtils.copyClipboard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*

class HistoryTopUpFragment : Fragment() {

    private var _binding: FragmentHistoryTopUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by sharedViewModel()
    private val sharedPrefDataSource : SharedPrefDataSource by inject()
    private val uuid get() = sharedPrefDataSource.getUUID()
    private val token get() = sharedPrefDataSource.getAccessToken()

    private lateinit var historyTopUpAdapter: TopUpPagingAdapter
    private lateinit var sdf: SimpleDateFormat
    private var transactionLabel : String?= "hari ini"
    private var dateHistory: DateHistory? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryTopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sdf = SimpleDateFormat("yyyyMMdd", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
        historyTopUpAdapter = TopUpPagingAdapter()
        historyTopUpAdapter.setOnButtonClickListener(object: TopUpPagingAdapter.OnButtonClickListener {
            override fun onCopiedRek(rekening: String) {
                copyClipboard(requireContext(), rekening)
            }

            override fun onCopiedNominal(nominal: String) {
                copyClipboard(requireContext(), nominal)
            }
        })
        with(binding) {
            with(rvHistoryTopup) {
                val headerAdapter = HistoryLoadStateAdapter()
                val footerAdapter = HistoryLoadStateAdapter()
                val concatAdapter = historyTopUpAdapter.withLoadStateHeaderAndFooter(
                    header = headerAdapter, footer = footerAdapter
                )
                layoutManager = LinearLayoutManager(requireContext())
                adapter = concatAdapter
                addItemDecoration(CashplusItemDecoration(24))
            }
            swiperefreshHistory.setOnRefreshListener {
                collectHistoryTopUp()
                swiperefreshHistory.isRefreshing = false
            }
        }
        observeDateHistory()
        loadStateListener()
    }

    private fun observeDateHistory() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dateHistory.collectLatest { response ->
                        if (response != null) {
                            dateHistory = response
                            transactionLabel = response.dateLabel

                            viewModel.historyTopUp(
                                uuid = uuid ?: "", accessToken = token ?: "",
                                dateStart = this@HistoryTopUpFragment.dateHistory?.dateStart.toString(),
                                dateEnd = this@HistoryTopUpFragment.dateHistory?.dateEnd.toString()
                            ).flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collectLatest {
                                if (::historyTopUpAdapter.isInitialized) historyTopUpAdapter.submitData(it)
                            }
                            return@collectLatest
                        }
                        with(binding) {
                            emptyHistoryTopUp.isVisible = true
                            emptyHistoryTopUp.text = getString(R.string.filter_not_specific)
                            rvHistoryTopupShimmer.isVisible = false
                            rvHistoryTopupShimmer.stopShimmer()
                        }
                    }
                }
            }
        }
    }

    private fun collectHistoryTopUp() {
        lifecycleScope.launch {
            viewModel.historyTopUp(
                uuid = uuid ?: "", accessToken = token ?: "",
                dateStart = this@HistoryTopUpFragment.dateHistory?.dateStart.toString(),
                dateEnd = this@HistoryTopUpFragment.dateHistory?.dateEnd.toString()
            ).flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collectLatest {
                if (::historyTopUpAdapter.isInitialized) historyTopUpAdapter.submitData(it)
            }
        }
    }

    private fun loadStateListener() {
        historyTopUpAdapter.addLoadStateListener { loadState ->
            with(binding) {
                when(loadState.source.refresh) {
                    is LoadState.Loading -> {
                        emptyHistoryTopUp.isVisible = false
                        rvHistoryTopup.isVisible = false
                        rvHistoryTopupShimmer.isVisible = true
                        rvHistoryTopupShimmer.startShimmer()
                    }
                    is LoadState.NotLoading -> {
                        emptyHistoryTopUp.isVisible = historyTopUpAdapter.itemCount == 0
                        emptyHistoryTopUp.text = getString(R.string.no_topUp_history_in_label, transactionLabel)
                        rvHistoryTopup.isVisible = historyTopUpAdapter.itemCount != 0
                        rvHistoryTopupShimmer.isVisible = false
                        rvHistoryTopupShimmer.stopShimmer()
                    }
                    is LoadState.Error -> {
                        rvHistoryTopup.isVisible = false
                        rvHistoryTopupShimmer.isVisible = false
                        rvHistoryTopupShimmer.stopShimmer()
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
            val buttomSheetNotif = ButtomSheetNotif("${it.error}", NotifType.NOTIF_ERROR)
            buttomSheetNotif.show(childFragmentManager, buttomSheetNotif.tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}