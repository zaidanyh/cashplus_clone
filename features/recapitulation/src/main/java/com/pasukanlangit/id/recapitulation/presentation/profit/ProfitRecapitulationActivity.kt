package com.pasukanlangit.id.recapitulation.presentation.profit

import android.Manifest
import android.content.ContentValues
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.id.core.extensions.onDone
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermission
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermissions
import com.pasukanlangit.id.core.utils.InputUtil.inputCharNumberFilter
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.recapitulation.R
import com.pasukanlangit.id.recapitulation.databinding.ActivityProfitRecapitulationBinding
import com.pasukanlangit.id.recapitulation.domain.model.ProfitByProductResponse
import com.pasukanlangit.id.recapitulation.presentation.utils.DateObjectClass
import com.pasukanlangit.id.recapitulation.presentation.utils.FilterDateFragment
import com.pasukanlangit.id.recapitulation.presentation.utils.RecapUtils.setDataXlsxFile
import com.pasukanlangit.id.recapitulation.presentation.utils.RecapUtils.setTitleXlsxFile
import com.pasukanlangit.id.recapitulation.presentation.utils.RecapUtils.toNestedListProductOfString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfitRecapitulationActivity : AppCompatActivity(), FilterDateFragment.SetOnSubmitListener {

    private lateinit var binding: ActivityProfitRecapitulationBinding
    private val viewModel: RecapProfitViewModel by viewModel()

    private lateinit var recapAdapter: RecapProfitAdapter
    private lateinit var sdf: SimpleDateFormat

    private var dataRecap: List<ProfitByProductResponse>? = null
    private var dateValue: DateObjectClass? = null

    private val permissionReadWrite = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            generateXlsFile(dataRecap)
            return@registerForActivityResult
        }
        Toast.makeText(this, getString(R.string.provide_request_permission_access_storage), Toast.LENGTH_SHORT).show()
    }

    private val titlesRecapTransaction = listOf("#", "Kategori", "Kode Produk", "Produk", "Deskripsi", "Modal", "Harga Jual", "Keuntungan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfitRecapitulationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        initRecyclerview()
        dateValue = DateObjectClass(
            dateStart = null, dateEnd = null, label = getString(R.string.today).lowercase()
        )
        sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        viewModel.onTriggerEvents(
            RecapProfitEvents.AllRecapProfit(
                dateStart = sdf.format(Date()),
                dateEnd = sdf.format(Date())
            )
        )
        with(binding) {
            imgBack.setOnClickListener { finish() }
            iconDocumentDownload.setOnClickListener {
                if (::recapAdapter.isInitialized) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        generateXlsFile(dataRecap)
                        return@setOnClickListener
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (hasPermission(
                                this@ProfitRecapitulationActivity,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) {
                            generateXlsFile(dataRecap)
                            return@setOnClickListener
                        }
                        permissionReadWrite.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                        return@setOnClickListener
                    }
                    if (hasPermissions(
                            this@ProfitRecapitulationActivity,
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        )
                    ) {
                        generateXlsFile(dataRecap)
                        return@setOnClickListener
                    }
                    permissionReadWrite.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }
            iconDocumentFilter.setOnClickListener {
                FilterDateFragment.newInstance(
                    date = dateValue, event = this@ProfitRecapitulationActivity
                ).show(supportFragmentManager, "filter_recap_profit")
            }
            edtSearchTrans.filters = arrayOf(InputFilter.LengthFilter(16), inputCharNumberFilter)
            edtSearchTrans.filterDataListener()
            edtSearchTrans.onDone {
                KeyboardUtil.closeKeyboardDialog(this@ProfitRecapitulationActivity, edtSearchTrans)
            }
            tvSearchNotFound.text = getString(R.string.any_not_found, getString(R.string.search_transaction))
        }
        collectData()
    }

    private fun initRecyclerview() {
        with(binding.rvRecapTransaction) {
            layoutManager = LinearLayoutManager(this@ProfitRecapitulationActivity)
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    private fun EditText.filterDataListener() {
        this.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s.toString()
                viewModel.onTriggerEvents(
                    RecapProfitEvents.SearchRecap(MutableStateFlow(value))
                )
            }
            override fun afterTextChanged(s: Editable?) {
                try {
                    this@filterDataListener.removeTextChangedListener(this)
                    val value = this@filterDataListener.text.toString()
                    viewModel.onTriggerEvents(
                        RecapProfitEvents.SearchRecap(MutableStateFlow(value))
                    )
                    this@filterDataListener.addTextChangedListener(this)
                    return
                } catch (e: Exception) {
                    e.printStackTrace()
                    this@filterDataListener.addTextChangedListener(this)
                }
            }
        })
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateLoading.collectLatest { state ->
                        with(binding) {
                            if (state) {
                                wrapperEmpty.isVisible = false
                                wrapperContent.isVisible = false
                                iconDocumentDownload.isVisible = false
                                shimmerRecapitulation.isVisible = true
                                shimmerRecapitulation.startShimmer()
                                return@collectLatest
                            }
                            shimmerRecapitulation.isVisible = false
                            shimmerRecapitulation.stopShimmer()
                        }
                    }
                }
                launch {
                    viewModel.allRecapTrans.collectLatest { response ->
                        binding.wrapperEmpty.isVisible = response?.products.isNullOrEmpty()
                        binding.wrapperContent.isVisible = !response?.products.isNullOrEmpty()
                        dataRecap = null
                        if (response != null) {
                            dataRecap = response.products
                            with(binding) {
                                recapAdapter = RecapProfitAdapter()
                                iconDocumentDownload.isVisible = response.products.isNotEmpty() && response.products.size >= 5
                                rvRecapTransaction.adapter = recapAdapter
                                tvDescFilter.text = getString(R.string.filter_desc, dateValue?.label)
                                tvTotalSales.text = getNumberRupiah(response.sellingTotal.toBigIntegerOrNull())
                                tvTotalPurchases.text = getNumberRupiah(response.capitalTotal.toBigIntegerOrNull())
                                tvTotalProfit.text = getNumberRupiah(response.profit.toBigIntegerOrNull())
                            }
                            recapAdapter.setRecapTrans(response.products)
                        }
                        recapAdapter.notifyDataSetChanged()
                        binding.tvTrxEmptyDesc.text = getString(R.string.recap_trans_current_day, dateValue?.label)
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.something_wrong))
                                .image(R.drawable.illustration_login_error)
                                .description(info)
                                .buttonNegative(
                                    NegativeButtonAction(
                                        btnLabel = getString(R.string.close),
                                        backgroundButton = R.drawable.bg_transparent_border_primary_rounded_16,
                                        buttonTextColor = Color.parseColor("#0A57FF"),
                                        setClickOnDismiss = true
                                    )
                                ).show(supportFragmentManager)
                            viewModel.onTriggerEvents(RecapProfitEvents.RemoveAllRecapProfitMessage)
                        }
                    }
                }

                launch {
                    viewModel.keywordInput.collectLatest { result ->
                        binding.tvDescFilter.text = if (!result.isNullOrEmpty()) getString(R.string.result_find, result)
                        else getString(R.string.filter_desc, dateValue?.label)
                        if (::recapAdapter.isInitialized) {
                            recapAdapter.filter.filter(result)
                            binding.rvRecapTransaction.isVisible = recapAdapter.itemCount >= 1
                            binding.tvSearchNotFound.isVisible = recapAdapter.itemCount < 1
                        }
                    }
                }
            }
        }
    }

    private fun generateXlsFile(data: List<ProfitByProductResponse>?) {
        if (data.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.save_recap_required), Toast.LENGTH_SHORT).show()
            return
        }
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Profit")
        sheet.setTitleXlsxFile(titles = titlesRecapTransaction)
        sheet.setDataXlsxFile(data = data.toNestedListProductOfString(), mutableListOf(7, 8, 11, 6, 9, 5, 10, 10))

        val fileName = "rekap_profit_${dateValue?.label?.replace(" ", "")}.xlsx"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val contentValues = ContentValues()
                contentValues.apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + File.separator + "Cashplus" + File.separator + "Rekap" + File.separator + "Profit")
                }
                val uri = contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), contentValues)

                if (uri != null) {
                    val outputStream = contentResolver.openOutputStream(uri)
                    workbook.write(outputStream)
                    outputStream?.close()
                    Toast.makeText(this, getString(R.string.save_recap_successfully, "Profit", dateValue?.label), Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.save_recap_failed, "Profit", dateValue?.label), Toast.LENGTH_SHORT).show()
            }
            return
        }

        val excelFilePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}" + File.separator + "Cashplus" + File.separator + "Rekap" + File.separator + "Profit"
        val directory = File(excelFilePath)
        try {
            if (!directory.exists()) directory.mkdirs()
            val file = File(directory, fileName)

            val out = FileOutputStream(file)
            workbook.write(out)
            out.close()
            Toast.makeText(this, getString(R.string.save_recap_successfully, "Profit", dateValue?.label), Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.save_recap_failed, "Profit", dateValue?.label), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSubmitEvent(date: DateObjectClass) {
        if (this.dateValue?.dateStart == date.dateStart && this.dateValue?.dateEnd == date.dateEnd) {
            Toast.makeText(this, getString(R.string.filter_no_change),Toast.LENGTH_SHORT).show()
            return
        }
        binding.edtSearchTrans.text?.clear()
        dateValue = DateObjectClass(dateStart = date.dateStart, dateEnd = date.dateEnd, label = date.label)
        viewModel.onTriggerEvents(
            RecapProfitEvents.AllRecapProfit(dateStart = dateValue?.dateStart.toString(), dateEnd = dateValue?.dateEnd.toString())
        )
    }
}