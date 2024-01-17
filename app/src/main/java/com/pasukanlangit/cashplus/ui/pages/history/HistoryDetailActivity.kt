package com.pasukanlangit.cashplus.ui.pages.history

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.pasukanlangit.cashplus.BuildConfig
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityHistoryDetailBinding
import com.pasukanlangit.cashplus.model.request_body.OrderCheckTokoRequest
import com.pasukanlangit.cashplus.model.request_body.ParamPassengersItem
import com.pasukanlangit.cashplus.model.request_body.ParamPrintTransport
import com.pasukanlangit.cashplus.model.response.TransactionItem
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.checkout.DetailTrxAdapter
import com.pasukanlangit.cashplus.ui.pages.history.detailhotel.DetailHistoryHotelActivity
import com.pasukanlangit.cashplus.ui.pages.history.detailtokoonline.DetailHistoryTokoActivity
import com.pasukanlangit.cashplus.ui.pages.history.preview.EditPriceFragment
import com.pasukanlangit.cashplus.ui.pages.history.preview.PreviewFragment
import com.pasukanlangit.cashplus.ui.pages.history.print.PrintActivity
import com.pasukanlangit.cashplus.ui.pages.history.voucher.HistoryTicketActivity
import com.pasukanlangit.cashplus.ui.pembayarancart.pay.ProductCode
import com.pasukanlangit.cashplus.utils.CategoryProduct
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.cashplus.utils.MyUtils.actionShareFile
import com.pasukanlangit.cashplus.utils.MyUtils.formatReceipt
import com.pasukanlangit.cashplus.utils.MyUtils.generatePdf
import com.pasukanlangit.cashplus.utils.MyUtils.getCompleteLocaleDateFormat
import com.pasukanlangit.cashplus.utils.MyUtils.getNumberRupiah
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CoreUtils.copyClipboard
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermission
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermissions
import com.pasukanlangit.id.core.utils.InputUtil.toCapitalize
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.id.library_core.utils.TrxStatus
import com.pasukanlangit.id.library_core.utils.TrxUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryDetailActivity : AppCompatActivity(),
    EditPriceFragment.OnButtonSetPriceClickListener,
    PreviewFragment.OnButtonClickListener {

    private lateinit var binding: ActivityHistoryDetailBinding
    private val viewModel: HistoryDetailViewModel by viewModel()
    val sharedPref : SharedPrefDataSource by inject()

    private lateinit var detailTrxAdapter: DetailTrxAdapter

    private var token: String? = null
    private var uuid: String? = null
    private var trxDetailIntent: TransactionItem ?= null
    private var trxId : String? = (1..100).random().toString()
    private var receiptStruck: String? = null

    private val permissionBluetoothPrint =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                actionToSelectPrint()
                return@registerForActivityResult
            }
            Toast.makeText(this, getString(R.string.provide_request_permission_bluetooth), Toast.LENGTH_SHORT).show()
        }

    private val permissionReadWrite = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            generatePDF()
            return@registerForActivityResult
        }
        Toast.makeText(this, getString(R.string.provide_request_permission_access_storage), Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) { finish() }
        } else {
            onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() { finish() }
            })
        }

        trxDetailIntent = intent.parcelable(KEY_TRX_DETAIL)
        uuid = sharedPref.getUUID()
        token = sharedPref.getAccessToken()

        initRecyclerView()
        with(binding) {
            appBar.setOnClickListener { finish() }

            trxDetailIntent?.let { trxDetail ->
                if(!uuid.isNullOrEmpty() && !token.isNullOrEmpty()) {
                    trxId  = trxDetail.trxId
                    tvDetailtrxCategory.text = trxDetail.category
                    txtCategoryPurchase.isVisible = !trxDetail.category.isNullOrEmpty()
                    tvDetailtrxCategory.isVisible = !trxDetail.category.isNullOrEmpty()

                    btnActionDetail.isVisible = ProductCode.values().singleOrNull { product ->
                        product.showBtnDetailFirst && product.value.uppercase() in (trxDetail.productCode?.uppercase()
                            ?: PRODUCT_CODE_INVALID)
                    } != null || (trxDetail.category == CategoryProduct.VOUCHER.value && !trxDetail.url.isNullOrEmpty())

                    if(trxDetail.category == CategoryProduct.VOUCHER.value) {
                        tvActionDetail.text = getString(R.string.show_voucher)
                    }

                    btnActionDetail.setOnClickListener {
                        when {
                            ProductCode.TOKO_ONLINE.value in (trxDetail.productCode?.uppercase()
                                ?: PRODUCT_CODE_INVALID) -> {
                                val intent = Intent(this@HistoryDetailActivity, DetailHistoryTokoActivity::class.java)
                                intent.putExtra(DetailHistoryTokoActivity.KEY_TRX_ID, trxDetail.trxId)
                                startActivity(intent)
                            }
                            trxDetail.category == CategoryProduct.VOUCHER.value && !trxDetail.url.isNullOrEmpty()-> {
                                val intent = Intent(this@HistoryDetailActivity, HistoryTicketActivity::class.java)
                                intent.putExtra(HistoryTicketActivity.KEY_URL_VOUCHER, trxDetail.url)
                                startActivity(intent)
                            }
                            """(?i)(HOTEL|KERETA|PESAWAT|KAPAL)""".toRegex().containsMatchIn(trxDetail.productCode!!) -> {
                                viewModel.printReciept(
                                    OrderCheckTokoRequest(trxDetail.trxId!!, uuid!!, ""),
                                    token!!
                                )
                            }
                            else -> {
                                MyUtils.showCoomingSon(supportFragmentManager)
                            }
                        }
                    }

                    tvDetailtrxDesc.text = trxDetail.dsc
                    tvItemPurchase.text = trxDetail.dsc
                    tvDetailtrxDesc.isVisible = !trxDetail.dsc.isNullOrEmpty()

                    val fee = trxDetail.fee?.toIntOrNull()
                    when {
                        fee != null -> {
                            txtFee.text = if (fee < 0) getString(R.string.discount_fee) else getString(R.string.service_fee)
                            tvFee.text =
                                if (fee < 0) getNumberRupiah(trxDetail.fee.replace("-", "").toIntOrNull())
                                else if (fee == 0) getString(R.string.admin_free)
                                else getNumberRupiah(fee)
                        }
                        else -> tvFee.text = getString(R.string.admin_free)
                    }
                    tvDetailtrxPrice.text = getNumberRupiah(trxDetail.value?.toIntOrNull())
                    tvPricePurchase.text = getNumberRupiah(trxDetail.value?.toIntOrNull())
                    tvEndingBalance.text = getNumberRupiah(trxDetail.endingBalance?.toInt())
                    rvSnSplit.layoutManager = LinearLayoutManager(this@HistoryDetailActivity)
                    tvInfoSetPrice.text = if (trxDetail.productCode?.contains("BANK", ignoreCase = true) == true) getString(R.string.make_sure_set_price, getString(R.string.service_fee))
                        else getString(R.string.make_sure_set_price, getString(R.string.selling_price))
                    btnCopyIdTransaction.setOnClickListener {
                        copyClipboard(this@HistoryDetailActivity, trxDetail.trxId)
                    }

                    val splittingSn = trxDetail.sn?.split("||")
                    val splittingSnDoubleSlash = trxDetail.sn?.split("//")
                    val splittingSnSlash = trxDetail.sn?.split("/")

                    val dateFormatted = getTimeAndDate(trxDetail.trxStart).last()
                    val timeFormatted = getTimeAndDate(trxDetail.trxStart).first()

                    if(!splittingSn.isNullOrEmpty() && splittingSn.size > 1) {
                        wrapperWithSn.isVisible = false
                        wrapperPln.isVisible = true
                        rvDetailHistory.isVisible = true
                        labelToken.isVisible = false
                        tvToken.isVisible = false
                        btnCopyToken.isVisible = false
                        labelDetailHistory.text = splittingSn.first()

                        val infoDetail = "ID TRANSAKSI : ${trxDetail.trxId}|TANGGAL : $dateFormatted |JAM: $timeFormatted|".plus(
                            splittingSn[1]
                        )
                        detailTrxAdapter.setInfoDetails(infoDetail.split("|"))
                    } else if(!splittingSnDoubleSlash.isNullOrEmpty() && splittingSnDoubleSlash.size > 1) {
                        wrapperWithSn.isVisible = false
                        wrapperPln.isVisible = true
                        rvDetailHistory.isVisible = true

                        val leftSplittedSlash = splittingSnDoubleSlash[0].split("/")
                        val rightSplittedSlash = splittingSnDoubleSlash[1].split("/")

                        val token = leftSplittedSlash[0]
                        val namaPelanggan = leftSplittedSlash[1]
                        val tarif = leftSplittedSlash[2]
                        val daya = leftSplittedSlash[3]
                        val kwh = leftSplittedSlash[4]
                        val ppn = rightSplittedSlash[0]
                        val ppj = rightSplittedSlash[1]
                        val materai = rightSplittedSlash[2]
                        val angsuran = rightSplittedSlash[3]
                        val tokenStrom = rightSplittedSlash[4]

                        val stringFormatted = "${getString(R.string.transaction_id)}:${trxDetail.trxId}|${getString(R.string.customer_id)}:${trxDetail.noHp}|${getString(R.string.on_behalf_of).toCapitalize()}:$namaPelanggan|Tarif:$tarif|Daya:$daya|KWH:$kwh|PPN:$ppn|PPJ:$ppj|Materai:$materai|Angsuran:$angsuran|Rp Stroom:$tokenStrom|Tanggal:$dateFormatted|Jam:$timeFormatted"
                        detailTrxAdapter.setInfoDetails(stringFormatted.split("|"))
                        tvToken.text = token
                        btnCopyToken.setOnClickListener { copyClipboard(this@HistoryDetailActivity, token) }
                    } else if (!splittingSnSlash.isNullOrEmpty() && splittingSnSlash.size > 1 && trxDetail.productCode?.contains("PLN", ignoreCase = true) == true) {
                        wrapperWithSn.isVisible = false
                        wrapperPln.isVisible = true
                        rvDetailHistory.isVisible = true

                        val token = splittingSnSlash.first()
                        val customerName = splittingSnSlash[1]
                        val rates = splittingSnSlash[2]
                        val power = splittingSnSlash[3]
                        val kwh = splittingSnSlash.last()

                        val stringFormatted = "${getString(R.string.transaction_id)}:${trxDetail.trxId}|${getString(R.string.customer_id)}:${trxDetail.noHp}|${getString(R.string.on_behalf_of).toCapitalize()}:$customerName|Tarif:$rates|Daya:$power VA|KWH:$kwh,0|Tanggal:$dateFormatted|Jam:$timeFormatted"
                        detailTrxAdapter.setInfoDetails(stringFormatted.split("|"))
                        tvToken.text = token
                        btnCopyToken.setOnClickListener { copyClipboard(this@HistoryDetailActivity, token) }
                    } else if (!splittingSnSlash.isNullOrEmpty() && splittingSnSlash.size > 1 && trxDetail.category?.contains("#EMONEY", ignoreCase = true) == true) {
                        tvDetailtrxId.text = trxDetail.trxId
                        tvDetailtrxDest.text = trxDetail.noHp
                        tvDetailtrxDate.text = dateFormatted
                        tvDetailtrxTime.text = timeFormatted
                        rvSnSplit.adapter = DetailSNAdapter(splittingSnSlash)
                    } else {
                        tvDetailtrxId.text = trxDetail.trxId
                        tvDetailtrxDest.text = trxDetail.noHp

                        tvDetailtrxDate.text = dateFormatted
                        tvDetailtrxTime.text = timeFormatted
                        val splittingWithPipe = trxDetail.sn?.split("|")
                        if (!splittingWithPipe.isNullOrEmpty() && splittingWithPipe.size > 1) {
                            rvSnSplit.adapter = DetailSNAdapter(splittingWithPipe)
                        } else {
                            val itemSN = listOf(trxDetail.sn.toString())
                            rvSnSplit.adapter = DetailSNAdapter(itemSN)
                        }
                    }

                    wrapperSn.isVisible = TrxUtil.getTrxStatusByCode(trxDetail.transStat) != TrxStatus.FAILED
                    tvSnIsPending.isVisible = TrxUtil.getTrxStatusByCode(trxDetail.transStat) == TrxStatus.PENDING
                    wrapperSetPrice.isVisible = TrxUtil.getTrxStatusByCode(trxDetail.transStat) == TrxStatus.SUCCESS
                    btnPrintOrShare.isVisible = TrxUtil.getTrxStatusByCode(trxDetail.transStat) == TrxStatus.SUCCESS
                    rvSnSplit.isVisible = TrxUtil.getTrxStatusByCode(trxDetail.transStat) == TrxStatus.SUCCESS
                    spaceContent.isVisible = rvSnSplit.isVisible == false
                    setStatusTrx(trxDetail.transStat)
                }
            }

            btnPrintOrShare.setOnClickListener {
                EditPriceFragment.newInstance(
                    productCode = trxDetailIntent?.productCode.toString(),
                    defaultPrice = if (trxDetailIntent?.productCode?.contains("pay", ignoreCase = true) == true)
                        (detailTrxAdapter.getAdminValue().plus(trxDetailIntent?.fee?.toIntOrNull() ?: 0)).toString()
                    else if (trxDetailIntent?.productCode?.contains("unit", ignoreCase = true) == true)
                        trxDetailIntent?.value?.toIntOrNull()?.minus(trxDetailIntent?.qty?.toIntOrNull() ?: 0).toString()
                    else trxDetailIntent?.value,
                    isTrxPay = trxDetailIntent?.productCode?.contains("pay", ignoreCase = true) == true || trxDetailIntent?.productCode?.contains("unit", ignoreCase = true) == true,
                    event = this@HistoryDetailActivity
                ).show(supportFragmentManager, "Set Price")
            }
        }

        observeReceipt()
        observeReceiptPrint()
    }

    private fun initRecyclerView() {
        detailTrxAdapter = DetailTrxAdapter()
        with(binding.rvDetailHistory) {
            layoutManager = LinearLayoutManager(this@HistoryDetailActivity)
            adapter = detailTrxAdapter
        }
    }

    private fun observeReceiptPrint() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.printLoading.collectLatest {
                        binding.loading.isVisible = it
                        binding.btnPrintOrShare.isEnabled = !it
                    }
                }
                launch {
                    viewModel.printReceipt.collectLatest { response ->
                        if (response != null) {
                            PreviewFragment.newInstance(
                                codeProvider = trxDetailIntent?.providerCode,
                                receipt = formatReceipt(
                                    receipt = response.receipt, codeProvider = trxDetailIntent?.providerCode.toString(),
                                    productCode = trxDetailIntent?.productCode.toString(),
                                    fee = if (trxDetailIntent?.productCode?.contains("pay", ignoreCase = true) == true)
                                        response.fee.toInt()
                                    else null,
                                    feeCounter = if (trxDetailIntent?.productCode?.contains("pay", ignoreCase = true) == true)
                                        response.feeCounter.toInt()
                                    else null
                                ), event = this@HistoryDetailActivity
                            ).show(supportFragmentManager, "Preview Print or Share")
                        }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.something_wrong))
                                .image(R.drawable.illustration_error)
                                .description(info)
                                .buttonNegative(
                                    NegativeButtonAction(
                                        btnLabel = getString(R.string.close),
                                        backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                        buttonTextColor = Color.parseColor("#0A57FF"),
                                        onBtnClicked = {
                                            viewModel.removePrintError()
                                        }
                                    )
                                )
                                .show(supportFragmentManager)
                        }
                    }
                }
            }
        }
    }

    private fun observeReceipt() {
        viewModel.receipt.observe(this@HistoryDetailActivity) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    binding.loading.isVisible = false

                    val splittedInfo = response.data?.receipt?.split("||")
                    val lastValue = splittedInfo?.last()

                    if (lastValue.isNullOrEmpty()) {
                        val menusAllFragment = ButtomSheetNotif(getString(R.string.data_not_found), NotifType.NOTIF_ERROR)
                        menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                        return@observe
                    }

                    lastValue.removeSuffix("|")

                    if("""(?i)(HOTEL)""".toRegex().containsMatchIn(trxDetailIntent?.productCode ?: PRODUCT_CODE_INVALID)) {
                        Intent(this@HistoryDetailActivity, DetailHistoryHotelActivity::class.java).apply {
                            putExtra(DetailHistoryHotelActivity.KEY_JSON_PRINT, lastValue)
                            startActivity(this)
                        }
                    } else {
                        if(lastValue.isEmpty()) return@observe
                        val jsonObject = JSONObject(lastValue)

                        lateinit var requestPrint: ParamPrintTransport
                        if("""(?i)(KERETA)""".toRegex().containsMatchIn(trxDetailIntent?.productCode  ?: PRODUCT_CODE_INVALID)){
                            val dateDeparture = jsonObject.getString("train_departure").split(" ").first()
                            val dateDepartureFormatted = getCompleteLocaleDateFormat(dateDeparture)

                            val date = jsonObject.getString("tanggal").split(" ").first()
                            val dateFormatted = getCompleteLocaleDateFormat(date)

                            val passengers = mutableListOf<ParamPassengersItem>()
                            val passengersArray = JSONArray(jsonObject.getString("train_datapassengers_json"))
                            for(i in 0 until passengersArray.length()) {
                                val passengerObject = passengersArray.getJSONObject(i)
                                val passengersItem = ParamPassengersItem(
                                    seat = passengerObject.getString("passenger_seat"),
                                    name = passengerObject.getString("passenger_fullname"),
                                    title = passengerObject.getString("passenger_title"),
                                    type = passengerObject.getString("passenger_type"),
                                    id = passengerObject.getString("passenger_idnumber"),
                                    gerbong = passengerObject.getString("passenger_gerbong"),
                                )
                                passengers.add(passengersItem)
                            }

                            requestPrint = ParamPrintTransport(
                                date = dateFormatted,
                                booking_code = jsonObject.getString("kodebooking"),
                                print_category = "KERETA",
                                transport_name = jsonObject.getString("train_name"),
                                departure_date = dateDepartureFormatted,
                                transport_rute = jsonObject.getString("train_inforoute"),
                                departure_time = jsonObject.getString("train_time"),
                                transport_class = jsonObject.getString("train_class"),
                                transport_number = jsonObject.getString("train_number"),
                                passengers = passengers
                            )
                            startActivity(
                                Intent(this@HistoryDetailActivity, HistoryDetailTransportActivity::class.java)
                                    .putExtra(HistoryDetailTransportActivity.DATA_PARAMS, Gson().toJson(requestPrint).toString())
                                    .putExtra(HistoryDetailTransportActivity.TRX_ID, jsonObject.getString("tid"))
                            )
                        } else if("""(?i)(PESAWAT)""".toRegex().containsMatchIn(trxDetailIntent?.productCode  ?: PRODUCT_CODE_INVALID)) {
                            val dateDepartureSplitted = jsonObject.getString("flight_departure").split(" ").toMutableList()
                            dateDepartureSplitted.removeLast()
                            val dateDeparture = dateDepartureSplitted.joinToString(" ")
                            val dateDepartureFormatted = getCompleteLocaleDateFormat(dateDeparture)

                            val date = jsonObject.getString("tanggal").split(" ").first()
                            val dateFormatted = getCompleteLocaleDateFormat(date)

                            val passengers = mutableListOf<ParamPassengersItem>()
                            val passengersArray = JSONArray(jsonObject.getString("flight_datapassengers_json"))
                            for(i in 0 until passengersArray.length()){
                                val passengerObject = passengersArray.getJSONObject(i)
                                val passengersItem = ParamPassengersItem(
                                    seat = passengerObject.getString("passenger_ffnumber"),
                                    name = passengerObject.getString("passenger_fullname"),
                                    title = passengerObject.getString("passenger_title"),
                                    type = passengerObject.getString("passenger_type"),
                                    id = passengerObject.getString("passenger_passportnumber"),
                                    gerbong = "",
                                )
                                passengers.add(passengersItem)
                            }

                            requestPrint = ParamPrintTransport(
                                date = dateFormatted,
                                booking_code = jsonObject.getString("kodebooking"),
                                print_category = "PEWASAWAT",
                                transport_name = jsonObject.getString("flight"),
                                departure_date = dateDepartureFormatted,
                                transport_rute = jsonObject.getString("flight_infotransit"),
                                departure_time = jsonObject.getString("flight_time"),
                                transport_class = jsonObject.getString("flight_class"),
                                transport_number = jsonObject.getString("flight_code"),
                                passengers = passengers
                            )
                            startActivity(
                                Intent(this@HistoryDetailActivity, HistoryDetailTransportActivity::class.java)
                                    .putExtra(HistoryDetailTransportActivity.DATA_PARAMS, Gson().toJson(requestPrint).toString())
                                    .putExtra(HistoryDetailTransportActivity.TRX_ID, jsonObject.getString("tid"))
                            )
                        } else if("""(?i)(KAPAL)""".toRegex().containsMatchIn(trxDetailIntent?.productCode  ?: PRODUCT_CODE_INVALID)) {
                            val dateDepartureSplitted = jsonObject.getString("ship_departure").split(" ").toMutableList()
                            dateDepartureSplitted.removeLast()
                            val dateDeparture = dateDepartureSplitted.joinToString(" ")
                            val dateDepartureFormatted = getCompleteLocaleDateFormat(dateDeparture)

                            val date = jsonObject.getString("tanggal").split(" ")[0]
                            val dateFormatted = getCompleteLocaleDateFormat(date)

                            val passengers = mutableListOf<ParamPassengersItem>()
                            val passengersArray = JSONArray(jsonObject.getString("ship_datapassengers_json"))
                            for(i in 0 until passengersArray.length()){
                                val passengerObject = passengersArray.getJSONObject(i)
                                val passengersItem = ParamPassengersItem(
                                    seat = passengerObject.getString("passenger_deck"),
                                    name = passengerObject.getString("passenger_fullname"),
                                    title = passengerObject.getString("passenger_title"),
                                    type = passengerObject.getString("passenger_type"),
                                    id = passengerObject.getString("passenger_idnumber"),
                                    gerbong = passengerObject.getString("passenger_cabin"),
                                )
                                passengers.add(passengersItem)
                            }

                            requestPrint = ParamPrintTransport(
                                date = dateFormatted,
                                booking_code = jsonObject.getString("kodebooking"),
                                print_category = "KAPAL",
                                transport_name = jsonObject.getString("ship_name"),
                                departure_date = dateDepartureFormatted,
                                transport_rute = jsonObject.getString("ship_route"),
                                departure_time = jsonObject.getString("ship_time"),
                                transport_class = jsonObject.getString("ship_class"),
                                transport_number = jsonObject.getString("ship_number"),
                                passengers = passengers
                            )
                            startActivity(
                                Intent(this@HistoryDetailActivity, HistoryDetailTransportActivity::class.java)
                                    .putExtra(HistoryDetailTransportActivity.DATA_PARAMS, Gson().toJson(requestPrint).toString())
                                    .putExtra(HistoryDetailTransportActivity.TRX_ID, jsonObject.getString("tid"))
                            )
                        }
                    }
                }
                Status.ERROR -> {
                    binding.loading.isVisible = false

                    val menusAllFragment = ButtomSheetNotif(response.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                }
                Status.LOADING -> binding.loading.isVisible = true
            }
        }
    }

    private fun getTimeAndDate(trxStart: String?) : List<String?> {
        val dateTrxSplited = trxStart?.split(" ")
        val dateTrx = dateTrxSplited?.first()
        val timeTrx = dateTrxSplited?.last()

        val inputFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateTime: LocalDate = LocalDate.parse(dateTrx, inputFormat)

        val outputFormat = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))

        return listOf(timeTrx, outputFormat.format(dateTime))
    }

    private fun setStatusTrx(transStat: String?) {
        with(binding) {
            with(tvDetailtrxStatus) {
                when(TrxUtil.getTrxStatusByCode(transStat)) {
                    TrxStatus.PENDING -> {
                        text = getString(R.string.pending)
                        setTextColor(Color.parseColor("#DBA800"))
                    }
                    TrxStatus.SUCCESS -> {
                        text = getString(R.string.success)
                        setTextColor(Color.parseColor("#22C55E"))
                    }
                    TrxStatus.FAILED -> {
                        text = getString(R.string.failed)
                        setTextColor(Color.parseColor("#FF6150"))
                    }
                }
            }
        }
    }

    override fun onButtonSetPriceClicked(newPrice: String) {
        trxDetailIntent?.let { detail ->
            viewModel.newPrintReceipt(
                uuid = uuid, productCode = detail.productCode.toString(),
                trxId = detail.trxId.toString(), adjustPrice = newPrice, accessToken = token
            )
        }
    }

    override fun onPrintClickListener(receipt: String) {
        receiptStruck = receipt
        permissionBluetooth()
    }

    override fun onShareClickListener(receipt: String) {
        receiptStruck = receipt
        permissionReadWrite()
    }

    private fun permissionBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (hasPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN))) {
                actionToSelectPrint()
                return
            }
            permissionBluetoothPrint.launch(arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN))
            return
        }

        if (hasPermissions(this, arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN))) {
            actionToSelectPrint()
            return
        }
        permissionBluetoothPrint.launch(arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN))
    }

    private fun permissionReadWrite() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            generatePDF()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (hasPermission(this@HistoryDetailActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                generatePDF()
                return
            }
            permissionReadWrite.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            return
        }
        if (hasPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            generatePDF()
            return
        }
        permissionReadWrite.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    private fun actionToSelectPrint() {
        val intent = Intent(this, PrintActivity::class.java).apply {
            putExtra(PrintActivity.PRINT_RECEIPT_KEY, receiptStruck)
        }
        startActivity(intent)
    }

    @SuppressLint("Recycle", "Range")
    private fun generatePDF() {
        val sdf = SimpleDateFormat("yyMMddHHmmss", Locale.getDefault())
        val currentTime = sdf.format(Date())
        val fileName = "$currentTime${trxDetailIntent?.productCode}$trxId.pdf"

        val resultGenerate = generatePdf(context = this, receipt = receiptStruck.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOCUMENTS + File.separator + "Cashplus" + File.separator + "Struk"
                    )
                }
                val uri = contentResolver.insert(
                    MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                    contentValues
                )
                if (uri != null) {
                    val outputStream = contentResolver.openOutputStream(uri)
                    outputStream?.flush()
                    resultGenerate.writeTo(outputStream)
                    outputStream?.close()
                    actionShareFile(uri = uri, type = "application/pdf")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return
        }

        val externalPath =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}" + File.separator + "Cashplus" + File.separator + "Struk"
        val directory = File(externalPath)
        try {
            if (!directory.exists()) directory.mkdirs()
            val file = File(directory, fileName)

            val outputStream = FileOutputStream(file)
            resultGenerate.writeTo(outputStream)
            outputStream.close()

            val uri = FileProvider.getUriForFile(
                this, "${BuildConfig.APPLICATION_ID}.$localClassName.provider",
                file
            )
            actionShareFile(uri = uri, type = "application/pdf")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val KEY_TRX_DETAIL = "KEY_TRX_DETAIL"
        const val PRODUCT_CODE_INVALID = "!@#!@!$$#$$#$!"
    }
}