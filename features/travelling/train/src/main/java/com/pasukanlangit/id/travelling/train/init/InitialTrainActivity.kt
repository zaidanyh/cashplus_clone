package com.pasukanlangit.id.travelling.train.init

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
import com.pasukanlangit.id.travelling.train.R
import com.pasukanlangit.id.travelling.train.databinding.ActivityInitialTrainBinding
import com.pasukanlangit.id.travelling.train.schedule.TrainScheduleActivity
import com.pasukanlangit.id.travelling.train.temp.Passengers
import com.pasukanlangit.id.travelling.train.temp.TrainRoute
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class InitialTrainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInitialTrainBinding
    private val viewModel: InitialTrainViewModel by viewModel()

    private val calendar = Calendar.getInstance()
    private lateinit var sdf: SimpleDateFormat
    private val sdfReq = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var dateRequest = sdfReq.format(calendar.time)

    private var departure: TrainRoute? = null
    private var destination: TrainRoute? = null
    private var passengers: Passengers? = null

    private var stateDeparture: Boolean = false
    private var stateDestination: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitialTrainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        sdf = SimpleDateFormat("dd MMMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))

        val isChange = intent.getBooleanExtra(IS_CHANGE_TRAIN_DESTINATION, false)
        var dateShow = calendar.time
        if (isChange) {
            dateRequest = intent.getStringExtra(TRAIN_DATE_KEY) ?: sdfReq.format(calendar.time)
            dateShow = sdfReq.parse(dateRequest) as Date
            with(viewModel) {
                intent.parcelable<TrainRoute>(TRAIN_DEPARTURE_KEY)?.let {
                    TrainEvents.SetDeparture(it)
                }?.let { onTriggerEvents(it) }
                intent.parcelable<TrainRoute>(TRAIN_DESTINATION_KEY)?.let {
                    TrainEvents.SetDestination(it)
                }?.let { onTriggerEvents(it) }
                intent.parcelable<Passengers>(PASSENGERS_KEY)?.let {
                    TrainEvents.SetPassengers(it)
                }?.let { onTriggerEvents(it) }
            }
        }

        with(binding) {
            imgBack.setOnClickListener { finish() }
            tvDateDeparture.text = sdf.format(dateShow)
            tvDateDeparture.setOnClickListener { openDatePicker(isChange) }
            tvDeparture.setOnClickListener { openTrainRoute() }
            tvDestination.setOnClickListener { openTrainRoute(true) }
            tvPassengers.setOnClickListener {
                PassengerFragment().show(supportFragmentManager, "Set Passenger")
            }
            btnFindTrain.text = if (isChange) getString(R.string.confirm) else getString(R.string.find_train)
            btnFindTrain.setOnClickListener {
                if (isChange) {
                    val intent = Intent().apply {
                        putExtra(TRAIN_DATE_KEY, dateRequest)
                        putExtra(TRAIN_DEPARTURE_KEY, departure)
                        putExtra(TRAIN_DESTINATION_KEY, destination)
                        putExtra(PASSENGERS_KEY, passengers)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@InitialTrainActivity,
                        Pair(wrapperContent, getString(R.string.layout_train))
                    )
                    startActivity(
                        Intent(this@InitialTrainActivity, TrainScheduleActivity::class.java).apply {
                            putExtra(TRAIN_DATE_KEY, dateRequest)
                            putExtra(TRAIN_DEPARTURE_KEY, departure)
                            putExtra(TRAIN_DESTINATION_KEY, destination)
                            putExtra(PASSENGERS_KEY, passengers)
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

    private fun openTrainRoute(isDestination: Boolean = false) {
        TrainRouteFragment.newInstance(isDestination).show(supportFragmentManager, "Train Route")
    }

    private fun stateButton() {
        binding.btnFindTrain.isEnabled = stateDeparture && stateDestination
    }

    private fun collectDataTrain() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE DEPARTURE
                launch {
                    viewModel.departure.collectLatest {
                        binding.tvDeparture.text = it?.location ?: getString(R.string.choose_train_departure)
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
                        binding.tvDestination.text = it?.location ?: getString(R.string.choose_train_destination)
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
                            R.string.train_passenger_list,
                            (it?.adult ?: 1).toString(),
                            (it?.infant ?: 0).toString()
                        )
                        passengers = it ?: Passengers(1, 0)
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
        const val IS_CHANGE_TRAIN_DESTINATION = "is_change_train_destination"
        const val TRAIN_DATE_KEY = "train_date_key"
        const val TRAIN_DEPARTURE_KEY = "train_departure_key"
        const val TRAIN_DESTINATION_KEY = "train_destination_key"
        const val PASSENGERS_KEY = "passengers_key"
    }
}