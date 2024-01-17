package com.pasukanlangit.id.travelling.hotel.viewall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.id.core.*
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.travelling.hotel.InitialHotelActivity
import com.pasukanlangit.id.travelling.hotel.R
import com.pasukanlangit.id.travelling.hotel.databinding.ActivityHotelViewAllBinding
import com.pasukanlangit.id.travelling.hotel.find.FindDetailHotelActivity
import com.pasukanlangit.id.travelling.hotel.findbycity.FindHotelByCityViewModel
import com.pasukanlangit.id.travelling.hotel.findbycity.HotelByCityEvents
import com.pasukanlangit.id.travelling.hotel.temp.CitySelected
import com.pasukanlangit.id.travelling.hotel.temp.getLocalCityList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class HotelViewAllActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHotelViewAllBinding
    private val viewModel: FindHotelByCityViewModel by viewModel()

    private var checkIn = Calendar.getInstance().time.time
    private var checkOut = Calendar.getInstance().time.time + 86400000

    private var city: CitySelected? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelViewAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sdfData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateCheckIn = sdfData.format(checkIn)
        val dateCheckOut = sdfData.format(checkOut)
        val citySelected = getLocalCityList(this).singleOrNull { city -> city.isActive }
        city = CitySelected(citySelected?.cityName.toString(), citySelected?.cityCode.toString())

        viewModel.onTriggerEvent(
            HotelByCityEvents.FindHotelByCity(
                citySelected?.cityCode ?: "",
                dateCheckIn, dateCheckOut, "1"
            )
        )

        with(binding) {
            appBar.setOnBackPressed { finish() }
            with(rvCityList) {
                layoutManager = LinearLayoutManager(
                    this@HotelViewAllActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                addItemDecoration(CashplusItemDecoration(10))
                adapter = ViewAllCityListAdapter(getLocalCityList(this@HotelViewAllActivity)) {
                    if (it.cityCode != "-1") {
                        city = CitySelected(it.cityName, it.cityCode)
                        viewModel.onTriggerEvent(
                            HotelByCityEvents.FindHotelByCity(
                                it.cityCode, dateCheckIn, dateCheckOut, "1"
                            )
                        )
                    } else {
                        startActivity(
                            Intent(this@HotelViewAllActivity, InitialHotelActivity::class.java)
                        )
                        finish()
                    }
                }
            }
            with(rvHotelList) {
                layoutManager = LinearLayoutManager(this@HotelViewAllActivity)
                addItemDecoration(CashplusItemDecoration(24))
            }
        }
        collectData()
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateLoading.collectLatest {
                        with(binding) {
                            if (it) {
                                rvHotelList.isVisible = false
                                rvHotelListShimmer.isVisible = true
                                rvHotelListShimmer.startShimmer()
                            } else {
                                rvHotelList.isVisible = true
                                rvHotelListShimmer.isVisible = false
                                rvHotelListShimmer.stopShimmer()
                            }
                        }
                    }
                }

                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(this@HotelViewAllActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                            }
                            viewModel.onTriggerEvent(HotelByCityEvents.RemoveHeadMessage)
                        }
                    }
                }

                launch {
                    viewModel.hotelByCity.collectLatest { data ->
                        if (data?.rc == "30") {
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.something_wrong))
                                .image(R.drawable.ilustration_warning)
                                .description(data.message)
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.call_us_cs),
                                        onBtnClicked = {
                                            finishAffinity()
                                            startActivity(
                                                ModuleRoute.internalIntent(this@HotelViewAllActivity, MAIN_ACTIVITY_PATH).apply {
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
                        } else if (data?.rc == "00") {
                            binding.rvHotelList.adapter = data.data?.let {
                                HotelListViewAllAdapter(it) {
                                    startActivity(
                                        Intent(this@HotelViewAllActivity, FindDetailHotelActivity::class.java).apply {
                                            putExtra(HOTEL_CITY_SELECTED, city)
                                            putExtra(CHECK_IN_HOTEL, checkIn)
                                            putExtra(CHECK_OUT_HOTEL, checkOut)
                                            putExtra(IS_DESTINATION_EDIT, true)
                                            putExtra(HOTEL_CODE, it.code)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}