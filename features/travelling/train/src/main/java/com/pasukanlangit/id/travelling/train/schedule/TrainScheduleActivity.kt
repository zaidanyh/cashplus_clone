package com.pasukanlangit.id.travelling.train.schedule

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.id.core.MAIN_ACTIVITY_FORWARDING_TO_HELP
import com.pasukanlangit.id.core.MAIN_ACTIVITY_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.travelling.train.R
import com.pasukanlangit.id.travelling.train.databinding.ActivityTrainScheduleBinding
import com.pasukanlangit.id.travelling.train.detailprice.TrainPriceActivity
import com.pasukanlangit.id.travelling.train.init.InitialTrainActivity
import com.pasukanlangit.id.travelling.train.temp.Passengers
import com.pasukanlangit.id.travelling.train.temp.TrainBooking
import com.pasukanlangit.id.travelling.train.temp.TrainRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class TrainScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainScheduleBinding
    private val viewModel: TrainScheduleViewModel by viewModel()

    private lateinit var scheduleAdapter: TrainScheduleAdapter
    private var trainSession: String? = null
    private var dateReq: String? = null
    private var departure: TrainRoute? = null
    private var arrival: TrainRoute? = null
    private var passenger: Passengers? = null

    private val changeDestination = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            dateReq = result?.data?.getStringExtra(InitialTrainActivity.TRAIN_DATE_KEY)
            departure = result?.data?.parcelable(InitialTrainActivity.TRAIN_DEPARTURE_KEY)
            arrival = result?.data?.parcelable(InitialTrainActivity.TRAIN_DESTINATION_KEY)
            passenger = result?.data?.parcelable(InitialTrainActivity.PASSENGERS_KEY)
            viewModel.onTriggerEvents(
                TrainScheduleEvents.TrainSchedule(
                    departure?.code.toString(),
                    arrival?.code.toString(),
                    dateReq.toString(),
                    passenger?.adult.toString(), passenger?.infant.toString()
                )
            )
            summaryTrain(dateReq, departure, arrival, passenger)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        dateReq = intent.getStringExtra(InitialTrainActivity.TRAIN_DATE_KEY)
        departure = intent.parcelable(InitialTrainActivity.TRAIN_DEPARTURE_KEY)
        arrival = intent.parcelable(InitialTrainActivity.TRAIN_DESTINATION_KEY)
        passenger = intent.parcelable(InitialTrainActivity.PASSENGERS_KEY)

        viewModel.onTriggerEvents(
            TrainScheduleEvents.TrainSchedule(
                departure?.code.toString(),
                arrival?.code.toString(),
                dateReq.toString(),
                passenger?.adult.toString(), passenger?.infant.toString()
            )
        )

        summaryTrain(dateReq, departure, arrival, passenger)
        with(binding) {
            imgBack.setOnClickListener { finish() }
            btnChange.setOnClickListener {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@TrainScheduleActivity,
                    Pair(wrapperContent, getString(R.string.layout_train))
                )
                changeDestination.launch(
                    Intent(this@TrainScheduleActivity, InitialTrainActivity::class.java).apply {
                        putExtra(InitialTrainActivity.IS_CHANGE_TRAIN_DESTINATION, true)
                        putExtra(InitialTrainActivity.TRAIN_DATE_KEY, dateReq)
                        putExtra(InitialTrainActivity.TRAIN_DEPARTURE_KEY, departure)
                        putExtra(InitialTrainActivity.TRAIN_DESTINATION_KEY, arrival)
                        putExtra(InitialTrainActivity.PASSENGERS_KEY, passenger)
                    }, options
                )
            }
            scheduleAdapter = TrainScheduleAdapter {
                val trainBook = trainSession?.let { session ->
                    TrainBooking(
                        session = session,
                        trainCode = it.code,
                        trainClass = it.trainClass,
                        trainSubClass = it.subClass
                    )
                }
                startActivity(
                    Intent(this@TrainScheduleActivity, TrainPriceActivity::class.java).apply {
                        putExtra(TrainPriceActivity.TRAIN_DATE_KEY, dateReq)
                        putExtra(TrainPriceActivity.DEPARTURE_KEY, departure)
                        putExtra(TrainPriceActivity.DESTINATION_KEY, arrival)
                        putExtra(TrainPriceActivity.PASSENGERS_KEY, passenger)
                        putExtra(TrainPriceActivity.TRAIN_BOOKING_KEY, trainBook)
                    }
                )
            }
            with(rvTrainSchedule) {
                layoutManager = LinearLayoutManager(this@TrainScheduleActivity)
                adapter = scheduleAdapter
                addItemDecoration(CashplusItemDecoration(24))
            }
        }
        collectDataTrainSchedule()
    }

    private fun summaryTrain(dateReq: String?, departure: TrainRoute?, arrival: TrainRoute?, passenger: Passengers?) {
        val sdfData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dataDate = sdfData.parse(dateReq.toString())
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
        val departureSplit = departure?.location?.split(",")
        val arrivalSplit = arrival?.location?.split(",")
        val departureCity = departureSplit?.last() ?: departure?.location
        val arrivalCity = arrivalSplit?.last() ?: arrival?.location
        with(binding) {
            tvDate.text = sdf.format(dataDate as Date)
            tvDeparture.text = getString(R.string.two_format_with_brackets, departureCity, departure?.code)
            tvDestination.text = getString(R.string.two_format_with_brackets, arrivalCity, arrival?.code)
            tvPassengers.text = getString(R.string.train_passenger_list, passenger?.adult.toString(), passenger?.infant.toString())
        }
    }

    private fun collectDataTrainSchedule() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateLoading.collectLatest {
                        with(binding) {
                            if (it) {
                                rvTrainSchedule.isVisible = false
                                rvTrainScheduleShimmer.isVisible = true
                                rvTrainScheduleShimmer.startShimmer()
                            } else {
                                rvTrainSchedule.isVisible = true
                                rvTrainScheduleShimmer.isVisible = false
                                rvTrainScheduleShimmer.stopShimmer()
                            }
                        }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(this@TrainScheduleActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvents(TrainScheduleEvents.RemoveHeadMessage)
                            }
                        }
                    }
                }
                launch {
                    viewModel.schedule.collectLatest {
                        if (it?.rc == "30") {
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.something_wrong))
                                .image(R.drawable.ilustration_warning)
                                .description(it.message)
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.call_us_cs),
                                        onBtnClicked = {
                                            finishAffinity()
                                            startActivity(
                                                ModuleRoute.internalIntent(this@TrainScheduleActivity, MAIN_ACTIVITY_PATH).apply {
                                                    putExtra(MAIN_ACTIVITY_FORWARDING_TO_HELP, true)
                                                }
                                            )
                                        }
                                    )
                                )
                                .buttonNegative(
                                    NegativeButtonAction(
                                        btnLabel = getString(R.string.close),
                                        setClickOnDismiss = true
                                    )
                                ).show(supportFragmentManager)
                        } else if (it?.rc == "00") {
                            trainSession = it.session
                            scheduleAdapter.submitList(it.data)
                        }
                    }
                }
            }
        }
    }
}