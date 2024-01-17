package com.pasukanlangit.cashplus.ui.pages.history.transaction

import android.content.Intent
import android.graphics.Color
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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.FragmentHistoryTrxBinding
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.pages.history.DateHistory
import com.pasukanlangit.cashplus.ui.pages.history.HistoryDetailActivity
import com.pasukanlangit.cashplus.ui.pages.history.HistoryViewModel
import com.pasukanlangit.id.core.extensions.onDone
import com.pasukanlangit.id.core.extensions.setOnlyNumberAllowed
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.KeyboardUtil.hideSoftKeyboard
import com.pasukanlangit.id.library_core.domain.model.NotifType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HistoryTrxFragment : Fragment() {

    private var _binding: FragmentHistoryTrxBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by sharedViewModel()
    private val sharedPrefDataSource : SharedPrefDataSource by inject()
    private val uuid get() = sharedPrefDataSource.getUUID()
    private val token get() = sharedPrefDataSource.getAccessToken()

    private lateinit var historyAdapter: HistoryPagingAdapter
    private lateinit var historyTrxAdapter: HistoryTrxAdapter
    private lateinit var concatAdapter: ConcatAdapter

    private var dateHistory: DateHistory? = null
    private var dest: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryTrxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewAdapter()
        with(binding) {
            swipeRefreshPagingTransaction.setOnRefreshListener {
                collectHistory()
                swipeRefreshPagingTransaction.isRefreshing = false
            }
            inputHistorySearch.onDone {
                inputHistorySearch.clearFocus()
                hideSoftKeyboard(requireContext())
            }
            inputHistorySearch.setOnlyNumberAllowed()
            btnSearch.setOnClickListener {
                val searchInput = inputHistorySearch.text.toString().trim()
                if (searchInput.isEmpty()) {
                    GenericModalDialogCashplus.Builder()
                        .title(getString(R.string.something_wrong))
                        .image(R.drawable.illustration_empty)
                        .description(getString(R.string.keyword_is_empty))
                        .buttonNegative(
                            NegativeButtonAction(
                                btnLabel = getString(R.string.close),
                                backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                buttonTextColor = Color.parseColor("#0A57FF"),
                                setClickOnDismiss = true
                            )
                        )
                        .show(requireActivity().supportFragmentManager, "keyword_is_empty")
                    return@setOnClickListener
                }
                dest = searchInput
                viewModel.searchTransactions(uuid = uuid ?: "", dest = dest?: "",  token ?: "")
                emptyHistoryTrx.isVisible = false
                hideSoftKeyboard(requireContext())
            }
        }
        collectData()
        loadStateListener()
    }

    private fun setUpRecyclerViewAdapter() {
        historyAdapter = HistoryPagingAdapter {
            val intent = Intent(activity, HistoryDetailActivity::class.java)
            intent.putExtra(HistoryDetailActivity.KEY_TRX_DETAIL, it)
            startActivity(intent)
        }
        historyTrxAdapter = HistoryTrxAdapter {
            val intent = Intent(activity, HistoryDetailActivity::class.java)
            intent.putExtra(HistoryDetailActivity.KEY_TRX_DETAIL, it)
            startActivity(intent)
        }

        with(binding) {
            with(rvTransactionPaging) {
                val headerAdapter = HistoryLoadStateAdapter()
                val footerAdapter = HistoryLoadStateAdapter()
                concatAdapter = historyAdapter.withLoadStateHeaderAndFooter(
                    header = headerAdapter, footer = footerAdapter
                )
                layoutManager = LinearLayoutManager(requireContext())
                adapter = concatAdapter
                addItemDecoration(CashplusItemDecoration(24))
            }
            with(rvTransactionNormal) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = historyTrxAdapter
                addItemDecoration(CashplusItemDecoration(24))
            }
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isSearch.collectLatest {
                        binding.swipeRefreshPagingTransaction.isVisible = !it
                    }
                }
                launch {
                    viewModel.dateHistory.collectLatest { response ->
                        dateHistory = response
                        binding.inputHistorySearch.text?.clear()
                        if (response != null) {
                            viewModel.historyTransaction(
                                uuid = uuid ?: "", accessToken = token ?: "",
                                dateStart = dateHistory?.dateStart.toString(), dateEnd = dateHistory?.dateEnd.toString()
                            ).flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collectLatest {
                                if (::historyAdapter.isInitialized) {
                                    historyAdapter.submitData(it)
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.searchLoading.collectLatest {
                        with(binding) {
                            rvActivityShimmer.isVisible = it
                            if (it) {
                                rvTransactionNormal.isVisible = false
                                rvActivityShimmer.startShimmer()
                                return@collectLatest
                            }
                            rvActivityShimmer.stopShimmer()
                        }
                    }
                }
                launch {
                    viewModel.searchTransaction.collectLatest { response ->
                        if (response != null) {
                            with(binding) {
                                emptyHistoryTrx.text = getString(R.string.history_not_found)
                                rvTransactionNormal.isVisible = !response.data.isNullOrEmpty()
                                emptyHistoryTrx.isVisible = response.data.isNullOrEmpty() || response.data.isEmpty()

                                historyTrxAdapter.setTransactionHistory(response.data)
                                historyTrxAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
                launch {
                    viewModel.searchError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val buttomSheetNotif = ButtomSheetNotif(info, NotifType.NOTIF_ERROR)
                            buttomSheetNotif.show(childFragmentManager, buttomSheetNotif.tag)
                            delay(300)
                            viewModel.removeSearchErrorMessage()
                        }
                    }
                }
            }
        }
    }

    private fun collectHistory() {
        lifecycleScope.launch {
            viewModel.historyTransaction(
                uuid = uuid ?: "", accessToken = token ?: "",
                dateStart = dateHistory?.dateStart.toString(), dateEnd = dateHistory?.dateEnd.toString()
            ).flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collectLatest {
                if (::historyAdapter.isInitialized) {
                    historyAdapter.submitData(it)
                }
            }
        }
    }

    private fun loadStateListener() {
        historyAdapter.addLoadStateListener { loadState ->
            with(binding) {
                when(loadState.source.refresh) {
                    is LoadState.Loading -> {
                        emptyHistoryTrx.isVisible = false
                        swipeRefreshPagingTransaction.isVisible = false
                        rvTransactionNormal.isVisible = false
                        rvActivityShimmer.isVisible = true
                        rvActivityShimmer.startShimmer()
                    }
                    is LoadState.NotLoading -> {
                        emptyHistoryTrx.isVisible = historyAdapter.itemCount == 0
                        emptyHistoryTrx.text = getString(R.string.no_transaction_history_in_label, dateHistory?.dateLabel)
                        swipeRefreshPagingTransaction.isVisible = historyAdapter.itemCount > 0
                        rvTransactionNormal.isVisible = false
                        rvActivityShimmer.isVisible = false
                        rvActivityShimmer.stopShimmer()
                    }
                    is LoadState.Error -> {
                        swipeRefreshPagingTransaction.isVisible = false
                        rvTransactionNormal.isVisible = false
                        rvActivityShimmer.isVisible = false
                        rvActivityShimmer.stopShimmer()
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