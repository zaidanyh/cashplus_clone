package com.pasukanlangit.id.recapitulation.presentation.deposit

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermission
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermissions
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.recapitulation.R
import com.pasukanlangit.id.recapitulation.databinding.ActivityDepositRecapitulationBinding
import com.pasukanlangit.id.recapitulation.domain.model.MutationDepositResponse
import com.pasukanlangit.id.recapitulation.presentation.deposit.pagination.RecapSummaryDepositAdapter
import com.pasukanlangit.id.recapitulation.presentation.deposit.pagination.RecapSummaryLoadStateAdapter
import com.pasukanlangit.id.recapitulation.presentation.utils.DateObjectClass
import com.pasukanlangit.id.recapitulation.presentation.utils.FilterDateFragment
import com.pasukanlangit.id.recapitulation.presentation.utils.RecapUtils.setDataXlsxFile
import com.pasukanlangit.id.recapitulation.presentation.utils.RecapUtils.setTitleXlsxFile
import com.pasukanlangit.id.recapitulation.presentation.utils.RecapUtils.toNestedListOfString
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DepositRecapitulationActivity : AppCompatActivity(), FilterDateFragment.SetOnSubmitListener {

    private lateinit var binding: ActivityDepositRecapitulationBinding
    private val viewModel: RecapDepositViewModel by viewModel()

    private lateinit var recapAdapter: RecapSummaryDepositAdapter
    private lateinit var sdf: SimpleDateFormat

    private var dataRecap: List<MutationDepositResponse>? = null
    private var dateValue: DateObjectClass? = null
    private var onSubmitState = false

    private val permissionReadWrite = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            generateXlsFile(dataRecap)
            return@registerForActivityResult
        }
        Toast.makeText(this, getString(R.string.provide_request_permission_access_storage), Toast.LENGTH_SHORT).show()
    }

    private val titlesRecapDeposit = listOf("#", "Tanggal Transaksi", "Nilai", "Tipe", "Saldo Akhir", "Kode Produk", "Tujuan", "Deskripsi", "Kategori")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepositRecapitulationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        initRecyclerView()
        sdf = SimpleDateFormat("yyyyMMdd", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
        dateValue = DateObjectClass(
            dateStart = null, dateEnd = null, label = getString(R.string.today).lowercase()
        )
        viewModel.getSummaryMutation(dateStart = sdf.format(Date()), dateEnd = sdf.format(Date()))
        with(binding) {
            imgBack.setOnClickListener { finish() }
            iconDocumentDownload.setOnClickListener {
                if (::recapAdapter.isInitialized) {
                    dataRecap = recapAdapter.snapshot().items
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        generateXlsFile(dataRecap)
                        return@setOnClickListener
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (hasPermission(this@DepositRecapitulationActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            generateXlsFile(dataRecap)
                            return@setOnClickListener
                        }
                        permissionReadWrite.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                        return@setOnClickListener
                    }
                    if (hasPermissions(this@DepositRecapitulationActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                        generateXlsFile(dataRecap)
                        return@setOnClickListener
                    }
                    permissionReadWrite.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }
            iconDocumentFilter.setOnClickListener {
                FilterDateFragment.newInstance(
                    date = dateValue, this@DepositRecapitulationActivity
                ).show(supportFragmentManager, "filter_date_deposit")
            }
        }
        collectData()
        loadStateListener()
    }

    private fun initRecyclerView() {
        recapAdapter = RecapSummaryDepositAdapter()
        with(binding.rvRecapDeposit) {
            val headerAdapter = RecapSummaryLoadStateAdapter()
            val footerAdapter = RecapSummaryLoadStateAdapter()
            val concatAdapter = recapAdapter.withLoadStateHeaderAndFooter(
                header = headerAdapter, footer = footerAdapter
            )
            layoutManager = LinearLayoutManager(this@DepositRecapitulationActivity)
            adapter = concatAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.summaryMutation.collectLatest { response ->
                        if (::recapAdapter.isInitialized) {
                            recapAdapter.submitData(response)
                        }
                    }
                }

                launch {
                    viewModel.mutationLoading.collectLatest { state ->
                        binding.apply {
                            if (state) {
                                wrapperSummary.isInvisible = true
                                shimmerMutationTotal.isInvisible = false
                                shimmerMutationTotal.startShimmer()
                                return@collectLatest
                            }
                            shimmerMutationTotal.isInvisible = true
                            shimmerMutationTotal.stopShimmer()
                        }
                    }
                }
                launch {
                    viewModel.mutationBalance.collectLatest { response ->
                        if (response != null) {
                            with(binding) {
                                wrapperSummary.isInvisible = response.debit.toInt() == 0 && response.credit.toInt() == 0
                                tvDescFilter.text = getString(R.string.filter_desc, dateValue?.label)
                                tvTotalDebit.text = getNumberRupiah(response.debit.toIntOrNull())
                                tvTotalCredit.text = getNumberRupiah(response.credit.toIntOrNull())
                            }
                        }
                    }
                }
                launch {
                    viewModel.mutationError.collectLatest { message ->
                        message.peek()?.let { info ->
                            handleError(loadState = null, error = info)
                            viewModel.removeMutationMessageError()
                        }
                    }
                }
            }
        }
    }

    private fun loadStateListener() {
        recapAdapter.addLoadStateListener { loadState ->
            binding.apply {
                when(loadState.source.refresh) {
                    is LoadState.Loading -> {
                        wrapperEmpty.isVisible = false
                        rvRecapDeposit.isVisible = false
                        iconDocumentDownload.isVisible = false
                        shimmerDepositMutation.isVisible = true
                        shimmerDepositMutation.startShimmer()
                    }
                    is LoadState.NotLoading -> {
                        rvRecapDeposit.isVisible = recapAdapter.snapshot().items.isNotEmpty()
                        wrapperEmpty.isVisible = recapAdapter.snapshot().items.isEmpty()
                        iconDocumentDownload.isVisible = recapAdapter.itemCount >= 5 && onSubmitState
                        tvTrxEmptyDesc.text = getString(R.string.recap_deposit_current, dateValue?.label)
                        shimmerDepositMutation.isVisible = false
                        shimmerDepositMutation.stopShimmer()
                    }
                    is LoadState.Error -> {
                        wrapperSummary.isInvisible = true
                        rvRecapDeposit.isVisible = false
                        iconDocumentDownload.isVisible = false
                        shimmerDepositMutation.isVisible = false
                        shimmerDepositMutation.stopShimmer()
                        handleError(loadState)
                    }
                }
            }
        }
    }

    private fun handleError(loadState: CombinedLoadStates?, error: String? = null) {
        val errorState = loadState?.source?.append as? LoadState.Error
            ?: loadState?.source?.prepend as? LoadState.Error
        errorState?.let {
            GenericModalDialogCashplus.Builder()
                .title(getString(R.string.something_wrong))
                .image(R.drawable.illustration_login_error)
                .description(if (error.isNullOrEmpty()) it.error.toString() else error)
                .buttonNegative(
                    NegativeButtonAction(
                        btnLabel = getString(R.string.close),
                        backgroundButton = R.drawable.bg_transparent_border_primary_rounded_16,
                        buttonTextColor = Color.parseColor("#0A57FF"),
                        setClickOnDismiss = true
                    )
                ).show(supportFragmentManager)
        }
    }

    @SuppressLint("Range", "Recycle")
    private fun generateXlsFile(data: List<MutationDepositResponse>?) {
        if (data.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.save_recap_required), Toast.LENGTH_SHORT).show()
            return
        }
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Deposit")
        sheet.setTitleXlsxFile(titles = titlesRecapDeposit)
        sheet.setDataXlsxFile(data = data.toNestedListOfString(this), cellLongest = mutableListOf(7, 17, 5, 4, 11, 11, 6, 9, 8))

        val fileName = "rekap_deposit_${dateValue?.label?.replace(" ", "")}.xlsx"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val contentValues = ContentValues()
                contentValues.apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + File.separator + "Cashplus" + File.separator + "Rekap" + File.separator + "Deposit")
                }
                val uri = contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), contentValues)

                if (uri != null) {
                    val outputStream = contentResolver.openOutputStream(uri)
                    workbook.write(outputStream)
                    outputStream?.close()
                    Toast.makeText(this, getString(R.string.save_recap_successfully, "Deposit", dateValue?.label), Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.save_recap_failed, "Deposit", dateValue?.label), Toast.LENGTH_SHORT).show()
            }
            return
        }

        val excelFilePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}" + File.separator + "Cashplus" + File.separator + "Rekap" + File.separator + "Deposit"
        val directory = File(excelFilePath)
        try {
            if (!directory.exists()) directory.mkdirs()
            val file = File(directory, fileName)

            val out = FileOutputStream(file)
            workbook.write(out)
            out.close()
            Toast.makeText(this, getString(R.string.save_recap_successfully, "Deposit", dateValue?.label), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.save_recap_failed, "Deposit", dateValue?.label), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSubmitEvent(date: DateObjectClass) {
        if (this.dateValue?.dateStart == date.dateStart && this.dateValue?.dateEnd == date.dateEnd) {
            Toast.makeText(this, getString(R.string.filter_no_change), Toast.LENGTH_SHORT).show()
            return
        }
        dateValue = DateObjectClass(dateStart = date.dateStart, dateEnd = date.dateEnd, label = date.label)
        onSubmitState = true
        viewModel.getSummaryMutation(dateStart = dateValue?.dateStart.toString(), dateEnd = dateValue?.dateEnd.toString())
    }
}