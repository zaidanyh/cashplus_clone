package com.pasukanlangit.id.travelling.flight.detail

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.core.MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE
import com.pasukanlangit.id.core.MAIN_ACTIVITY_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.EnterPinDialogFragment
import com.pasukanlangit.id.core.presentation.components.*
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.InputUtil.toDoubleNumber
import com.pasukanlangit.id.core.utils.SaldoNotEnoughNotif
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.putExtra
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.id.travelling.domain.utils.TravellingProductCode
import com.pasukanlangit.id.travelling.flight.R
import com.pasukanlangit.id.travelling.flight.databinding.ActivityFlightPriceBinding
import com.pasukanlangit.id.travelling.flight.temp.FlightRoute
import com.pasukanlangit.id.travelling.flight.temp.Passengers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class FlightPriceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlightPriceBinding
    private val viewModel: FlightPriceViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var departure: FlightRoute? = null
    private var destination: FlightRoute? = null
    private var flightDate: String? = null
    private var flightCode: String? = null
    private var passengers: Passengers? = null
    private var totalPassenger = 0

    private var stateCanBack = true
    private var balance: Double? = 0.0
    private var bookingCode: String? = null

    private val enterPIN: EnterPinDialogFragment =
        EnterPinDialogFragment(
            onEnterPinCompleted = {
                viewModel.onTriggersEvent(
                    FlightPriceEvent.BookingTransaction(
                        codeProduct = TravellingProductCode.FLIGHT.value,
                        destination = bookingCode,
                        pin = it
                    )
                )
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlightPriceBinding.inflate(layoutInflater)
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

        departure = intent.parcelable(DEPARTURE_KEY)
        destination = intent.parcelable(DESTINATION_KEY)
        flightDate = intent.getStringExtra(FLIGHT_DATE_KEY)
        flightCode = intent.getStringExtra(FLIGHT_CODE_KEY)
        passengers = intent.parcelable(PASSENGERS_KEY)

        viewModel.onTriggersEvent(
            FlightPriceEvent.FlightPrice(
                departure?.code.toString(), destination?.code.toString(),
                flightDate.toString(), flightCode.toString(),
                passengers?.adult.toString(), passengers?.child.toString(), passengers?.infant.toString()
            )
        )

        passengers?.let { setUpPassengersData(it) }
        enterPIN.isCancelable = false
        val sdfData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dataDate = sdfData.parse(flightDate.toString())
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale(getString(R.string.locale_language), getString(
            R.string.locale_country)))
        with(binding) {
            imgBack.setOnClickListener { onBackState() }
            tvDateFlight.text = sdf.format(dataDate as Date)
            tvDeparture.text = getString(R.string.flight_route, departure?.city, departure?.code)
            tvDestination.text = getString(R.string.flight_route, destination?.city, destination?.code)
            tvPassengers.text = getString(R.string.passenger_list, passengers?.adult.toString(), passengers?.child.toString(), passengers?.infant.toString())

            switchFill.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    with(sharedPref) {
                        edtFullName.setText(getNameProfile())
                        edtPhoneNumber.setText(getPhoneNumber())
                        edtEmail.setText(getEmailProfile())
                    }
                    return@setOnCheckedChangeListener
                }
            }
            btnPayment.setOnClickListener { actionOrder() }
        }

        collectFlightPrice()
    }

    private fun setUpPassengersData(data: Passengers) {
        totalPassenger = passengers?.adult?.plus(passengers?.child ?: 0)?.plus(passengers?.infant ?: 0) ?: 0
        val passengers = mutableListOf<String>()
        for (i in 0 until data.adult) passengers.add("Dewasa ${i+1}")
        for (j in 0 until data.child) passengers.add("Anak ${j+1}")
        for (k in 0 until data.infant) passengers.add("Bayi ${k+1}")

        with(binding.rvPassengersData) {
            layoutManager = LinearLayoutManager(this@FlightPriceActivity)
            adapter = DataPassengerAdapter(passengers)
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    private fun actionOrder() {
        with(binding) {
            val contactName = edtFullName.toString().trim()
            val contactEmail = edtEmail.text.toString().trim()
            val contactPhone = edtPhoneNumber.text.toString().trim()

            if (contactName.isEmpty() || contactEmail.isEmpty() || contactPhone.isEmpty()) {
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
                return
            }

            if (balance!! < tvPayTotal.text.toString().toDoubleNumber()) {
                SaldoNotEnoughNotif().show(supportFragmentManager, "Balance Not Enough")
                return
            }
            val namePassengers = StringBuilder("")
            val identitiesNumbers = StringBuilder("")
            val dateBirthPassengers = StringBuilder("")

            for (i in 0 until totalPassenger) {
                val viewHolder: RecyclerView.ViewHolder? = rvPassengersData.findViewHolderForAdapterPosition(i)
                val view: View? = viewHolder?.itemView

                val edtTitlePassenger = view?.findViewById<AppCompatSpinner>(R.id.spinner_titel_passenger)
                val edtNamePassenger = view?.findViewById<EditText>(R.id.edtFullNamePassenger)
                val edtIdentityNumberPassenger = view?.findViewById<EditText>(R.id.edt_nik)
                val edtDateBirthPassenger = view?.findViewById<EditText>(R.id.edtBirthDate)

                if (edtTitlePassenger != null || edtNamePassenger != null || edtIdentityNumberPassenger != null || edtDateBirthPassenger != null) {
                    val titlePassenger = edtTitlePassenger?.selectedItem.toString()
                    val namePassenger = edtNamePassenger?.text.toString().trim()
                    val identityNumberPassenger = edtIdentityNumberPassenger?.text.toString().trim()
                    val dateBirthPassenger = edtDateBirthPassenger?.text.toString().trim()

                    if (titlePassenger.contains(getString(R.string.choose)) || namePassenger.isEmpty() || identityNumberPassenger.isEmpty() || dateBirthPassenger.isEmpty()) {
                        GenericModalDialogCashplus.Builder()
                            .title(getString(R.string.incomplete_data))
                            .description(getString(R.string.fill_passengers_data))
                            .image(R.drawable.ilustration_warning)
                            .buttonNegative(
                                NegativeButtonAction(
                                    btnLabel = getString(R.string.close),
                                    setClickOnDismiss = true
                                )
                            ).show(supportFragmentManager)
                        return
                    }
                    val sdfParsing = SimpleDateFormat("dd MMMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
                    val dateParse = sdfParsing.parse(dateBirthPassenger)
                    val sdfReq = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                    namePassengers.append(
                        "$titlePassenger $namePassenger:" +
                        if (i != totalPassenger - 1) ", " else ""
                    )
                    identitiesNumbers.append("$identityNumberPassenger:")
                    dateBirthPassengers.append("${sdfReq.format(dateParse as Date)}:")
                }
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
                            viewModel.onTriggersEvent(
                                FlightPriceEvent.FlightBooking(
                                    from = departure?.code.toString(), to = destination?.code.toString(),
                                    date = flightDate.toString(), flightCode = flightCode.toString(),
                                    adult = passengers?.adult.toString(), child = passengers?.child.toString(),
                                    infant = passengers?.infant.toString(), email = contactEmail, phone = contactPhone,
                                    passengersName = namePassengers.removeSuffix(":").toString(),
                                    passengersDateOfBirth = dateBirthPassengers.removeSuffix(":").toString(),
                                    baggageVolumes = ""
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
                ).show(supportFragmentManager, "Confirm Data Flight")
        }
    }

    private fun collectFlightPrice() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE BALANCE
                launch {
                    viewModel.balance.collectLatest { balance ->
                        if (balance != null) this@FlightPriceActivity.balance = balance.balance
                    }
                }

                // STATE FLIGHT PRICE
                launch {
                    viewModel.loadingPrice.collectLatest {
                        with(binding) {
                            stateCanBack = !it
                            progressBar.isVisible = it
                            tvTotalPayment.isInvisible = it
                        }
                    }
                }
                launch {
                    viewModel.flightPrice.collectLatest { response ->
                        if (response != null) {
                            with(binding) {
                                wrapperSummaryOrder.isVisible = true
                                wrapperBalance.isVisible = balance!! >= response.totalFare
                                txtBalanceNotEnough.isVisible = balance!! < response.totalFare
                                btnPayment.isEnabled = balance!! >= response.totalFare

                                tvFlightOrder.text = getString(
                                    R.string.flight_order_format,
                                    response.flight,
                                    response.flightCode,
                                    response.flightInfoTransit
                                )
                                tvPriceFlight.text = getNumberRupiah(response.publish)
                                tvTaxAirport.text = getNumberRupiah(response.tax)
                                tvPayTotal.text = getNumberRupiah(response.totalFare)
                                tvTotalPayment.text = getNumberRupiah(response.totalFare)
                            }
                        }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(this@FlightPriceActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggersEvent(FlightPriceEvent.RemoveHeadMessage)
                            }
                        }
                    }
                }

                // STATE BOOKING
                launch {
                    viewModel.stateLoading.collectLatest {
                        binding.progressBar.isVisible = it
                        stateCanBack = !it
                    }
                }
                launch {
                    viewModel.flightBooking.collectLatest { response ->
                        if (response != null) {
                            bookingCode = response.bookingCode
                            binding.btnPayment.isEnabled = true
                            enterPIN.show(supportFragmentManager, enterPIN.tag)
                        }
                    }
                }
                launch {
                    viewModel.bookingError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(this@FlightPriceActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                binding.btnPayment.isEnabled = true
                                delay(toast.duration.toLong())
                                viewModel.onTriggersEvent(FlightPriceEvent.RemoveHeadMessageBooking)
                            }
                        }
                    }
                }

                // STATE TRANSACTION
                launch {
                    viewModel.transactionLoading.collectLatest { enterPIN.setLoading(it) }
                }
                launch {
                    viewModel.transactionBooking.collectLatest { response ->
                        if (response != null) {
                            enterPIN.dismiss()
                            delay(300)
                            finishAffinity()
                            startActivity(
                                ModuleRoute.internalIntent(this@FlightPriceActivity, MAIN_ACTIVITY_PATH).apply {
                                    putExtra(MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE, getString(R.string.transaction_ticket, bookingCode))
                                    putExtra(NotifType.NOTIF_TRX_SUCCESS)
                                }
                            )
                        }
                    }
                }
                launch {
                    viewModel.transactionError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                enterPIN.dismiss()
                                val toast =
                                    Toast.makeText(this@FlightPriceActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggersEvent(FlightPriceEvent.RemoveHeadMessageTransaction)
                            }
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
        const val DEPARTURE_KEY = "departure_key"
        const val DESTINATION_KEY = "destination_key"
        const val FLIGHT_DATE_KEY = "flight_date_key"
        const val FLIGHT_CODE_KEY = "flight_code_key"
        const val PASSENGERS_KEY = "passengers_key"
    }
}