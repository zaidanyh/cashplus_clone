package com.pasukanlangit.id.ui_downline_home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions.ACTIVATE_DATE_PICKER
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.REGISTER_DOWNLINE_PATH
import com.pasukanlangit.id.core.presentation.SublimePickerDialogFragment
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.CenterZoomLinearLayoutManager
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyWords
import com.pasukanlangit.id.core.utils.InputUtil.inputAlphabetFilter
import com.pasukanlangit.id.domain_downline.model.DownlineTrxCount
import com.pasukanlangit.id.domain_downline.model.SummaryDownlineResponse
import com.pasukanlangit.id.domain_downline.utils.PagingDataType
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiahWithoutRp
import com.pasukanlangit.id.ui_downline_home.databinding.ActivityDownlineBinding
import com.pasukanlangit.id.ui_downline_home.detail.DetailDownLineActivity
import com.pasukanlangit.id.ui_downline_home.detail.DownLineDetail
import com.pasukanlangit.id.ui_downline_home.detail.MoreOptionsDownline
import com.pasukanlangit.id.ui_downline_home.detail.allProduct.SetAllProductMarkupModal
import com.pasukanlangit.id.ui_downline_home.mintasaldoqr.MintaSaldoWrapperFragment
import com.pasukanlangit.id.ui_downline_home.mintasaldoqr.ScanQRAgentActivity
import com.pasukanlangit.id.ui_downline_home.paging.DownlineLoadStateAdapter
import com.pasukanlangit.id.ui_downline_home.paging.DownlinePagingAdapter
import com.pasukanlangit.id.ui_downline_home.priceplan.PricePlanActivity
import com.pasukanlangit.id.ui_downline_home.resetmarkup.ResetMarkUpFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class DownLineActivity : AppCompatActivity() {

    private var animSetPaymentScroll: AnimatorSet? = null
    private lateinit var mDownLineAdapter: DownlinePagingAdapter
    private var formatDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private var dateStartSelected: String = formatDate.format(Date())
    private var dateEndSelected: String = formatDate.format(Date())
    private var balanceAdapter = lazy {
        BalanceAgentAdapter()
    }

    private lateinit var pickerFrag: SublimePickerDialogFragment
    private lateinit var binding: ActivityDownlineBinding
    private val viewModel: DownLineViewModel by viewModel()

    private lateinit var resetMarkup: ResetMarkUpFragment

    private var trxCount: String? = null
    private var downLineCount: String? = null
    private var stateShowingTrxCount: Boolean = true
    private var stateShowingDownlineCount: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownlineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val isAskingForMoney = intent.getBooleanExtra(IS_ASKING_FOR_MONEY_KEY, false)
        if (isAskingForMoney) CoroutineScope(Dispatchers.Main).launch {
            delay(300L)
            val askingForMoney = MintaSaldoWrapperFragment()
            askingForMoney.isCancelable = false
            askingForMoney.show(supportFragmentManager, askingForMoney.tag)
        }

        mDownLineAdapter = DownlinePagingAdapter()
        resetMarkup = ResetMarkUpFragment()
        mDownLineAdapter.setOnItemClickListener(object: DownlinePagingAdapter.OnItemClickListener {
            override fun onRootClicked(downlineDetail: DownLineDetail) {
                startActivity(
                    Intent(this@DownLineActivity, DetailDownLineActivity::class.java).apply {
                        putExtra(DetailDownLineActivity.DOWNLINE_ITEM_KEY, downlineDetail)
                    }
                )
            }
            override fun onSetMarkupClicked(phone: String, name: String, mainMarkup: String) {
                SetAllProductMarkupModal.newInstance(
                    downlinePhone = phone,
                    name = name,
                    markup = mainMarkup
                ).show(supportFragmentManager, "Set All Product Markup")
            }
            override fun onMoreClicked(downlineDetail: DownLineDetail) {
                MoreOptionsDownline.newInstance(downlineDetail).show(supportFragmentManager, "Pilihan Lainnya")
            }
            override fun onMarkupPlanClicked(phone: String, name: String, markupPlan: String, mainMarkup: String) {
                startActivity(
                    Intent(this@DownLineActivity, PricePlanActivity::class.java).apply {
                        putExtra(PricePlanActivity.PHONE_NUMBER_DOWNLINE_KEY, phone)
                        putExtra(PricePlanActivity.NAME_DOWNLINE_KEY, name)
                        putExtra(PricePlanActivity.MARKUP_PLAN_KEY, markupPlan)
                        putExtra(PricePlanActivity.MAIN_MARKUP_VALUE_KEY, mainMarkup)
                    }
                )
            }
        })

        with(binding) {
            imgBack.setOnClickListener { finish() }
            swipeRefreshDownline.setOnRefreshListener {
                animSetPaymentScroll = AnimatorSet()
                viewModel.onTriggerEvent(
                    DownLineEvent.SetDate(
                        dateStart = dateStartSelected,
                        dateEnd = dateEndSelected,
                    )
                )
                collectDownLine(PagingDataType.NONE.value, "")
                swipeRefreshDownline.isRefreshing = false
            }
            nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (abs(scrollY - oldScrollY) > 15) {
                    if (scrollY > oldScrollY) {
                        slideDown(binding.btnRegister)
                    } else {
                        slideUp(binding.btnRegister)
                    }
                }
            }
            iconQr.setOnClickListener {
                startActivity(Intent(this@DownLineActivity, ScanQRAgentActivity::class.java))
            }
            btnReset.setOnClickListener {
                resetMarkup.show(supportFragmentManager, resetMarkup.tag)
            }
            edtSearch.filters = arrayOf(InputFilter.LengthFilter(16), inputAlphabetFilter)
            edtSearch.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString()
                    val wordOnly = value.checkIsOnlyWords()
                    if (value.length > 2 && wordOnly) {
                        collectDownLine(PagingDataType.SEARCH_BY_NAME.value, value)
                        return
                    }
                    if (value.isEmpty()) collectDownLine(PagingDataType.NONE.value, "")
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            btnFilter.setOnClickListener {
                openDateRangePicker()
            }
            trxCountIsVisible.setOnClickListener {
                if (!trxCount.isNullOrEmpty())
                    visibilityTextView(isTrxCount = true)
            }
            downlineCountIsVisible.setOnClickListener {
                if (!downLineCount.isNullOrEmpty())
                    visibilityTextView()
            }
            with(rvDownline) {
                val headerAdapter = DownlineLoadStateAdapter()
                val footAdapter = DownlineLoadStateAdapter()
                val concatAdapter = mDownLineAdapter.withLoadStateHeaderAndFooter(
                    header = headerAdapter, footer = footAdapter
                )
                layoutManager = LinearLayoutManager(this@DownLineActivity)
                adapter = concatAdapter
                addItemDecoration(CashplusItemDecoration(24))
            }
            btnRegister.setOnClickListener {
                startActivity(ModuleRoute.internalIntent(this@DownLineActivity, REGISTER_DOWNLINE_PATH))
            }
        }

        collectState()
        loadStateListener()
        initDatePicker()
        setUpRvBalance()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpRvBalance() {
        binding.rvBalance.apply {
            adapter = balanceAdapter.value
            layoutManager = CenterZoomLinearLayoutManager(this@DownLineActivity)

            setOnTouchListener { _, _ ->
                animSetPaymentScroll?.cancel()
                false
            }
        }
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // LOADING STATE
                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        if (isLoading != null)
                            binding.loading.isVisible = isLoading
                    }
                }

                // DOWNLINE SUMMARY
                launch {
                    viewModel.summaryDownLine.collectLatest {
                        it?.let {
                            bindSummary(it)
                        }
                    }
                }
                launch {
                    viewModel.stateSummaryError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@DownLineActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(DownLineEvent.RemoveHeadMessageSummary)
                        }
                    }
                }

                // DOWNLINE TRX COUNT
                launch {
                    viewModel.downLineTrxCount.collectLatest {
                        it?.let {
                            bindTrxCount(it)
                        }
                    }
                }
                launch {
                    viewModel.stateDownLineTrxCountError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@DownLineActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(DownLineEvent.RemoveHeadMessageTrxCount)
                        }
                    }
                }
            }
        }
    }

    fun collectDownLine(type: String, value: String) {
        lifecycleScope.launch {
            viewModel.getDownLine(type = type, value = value)
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    if (::mDownLineAdapter.isInitialized) mDownLineAdapter.submitData(it)
                }
        }
    }

    private fun bindTrxCount(downLineTrxCount: DownlineTrxCount) {
        stateShowingTrxCount = true
        trxCount = downLineTrxCount.trxCount
        with(binding) {
            trxCountIsVisible.isVisible = stateShowingTrxCount
            trxCountIsVisible.setImageResource(R.drawable.ic_eye_12)

            startCountAnimation(trxCount?.toIntOrNull() ?: 0, tvTotalTrx)
        }
    }

    private fun bindSummary(summary: SummaryDownlineResponse) {
        stateShowingDownlineCount = true
        downLineCount = summary.downlineCount
        with(binding) {
            downlineCountIsVisible.isVisible = stateShowingDownlineCount
            downlineCountIsVisible.setImageResource(R.drawable.ic_eye_12)
            val listBalance = listOf(
                BalanceAgent(
                    "Deposit Cashplus",
                    getNumberRupiah(summary.myBalance?.toDoubleOrNull())
                ),
                BalanceAgent(
                    "Deposit Downline",
                    getNumberRupiah(summary.downlineTotalBalance?.toDoubleOrNull())
                ),
            )
            balanceAdapter.value.submitList(listBalance)

            if(rvBalance.onFlingListener == null) arIndicator.attachTo(rvBalance, true)
            animateRvScrolling()

            startCountAnimation(downLineCount?.toIntOrNull() ?: 0, tvTotalDownline)
        }
    }

    private fun animateRvScrolling() {
        //the idea is to scroll bottom and go back to top, to make user understand this view is scrollable
        lifecycleScope.launch(Dispatchers.Main) {
            delay(200)
            val vaScrollDown = ValueAnimator.ofFloat(0f, 15f)
            vaScrollDown.duration = 1700 //in millis
            vaScrollDown.addUpdateListener {
                animation -> binding.rvBalance.smoothScrollBy(0, (animation.animatedValue as Float).toInt())
            }

            val vaScrollUp = ValueAnimator.ofFloat(0f, -15f)
            vaScrollUp.duration = 1700 //in millis
            vaScrollUp.addUpdateListener { animation ->
                binding.rvBalance.smoothScrollBy(0, (animation.animatedValue as Float).toInt())
            }
            animSetPaymentScroll?.playSequentially(vaScrollDown, vaScrollUp)
            animSetPaymentScroll?.start()
        }
    }

    private fun startCountAnimation(maxValue: Int, textView: TextView) {
        val animator = ValueAnimator.ofInt(0, maxValue)
        animator.duration = 1000
        animator.addUpdateListener { animation ->
            textView.text = animation.animatedValue.toString()
        }
        animator.start()
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
                dateStartSelected = formatDate.format(selectedDate.startDate.time)
                dateEndSelected = formatDate.format(selectedDate.endDate.time)

                viewModel.onTriggerEvent(
                    DownLineEvent.SetDate(
                        dateStart = dateStartSelected,
                        dateEnd = dateEndSelected
                    )
                )
                collectDownLine(PagingDataType.NONE.value, "")
            }
        })

        // ini configurasi agar library menggunakan method Date Range Picker
        val options = SublimeOptions()
        options.setDateRange(Long.MIN_VALUE, Date().time)
        options.setCanPickDateRange(true)
        options.pickerToShow = SublimeOptions.Picker.DATE_PICKER
        options.setDisplayOptions(ACTIVATE_DATE_PICKER)
        val bundle = Bundle()
        bundle.putParcelable("SUBLIME_OPTIONS", options)
        pickerFrag.arguments = bundle
        pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
    }

    private fun openDateRangePicker() {
        pickerFrag.show(supportFragmentManager, "SUBLIME_PICKER")
        binding.loading.isVisible = true
    }

    private fun slideDown(view: View) {
        val height = view.height
        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, height.toFloat() * 2.5f).apply {
            duration = 300
            start()
        }
    }

    private fun slideUp(view: View) {
        val height = view.height
        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, height.toFloat() * 2.5f, 0f).apply {
            duration = 300
            start()
        }
    }

    private fun visibilityTextView(isTrxCount: Boolean = false) {
        with(binding) {
            if (isTrxCount) {
                stateShowingTrxCount = !stateShowingTrxCount
                if (stateShowingTrxCount) {
                    tvTotalTrx.text = getNumberRupiahWithoutRp(trxCount?.toDouble())
                    trxCountIsVisible.setImageResource(R.drawable.ic_eye_12)
                } else {
                    var textCensor = ""
                    for (i in 0 until trxCount?.length!!.toInt()) {
                        textCensor += "-"
                    }
                    tvTotalTrx.text = textCensor
                    trxCountIsVisible.setImageResource(R.drawable.ic_eye_slash_12)
                }
            } else {
                stateShowingDownlineCount = !stateShowingDownlineCount
                if (stateShowingDownlineCount) {
                    tvTotalDownline.text = getNumberRupiahWithoutRp(downLineCount?.toDouble())
                    downlineCountIsVisible.setImageResource(R.drawable.ic_eye_12)
                } else {
                    var textCensor = ""
                    for (i in 0 until downLineCount?.length!!.toInt()) {
                        textCensor += "-"
                    }
                    tvTotalDownline.text = textCensor
                    downlineCountIsVisible.setImageResource(R.drawable.ic_eye_slash_12)
                }
            }
        }
    }

    private fun loadStateListener() {
        mDownLineAdapter.addLoadStateListener { loadState ->
            with(binding) {
                when (loadState.source.refresh) {
                    is LoadState.Loading -> {
                        emptyView.isVisible = false
                        rvDownline.isVisible = false
                        rvDownlineShimmer.isVisible = true
                        rvDownlineShimmer.startShimmer()
                    }
                    is LoadState.NotLoading -> {
                        emptyView.isVisible = mDownLineAdapter.itemCount == 0
                        rvDownline.isVisible = mDownLineAdapter.itemCount > 0
                        rvDownlineShimmer.isVisible = false
                        rvDownlineShimmer.stopShimmer()
                    }
                    is LoadState.Error -> {
                        rvDownline.isVisible = false
                        rvDownlineShimmer.isVisible = false
                        rvDownlineShimmer.stopShimmer()
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

    override fun onPause() {
        super.onPause()
        animSetPaymentScroll?.cancel()
    }

    override fun onResume() {
        super.onResume()
        animSetPaymentScroll = AnimatorSet()
        viewModel.onTriggerEvent(
            DownLineEvent.SetDate(
                dateStart = dateStartSelected,
                dateEnd = dateEndSelected,
            )
        )
        collectDownLine(PagingDataType.NONE.value, "")
    }

    override fun onDestroy() {
        super.onDestroy()
        animSetPaymentScroll?.cancel()
        animSetPaymentScroll = null
    }

    companion object {
        const val IS_ASKING_FOR_MONEY_KEY = "is_asking_for_money_key"
    }
}