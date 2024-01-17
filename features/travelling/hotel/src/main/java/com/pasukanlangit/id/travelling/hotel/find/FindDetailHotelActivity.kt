package com.pasukanlangit.id.travelling.hotel.find

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.bumptech.glide.Glide
import com.pasukanlangit.id.core.*
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.travelling.domain.model.hotel.Hotels
import com.pasukanlangit.id.travelling.domain.model.hotel.RoomHotel
import com.pasukanlangit.id.travelling.hotel.InitialHotelActivity
import com.pasukanlangit.id.travelling.hotel.R
import com.pasukanlangit.id.travelling.hotel.databinding.ActivityFindDetailHotelBinding
import com.pasukanlangit.id.travelling.hotel.fill.FillDataActivity
import com.pasukanlangit.id.travelling.hotel.temp.CitySelected
import com.pasukanlangit.id.travelling.hotel.temp.RoomVisitor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class FindDetailHotelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindDetailHotelBinding
    private val viewModel: FindDetailHotelViewModel by viewModel()

    private lateinit var hotelImagesAdapter: HotelImageAdapter
    private lateinit var hotelRoomAdapter: RoomHotelAdapter

    private val sdfData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var city: CitySelected? = null
    private var checkIn: String? = null
    private var checkOut: String? = null
    private var roomVisitor: RoomVisitor? = null
    private var hotelKey: String? = null
    private var hotelCode: String? = null
    private var hotelName: String? = null
    private var hotelAddress: String? = null

    private val changeDestination = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            checkIn = result?.data?.getStringExtra(InitialHotelActivity.DATE_CHECK_IN)
            checkOut = result?.data?.getStringExtra(InitialHotelActivity.DATE_CHECK_OUT)
            roomVisitor = result?.data?.parcelable(InitialHotelActivity.ROOM_VISITOR)

            initSummaryHotel(checkIn, checkOut, roomVisitor)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindDetailHotelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        city = intent.parcelable(HOTEL_CITY_SELECTED)
        checkIn = intent.getStringExtra(CHECK_IN_HOTEL) ?: sdfData.format(Calendar.getInstance().time.time)
        checkOut = intent.getStringExtra(CHECK_OUT_HOTEL) ?: sdfData.format(Calendar.getInstance().time.time + 86400000)
        roomVisitor = intent.parcelable(HOTEL_ROOM_VISITOR) ?: RoomVisitor(1, 1, 0)
        hotelCode = intent.getStringExtra(HOTEL_CODE)
        binding.btnChange.isVisible = intent.getBooleanExtra(IS_DESTINATION_EDIT, false)

        viewModel.onTriggerEvents(
            DetailHotelEvents.FindDetailHotel(
                city?.cityCode ?: "", hotelCode ?: "", checkIn ?: "",
                checkOut ?: "", roomVisitor?.room.toString()
            )
        )

        initSummaryHotel(checkIn, checkOut, roomVisitor)
        with(binding) {
            imgBack.setOnClickListener { finish() }
            tvCityName.text = city?.cityName
            btnChange.setOnClickListener {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@FindDetailHotelActivity,
                    Pair(wrapperContent, getString(R.string.layout_hotel))
                )
                changeDestination.launch(
                    Intent(this@FindDetailHotelActivity, InitialHotelActivity::class.java).apply {
                        putExtra(InitialHotelActivity.IS_CHANGE_HOTEL_ORDER, true)
                        putExtra(InitialHotelActivity.IS_CHANGE_DESTINATION, false)
                        putExtra(InitialHotelActivity.CITY_SELECTED, city)
                        putExtra(InitialHotelActivity.DATE_CHECK_IN, checkIn)
                        putExtra(InitialHotelActivity.DATE_CHECK_OUT, checkOut)
                        putExtra(InitialHotelActivity.ROOM_VISITOR, roomVisitor)
                    }, options
                )
            }
            with(rvImageHotel) {
                layoutManager = LinearLayoutManager(
                    this@FindDetailHotelActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = hotelImagesAdapter
                addItemDecoration(CashplusItemDecoration(4))
            }
            with(rvRoomHotel) {
                layoutManager = LinearLayoutManager(this@FindDetailHotelActivity)
                adapter = hotelRoomAdapter
                addItemDecoration(CashplusItemDecoration(24))
            }
            hotelRoomAdapter.setOnClickListener(object: RoomHotelAdapter.OnButtonClickListener {
                override fun onOrderRoomListener(data: RoomHotel) {
                    startActivity(
                        Intent(this@FindDetailHotelActivity, FillDataActivity::class.java).apply {
                            putExtra(FillDataActivity.ID_HOTEL_KEY, hotelKey)
                            putExtra(FillDataActivity.HOTEL_CODE_KEY, hotelCode)
                            putExtra(FillDataActivity.HOTEL_NAME_KEY, hotelName)
                            putExtra(FillDataActivity.HOTEL_RATE_KEY, data.rateKey)
                            putExtra(FillDataActivity.HOTEL_ADDRESS_KEY, hotelAddress)
                            putExtra(FillDataActivity.HOTEL_ORDER_PRICE_KEY, data.price)
                            putExtra(FillDataActivity.CHECK_IN_KEY, this@FindDetailHotelActivity.checkIn)
                            putExtra(FillDataActivity.CHECK_OUT_KEY, this@FindDetailHotelActivity.checkOut)
                            putExtra(FillDataActivity.ROOM_VISITOR_KEY, roomVisitor)
                        }
                    )
                }
            })
        }

        collectDetailHotel()
    }

    private fun initSummaryHotel(checkIn: String?, checkOut: String?, visitor: RoomVisitor?) {
        hotelImagesAdapter = HotelImageAdapter()
        hotelRoomAdapter = RoomHotelAdapter()
        val dateCheckIn = sdfData.parse(checkIn.toString())
        val dateCheckOut = sdfData.parse(checkOut.toString())
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale(getString(R.string.locale_language), getString(
            R.string.locale_country)))
        with(binding) {
            tvDateCheckin.text = sdf.format(dateCheckIn as Date)
            tvDateCheckout.text = sdf.format(dateCheckOut as Date)
            tvRoomVisitor.text = getString(
                R.string.visitor_format,
                visitor?.room,
                visitor?.visitor,
                visitor?.child
            )
            hotelImagesAdapter.setOnItemClickListener(object: HotelImageAdapter.OnItemClickListener {
                override fun onItemClicked(item: String) {
                    Glide.with(this@FindDetailHotelActivity)
                        .load(item)
                        .error(R.drawable.ic_empty)
                        .into(imgHotelMain)
                    hotelImagesAdapter.setSelectedImage(item)
                }
            })
        }
    }

    private fun collectDetailHotel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateLoading.collectLatest { binding.hotelProgressBar.isVisible = it }
                }

                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                Log.d("Exception", info)
                                val toast =
                                    Toast.makeText(this@FindDetailHotelActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                            }
                            viewModel.onTriggerEvents(DetailHotelEvents.RemoveHeadMessage)
                        }
                    }
                }

                launch {
                    viewModel.findDetailHotel.collectLatest { data ->
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
                                                ModuleRoute.internalIntent(this@FindDetailHotelActivity, MAIN_ACTIVITY_PATH).apply {
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
                            val dataHotel = data.data?.first()
                            hotelKey = dataHotel?.key
                            hotelName = dataHotel?.name
                            hotelAddress = getString(
                                R.string.hotel_address_format,
                                dataHotel?.address,
                                dataHotel?.city,
                                dataHotel?.country
                            )
                            Glide.with(this@FindDetailHotelActivity)
                                .load(dataHotel?.images?.first())
                                .thumbnail(
                                    Glide.with(this@FindDetailHotelActivity)
                                        .load(R.raw.image_loading_state)
                                )
                                .error(R.drawable.ic_empty)
                                .into(binding.imgHotelMain)
                            hotelImagesAdapter.setImages(dataHotel?.images)
                            bindLayout(dataHotel)
                        }
                        hotelImagesAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun bindLayout(data: Hotels?) {
        data?.let {
            with(binding) {
                tvNameHotel.text = hotelName
                ratingHotel.rating = it.rating?.toFloat() ?: (0.0).toFloat()
                tvHotelAddress.text = hotelAddress
                hotelRoomAdapter.submitList(it.room)
                nestedScrollView.isVisible = true
            }
        }
    }
}