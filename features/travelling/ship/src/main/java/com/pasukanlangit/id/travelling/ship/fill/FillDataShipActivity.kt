package com.pasukanlangit.id.travelling.ship.fill

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
import com.pasukanlangit.id.core.utils.*
import com.pasukanlangit.id.core.utils.InputUtil.toDoubleNumber
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.id.travelling.domain.utils.TravellingProductCode
import com.pasukanlangit.id.travelling.ship.R
import com.pasukanlangit.id.travelling.ship.databinding.ActivityFillDataShipBinding
import com.pasukanlangit.id.travelling.ship.temp.ShipPassengers
import com.pasukanlangit.id.travelling.ship.temp.ShipRoute
import com.pasukanlangit.id.travelling.ship.temp.ShipSchedules
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class FillDataShipActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFillDataShipBinding
    private val viewModel: FillDataShipViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var departure: ShipRoute? = null
    private var arrival: ShipRoute? = null
    private var shipDate: String? = null
    private var shipSchedule: ShipSchedules? = null
    private var passengers: ShipPassengers? = null
    private var totalPassengers: Int = 0

    private var stateCanBack = true
    private var balance: Double? = 0.0
    private var bookingCode: String? = null

    private val enterPIN: EnterPinDialogFragment =
        EnterPinDialogFragment(
            onEnterPinCompleted = {
                viewModel.onTriggerEvents(
                    FillDataShipEvents.ShipBookingTransaction(
                        codeProduct = TravellingProductCode.SHIP.value,
                        destination = bookingCode,
                        pin = it
                    )
                )
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillDataShipBinding.inflate(layoutInflater)
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

        departure = intent.parcelable(SHIP_DEPARTURE_KEY)
        arrival = intent.parcelable(SHIP_DESTINATION_KEY)
        shipDate = intent.getStringExtra(SHIP_DATE_KEY)
        shipSchedule = intent.parcelable(SHIP_SCHEDULE_KEY)
        passengers = intent.parcelable(SHIP_PASSENGERS_KEY)

        viewModel.onTriggerEvents(FillDataShipEvents.CheckBalance)

        passengers?.let { setUpDataPassengers(it) }
        val sdfData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dataDate = sdfData.parse(shipDate.toString())
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
        val departureList = departure?.name?.split(",")
        val destinationList = arrival?.name?.split(",")
        with(binding) {
            imgBack.setOnClickListener { onBackState() }
            tvDate.text = sdf.format(dataDate as Date)
            tvCityHarborDeparture.isVisible = departureList != null
            tvCityHarborDestination.isVisible = destinationList != null
            tvNameHarborDeparture.text = if (departureList.isNullOrEmpty()) departure?.name else departureList.first().trim()
            tvNameHarborDestination.text = if (destinationList.isNullOrEmpty()) arrival?.name else destinationList.first().trim()
            tvCityHarborDeparture.text = departureList?.last()?.trim() ?: ""
            tvCityHarborDestination.text = destinationList?.last()?.trim() ?: ""
            tvPassengers.text = getString(
                R.string.harbor_passenger_list,
                passengers?.male?.plus(passengers?.female ?: 0).toString(),
                passengers?.infant.toString()
            )
            switchFill.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    with(sharedPref) {
                        edtFullName.setText(getNameProfile())
                        edtEmail.setText(getEmailProfile())
                        edtPhoneNumber.setText(getPhoneNumber())
                    }
                }
            }
            btnPayment.setOnClickListener { actionOrder() }
        }
        collectData()
    }

    private fun setUpDataPassengers(data: ShipPassengers) {
        totalPassengers = data.male + data.female + data.infant
        val passengers = mutableListOf<String>()
        for (i in 0 until data.male) passengers.add("Dewasa ${i+1} (Laki-laki)")
        for (j in 0 until data.female) passengers.add("Dewasa ${j+1} (Perempuan)")
        for (k in 0 until data.infant) passengers.add("Bayi ${k+1}")

        with(binding.rvFillDataShipPassengers) {
            layoutManager = LinearLayoutManager(this@FillDataShipActivity)
            adapter = ShipPassengersAdapter(passengers)
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    private fun actionOrder() {
        with(binding) {
            val name = edtFullName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val phone = edtPhoneNumber.text.toString().trim()

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
                return
            }

            if (balance!! < tvTotalPayment.text.toString().toDoubleNumber()) {
                SaldoNotEnoughNotif().show(supportFragmentManager, "Balance Not Enough")
                return
            }
            val namePassengers = StringBuilder("")
            val identitiesNumbers = StringBuilder("")
            val dateBirthDates = StringBuilder("")

            for (i in 0 until totalPassengers) {
                val viewHolder: RecyclerView.ViewHolder? = rvFillDataShipPassengers.findViewHolderForAdapterPosition(i)
                val view: View? = viewHolder?.itemView

                val edtTitlePassenger = view?.findViewById<AppCompatSpinner>(R.id.spinner_title_ship_passenger)
                val edtNamePassenger = view?.findViewById<EditText>(R.id.edt_full_name_ship_passenger)
                val edtIdentityNumberPassenger = view?.findViewById<EditText>(R.id.edt_nik_ship)
                val edtBirthDate = view?.findViewById<EditText>(R.id.edtBirthDatePassengerShip)

                if (edtTitlePassenger != null || edtNamePassenger != null || edtIdentityNumberPassenger != null || edtBirthDate != null) {
                    val titlePassenger = edtTitlePassenger?.selectedItem.toString()
                    val namePassenger = edtNamePassenger?.text.toString().trim()
                    val identityNumberPassenger = edtIdentityNumberPassenger?.text.toString().trim()
                    val birthDatePassenger = edtBirthDate?.text.toString().trim()

                    if (titlePassenger.contains(getString(R.string.choose)) || namePassenger.isEmpty() || identityNumberPassenger.isEmpty() || birthDatePassenger.isEmpty()) {
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
                    val dateParse = sdfParsing.parse(birthDatePassenger)
                    val sdfReq = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                    namePassengers.append(
                        "$titlePassenger $namePassenger:" +
                        if (i != totalPassengers - 1) ", " else ""
                    )
                    identitiesNumbers.append("$identityNumberPassenger:")
                    dateBirthDates.append("${sdfReq.format(dateParse as Date)}:")
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
                            viewModel.onTriggerEvents(
                                FillDataShipEvents.ShipBooking(
                                    from = departure?.code.toString(), to = arrival?.code.toString(),
                                    date = shipDate.toString(), shipSchedules = shipSchedule!!,
                                    passengers = passengers!!, passengerName = namePassengers.removeSuffix(":").toString(),
                                    dateOfBirth = dateBirthDates.removeSuffix(":").toString(),
                                    idNumber = identitiesNumbers.removeSuffix(":").toString(),
                                    phone = phone, email = email
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
                            balance = it.balance
                            with(binding) {
                                wrapperBalance.isVisible = it.balance!! >= shipSchedule?.price!!
                                txtBalanceNotEnough.isVisible = it.balance!! < shipSchedule?.price!!
                                tvTotalPayment.text = getNumberRupiah(shipSchedule?.price!!)
                                btnPayment.isEnabled = it.balance!! >= shipSchedule?.price!!
                            }
                        }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(this@FillDataShipActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvents(FillDataShipEvents.RemoveHeadMessage)
                            }
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
                    viewModel.shipBooking.collectLatest {
                        if (it != null) {
                            bookingCode = it.bookingCode
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
                                    Toast.makeText(this@FillDataShipActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvents(FillDataShipEvents.RemoveHeadBookingMessage)
                            }
                        }
                    }
                }

                // STATE TRANSACTION
                launch {
                    viewModel.transactionLoading.collectLatest { enterPIN.setLoading(it) }
                }
                launch {
                    viewModel.transactionBooking.collectLatest {
                        if (it != null) {
                            enterPIN.dismiss()
                            delay(300)
                            finishAffinity()
                            startActivity(
                                ModuleRoute.internalIntent(this@FillDataShipActivity, MAIN_ACTIVITY_PATH).apply {
                                    putExtra(MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE, getString(R.string.transaction_ship, bookingCode))
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
                                    Toast.makeText(this@FillDataShipActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvents(FillDataShipEvents.RemoveHeadMessageTransaction)
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
        const val SHIP_DATE_KEY = "ship_date_key"
        const val SHIP_DEPARTURE_KEY = "ship_departure_key"
        const val SHIP_DESTINATION_KEY = "ship_destination_key"
        const val SHIP_PASSENGERS_KEY = "ship_passengers_key"
        const val SHIP_SCHEDULE_KEY = "ship_schedule_key"
    }
}