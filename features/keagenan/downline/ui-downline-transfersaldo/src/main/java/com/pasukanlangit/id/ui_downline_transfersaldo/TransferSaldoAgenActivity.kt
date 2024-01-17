package com.pasukanlangit.id.ui_downline_transfersaldo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.*
import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.utils.PagingDataType
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.ui_downline_transfersaldo.databinding.ActivityTransferSaldoAgenBinding
import com.pasukanlangit.id.ui_downline_transfersaldo.model.ListDownlinePhone
import com.pasukanlangit.id.ui_downline_transfersaldo.paging.DownlineLoadStateAdapter
import com.pasukanlangit.id.ui_downline_transfersaldo.paging.DownlinePagingAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class TransferSaldoAgenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransferSaldoAgenBinding
    private val viewModel: TransferSaldoAgenViewModel by viewModel()
    private val repository: DownLineRepository by inject()

    private lateinit var mDownLineAdapter: DownlinePagingAdapter
    private lateinit var adapterNominalAdapter: NominalAdapter
    private var destination: ListDownlinePhone? = null

    private val current = Date()
    private val formatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private val formatted = formatter.format(current)

    private var btnShowAll = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferSaldoAgenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRvNominal()
        collectState(type = PagingDataType.NONE.value, value = "")
        with(binding){
            appbar.setOnBackPressed { finish() }
            mDownLineAdapter = DownlinePagingAdapter {
                destination = ListDownlinePhone(it.img_url, it.name, it.phoneActive)
                rvDestDownLine.isVisible = false
                edtInputDest.setText(it.phoneActive)
                edtInputDest.setCompoundDrawablesWithIntrinsicBounds(
                    AppCompatResources.getDrawable(this@TransferSaldoAgenActivity, R.drawable.icon_contact_input),
                    null,
                    AppCompatResources.getDrawable(this@TransferSaldoAgenActivity, R.drawable.icon_expand_black),
                    null
                )
            }
            edtInputNominal.filters = arrayOf(InputFilter.LengthFilter(10))
            edtInputNominal.setRupiahFormat()
            edtInputDest.filters = arrayOf(InputFilter.LengthFilter(16))
            edtInputDest.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edtInputDest.right - edtInputDest.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                        btnShowAll = !btnShowAll
                        rvDestDownLine.isVisible = btnShowAll
                        if (btnShowAll) {
                            edtInputDest.setCompoundDrawablesWithIntrinsicBounds(
                                AppCompatResources.getDrawable(this@TransferSaldoAgenActivity, R.drawable.icon_contact_input),
                                null,
                                AppCompatResources.getDrawable(this@TransferSaldoAgenActivity, R.drawable.icon_arrow_up_primary),
                                null
                            )
                        } else {
                            edtInputDest.setCompoundDrawablesWithIntrinsicBounds(
                                AppCompatResources.getDrawable(this@TransferSaldoAgenActivity, R.drawable.icon_contact_input),
                                null,
                                AppCompatResources.getDrawable(this@TransferSaldoAgenActivity, R.drawable.icon_expand_black),
                                null
                            )
                        }
                        return@setOnTouchListener true
                    }
                }
                false
            }
            edtInputDest.setOnFocusChangeListener { _, hasFocus ->
                rvDestDownLine.isVisible = hasFocus
                btnShowAll = hasFocus
                if (hasFocus) {
                    edtInputDest.setCompoundDrawablesWithIntrinsicBounds(
                        AppCompatResources.getDrawable(this@TransferSaldoAgenActivity, R.drawable.icon_contact_input),
                        null,
                        AppCompatResources.getDrawable(this@TransferSaldoAgenActivity, R.drawable.icon_arrow_up_primary),
                        null
                    )
                } else {
                    edtInputDest.setCompoundDrawablesWithIntrinsicBounds(
                        AppCompatResources.getDrawable(this@TransferSaldoAgenActivity, R.drawable.icon_contact_input),
                        null,
                        AppCompatResources.getDrawable(this@TransferSaldoAgenActivity, R.drawable.icon_expand_black),
                        null
                    )
                }
            }
            edtInputDest.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString()
                    if (value.length > 4) {
                        btnShowAll = true
                        val digitOnly = value.filter { num -> num.isDigit() }
                        if (digitOnly.isNotEmpty() && digitOnly.length >= 9) {
                            val phoneDigit = if (digitOnly.substring(0, 1) == "0")
                                digitOnly.replaceRange(0, 1, "62")
                            else digitOnly
                            collectState(type = PagingDataType.SEARCH_BY_PHONE.value, value = phoneDigit)
                        } else {
                            collectState(type = PagingDataType.SEARCH_BY_NAME.value, value = value)
                        }
                    } else {
                        collectState(type = PagingDataType.NONE.value, value = "")
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            with(rvDestDownLine) {
                val headerAdapter = DownlineLoadStateAdapter()
                val footAdapter = DownlineLoadStateAdapter()
                val concatAdapter = mDownLineAdapter.withLoadStateHeaderAndFooter(
                    header = headerAdapter, footer = footAdapter
                )
                layoutManager = LinearLayoutManager(this@TransferSaldoAgenActivity)
                adapter = concatAdapter
                addItemDecoration(CashplusItemDecoration(24))
            }
            buttonSend.setUpToProgressButton(this@TransferSaldoAgenActivity)
            buttonSend.setOnClickListener {
                buttonSend.showLoading()
                processTransferSaldo()
            }
        }
        loadStateListener()
    }

    private fun processTransferSaldo() {
        val dest = binding.edtInputDest.text.toString().trim()
        val nominal = binding.edtInputNominal.text.toString().replace(",","").trim()
        binding.buttonSend.hideProgress(getString(R.string.send))

        if (nominal.length > 10) {
            GenericModalDialogCashplus.Builder()
                .title(getString(R.string.something_wrong))
                .image(R.raw.cashplus_gagal, true)
                .description(getString(R.string.top_up_balance_error_out_of_capacity))
                .buttonNegative(
                    NegativeButtonAction(
                        btnLabel = getString(R.string.close),
                        setClickOnDismiss = true
                    )
                ).show(supportFragmentManager)
            return
        }
        if(dest.isEmpty() || nominal.isEmpty()) {
            Toast.makeText(this, getString(R.string.input_name_or_number_required), Toast.LENGTH_SHORT).show()
            return
        }
        if (dest != destination?.phoneNumber) {
            Toast.makeText(this, getString(R.string.destination_not_accordance), Toast.LENGTH_SHORT).show()
            return
        }
        if (nominal.toInt() > 10000000) {
            Toast.makeText(this, getString(R.string.transfer_balance_error_maximal, getNumberRupiah(10000000)), Toast.LENGTH_SHORT).show()
            return
        }

        startActivity(
            Intent(this, DetailTransferAgentActivity::class.java).apply {
                putExtra(DetailTransferAgentActivity.DATA_DOWNLINE_CHOOSED_KEY, destination)
                putExtra(DetailTransferAgentActivity.BALANCE_TRANSFER_KEY, nominal)
            }
        )
    }

    private fun collectState(type: String, value: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE DOWNLINE
                launch {
                    viewModel.getDownLine(
                        repository = repository, dateStart = formatted, dateEnd = formatted,
                        type = type, value = value
                    ).collectLatest {
                        if (::mDownLineAdapter.isInitialized) mDownLineAdapter.submitData(it)
                    }
                }
            }
        }
    }

    private fun loadStateListener() {
        mDownLineAdapter.addLoadStateListener { loadState ->
            with(binding) {
                when(loadState.source.refresh) {
                    is LoadState.Loading -> progressLoading.isVisible = true
                    is LoadState.NotLoading -> progressLoading.isVisible = false
                    is LoadState.Error -> {
                        progressLoading.isVisible = false
                        handleError(loadState)
                    }
                }
            }
        }
    }

    private fun handleError(loadState: CombinedLoadStates) {
        val errorState = loadState.source.append as? LoadState.Error
            ?: loadState.source.prepend as? LoadState.Error
        errorState?.let {
            Toast.makeText(this, "${it.error}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpRvNominal() {
        adapterNominalAdapter = NominalAdapter { nominal ->
            binding.edtInputNominal.setText(nominal)
        }
        with(binding.rvNominal) {
            layoutManager = GridLayoutManager(this@TransferSaldoAgenActivity, 3)
            adapter = adapterNominalAdapter
        }
    }

    private fun EditText.setRupiahFormat() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable) {
                try {
                    this@setRupiahFormat.removeTextChangedListener(this)
                    val value = this@setRupiahFormat.text.toString()
                    if (value != "") {
                        if (value.startsWith("0")) {
                            this@setRupiahFormat.setText("")
                        }
                        val str: String = this@setRupiahFormat.text.toString().replace(",", "")
                        if (value.isNotEmpty())  this@setRupiahFormat.setText(getDecimalFormattedString(str))
                        this@setRupiahFormat.setSelection(this@setRupiahFormat.text.toString().length)
                        adapterNominalAdapter.setOnItemClickSelected(str)
                    }
                    this@setRupiahFormat.addTextChangedListener(this)
                    return
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    this@setRupiahFormat.addTextChangedListener(this)
                }
            }
        })
    }
}