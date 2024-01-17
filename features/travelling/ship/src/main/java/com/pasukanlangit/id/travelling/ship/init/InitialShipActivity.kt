package com.pasukanlangit.id.travelling.ship.init

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
import com.pasukanlangit.id.travelling.ship.R
import com.pasukanlangit.id.travelling.ship.databinding.ActivityInitialShipBinding
import com.pasukanlangit.id.travelling.ship.schedule.ShipScheduleActivity
import com.pasukanlangit.id.travelling.ship.temp.ShipPassengers
import com.pasukanlangit.id.travelling.ship.temp.ShipRoute
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class InitialShipActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInitialShipBinding
    private val viewModel: InitialShipViewModel by viewModel()

    private val calendar = Calendar.getInstance()
    private lateinit var sdf: SimpleDateFormat
    private val sdfReq = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var dateRequest = sdfReq.format(calendar.time)

    private var departure: ShipRoute? = null
    private var destination: ShipRoute? = null
    private var passengers: ShipPassengers? = null

    private var stateDeparture: Boolean = false
    private var stateDestination: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitialShipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        sdf = SimpleDateFormat("dd MMMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))

        val isChange = intent.getBooleanExtra(IS_CHANGE_HARBOR_DESTINATION, false)
        var dateShow = calendar.time
        if (isChange) {
            dateRequest = intent.getStringExtra(SHIP_DATE_KEY) ?: sdfReq.format(calendar.time)
            dateShow = sdfReq.parse(dateRequest) as Date
            with(viewModel) {
                intent.parcelable<ShipRoute>(SHIP_DEPARTURE_KEY)?.let {
                    ShipEvents.SetDeparture(it)
                }?.let { onTriggerEvents(it) }
                intent.parcelable<ShipRoute>(SHIP_DESTINATION_KEY)?.let {
                    ShipEvents.SetDestination(it)
                }?.let { onTriggerEvents(it) }
                intent.parcelable<ShipPassengers>(SHIP_PASSENGERS_KEY)?.let {
                    ShipEvents.SetPassengers(it)
                }?.let { onTriggerEvents(it) }
            }
        }

        with(binding) {
            imgBack.setOnClickListener { finish() }
            tvDateDeparture.text = sdf.format(dateShow)
            tvDateDeparture.setOnClickListener { openDatePicker(isChange) }
            tvDeparture.setOnClickListener { openShipRoute() }
            tvDestination.setOnClickListener { openShipRoute(true) }
            tvPassengers.setOnClickListener {
                ShipPassengerFragment().show(supportFragmentManager, "Set Passenger")
            }
            btnFindShip.text = if (isChange) getString(R.string.confirm) else getString(R.string.find_ship)
            btnFindShip.setOnClickListener {
                if (isChange) {
                    val intent = Intent().apply {
                        putExtra(SHIP_DATE_KEY, dateRequest)
                        putExtra(SHIP_DEPARTURE_KEY, departure)
                        putExtra(SHIP_DESTINATION_KEY, destination)
                        putExtra(SHIP_PASSENGERS_KEY, passengers)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@InitialShipActivity,
                        Pair(wrapperContent, getString(R.string.layout_ship))
                    )
                    startActivity(
                        Intent(this@InitialShipActivity, ShipScheduleActivity::class.java).apply {
                            putExtra(SHIP_DATE_KEY, dateRequest)
                            putExtra(SHIP_DEPARTURE_KEY, departure)
                            putExtra(SHIP_DESTINATION_KEY, destination)
                            putExtra(SHIP_PASSENGERS_KEY, passengers)
                        }, options.toBundle()
                    )
                }
            }
        }
        collectDataTrain()
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

    private fun openShipRoute(isDestination: Boolean = false) {
        ShipRouteFragment.newInstance(isDestination).show(supportFragmentManager, "Ship Route")
    }

    private fun stateButton() {
        binding.btnFindShip.isEnabled = stateDeparture && stateDestination
    }

    private fun collectDataTrain() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE DEPARTURE
                launch {
                    viewModel.departure.collectLatest {
                        binding.tvDeparture.text = it?.name ?: getString(R.string.choose_harbor)
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
                        binding.tvDestination.text = it?.name ?: getString(R.string.choose_harbor)
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
                            R.string.harbor_passenger_list,
                            ((it?.male ?: 1).plus(it?.female ?: 0)).toString(),
                            (it?.infant ?: 0).toString()
                        )
                        passengers = it ?: ShipPassengers(1, 0)
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
        const val IS_CHANGE_HARBOR_DESTINATION = "is_change_harbor_destination"
        const val SHIP_DATE_KEY = "ship_date_key"
        const val SHIP_DEPARTURE_KEY = "ship_departure_key"
        const val SHIP_DESTINATION_KEY = "ship_destination_key"
        const val SHIP_PASSENGERS_KEY = "ship_passengers_key"
    }
}