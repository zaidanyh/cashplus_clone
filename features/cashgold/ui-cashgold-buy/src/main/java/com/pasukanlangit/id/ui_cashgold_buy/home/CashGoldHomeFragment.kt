package com.pasukanlangit.id.ui_cashgold_buy.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.pasukanlangit.id.core.CASHGOLD_WITHDRAW_HOME_PATH
import com.pasukanlangit.id.core.KYC_INIT_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.model.ChartResponse
import com.pasukanlangit.id.ui_cashgold_buy.R
import com.pasukanlangit.id.ui_cashgold_buy.buy.BuyChooserActivity
import com.pasukanlangit.id.ui_cashgold_buy.databinding.FragmentCashGoldHomeBinding
import com.pasukanlangit.id.ui_cashgold_buy.register.RegisterCashGoldActivity
import com.pasukanlangit.id.ui_cashgold_buy.utils.CustomMarkerView
import com.pasukanlangit.id.ui_core.components.GenericCashGoldModalDialog
import com.pasukanlangit.id.ui_core.utils.getPrimaryColor
import com.pasukanlangit.id.usecase.ChartDuration
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CashGoldHomeFragment : Fragment() {

    private var _binding: FragmentCashGoldHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CashGoldHomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCashGoldHomeBinding.inflate(layoutInflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            cardBuyGold.setOnClickListener {
                viewModel.onTriggerEvent(HomeEvent.CheckStatusRegister)
            }

            cardWithdraw.setOnClickListener {
                startActivity(
                    ModuleRoute.internalIntent(requireContext(), CASHGOLD_WITHDRAW_HOME_PATH)
                )
            }

            noteKyc.setOnClickListener {
                startActivity(ModuleRoute.internalIntent(requireContext(), KYC_INIT_PATH))
            }

            rb1Month.setOnClickListener { viewModel.onTriggerEvent(HomeEvent.GetChart(ChartDuration.A_MONTH)) }
            rb3Month.setOnClickListener { viewModel.onTriggerEvent(HomeEvent.GetChart(ChartDuration.THREE_MONTH)) }
            rb1Year.setOnClickListener { viewModel.onTriggerEvent(HomeEvent.GetChart(ChartDuration.A_YEAR)) }
        }


        initLineChart()
        collectAllState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(HomeEvent.CheckKycStatus)
        viewModel.onTriggerEvent(HomeEvent.GetUserBalance)
        viewModel.onTriggerEvent(HomeEvent.GetGoldPrice)
        viewModel.onTriggerEvent(HomeEvent.GetChart(ChartDuration.A_MONTH))
        binding.rb1Month.isChecked = true
    }

    private fun collectAllState() {
        collectStateLoading()
        collectStateError()
        collectGoldPrice()
        collectBalanceGold()
        collectChart()
        collectStatusKyc()
        collectStatusRegister()
    }

    private fun collectStatusKyc() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showKycNotif.collectLatest {
                    it?.let { value ->
                        binding.noteKyc.isVisible = value
                    }
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun collectStatusRegister() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stateStatusRegister.collectLatest {
                    it?.let { status ->
                        if(status){
                            startToBuyActivity()
                        }else{
                            GenericCashGoldModalDialog.Builder()
                                .title(getString(R.string.warning))
                                .description("Untuk menggunakan fitur ini silahkan daftar terlebih dahulu.")
                                .image(R.drawable.ilustration_warning)
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.register),
                                        onBtnClicked = {
                                            startActivity(
                                                Intent(requireContext(), RegisterCashGoldActivity::class.java)
                                            )
                                        }
                                    )
                                )
                                .buttonNegative(
                                    NegativeButtonAction(
                                        btnLabel = getString(R.string.close),
                                        setClickOnDismiss = true
                                    )
                                )
                                .show(childFragmentManager)
                        }
                    }
                }
            }
        }
    }

    @FlowPreview
    private fun startToBuyActivity(){
        startActivity(
            Intent(requireContext(), BuyChooserActivity::class.java)
        )
    }


    private fun collectChart() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stateChart.collectLatest { chart ->
                    if(chart.isNotEmpty()){
                        setDataChart(chart)
                    }
                }
            }
        }
    }

    private fun collectBalanceGold() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stateBalance.collectLatest {
                    it?.let { saldo ->
                        binding.coinWithLabel.setValue(saldo)
                    }
                }
            }
        }
    }

    private fun collectGoldPrice() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stateGoldPrice.collectLatest {
                    it?.let { price ->
                        binding.cardInfoPriceGold.setPrice(price)
                    }
                }
            }
        }
    }

    private fun collectStateError() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stateError.collectLatest { message ->
                    message.peek()?.let { info ->
                        val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                        toast.show()
                        delay(toast.duration.toLong() + 500L)
                        viewModel.onTriggerEvent(HomeEvent.RemoveHeadQueue)
                    }
                }
            }
        }
    }

    private fun collectStateLoading() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stateLoading.collectLatest { isLoading ->
                    binding.loading.isVisible = isLoading
                }
            }
        }
    }


    private fun setDataChart(charts: List<ChartResponse>) {
        //now draw bar chart with dynamic data
        val entries: ArrayList<Entry> = ArrayList()

        //you can replace this data object with  your custom object
        for (i in charts.indices) {
            val chart = charts[i]
            entries.add(Entry(i.toFloat(), chart.highPrice.toFloatOrNull() ?: 0f, "<b>${chart.date}</b><br>${getNumberRupiah(chart.highPrice.toIntOrNull() ?: 0)}"))
        }

        val lineDataSet = LineDataSet(entries, "")
        lineDataSet.lineWidth = 1.5f
        //disable draw circle point
        lineDataSet.setDrawCircles(false)
        //disable value point in line
        lineDataSet.setDrawValues(false)
        //to enable background color line gradient
        lineDataSet.setDrawFilled(true)
        //set line color
        lineDataSet.color = requireContext().getPrimaryColor()
        //set gradient color below line
        lineDataSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_yellow_white)

        val data = LineData(lineDataSet)
        binding.lineChart.data = data
        binding.lineChart.invalidate()
    }

    private fun initLineChart() {
        binding.lineChart.apply{
            setTouchEnabled(true)
            marker = CustomMarkerView(requireContext(), R.layout.layout_marker_chart)
            //        hide grid lines
            axisLeft.setDrawGridLines(false)
            val xAxis: XAxis = xAxis
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)

            //remove right y-axis
            axisRight.isEnabled = false
            axisLeft.isEnabled = true

            //remove legend
            legend.isEnabled = false

            //remove description label
            description.isEnabled = false

            //add animation
            animateX(1000, Easing.EaseInSine)

            // to draw label on xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
    //            xAxis.valueFormatter = MyAxisFormatter(charts)
            xAxis.setDrawLabels(false)
    //            setBackgroundColor(context.getThemeColor(R.attr.colorPrimaryVariant))
            xAxis.granularity = 1f
    //            xAxis.labelRotationAngle = +90f
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}