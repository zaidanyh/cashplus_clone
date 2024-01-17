package com.pasukanlangit.id.travelling.hotel

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
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.travelling.hotel.databinding.ActivityInitialHotelBinding
import com.pasukanlangit.id.travelling.hotel.findbycity.FindHotelByCityActivity
import com.pasukanlangit.id.travelling.hotel.temp.CitySelected
import com.pasukanlangit.id.travelling.hotel.temp.RoomVisitor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class InitialHotelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInitialHotelBinding
    private val viewModel: InitialHotelViewModel by viewModel()

    private val calendarStart = Calendar.getInstance()
    private val calendarEnd = Calendar.getInstance()
    private lateinit var sdf: SimpleDateFormat
    private val sdfReq = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Date for show
    private var checkIn = calendarStart.time.time
    private var checkOut = calendarEnd.time.time + 86400000

    // Date for request
    private var dateCheckIn = sdfReq.format(checkIn)
    private var dateCheckOut = sdfReq.format(checkOut)

    private var citySelected: CitySelected? = null
    private var roomVisitor: RoomVisitor? = null

    private var dateStartMills by Delegates.notNull<Long>()
    private var dateEndMills by Delegates.notNull<Long>()
    private var stateCitySelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitialHotelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        sdf = SimpleDateFormat("dd MMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))

        val isChange = intent.getBooleanExtra(IS_CHANGE_HOTEL_ORDER, false)
        val changeDest = intent.getBooleanExtra(IS_CHANGE_DESTINATION, true)
        if (isChange) {
            dateCheckIn = intent.getStringExtra(DATE_CHECK_IN)
            dateCheckOut = intent.getStringExtra(DATE_CHECK_OUT)
            checkIn = (sdfReq.parse(dateCheckIn) as Date).time
            checkOut = (sdfReq.parse(dateCheckOut) as Date).time
            with(viewModel) {
                intent.parcelable<CitySelected>(CITY_SELECTED)?.let {
                    HotelEvents.SetCitySelected(it)
                }?.let { onTriggerEvent(it) }
                intent.parcelable<RoomVisitor>(ROOM_VISITOR)?.let {
                    HotelEvents.SetRoomVisitor(it)
                }?.let { onTriggerEvent(it) }
            }
        }

        with(binding) {
            imgBack.setOnClickListener { finish() }
            dateStartMills = checkIn
            dateEndMills = checkOut
            tvDateCheckin.text = sdf.format(checkIn)
            tvDateCheckout.text = sdf.format(checkOut)
            tvCity.setOnClickListener {
                if (!changeDest) return@setOnClickListener
                else FindCityBottomSheetFragment().show(supportFragmentManager, "Find Hotel City")
            }
            tvRoomAndVisitor.setOnClickListener {
                RoomVisitorBottomSheet().show(supportFragmentManager, "Set Room and Visitor")
            }
            tvDateCheckin.setOnClickListener { openDatePicker() }
            tvDateCheckout.setOnClickListener { openDatePicker(true) }
            btnFindHotel.setOnClickListener {
                if (dateEndMills <= dateStartMills)
                    GenericModalDialogCashplus.Builder()
                        .title(getString(R.string.something_wrong))
                        .description(getString(R.string.unsupported_date_format))
                        .image(R.drawable.ilustration_warning)
                        .buttonNegative(
                            NegativeButtonAction(
                                btnLabel = getString(R.string.close),
                                setClickOnDismiss = true
                            )
                        ).show(supportFragmentManager)
                else {
                    if (isChange) {
                        val intent = Intent().apply {
                            putExtra(CITY_SELECTED, citySelected)
                            putExtra(DATE_CHECK_IN, dateCheckIn)
                            putExtra(DATE_CHECK_OUT, dateCheckOut)
                            putExtra(ROOM_VISITOR, roomVisitor)
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@InitialHotelActivity,
                            Pair.create(binding.wrapperContent, getString(R.string.layout_hotel))
                        )
                        startActivity(
                            Intent(this@InitialHotelActivity, FindHotelByCityActivity::class.java).apply {
                                putExtra(CITY_SELECTED, citySelected)
                                putExtra(DATE_CHECK_IN, dateCheckIn)
                                putExtra(DATE_CHECK_OUT, dateCheckOut)
                                putExtra(ROOM_VISITOR, roomVisitor)
                            }, options.toBundle()
                        )
                    }
                }
            }
        }

        collectDataOrderHotel()
    }

    private fun openDatePicker(isCheckOut: Boolean = false) {
        val setDateCheckIn = dateCheckIn.split("-")
        val setDateCheckOut = dateCheckOut.split("-")
        val year: Int
        val month: Int
        val day: Int

        if (isCheckOut) {
            year = setDateCheckOut.first().toInt()
            month = setDateCheckOut[1].toInt() - 1
            day = setDateCheckOut.last().toInt()
        } else {
            year = setDateCheckIn.first().toInt()
            month = setDateCheckIn[1].toInt() - 1
            day = setDateCheckIn.last().toInt()
        }

        val datePickerDialog = DatePickerDialog(
            this, R.style.DatePickerCustom,
            { _, _, _, _ -> }, year, month, day
        )
        datePickerDialog.datePicker.minDate = if (isCheckOut) (checkIn + 86400000) else System.currentTimeMillis()
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.choose_button)) { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                val datePicker = datePickerDialog.datePicker

                val dayPicked = datePicker.dayOfMonth
                val monthPicked = datePicker.month
                val yearPicked = datePicker.year

                val formatForShow = if (isCheckOut) {
                    calendarEnd.set(yearPicked, monthPicked, dayPicked)
                    dateEndMills = calendarEnd.time.time
                    sdf.format(calendarEnd.time)
                } else {
                    calendarStart.set(yearPicked, monthPicked, dayPicked)
                    dateStartMills = calendarStart.time.time
                    checkIn = dateStartMills
                    sdf.format(calendarStart.time)
                }

                if (isCheckOut) {
                    dateCheckOut = sdfReq.format(calendarEnd.time)
                    binding.tvDateCheckout.text = formatForShow
                } else {
                    dateCheckIn = sdfReq.format(calendarStart.time)
                    binding.tvDateCheckin.text = formatForShow
                }
            }
        }
        datePickerDialog.show()
    }

    private fun collectDataOrderHotel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.citySelected.collectLatest {
                        binding.tvCity.text = it?.cityName ?: getString(R.string.find_hotel_by_city)
                        if (it != null) {
                            citySelected = it
                            stateCitySelected = true
                        }
                        stateButton()
                    }
                }

                launch {
                    viewModel.roomVisitor.collectLatest {
                        binding.tvRoomAndVisitor.text = getString(
                            R.string.visitor_format,
                            it?.room ?: 1, it?.visitor ?: 1, it?.child ?: 0
                        )
                        roomVisitor = it ?: RoomVisitor(1, 1, 0)
                    }
                }
            }
        }
    }

    private fun stateButton() {
        binding.btnFindHotel.isEnabled = stateCitySelected
    }

    override fun onResume() {
        super.onResume()
        stateButton()
    }

    companion object {
        const val IS_CHANGE_HOTEL_ORDER = "is_change_hotel_order"
        const val IS_CHANGE_DESTINATION = "is_change_destination"
        const val CITY_SELECTED = "hotel_city_selected"
        const val DATE_CHECK_IN = "date_check_in"
        const val DATE_CHECK_OUT = "date_check_out"
        const val ROOM_VISITOR = "hotel_room_visitor"
    }
}