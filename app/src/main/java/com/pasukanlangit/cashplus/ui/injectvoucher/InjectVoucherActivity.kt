package com.pasukanlangit.cashplus.ui.injectvoucher

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.adapter.DataProductAdapter
import com.pasukanlangit.cashplus.databinding.ActivityInjectVoucherBinding
import com.pasukanlangit.cashplus.domain.model.DataInject
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.cashplus.utils.MyUtils.goToMainAndSendMessage
import com.pasukanlangit.cashplus.utils.MyUtils.httpToHttps
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.EnterPinDialogFragment
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyNumber
import com.pasukanlangit.id.core.utils.InputUtil.toCapitalize
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.core.utils.setDropDownView
import com.pasukanlangit.id.library_core.domain.model.NotifType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class InjectVoucherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInjectVoucherBinding
    private val viewModel: InjectVoucherViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null

    private lateinit var dataVouchers: List<ProductModel>
    private lateinit var dataProviders: List<ProductModel>
    private val productsVoucher = mutableListOf<ProductModel>()
    private var productChoosed: ProductModel? = null

    private var stateProviderPosition = false
    private var stateBarcodeScanResult = false
    private var stateInitialBarcodeScan = false
    private var stateFinalBarcodeScan = false
    private var isScan = false

    private var providerPosition = 0
    private var typePrintPosition = 0

    private var balance: Double? = null
    private var codeProduct: String? = null

    private val dataBulk = arrayListOf<DataInject>()

    val enterPIN = EnterPinDialogFragment(
        onEnterPinCompleted = {
            viewModel.transactionBulk(
                uuid = uuid, codeProduct = codeProduct.toString(), pin = it,
                accessToken = accessToken, dataBulk = dataBulk
            )
        }
    )

    private val scanBarcode = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        with(binding) {
            when(result.resultCode) {
                RESULT_OK -> {
                    val data = result.data?.getStringExtra(RESULT_SCAN_BARCODE)
                    when(result.data?.getStringExtra(DEST_SCAN)) {
                        "1" -> {
                            edtBarcodeScanResult.setText(data)
                            btnScanBarcode.isEnabled = true
                            if (!data.isNullOrEmpty()) stateBarcodeScanResult = true
                        }
                        "11" -> {
                            edtInitialBarcodeScanResult.setText(data)
                            btnScanInitialBarcode.isEnabled = true
                            if (!data.isNullOrEmpty()) stateInitialBarcodeScan = true
                        }
                        "22" -> {
                            edtFinalBarcodeScanResult.setText(data)
                            btnScanFinalBarcode.isEnabled = true
                            if (!data.isNullOrEmpty()) stateFinalBarcodeScan = true
                        }
                        "404" -> Toast.makeText(this@InjectVoucherActivity, result.data?.getStringExtra(RESULT_SCAN_BARCODE), Toast.LENGTH_SHORT).show()
                    }
                }
                RESULT_CANCELED -> {} // Nothing
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInjectVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()
        viewModel.getProductInject(uuid = uuid, accessToken = accessToken)
        with(binding) {
            setUpRecyclerView()
            imgBack.setOnClickListener { finish() }
            swipeRefreshInjectVoucher.setOnRefreshListener {
                viewModel.getProductInject(uuid = uuid, accessToken = accessToken)
                txtProduct.isVisible = false
                rvProductVoucher.isVisible = false
                wrapperVoucherChoose.isVisible = false
                txtPrintType.isVisible = false
                spinnerPrintType.setSelection(0)
                typePrintPosition = 0
                spinnerPrintType.isVisible = false
                wrapperBarcodeVoucherPcs.isVisible = false
                wrapperRangeInputVoucher.isVisible = false
                edtBarcodeScanResult.text?.clear()
                edtInitialBarcodeScanResult.text?.clear()
                edtFinalBarcodeScanResult.text?.clear()
                btnNextToPayment.isEnabled = false
                isScan = false
                swipeRefreshInjectVoucher.isRefreshing = false
            }
            spinnerProvider.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    stateProviderPosition = position > 0
                    if (position > 0) {
                        txtProduct.isVisible = true
                        rvProductVoucher.isVisible = !isScan
                        chooseProductInject(position-1)
                        if (position != providerPosition) {
                            providerPosition = position
                            spinnerPrintType.setSelection(0)
                            wrapperBarcodeVoucherPcs.isVisible = false
                            edtBarcodeScanResult.text?.clear()
                            edtInitialBarcodeScanResult.text?.clear()
                            edtFinalBarcodeScanResult.text?.clear()
                            btnNextToPayment.isVisible = false
                        }
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spinnerPrintType.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        typePrintPosition = position
                        if (position == 1) {
                            wrapperBarcodeVoucherPcs.isVisible = true
                            wrapperRangeInputVoucher.isVisible = false
                            btnNextToPayment.isEnabled = if (isScan) true else
                                stateProviderPosition && (productChoosed != null) && stateBarcodeScanResult
                            return
                        }
                        wrapperBarcodeVoucherPcs.isVisible = false
                        wrapperRangeInputVoucher.isVisible = true
                        btnNextToPayment.isEnabled = if (isScan) true else
                            stateProviderPosition && (productChoosed != null) &&
                                    stateInitialBarcodeScan && stateFinalBarcodeScan
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            btnChangeProduct.setOnClickListener {
                rvProductVoucher.isVisible = true
                wrapperVoucherChoose.isVisible = false
            }

            edtBarcodeScanResult.addTextChangeListener("1")
            btnScanBarcode.setOnClickListener {
                btnScanFinalBarcode.isEnabled = false
                scanBarcode("1")
            }
            edtInitialBarcodeScanResult.addTextChangeListener("11")
            btnScanInitialBarcode.setOnClickListener {
                btnScanFinalBarcode.isEnabled = false
                scanBarcode("11")
            }
            edtFinalBarcodeScanResult.addTextChangeListener("22")
            btnScanFinalBarcode.setOnClickListener {
                btnScanFinalBarcode.isEnabled = false
                scanBarcode("22")
            }
            btnNextToPayment.setOnClickListener {
                KeyboardUtil.hideSoftKeyboard(this@InjectVoucherActivity)
                dataBulk.clear()

                if (balance == null) {
                    GenericModalDialogCashplus.Builder()
                        .title(getString(R.string.something_wrong))
                        .image(R.raw.cashplus_gagal, true)
                        .description(getString(R.string.get_balance_error))
                        .buttonNegative(
                            NegativeButtonAction(
                                btnLabel = getString(R.string.close),
                                setClickOnDismiss = true
                            )
                        ).show(supportFragmentManager)
                    return@setOnClickListener
                }

                when(typePrintPosition) {
                    1 -> {
                        val serialNumber = edtBarcodeScanResult.text.toString().trim()
                        dataBulk.add(
                            DataInject(
                                dest = serialNumber,
                                reqId = serialNumber.substring(serialNumber.length/2, serialNumber.length)
                            )
                        )
                    }
                    2 -> {
                        val initialVoucher = edtInitialBarcodeScanResult.text.toString().trim()
                        val finalVoucher = edtFinalBarcodeScanResult.text.toString().trim()
                        val initVFirstChar = initialVoucher.substring(0, initialVoucher.length-4)
                        val finalVFirstChar = finalVoucher.substring(0, finalVoucher.length-4)

                        if (initialVoucher >= finalVoucher ||
                            initialVoucher.length != finalVoucher.length ||
                            initVFirstChar != finalVFirstChar) {
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.something_wrong))
                                .image(R.raw.cashplus_gagal, true)
                                .description(getString(R.string.input_sn_voucher_not_valid))
                                .buttonNegative(
                                    NegativeButtonAction(
                                        btnLabel = getString(R.string.close),
                                        setClickOnDismiss = true
                                    )
                                ).show(supportFragmentManager)
                            return@setOnClickListener
                        }

                        var destIndex = initialVoucher.toLong()
                        val amountVoucher = finalVoucher.toLong() - initialVoucher.toLong()
                        for (index in 0..amountVoucher) {
                            val dest = destIndex.toString()
                            dataBulk.add(DataInject(
                                dest = dest,
                                reqId = dest.substring(dest.length/2, dest.length)
                            ))
                            destIndex++
                        }
                    }
                }

                codeProduct = productChoosed?.kode_produk
                DetailInjectVoucherFragment.newInstance(
                    product = productChoosed,
                    balance = this@InjectVoucherActivity.balance,
                    serialNumbers = dataBulk
                ).show(supportFragmentManager, "Detail inject voucher")
            }
        }

        collectInjectVoucher()
    }

    private fun setUpRecyclerView() {
        with(binding.rvProductVoucher) {
            layoutManager = LinearLayoutManager(this@InjectVoucherActivity)
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    private fun chooseProductInject(position: Int) {
        if (productsVoucher.isNotEmpty()) {
            productsVoucher.clear()
        }
        dataVouchers.forEach { data ->
            if (dataProviders[position].kode_provider == data.kode_provider) {
                productsVoucher.add(data)
            }
        }
        with(binding) {
            rvProductVoucher.adapter = DataProductAdapter(productsVoucher) {
                productChoosed = ProductModel(
                    dsc = it.dsc, kode_provider = it.kode_provider, kode_produk = it.kode_produk,
                    short_dsc = it.short_dsc, admin = it.admin, price = it.price, outlet_price = it.outlet_price,
                    category = it.category, img_url = it.img_url, img_url_2 = it.img_url_2, opr_name = it.opr_name,
                    product_type = it.product_type
                )
                rvProductVoucher.isVisible = false
                bindToProductChoose(it)
                wrapperVoucherChoose.isVisible = true
                txtPrintType.isVisible = true
                spinnerPrintType.isVisible = true
                chooseTypePrint()
                btnNextToPayment.isVisible = true
            }
        }
    }

    private fun bindToProductChoose(product: ProductModel) {
        with(binding) {
            var imgUrl = product.img_url_2.ifEmpty { product.img_url.httpToHttps() }
            if(imgUrl.isEmpty()) imgUrl =
                MyUtils.getAvaImagePlaceholder(product.kode_provider.replace("_", " "))

            Glide.with(this@InjectVoucherActivity)
                .load(imgUrl)
                .skipMemoryCache(true)
                .into(ivOperator)
            tvShortDesc.text = product.short_dsc
            tvDesc.text = product.dsc
        }
    }

    private fun chooseTypePrint() {
        val printTypeAdapter = setDropDownView(
            this@InjectVoucherActivity,
            resources.getStringArray(R.array.inject_voucher_print_type).toList()
        )
        printTypeAdapter.setDropDownViewResource(R.layout.item_spinner_small)
        binding.spinnerPrintType.adapter = printTypeAdapter
        binding.spinnerPrintType.setSelection(typePrintPosition)
    }

    private fun collectInjectVoucher() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.productLoading.collectLatest {
                        binding.progressBar.isVisible = it
                    }
                }
                launch {
                    viewModel.productInject.collectLatest { response ->
                        if (response != null) {
                            dataVouchers = response
                            dataProviders = response.distinctBy { it.kode_provider }
                            val providers = mutableListOf<String>()
                            providers.add(getString(R.string.choose_provider))
                            this@InjectVoucherActivity.dataProviders.forEach {
                                providers.add(it.kode_provider.toCapitalize())
                            }
                            val adapter = setDropDownView(this@InjectVoucherActivity, providers)
                            adapter.setDropDownViewResource(R.layout.item_spinner_small)
                            binding.spinnerProvider.adapter = adapter
                        }
                    }
                }
                launch {
                    viewModel.productError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@InjectVoucherActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeProductInjectMessage()
                        }
                    }
                }

                launch {
                    viewModel.loadingBalance.collectLatest {
                        binding.progressBar.isVisible = it
                    }
                }
                launch {
                    viewModel.balance.collectLatest { response ->
                        response?.let { balance = it.balance }
                    }
                }
                launch {
                    viewModel.errorBalance.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@InjectVoucherActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeBalanceMessage()
                        }
                    }
                }

                launch {
                    viewModel.bulkLoading.collectLatest {
                        enterPIN.setLoading(it)
                    }
                }
                launch {
                    viewModel.trxBulk.collectLatest {
                        if (it) {
                            enterPIN.dismiss()
                            delay(300)
                            goToMainAndSendMessage(
                                this@InjectVoucherActivity,
                                getString(R.string.inject_voucher_successfully, dataProviders[providerPosition-1].kode_provider),
                                NotifType.NOTIF_SUCCESS
                            )
                        }
                    }
                }
                launch {
                    viewModel.bulkError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@InjectVoucherActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeTrxBulkMessage()
                        }
                    }
                }
            }
        }
    }

    private fun scanBarcode(scanDest: String) {
        isScan = true
        scanBarcode.launch(
            Intent(this@InjectVoucherActivity, ScanBarcodeActivity::class.java).apply {
                putExtra(DEST_SCAN, scanDest)
            }
        )
    }

    private fun EditText.addTextChangeListener(type: String) {
        this.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                if (input.isEmpty()) {
                    if (type == "1") stateBarcodeScanResult = false
                    if (type == "11") stateInitialBarcodeScan = false
                    if (type == "22") stateFinalBarcodeScan = false
                    this@addTextChangeListener.error = getString(R.string.input_sn_voucher_is_empty)
                    return
                }
                if (input.isNotEmpty() && input.length < 10) {
                    if (type == "1") stateBarcodeScanResult = false
                    if (type == "11") stateInitialBarcodeScan = false
                    if (type == "22") stateFinalBarcodeScan = false
                    return
                }
                if (!input.checkIsOnlyNumber()) {
                    if (type == "1") stateBarcodeScanResult = false
                    if (type == "11") stateInitialBarcodeScan = false
                    if (type == "22") stateFinalBarcodeScan = false
                    this@addTextChangeListener.error = getString(R.string.input_sn_voucher_only_number)
                    return
                }

                if (type == "1") stateBarcodeScanResult = true
                if (type == "11") stateInitialBarcodeScan = true
                if (type == "22") stateFinalBarcodeScan = true
                this@addTextChangeListener.error = null
            }
            override fun afterTextChanged(s: Editable?) {
                binding.btnNextToPayment.isEnabled = if (typePrintPosition == 1)
                    stateProviderPosition && (productChoosed != null) && stateBarcodeScanResult
                else stateProviderPosition && (productChoosed != null) && stateInitialBarcodeScan && stateFinalBarcodeScan
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getBalance(uuid = uuid ?: "", accessToken ?: "")
        with(binding) {
            spinnerProvider.setSelection(providerPosition)
            spinnerPrintType.setSelection(typePrintPosition)
            binding.btnNextToPayment.isEnabled = if (typePrintPosition == 1)
                stateProviderPosition && (productChoosed != null) && stateBarcodeScanResult
            else stateProviderPosition && (productChoosed != null) && stateInitialBarcodeScan && stateFinalBarcodeScan
        }
    }

    companion object {
        const val RESULT_SCAN_BARCODE = "result_scan_barcode"
        const val DEST_SCAN = "dest_scan"
    }
}