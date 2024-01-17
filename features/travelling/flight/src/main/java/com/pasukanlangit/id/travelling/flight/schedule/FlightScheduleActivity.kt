package com.pasukanlangit.id.travelling.flight.schedule

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
import com.pasukanlangit.id.travelling.flight.FlightActivity
import com.pasukanlangit.id.travelling.flight.R
import com.pasukanlangit.id.travelling.flight.databinding.ActivityFlightScheduleBinding
import com.pasukanlangit.id.travelling.flight.detail.FlightPriceActivity
import com.pasukanlangit.id.travelling.flight.temp.FlightRoute
import com.pasukanlangit.id.travelling.flight.temp.Passengers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class FlightScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlightScheduleBinding
    private val viewModel: FlightScheduleViewModel by viewModel()

    private lateinit var scheduleAdapter: FlightScheduleAdapter
    private var dateReq: String? = null
    private var departure: FlightRoute? = null
    private var destination: FlightRoute? = null
    private var passengers: Passengers? = null

    private val changeDestination = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            dateReq = result?.data?.getStringExtra(FlightActivity.FLIGHT_DATE_KEY)
            departure = result?.data?.parcelable(FlightActivity.FLIGHT_DEPARTURE_KEY)
            destination = result?.data?.parcelable(FlightActivity.FLIGHT_DESTINATION_KEY)
            passengers = result?.data?.parcelable(FlightActivity.PASSENGERS_KEY)
            viewModel.onTriggersEvent(
                ScheduleEvents.FlightSchedule(
                    departure?.code.toString(),
                    destination?.code.toString(),
                    dateReq.toString()
                )
            )
            setFlightDestination(dateReq, departure, destination, passengers)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlightScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        dateReq = intent.getStringExtra(FlightActivity.FLIGHT_DATE_KEY)
        departure = intent.parcelable(FlightActivity.FLIGHT_DEPARTURE_KEY)
        destination = intent.parcelable(FlightActivity.FLIGHT_DESTINATION_KEY)
        passengers = intent.parcelable(FlightActivity.PASSENGERS_KEY)

        viewModel.onTriggersEvent(
            ScheduleEvents.FlightSchedule(
                departure?.code.toString(),
                destination?.code.toString(),
                dateReq.toString()
            )
        )

        setFlightDestination(dateReq, departure, destination, passengers)
        with(binding) {
            imgBack.setOnClickListener { finish() }
            btnChange.setOnClickListener {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@FlightScheduleActivity,
                    Pair(wrapperContent, getString(R.string.layout_flight))
                )
                changeDestination.launch(
                    Intent(this@FlightScheduleActivity, FlightActivity::class.java).apply {
                        putExtra(FlightActivity.IS_CHANGE_FLIGHT_DESTINATION, true)
                        putExtra(FlightActivity.FLIGHT_DATE_KEY, dateReq)
                        putExtra(FlightActivity.FLIGHT_DEPARTURE_KEY, departure)
                        putExtra(FlightActivity.FLIGHT_DESTINATION_KEY, destination)
                        putExtra(FlightActivity.PASSENGERS_KEY, passengers)
                    }, options
                )
            }
            scheduleAdapter = FlightScheduleAdapter {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@FlightScheduleActivity,
                    Pair(wrapperContent, getString(R.string.layout_flight)),
                    Pair(txtFlightSchedule, getString(R.string.flight_schedule)),
                    Pair(tvDateFlight, getString(R.string.flight_date)),
                    Pair(tvDeparture, getString(R.string.departure)),
                    Pair(icArrowForward, getString(R.string.content_arrow)),
                    Pair(tvDestination, getString(R.string.destination))
                )
                startActivity(
                    Intent(this@FlightScheduleActivity, FlightPriceActivity::class.java).apply {
                        putExtra(FlightPriceActivity.DEPARTURE_KEY, departure)
                        putExtra(FlightPriceActivity.DESTINATION_KEY, destination)
                        putExtra(FlightPriceActivity.FLIGHT_DATE_KEY, it.flightDate)
                        putExtra(FlightPriceActivity.FLIGHT_CODE_KEY, it.flightCode)
                        putExtra(FlightPriceActivity.PASSENGERS_KEY, passengers)
                    }, options.toBundle()
                )
            }
            with(rvFlightSchedule) {
                layoutManager = LinearLayoutManager(this@FlightScheduleActivity)
                adapter = scheduleAdapter
                addItemDecoration(CashplusItemDecoration(24))
            }
        }
        collectFlightSchedule()
    }

    private fun setFlightDestination(dateReq: String?, departure: FlightRoute?, destination: FlightRoute?, passengers: Passengers?) {
        val sdfData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dataDate = sdfData.parse(dateReq.toString())
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale(getString(R.string.locale_language), getString(
            R.string.locale_country)))
        with(binding) {
            tvDateFlight.text = sdf.format(dataDate as Date)
            tvDeparture.text = getString(R.string.flight_route, departure?.city, departure?.code)
            tvDestination.text = getString(R.string.flight_route, destination?.city, destination?.code)
            tvPassengers.text = getString(R.string.passenger_list, passengers?.adult.toString(), passengers?.child.toString(), passengers?.infant.toString())
        }
    }

    private fun collectFlightSchedule() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE LOADING
                launch {
                    viewModel.stateLoading.collectLatest {
                        with(binding) {
                            if (it) {
                                rvFlightSchedule.isVisible = false
                                rvFlightScheduleShimmer.isVisible = true
                                rvFlightScheduleShimmer.startShimmer()
                            } else {
                                rvFlightSchedule.isVisible = true
                                rvFlightScheduleShimmer.isVisible = false
                                rvFlightScheduleShimmer.stopShimmer()
                            }
                        }
                    }
                }

                // STATE ERROR
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(this@FlightScheduleActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggersEvent(ScheduleEvents.RemoveHeadMessage)
                            }
                        }
                    }
                }

                // STATE SCHEDULE
                launch {
                    viewModel.flightSchedule.collectLatest {
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
                                                ModuleRoute.internalIntent(this@FlightScheduleActivity, MAIN_ACTIVITY_PATH).apply {
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
                            scheduleAdapter.submitList(it.data)
                        }
                    }
                }
            }
        }
    }
}