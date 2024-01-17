package com.pasukanlangit.id.travelling.ship.schedule

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
import com.pasukanlangit.id.travelling.domain.model.ship.HarborSchedules
import com.pasukanlangit.id.travelling.ship.R
import com.pasukanlangit.id.travelling.ship.databinding.ActivityShipScheduleBinding
import com.pasukanlangit.id.travelling.ship.fill.FillDataShipActivity
import com.pasukanlangit.id.travelling.ship.init.InitialShipActivity
import com.pasukanlangit.id.travelling.ship.temp.ShipPassengers
import com.pasukanlangit.id.travelling.ship.temp.ShipRoute
import com.pasukanlangit.id.travelling.ship.temp.ShipSchedules
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class ShipScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShipScheduleBinding
    private val viewModel: ShipScheduleViewModel by viewModel()

    private lateinit var scheduleAdapter: ShipScheduleAdapter
    private var dateReq: String? = null
    private var departure: ShipRoute? = null
    private var arrival: ShipRoute? = null
    private var passengers: ShipPassengers? = null

    private val changeDestination = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            dateReq = result.data?.getStringExtra(InitialShipActivity.SHIP_DATE_KEY)
            departure = result.data?.parcelable(InitialShipActivity.SHIP_DEPARTURE_KEY)
            arrival = result.data?.parcelable(InitialShipActivity.SHIP_DESTINATION_KEY)
            passengers = result.data?.parcelable(InitialShipActivity.SHIP_PASSENGERS_KEY)
            viewModel.onTriggerEvents(
                ShipScheduleEvents.ShipSchedule(
                    departure?.code.toString(), arrival?.code.toString(), dateReq.toString(),
                    passengers
                )
            )
            setShipSummary(dateReq, departure, arrival, passengers)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShipScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        dateReq = intent.getStringExtra(InitialShipActivity.SHIP_DATE_KEY)
        departure = intent.parcelable(InitialShipActivity.SHIP_DEPARTURE_KEY)
        arrival = intent.parcelable(InitialShipActivity.SHIP_DESTINATION_KEY)
        passengers = intent.parcelable(InitialShipActivity.SHIP_PASSENGERS_KEY)

        viewModel.onTriggerEvents(
            ShipScheduleEvents.ShipSchedule(
                departure?.code.toString(), arrival?.code.toString(), dateReq.toString(),
                passengers
            )
        )

        setShipSummary(dateReq, departure, arrival, passengers)
        with(binding) {
            imgBack.setOnClickListener { finish() }
            btnChange.setOnClickListener {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@ShipScheduleActivity,
                    Pair(wrapperContent, getString(R.string.layout_ship))
                )
                changeDestination.launch(
                    Intent(this@ShipScheduleActivity, InitialShipActivity::class.java).apply {
                        putExtra(InitialShipActivity.IS_CHANGE_HARBOR_DESTINATION, true)
                        putExtra(InitialShipActivity.SHIP_DATE_KEY, dateReq)
                        putExtra(InitialShipActivity.SHIP_DEPARTURE_KEY, departure)
                        putExtra(InitialShipActivity.SHIP_DESTINATION_KEY, arrival)
                        putExtra(InitialShipActivity.SHIP_PASSENGERS_KEY, passengers)
                    }, options
                )
            }
            scheduleAdapter = ShipScheduleAdapter()
            scheduleAdapter.setOnClickListener(object: ShipScheduleAdapter.OnButtonClickListener {
                override fun onClickHarborSchedule(harbor: HarborSchedules) {
                    val shipSchedule = ShipSchedules(
                        name = harbor.name, number = harbor.number, code = harbor.code,
                        shipClass = harbor.shipClass, basicFare = harbor.basicFare, admin = harbor.admin,
                        price = harbor.price, maleSeat = harbor.maleSeat, femaleSeat = harbor.femaleSeat
                    )
                    MoreInfoScheduleFragment.newInstance(shipSchedule).show(supportFragmentManager, "More Info Schedule")
                }

                override fun onChooseHarborSchedule(harbor: HarborSchedules) {
                    val shipSchedule = ShipSchedules(
                        name = harbor.name, number = harbor.number, code = harbor.code,
                        shipClass = harbor.shipClass, basicFare = harbor.basicFare, admin = harbor.admin,
                        price = harbor.price, maleSeat = harbor.maleSeat, femaleSeat = harbor.femaleSeat
                    )
                    startActivity(
                        Intent(this@ShipScheduleActivity, FillDataShipActivity::class.java).apply {
                            putExtra(FillDataShipActivity.SHIP_DATE_KEY, dateReq)
                            putExtra(FillDataShipActivity.SHIP_DEPARTURE_KEY, departure)
                            putExtra(FillDataShipActivity.SHIP_DESTINATION_KEY, arrival)
                            putExtra(FillDataShipActivity.SHIP_PASSENGERS_KEY, passengers)
                            putExtra(FillDataShipActivity.SHIP_SCHEDULE_KEY, shipSchedule)
                        }
                    )
                }
            })
            with(rvShipSchedule) {
                layoutManager = LinearLayoutManager(this@ShipScheduleActivity)
                adapter = scheduleAdapter
                addItemDecoration(CashplusItemDecoration(24))
            }
        }
        collectShipSchedule()
    }

    private fun setShipSummary(dateReq: String?, departure: ShipRoute?, arrival: ShipRoute?, passengers: ShipPassengers?) {
        val sdfData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dataDate = sdfData.parse(dateReq.toString())
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
        val departureList = departure?.name?.split(",")
        val destinationList = arrival?.name?.split(",")
        with(binding) {
            tvDate.text = sdf.format(dataDate as Date)
            tvCityHarborDeparture.isVisible = departureList != null
            tvCityHarborDestination.isVisible = destinationList != null
            tvNameHarborDeparture.text = if (departureList.isNullOrEmpty()) departure?.name else departureList.first().trim()
            tvNameHarborDestination.text = if (destinationList.isNullOrEmpty()) arrival?.name else destinationList.first().trim()
            tvCityHarborDeparture.text = departureList?.last()?.trim() ?: ""
            tvCityHarborDestination.text = destinationList?.last()?.trim() ?: ""
            tvPassengers.text = getString(
                R.string.harbor_passenger_list,
                passengers?.male?.plus(passengers.female).toString(),
                passengers?.infant.toString()
            )
        }
    }

    private fun collectShipSchedule() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateLoading.collectLatest {
                        with(binding) {
                            if (it) {
                                rvShipSchedule.isVisible = false
                                rvShipScheduleShimmer.isVisible = true
                                rvShipScheduleShimmer.startShimmer()
                            } else {
                                rvShipSchedule.isVisible = true
                                rvShipScheduleShimmer.isVisible = false
                                rvShipScheduleShimmer.stopShimmer()
                            }
                        }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(this@ShipScheduleActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvents(ShipScheduleEvents.RemoveHeadMessage)
                            }
                        }
                    }
                }
                launch {
                    viewModel.shipSchedule.collectLatest {
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
                                                ModuleRoute.internalIntent(this@ShipScheduleActivity, MAIN_ACTIVITY_PATH).apply {
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