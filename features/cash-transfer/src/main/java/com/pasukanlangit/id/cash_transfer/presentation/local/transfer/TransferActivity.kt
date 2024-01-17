package com.pasukanlangit.id.cash_transfer.presentation.local.transfer

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.ActivityTransferBinding
import com.pasukanlangit.id.cash_transfer.domain.model.LocalBankSavedResponse
import com.pasukanlangit.id.core.presentation.EnterPinDialogFragment
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.getDecimalFormattedString
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransferActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransferBinding
    private val viewModel: TransferViewModel by viewModel()

    private lateinit var nominalTransferAdapter: NominalTransferAdapter
    private val nominalTransfer = listOf("50,000", "100,000", "200,000", "500,000", "1,000,000", "2,000,000")

    private var bankSaved: LocalBankSavedResponse? = null
    private var stateNominal = false
    private var destination: String? = null
    private var randomInt: String? = null
    private var isPay = false

    val enterPIN = EnterPinDialogFragment(
        onEnterPinCompleted = { pin ->
            isPay = true
            viewModel.onEventState(
                TransferStateEvent.BankTransferTransaction(
                    codeProduct = "PAYBANK", dest = destination.toString(), pin = pin, reqId = randomInt
                )
            )
        }
    )
    private lateinit var transferNews: String
    private lateinit var detailTransfer: DetailTransferBottomSheetFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bankSaved = intent.parcelable(BANK_SAVED_KEY)
        with(binding) {
            appBar.setOnBackPressed { finish() }
            if (bankSaved != null) {
                Glide.with(root.context)
                    .load(bankSaved?.bank_img)
                    .into(imgBank)
                tvBankName.text = bankSaved?.bank_acc_name
                tvBankNum.text = bankSaved?.bank_acc_num
            }
            edtNominal.filters = arrayOf(InputFilter.LengthFilter(10))
            edtNominal.onTextChangedListenerFilter()
            btnNext.setUpToProgressButton(this@TransferActivity)
            btnNext.setOnClickListener { onNextClicked() }
        }
        setupNominalList()
        collectBankTransferTransaction()
    }

    private fun setupNominalList() {
        nominalTransferAdapter = NominalTransferAdapter()
        with(binding.rvNominal) {
            layoutManager = GridLayoutManager(this@TransferActivity, 3)
            adapter = nominalTransferAdapter
        }
        nominalTransferAdapter.setNominalList(nominalTransfer)
        nominalTransferAdapter.setOnItemClickListener(object:
            NominalTransferAdapter.OnItemClickListener {
            override fun onItemClicked(item: String) {
                binding.edtNominal.setText(item)
            }
        })
    }

    private fun EditText.onTextChangedListenerFilter() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val input = s.toString()
                if (input.isEmpty()) {
                    binding.inputNominalTransfer.error = getString(R.string.input_format_required_first, getString(R.string.nominal_transfer))
                    stateNominal = false
                    return
                }
                stateNominal = true
                binding.inputNominalTransfer.error = null
            }
            override fun afterTextChanged(s: Editable) {
                binding.btnNext.isEnabled = stateNominal
                val editText = this@onTextChangedListenerFilter
                try {
                    editText.removeTextChangedListener(this)
                    val value = editText.text.toString()
                    if (value != "") {
                        if (value.startsWith(".")) {
                            editText.setText("0.")
                        }
                        if (value.startsWith("0") && !value.startsWith("0.")) {
                            editText.setText("")
                        }
                        val str: String = editText.text.toString().replace(",", "")
                        if (value.isNotEmpty()) editText.setText(getDecimalFormattedString(str))
                        editText.setSelection(editText.text.toString().length)
                        nominalTransferAdapter.setOnItemClickSelected(getDecimalFormattedString(str))
                    }
                    editText.addTextChangedListener(this)
                    return
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    editText.addTextChangedListener(this)
                }
            }
        })
    }

    private fun onNextClicked() {
        with(binding) {
            val nominal = edtNominal.text.toString().trim().replace(",", "")
            transferNews = edtDesc.text.toString().trim()

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
            if (nominal.toInt() > 20000000) {
                binding.edtNominal.error = getString(R.string.transfer_balance_error_maximal, getNumberRupiah(20000000))
                return
            }

            randomInt = (1..100).random().toString()
            destination = "${bankSaved?.bank_code}-${bankSaved?.bank_acc_num}-$nominal-$transferNews"

            viewModel.onEventState(
                TransferStateEvent.BankTransferTransaction(
                    codeProduct = "TAGBANK", dest = destination.toString(), "", randomInt
                )
            )
        }
    }

    private fun collectBankTransferTransaction() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.transferLoading.collectLatest { state ->
                        if (isPay) {
                            enterPIN.setLoading(state)
                            return@collectLatest
                        }
                        if (state) {
                            binding.btnNext.showLoading()
                            return@collectLatest
                        }
                        binding.btnNext.hideProgress(getString(R.string.next))
                    }
                }
                launch {
                    viewModel.transfer.collectLatest { response ->
                        if (response != null) {
                            if (isPay) {
                                detailTransfer.dismiss()
                                enterPIN.dismiss()
                                GenericModalDialogCashplus.Builder()
                                    .title(getString(R.string.transfer_success))
                                    .image(R.drawable.illustration_success2)
                                    .description(getString(R.string.transfer_success_desc))
                                    .setIsCanClose(isCanClose = false)
                                    .buttonNegative(
                                        NegativeButtonAction(
                                            btnLabel = getString(R.string.close),
                                            backgroundButton = R.drawable.bg_emerald600_rounded_12,
                                            buttonTextColor = Color.parseColor("#F8FAFC"),
                                            onBtnClicked = { this@TransferActivity.finish() }
                                        )
                                    ).show(supportFragmentManager)
                                return@collectLatest
                            }
                            detailTransfer = DetailTransferBottomSheetFragment.newInstance(
                                imgUrl = bankSaved?.bank_img, destination = destination,
                                fee = response.fee, info = response.transferBillData
                            )
                            detailTransfer.show(supportFragmentManager, detailTransfer.tag)
                        }
                    }
                }
                launch {
                    viewModel.transferError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (isPay) enterPIN.clearTextOnDialog()
                            val toast = Toast.makeText(
                                this@TransferActivity,
                                if (info.contains("pin", ignoreCase = true)) getString(R.string.state_wrong_pin)
                                else if (info.contains(transferNews, ignoreCase = true)) getString(R.string.something_wrong_transfer_news)
                                else info,
                                Toast.LENGTH_SHORT
                            )
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onEventState(TransferStateEvent.RemoveTransferErrorMessage)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val BANK_SAVED_KEY = "bank_saved_key"
    }
}