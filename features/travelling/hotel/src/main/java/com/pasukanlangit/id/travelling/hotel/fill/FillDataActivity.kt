package com.pasukanlangit.id.travelling.hotel.fill

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pasukanlangit.id.core.MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE
import com.pasukanlangit.id.core.MAIN_ACTIVITY_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.EnterPinDialogFragment
import com.pasukanlangit.id.core.presentation.components.*
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.putExtra
import com.pasukanlangit.id.core.utils.setDropDownView
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.id.travelling.domain.utils.TravellingProductCode
import com.pasukanlangit.id.travelling.hotel.R
import com.pasukanlangit.id.travelling.hotel.temp.RoomVisitor
import com.pasukanlangit.id.travelling.hotel.databinding.ActivityFillDataBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class FillDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFillDataBinding
    private val viewModel: FillDataViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var stateCanBack = true
    private var paymentCode: String? = null
    private var orderPrice: Int = 0

    private val enterPIN: EnterPinDialogFragment =
        EnterPinDialogFragment(
            onEnterPinCompleted = {
                viewModel.onTriggerEvent(
                    FillDataEvents.HotelBookingTransaction(
                        codeProduct = TravellingProductCode.HOTEL.value,
                        destination = paymentCode,
                        pin = it
                    )
                )
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) { onBackState() }
        } else {
            onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() { onBackState() }
            })
        }

        val hotelKey = intent.getStringExtra(ID_HOTEL_KEY)
        val hotelCode = intent.getStringExtra(HOTEL_CODE_KEY)
        val rateKey = intent.getStringExtra(HOTEL_RATE_KEY)
        val checkIn = intent.getStringExtra(CHECK_IN_KEY)
        val checkOut = intent.getStringExtra(CHECK_OUT_KEY)
        val roomVisitor = intent.parcelable<RoomVisitor>(ROOM_VISITOR_KEY)
        orderPrice = intent.getIntExtra(HOTEL_ORDER_PRICE_KEY, 0)
        initSummaryHotel(checkIn, checkOut, roomVisitor)

        viewModel.onTriggerEvent(FillDataEvents.CheckBalance)
        enterPIN.isCancelable = false
        with(binding) {
            imgBack.setOnClickListener { finish() }
            tvHotelName.text = intent.getStringExtra(HOTEL_NAME_KEY)
            tvHotelAddress.text = intent.getStringExtra(HOTEL_ADDRESS_KEY)
            switchFill.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    with(sharedPref) {
                        edtFullName.setText(getNameProfile())
                        edtEmail.setText(getEmailProfile())
                        edtPhoneNumber.setText(getPhoneNumber())
                    }
                }
            }
            val adapterSpinner = setDropDownView(this@FillDataActivity, listOf("Pilih", "Mr.", "Mrs.", "Ms."))
            adapterSpinner.setDropDownViewResource(R.layout.item_spinner_small)
            spinnerTitelPerson.adapter = adapterSpinner
            btnPayment.setOnClickListener {
                val name = edtFullName.text.toString().trim()
                val email = edtEmail.text.toString().trim()
                val phone = edtPhoneNumber.text.toString().trim()
                val requestAdditional = edtAdditionalRequest.text.toString().trim()
                val title = spinnerTitelPerson.selectedItem.toString()

                if (title.contains(getString(R.string.choose))) {
                    Toast.makeText(this@FillDataActivity, getString(R.string.choose_title_error), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    GenericModalDialogCashplus.Builder()
                        .title(getString(R.string.incomplete_data))
                        .description(getString(R.string.contact_information_cannot_empty))
                        .image(R.drawable.ilustration_warning)
                        .buttonNegative(
                            NegativeButtonAction(
                                btnLabel = getString(R.string.close),
                                setClickOnDismiss = true
                            )
                        ).show(supportFragmentManager)
                    return@setOnClickListener
                }

                GenericConfirmDialogFragment.Builder()
                    .title(getString(R.string.check_detail))
                    .description(getString(R.string.desc_check_detail))
                    .buttonPositive(
                        PositiveButton(
                            backgroundButton = R.drawable.bg_primary_rounded_12,
                            buttonText = getString(R.string.yes_right),
                            buttonTextColor = Color.parseColor("#FFFFFF"),
                            onBtnAction = {
                                viewModel.onTriggerEvent(
                                    FillDataEvents.HotelBooking(
                                        hotelCode = hotelCode ?: "", hotelKey = hotelKey ?: "", rateKey = rateKey ?: "",
                                        checkIn = checkIn ?: "", checkOut = checkOut ?: "", hotelRoom = roomVisitor?.room.toString(),
                                        paxName = "$title $name", email = email, phone = phone, hotelRequest = requestAdditional
                                    )
                                )
                                btnPayment.isEnabled = false
                            }
                        )
                    )
                    .buttonNegative(
                        NegativeButton(
                            backgroundButton = R.drawable.bg_blue50_rounded_12,
                            buttonText = getString(R.string.change),
                            buttonTextColor = Color.parseColor("#0A57FF"),
                            actionDismiss = true
                        )
                    ).show(supportFragmentManager, "Confirm Data Hotel")
            }
        }

        collectData()
    }

    private fun initSummaryHotel(checkIn: String?, checkOut: String?, visitor: RoomVisitor?) {
        val sdfData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE BALANCE
                launch {
                    viewModel.stateLoading.collectLatest {
                        binding.progressBar.isVisible = it
                        stateCanBack = !it
                    }
                }
                launch {
                    viewModel.balance.collectLatest {
                        if (it != null) {
                            with(binding) {
                                wrapperBalance.isVisible = it.balance!! >= orderPrice
                                txtBalanceNotEnough.isVisible = it.balance!! < orderPrice
                                tvTotalPayment.text = getNumberRupiah(orderPrice)
                                btnPayment.isEnabled = it.balance!! >= orderPrice
                            }
                        }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(this@FillDataActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                            }
                            viewModel.onTriggerEvent(FillDataEvents.RemoveHeadMessage)
                        }
                    }
                }

                // STATE BOOKING
                launch {
                    viewModel.stateBookingLoading.collectLatest {
                        binding.progressBar.isVisible = it
                        stateCanBack = !it
                    }
                }
                launch {
                    viewModel.hotelBooking.collectLatest {
                        if (it != null) {
                            paymentCode = it.bookingCode
                            binding.btnPayment.isEnabled = true
                            enterPIN.show(supportFragmentManager, enterPIN.tag)
                        }
                    }
                }
                launch {
                    viewModel.stateBookingError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(this@FillDataActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                            }
                            viewModel.onTriggerEvent(FillDataEvents.RemoveHeadMessageBooking)
                        }
                    }
                }

                // STATE TRANSACTION
                launch {
                    viewModel.stateTransactionLoading.collectLatest { enterPIN.setLoading(it) }
                }
                launch {
                    viewModel.transaction.collectLatest {
                        if (it != null) {
                            enterPIN.dismiss()
                            delay(300)
                            finishAffinity()
                            startActivity(
                                ModuleRoute.internalIntent(this@FillDataActivity, MAIN_ACTIVITY_PATH).apply {
                                    putExtra(MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE, getString(R.string.transaction_hotel, paymentCode))
                                    putExtra(NotifType.NOTIF_TRX_SUCCESS)
                                }
                            )
                        }
                    }
                }
                launch {
                    viewModel.stateTransactionError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                enterPIN.dismiss()
                                val toast =
                                    Toast.makeText(this@FillDataActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                            }
                            viewModel.onTriggerEvent(FillDataEvents.RemoveHeadMessageTransaction)
                        }
                    }
                }
            }
        }
    }

    private fun onBackState() {
        if (!stateCanBack) return
        else finish()
    }

    companion object {
        const val ID_HOTEL_KEY = "id_hotel_key"
        const val HOTEL_CODE_KEY = "hotel_code_key"
        const val HOTEL_NAME_KEY = "hotel_name_key"
        const val HOTEL_RATE_KEY = "hotel_rate_key"
        const val HOTEL_ADDRESS_KEY = "hotel_address_key"
        const val HOTEL_ORDER_PRICE_KEY = "hotel_order_price_key"
        const val CHECK_IN_KEY = "check_in_key"
        const val CHECK_OUT_KEY = "check_out_key"
        const val ROOM_VISITOR_KEY = "room_visitor_key"
    }
}