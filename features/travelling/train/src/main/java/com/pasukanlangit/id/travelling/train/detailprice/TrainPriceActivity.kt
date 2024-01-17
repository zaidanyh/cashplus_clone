package com.pasukanlangit.id.travelling.train.detailprice

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
import com.pasukanlangit.id.travelling.train.R
import com.pasukanlangit.id.travelling.train.databinding.ActivityTrainPriceBinding
import com.pasukanlangit.id.travelling.train.temp.Passengers
import com.pasukanlangit.id.travelling.train.temp.TrainBooking
import com.pasukanlangit.id.travelling.train.temp.TrainRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class TrainPriceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainPriceBinding
    private val viewModel: TrainPriceViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var departure: TrainRoute? = null
    private var destination: TrainRoute? = null
    private var trainDate: String? = null
    private var trainBook: TrainBooking? = null
    private var passengers: Passengers? = null
    private var totalPassenger = 0

    private var stateCanBack = true
    private var balance: Double = 0.0
    private var bookingCode: String? = null

    private val enterPIN: EnterPinDialogFragment =
        EnterPinDialogFragment(
            onEnterPinCompleted = {
                viewModel.onTriggerEvents(
                    TrainPriceEvent.BookingTransaction(
                        codeProduct = TravellingProductCode.TRAIN.value,
                        destination = bookingCode,
                        pin = it
                    )
                )
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainPriceBinding.inflate(layoutInflater)
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
        trainDate = intent.getStringExtra(TRAIN_DATE_KEY)
        trainBook = intent.parcelable(TRAIN_BOOKING_KEY)
        passengers = intent.parcelable(PASSENGERS_KEY)

        viewModel.onTriggerEvents(
            TrainPriceEvent.TrainPrice(
                from = departure?.code.toString(), to = destination?.code.toString(),
                date = trainDate.toString(), passenger = passengers, trainBooking = trainBook
            )
        )

        passengers?.let { setUpPassengersData(it) }
        enterPIN.isCancelable = false
        val sdfData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dataDate = sdfData.parse(trainDate.toString())
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
        with(binding) {
            imgBack.setOnClickListener { onBackState() }
            tvDateTrain.text = sdf.format(dataDate as Date)
            tvDeparture.text = getString(R.string.two_format_with_brackets, departure?.location, departure?.code)
            tvDestination.text = getString(R.string.two_format_with_brackets, destination?.location, destination?.code)
            tvPassengers.text = getString(R.string.train_passenger_list, passengers?.adult.toString(), passengers?.infant.toString())

            switchFill.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    with(sharedPref) {
                        edtFullName.setText(getNameProfile())
                        edtPhoneNumber.setText(getPhoneNumber())
                        edtEmail.setText(getEmailProfile())
                    }
                }
            }
            btnPayment.setOnClickListener { actionOrder() }
        }

        collectTrainPrice()
    }

    private fun setUpPassengersData(data: Passengers) {
        totalPassenger = passengers?.adult?.plus(passengers?.infant ?: 0) ?: 0
        val passengers = mutableListOf<String>()
        for (i in 0 until data.adult) passengers.add("Dewasa ${i+1}")
        for (j in 0 until data.infant) passengers.add("Bayi ${j+1}")

        with(binding.rvPassengersData) {
            layoutManager = LinearLayoutManager(this@TrainPriceActivity)
            adapter = TrainPassengerAdapter(passengers)
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

            if (balance < tvPayTotal.text.toString().toDoubleNumber()) {
                SaldoNotEnoughNotif().show(supportFragmentManager, "Balance Not Enough")
                return
            }
            val namePassengers = StringBuilder("")
            val identitiesNumbers = StringBuilder("")

            for (i in 0 until totalPassenger) {
                val viewHolder: RecyclerView.ViewHolder? = rvPassengersData.findViewHolderForAdapterPosition(i)
                val view: View? = viewHolder?.itemView

                val edtTitlePassenger = view?.findViewById<AppCompatSpinner>(R.id.spinner_title_train_passenger)
                val edtNamePassenger = view?.findViewById<EditText>(R.id.edt_full_name_train_passenger)
                val edtIdentityNumberPassenger = view?.findViewById<EditText>(R.id.edt_nik_train)

                if (edtTitlePassenger != null || edtNamePassenger != null || edtIdentityNumberPassenger != null) {
                    val titlePassenger = edtTitlePassenger?.selectedItem.toString()
                    val namePassenger = edtNamePassenger?.text.toString().trim()
                    val identityNumberPassenger = edtIdentityNumberPassenger?.text.toString().trim()

                    if (titlePassenger.contains(getString(R.string.choose)) || namePassenger.isEmpty() || identityNumberPassenger.isEmpty()) {
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
                    namePassengers.append(
                        "$titlePassenger $namePassenger:" +
                        if (i != totalPassenger - 1) ", " else ""
                    )
                    identitiesNumbers.append("$identityNumberPassenger:")
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
                                TrainPriceEvent.TrainBookingTicket(
                                    idNumbers = identitiesNumbers.removeSuffix(":").toString(),
                                    phone = contactPhone, email = contactEmail, from = departure?.code.toString(),
                                    to = destination?.code.toString(), date = trainDate.toString(),
                                    passengersName = namePassengers.removeSuffix(":").toString(),
                                    passenger = passengers!!, trainBooking = trainBook!!
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
                ).show(supportFragmentManager, "Confirm Data Train")
        }
    }

    private fun collectTrainPrice() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE BALANCE
                launch {
                    viewModel.balance.collectLatest {
                        if (it != null) this@TrainPriceActivity.balance = it.balance ?: 0.0
                    }
                }

                // STATE TRAIN PRICE
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
                    viewModel.trainPrice.collectLatest { response ->
                        if (response != null) {
                            with(binding) {
                                wrapperSummaryOrder.isVisible = true
                                wrapperBalance.isVisible = balance >= response.trainPrice?.totalFare!!
                                txtBalanceNotEnough.isVisible = balance < response.trainPrice?.totalFare!!
                                btnPayment.isEnabled = balance >= response.trainPrice?.totalFare!!

                                tvTrainOrder.text = getString(
                                    R.string.format_summary_order,
                                    response.trainPrice?.id, response.trainPrice?.name,
                                    response.trainPrice?.code, response.trainPrice?.route
                                )
                                tvPriceTrain.text = getNumberRupiah(response.trainPrice?.basicFare)
                                tvServiceFee.text = getNumberRupiah(response.trainPrice?.serviceCharge)
                                tvPayTotal.text = getNumberRupiah(response.trainPrice?.totalFare)
                                tvTotalPayment.text = getNumberRupiah(response.trainPrice?.totalFare)
                            }
                        }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(this@TrainPriceActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvents(TrainPriceEvent.RemoveHeadMessage)
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
                    viewModel.trainBooking.collectLatest { response ->
                        if (response != null) {
                            binding.btnPayment.isEnabled = true
                            if (response.rc == "00") {
                                bookingCode = response.bookingCode
                                enterPIN.show(supportFragmentManager, enterPIN.tag)
                                return@collectLatest
                            }
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.something_wrong))
                                .image(R.drawable.ilustration_warning)
                                .description(response.message)
                                .buttonNegative(
                                    NegativeButtonAction(
                                        btnLabel = getString(R.string.close),
                                        setClickOnDismiss = true
                                    )
                                ).show(supportFragmentManager)
                        }
                    }
                }
                launch {
                    viewModel.bookingError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(this@TrainPriceActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvents(TrainPriceEvent.RemoveHeadBookingMessage)
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
                                ModuleRoute.internalIntent(this@TrainPriceActivity, MAIN_ACTIVITY_PATH).apply {
                                    putExtra(MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE, getString(R.string.transaction_train_ticket, bookingCode))
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
                                    Toast.makeText(this@TrainPriceActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvents(TrainPriceEvent.RemoveHeadTransactionMessage)
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
        const val TRAIN_DATE_KEY = "train_date_key"
        const val DEPARTURE_KEY = "departure_key"
        const val DESTINATION_KEY = "destination_key"
        const val PASSENGERS_KEY = "passengers_key"
        const val TRAIN_BOOKING_KEY = "train_booking_key"
    }
}