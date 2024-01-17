package com.pasukanlangit.id.travelling.flight

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.travelling.flight.databinding.ActivityFlightBinding
import com.pasukanlangit.id.travelling.flight.schedule.FlightScheduleActivity
import com.pasukanlangit.id.travelling.flight.temp.FlightRoute
import com.pasukanlangit.id.travelling.flight.temp.Passengers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class FlightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlightBinding
    private val viewModel: FlightViewModel by viewModel()

    private val calendar = Calendar.getInstance()
    private lateinit var sdf: SimpleDateFormat
    private val sdfReq = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var dateRequest = sdfReq.format(calendar.time)

    private var departure: FlightRoute? = null
    private var destination: FlightRoute? = null
    private var passengers: Passengers? = null

    private var stateDeparture: Boolean = false
    private var stateDestination: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        sdf = SimpleDateFormat("dd MMMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))

        val isChange = intent.getBooleanExtra(IS_CHANGE_FLIGHT_DESTINATION, false)
        var dateShow = calendar.time
        if (isChange) {
            dateRequest = intent.getStringExtra(FLIGHT_DATE_KEY) ?: sdfReq.format(calendar.time)
            dateShow = sdfReq.parse(dateRequest) as Date
            with(viewModel) {
                intent.parcelable<FlightRoute>(FLIGHT_DEPARTURE_KEY)?.let {
                    FlightEvents.SetDeparture(it)
                }?.let { onTriggerEvent(it) }
                intent.parcelable<FlightRoute>(FLIGHT_DESTINATION_KEY)?.let {
                    FlightEvents.SetDestination(it)
                }?.let { onTriggerEvent(it) }
                intent.parcelable<Passengers>(PASSENGERS_KEY)?.let {
                    FlightEvents.SetPassengers(it)
                }?.let { onTriggerEvent(it) }
            }
        }

        with(binding) {
            imgBack.setOnClickListener { finish() }
            tvDateDeparture.text = sdf.format(dateShow)
            tvDateDeparture.setOnClickListener { openDatePicker(isChange) }
            tvDeparture.setOnClickListener { openFlightRoute() }
            tvDestination.setOnClickListener { openFlightRoute(true) }
            tvPassengers.setOnClickListener {
                BottomSheetPassengerFragment().show(supportFragmentManager, "Set Passenger")
            }
            btnFindTicket.text = if (isChange) getString(R.string.confirm) else getString(R.string.find_ticket)
            btnFindTicket.setOnClickListener {
                if (isChange) {
                    val intent = Intent().apply {
                        putExtra(FLIGHT_DATE_KEY, dateRequest)
                        putExtra(FLIGHT_DEPARTURE_KEY, departure)
                        putExtra(FLIGHT_DESTINATION_KEY, destination)
                        putExtra(PASSENGERS_KEY, passengers)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@FlightActivity,
                        Pair.create(binding.wrapperContent, getString(R.string.layout_flight))
                    )
                    startActivity(
                        Intent(this@FlightActivity, FlightScheduleActivity::class.java).apply {
                            putExtra(FLIGHT_DATE_KEY, dateRequest)
                            putExtra(FLIGHT_DEPARTURE_KEY, departure)
                            putExtra(FLIGHT_DESTINATION_KEY, destination)
                            putExtra(PASSENGERS_KEY, passengers)
                        }, options.toBundle()
                    )
                }
            }
        }

        collectDataFlight()
    }

    private fun openDatePicker(isChange: Boolean) {
        val year: Int
        val month: Int
        val day: Int
        if (isChange) {
            val date = dateRequest.split("-")
            year = date.first().toInt()
            month = date[1].toInt() - 1
            day = date.last().toInt()
        } else {
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
        }

        val datePickerDialog = DatePickerDialog(
            this, R.style.DatePickerCustom,
            { _, _, _, _ -> }, year, month, day
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.choose)) { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                val datePicker = datePickerDialog.datePicker

                val dayPicked = datePicker.dayOfMonth
                val monthPicked = datePicker.month
                val yearPicked = datePicker.year
                calendar.set(yearPicked, monthPicked, dayPicked)

                val formatDate = sdf.format(calendar.time)
                dateRequest = sdfReq.format(calendar.time)
                binding.tvDateDeparture.text = formatDate
            }
        }
        datePickerDialog.show()
    }

    private fun openFlightRoute(isDestination: Boolean = false) {
        FlightRouteFragment.newInstance(isDestination).show(supportFragmentManager, "Flight Route")
    }

    private fun stateButton() {
        binding.btnFindTicket.isEnabled = stateDeparture && stateDestination
    }

    private fun collectDataFlight() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE DEPARTURE
                launch {
                    viewModel.departure.collectLatest {
                        binding.tvDeparture.text = it?.airport ?: getString(R.string.choose_departure)
                        if (it != null) {
                            departure = it
                            stateDeparture = true
                        }
                        stateButton()
                    }
                }

                // STATE DESTINATION
                launch {
                    viewModel.destination.collectLatest {
                        binding.tvDestination.text = it?.airport ?: getString(R.string.choose_destination)
                        if (it != null) {
                            destination = it
                            stateDestination = true
                        }
                        stateButton()
                    }
                }

                // STATE PASSENGERS
                launch {
                    viewModel.passengers.collectLatest {
                        binding.tvPassengers.text = getString(
                            R.string.passenger_list,
                            (it?.adult ?: 1).toString(),
                            (it?.child ?: 0).toString(),
                            (it?.infant ?: 0).toString()
                        )
                        passengers = it ?: Passengers(1, 0, 0)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        stateButton()
    }

    companion object {
        const val IS_CHANGE_FLIGHT_DESTINATION = "is_change_flight_destination"
        const val FLIGHT_DATE_KEY = "flight_date_key"
        const val FLIGHT_DEPARTURE_KEY = "flight_departure_key"
        const val FLIGHT_DESTINATION_KEY = "flight_destination_key"
        const val PASSENGERS_KEY = "passengers_key"
    }
}