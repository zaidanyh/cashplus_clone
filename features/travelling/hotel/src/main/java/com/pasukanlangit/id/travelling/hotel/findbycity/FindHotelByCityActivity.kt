package com.pasukanlangit.id.travelling.hotel.findbycity

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
import com.pasukanlangit.id.core.*
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.travelling.hotel.InitialHotelActivity
import com.pasukanlangit.id.travelling.hotel.R
import com.pasukanlangit.id.travelling.hotel.databinding.ActivityFindHotelByCityBinding
import com.pasukanlangit.id.travelling.hotel.find.FindDetailHotelActivity
import com.pasukanlangit.id.travelling.hotel.temp.CitySelected
import com.pasukanlangit.id.travelling.hotel.temp.RoomVisitor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class FindHotelByCityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindHotelByCityBinding
    private val viewModel: FindHotelByCityViewModel by viewModel()

    private var city: CitySelected? = null
    private var checkIn: String? = null
    private var checkOut: String? = null
    private var roomVisitor: RoomVisitor? = null

    private val changeOrderHotel = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            city = result?.data?.parcelable(InitialHotelActivity.CITY_SELECTED)
            checkIn = result?.data?.getStringExtra(InitialHotelActivity.DATE_CHECK_IN)
            checkOut = result?.data?.getStringExtra(InitialHotelActivity.DATE_CHECK_OUT)
            roomVisitor = result?.data?.parcelable(InitialHotelActivity.ROOM_VISITOR)
            viewModel.onTriggerEvent(
                HotelByCityEvents.FindHotelByCity(
                    city?.cityCode.toString(), checkIn.toString(),
                    checkOut.toString(), roomVisitor?.room.toString()
                )
            )
            initSummaryHotel(city, checkIn, checkOut, roomVisitor)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindHotelByCityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        city = intent.parcelable(InitialHotelActivity.CITY_SELECTED)
        checkIn = intent.getStringExtra(InitialHotelActivity.DATE_CHECK_IN)
        checkOut = intent.getStringExtra(InitialHotelActivity.DATE_CHECK_OUT)
        roomVisitor = intent.parcelable(InitialHotelActivity.ROOM_VISITOR)

        viewModel.onTriggerEvent(
            HotelByCityEvents.FindHotelByCity(
                city?.cityCode.toString(), checkIn.toString(),
                checkOut.toString(), roomVisitor?.room.toString()
            )
        )
        initSummaryHotel(city, checkIn, checkOut, roomVisitor)
        with(binding) {
            imgBack.setOnClickListener { finish() }
            btnChange.setOnClickListener {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@FindHotelByCityActivity,
                    Pair(wrapperContent, getString(R.string.layout_hotel))
                )
                changeOrderHotel.launch(
                    Intent(this@FindHotelByCityActivity, InitialHotelActivity::class.java).apply {
                        putExtra(InitialHotelActivity.IS_CHANGE_HOTEL_ORDER, true)
                        putExtra(InitialHotelActivity.CITY_SELECTED, city)
                        putExtra(InitialHotelActivity.DATE_CHECK_IN, checkIn)
                        putExtra(InitialHotelActivity.DATE_CHECK_OUT, checkOut)
                        putExtra(InitialHotelActivity.ROOM_VISITOR, roomVisitor)
                    }, options
                )
            }
            with(rvHotelList) {
                layoutManager = LinearLayoutManager(this@FindHotelByCityActivity)
                addItemDecoration(CashplusItemDecoration(24))
            }
        }
        collectHotelByCity()
    }

    private fun initSummaryHotel(city: CitySelected?, checkIn: String?, checkOut: String?, roomVisitor: RoomVisitor?) {
        val sdfData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateCheckIn = sdfData.parse(checkIn.toString())
        val dateCheckOut = sdfData.parse(checkOut.toString())
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale(getString(R.string.locale_language), getString(
            R.string.locale_country)))
        with(binding) {
            tvCityName.text = city?.cityName
            tvDateCheckin.text = sdf.format(dateCheckIn as Date)
            tvDateCheckout.text = sdf.format(dateCheckOut as Date)
            tvRoomVisitor.text = getString(R.string.visitor_format, roomVisitor?.room, roomVisitor?.visitor, roomVisitor?.child)
        }
    }

    private fun collectHotelByCity() {
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
                                    Toast.makeText(this@FindHotelByCityActivity, info, Toast.LENGTH_SHORT)
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
                                                ModuleRoute.internalIntent(this@FindHotelByCityActivity, MAIN_ACTIVITY_PATH).apply {
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
                                FindHotelByCityAdapter(it) {
                                    startActivity(
                                        Intent(this@FindHotelByCityActivity, FindDetailHotelActivity::class.java).apply {
                                            putExtra(HOTEL_CITY_SELECTED, city)
                                            putExtra(CHECK_IN_HOTEL, checkIn)
                                            putExtra(CHECK_OUT_HOTEL, checkOut)
                                            putExtra(HOTEL_ROOM_VISITOR, roomVisitor)
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