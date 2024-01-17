package com.pasukanlangit.id.cash_transfer.presentation.local

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.ActivityListRekeningBinding
import com.pasukanlangit.id.cash_transfer.domain.model.LocalBankSavedResponse
import com.pasukanlangit.id.cash_transfer.presentation.local.bank_add.AddBankSuccessFragment
import com.pasukanlangit.id.cash_transfer.presentation.local.bank_add.BankListBottomSheetFragment
import com.pasukanlangit.id.cash_transfer.presentation.local.transfer.TransferActivity
import com.pasukanlangit.id.cash_transfer.presentation.local.utils.AccountBank
import com.pasukanlangit.id.core.presentation.EnterPinDialogFragment
import com.pasukanlangit.id.core.presentation.components.*
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.InputUtil.inputCharNumberFilter
import com.pasukanlangit.id.core.utils.KeyboardUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListRekeningActivity : AppCompatActivity(), AddBankSuccessFragment.SetOnButtonsClickListener {

    private lateinit var binding: ActivityListRekeningBinding
    private val viewModel: LocalBankViewModel by viewModel()

    private lateinit var bankSavedAdapter: BankSavedAdapter
    private lateinit var accountBank: AccountBank
    private lateinit var enterPIN: EnterPinDialogFragment
    private lateinit var addBankSuccess: AddBankSuccessFragment
    private lateinit var bankSaved: LocalBankSavedResponse

    private val bankSavedList = arrayListOf<LocalBankSavedResponse>()
    private var isShownClose = false
    private var isContinueTransfer = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListRekeningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        with(binding) {
            appBar.setOnBackPressed { finish() }
            edtSearchBankSaved.filters = arrayOf(inputCharNumberFilter)
            edtSearchBankSaved.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (::bankSavedAdapter.isInitialized) bankSavedAdapter.filter.filter(s.toString())
                    if (!s.isNullOrEmpty()) {
                        isShownClose = true
                        edtSearchBankSaved.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_close, 0)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            edtSearchBankSaved.setOnTouchListener { _, event ->
                val drawableEnd = 2
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edtSearchBankSaved.right - edtSearchBankSaved.compoundDrawables[drawableEnd].bounds.width())) {
                        if (isShownClose) {
                            with(binding) {
                                edtSearchBankSaved.text?.clear()
                                edtSearchBankSaved.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_search, 0)
                                KeyboardUtil.closeKeyboardDialog(this@ListRekeningActivity, edtSearchBankSaved)
                                edtSearchBankSaved.clearFocus()
                                isShownClose = !isShownClose
                                return@setOnTouchListener true
                            }
                        }
                    }
                }
                false
            }
            btnCreateNow.setOnClickListener {
                BankListBottomSheetFragment.newInstance().show(supportFragmentManager, BankListBottomSheetFragment::class.java.name)
            }
            btnAddList.setOnClickListener {
                BankListBottomSheetFragment.newInstance(
                    savedBank = bankSavedList
                ).show(supportFragmentManager, BankListBottomSheetFragment::class.java.name)
            }
        }

        collectBankSaved()
    }

    private fun setupRecyclerView() {
        bankSavedAdapter = BankSavedAdapter { bank, isDelete ->
            if (isDelete) {
                GenericConfirmDialogFragment.Builder()
                    .title(getString(R.string.warning))
                    .description(getString(R.string.confirm_delete_bank_acc))
                    .buttonPositive(
                        PositiveButton(
                            backgroundButton = R.drawable.bg_red600_rounded_12,
                            buttonText = getString(R.string.yes),
                            buttonTextColor = Color.parseColor("#FFFFFF"),
                            onBtnAction = {
                                viewModel.onTriggerEvent(
                                    LocalBankEvent.DeleteBankAcc(
                                        bankCode = bank.bank_code, bankAccNum = bank.bank_acc_num
                                    )
                                )
                            }
                        )
                    )
                    .buttonNegative(
                        NegativeButton(
                            backgroundButton = R.drawable.bg_red50_rounded_12,
                            buttonText = getString(R.string.cancel),
                            buttonTextColor = Color.parseColor("#FF6150"),
                            actionDismiss = true
                        )
                    ).show(supportFragmentManager, "Confirm Delete Bank Acc")
                return@BankSavedAdapter
            }
            startActivity(
                Intent(this, TransferActivity::class.java).apply {
                    putExtra(TransferActivity.BANK_SAVED_KEY, bank)
                }
            )
        }
        with(binding.rvBankSaved) {
            layoutManager = LinearLayoutManager(this@ListRekeningActivity)
            adapter = bankSavedAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    private fun collectBankSaved() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE BankSaved
                launch {
                    viewModel.stateLoading.collectLatest { state ->
                        with(binding) {
                            progressBar.isVisible = state
                            if (state) {
                                wrapperBankSavedEmpty.isVisible = false
                                wrapperListBankSaved.isVisible = false
                            }
                        }
                    }
                }
                launch {
                    viewModel.banksSaved.collectLatest { it?.let { banks -> bindBanksSaved(banks) } }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@ListRekeningActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(LocalBankEvent.RemoveBankSavedError)
                        }
                    }
                }

                // State Delete Bank
                launch {
                    viewModel.deleteBankLoading.collectLatest { state ->
                        binding.progressBar.isVisible = state
                    }
                }
                launch {
                    viewModel.deleteBankAcc.collectLatest { response ->
                        if (response != null) viewModel.onTriggerEvent(LocalBankEvent.GetBankSaved)
                    }
                }
                launch {
                    viewModel.deleteBankError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@ListRekeningActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(LocalBankEvent.RemoveDeleteBankError)
                        }
                    }
                }

                launch {
                    viewModel.account.collectLatest { response ->
                        if (response != null) {
                            accountBank = response
                            enterPIN = EnterPinDialogFragment(
                                onEnterPinCompleted = { pin ->
                                    viewModel.onTriggerEvent(
                                        LocalBankEvent.SendTAGBank(
                                            codeProduct = response.productCode, dest = response.destination,
                                            pin = pin
                                        )
                                    )
                                }
                            )
                            enterPIN.show(supportFragmentManager, enterPIN.tag)
                        }
                    }
                }

                // State TAG Bank
                launch {
                    viewModel.tagBankLoading.collectLatest { state ->
                        if (::enterPIN.isInitialized) enterPIN.setLoading(state)
                    }
                }
                launch {
                    viewModel.tagBank.collectLatest { response ->
                        if (response != null) {
                            enterPIN.dismiss()
                            val destSplit = accountBank.destination.split("-")
                            bankSaved = LocalBankSavedResponse(
                                bank_code = destSplit.first(), bank_name = response.transferBillData.bankName ?: "",
                                bank_acc_num = destSplit[1], bank_acc_name = response.transferBillData.name ?: "",
                                bank_img = accountBank.imgUrl
                            )
                            addBankSuccess = AddBankSuccessFragment.newInstance(
                                destination = accountBank.destination, infoName = response.transferBillData.name,
                                imgUrl = accountBank.imgUrl, event = this@ListRekeningActivity
                            )
                            addBankSuccess.show(supportFragmentManager, addBankSuccess.tag)
                            viewModel.onTriggerEvent(LocalBankEvent.ResetAccountBank)
                        }
                    }
                }
                launch {
                    viewModel.tagBankError.collectLatest { message ->
                        message.peek()?.let { info ->
                            enterPIN.clearTextOnDialog()
                            val toast = Toast.makeText(this@ListRekeningActivity, getString(R.string.state_wrong_pin), Toast.LENGTH_SHORT)
                            if (info.contains("pin", ignoreCase = true)) toast.show()
                            else handleErrorTAGBank(info)
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(LocalBankEvent.ResetAccountBank)
                            viewModel.onTriggerEvent(LocalBankEvent.RemoveTAGBankError)
                        }
                    }
                }

                // State Saving Bank
                launch {
                    viewModel.savingBankLoading.collectLatest {
                        if (::addBankSuccess.isInitialized) addBankSuccess.setLoading(it)
                    }
                }
                launch {
                    viewModel.savingBankAcc.collectLatest { response ->
                        if (response != null) {
                            addBankSuccess.dismiss()
                            if (isContinueTransfer) {
                                startActivity(
                                    Intent(this@ListRekeningActivity, TransferActivity::class.java).apply {
                                        putExtra(TransferActivity.BANK_SAVED_KEY, bankSaved)
                                    }
                                )
                                return@collectLatest
                            }
                            viewModel.onTriggerEvent(LocalBankEvent.GetBankSaved)
                        }
                    }
                }
                launch {
                    viewModel.savingBankError.collectLatest { message ->
                        message.peek()?.let { info ->
                            addBankSuccess.dismiss()
                            val toast = Toast.makeText(this@ListRekeningActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(LocalBankEvent.RemoveSavingBankError)
                        }
                    }
                }
            }
        }
    }

    private fun bindBanksSaved(banks: List<LocalBankSavedResponse>) {
        with(binding) {
            if (banks.isEmpty()) {
                wrapperBankSavedEmpty.isVisible = true
                wrapperListBankSaved.isVisible = false
            } else {
                bankSavedList.clear()
                bankSavedList.addAll(banks)
                wrapperBankSavedEmpty.isVisible = false
                wrapperListBankSaved.isVisible = true
                bankSavedAdapter.setBankSavedList(banks)
                edtSearchBankSaved.text?.clear()
            }
        }
    }

    private fun handleErrorTAGBank(message: String) {
        enterPIN.dismiss()
        GenericModalDialogCashplus.Builder()
            .title(getString(R.string.something_wrong))
            .image(R.drawable.illustration_empty)
            .description(
                if (message.contains("format salah", ignoreCase = true))
                    getString(R.string.any_not_found, getString(R.string.bank_account_number))
                else if (message.contains("tidak tersedia", ignoreCase = true))
                    getString(R.string.product_is_unavailable_contact_cs_please)
                else message
            )
            .buttonNegative(
                NegativeButtonAction(
                    btnLabel = getString(R.string.close),
                    backgroundButton = R.drawable.bg_transparent_border_primary_rounded_16,
                    buttonTextColor = Color.parseColor("#0A57FF"),
                    setClickOnDismiss = true
                )
            )
            .show(supportFragmentManager, "handle_error")
    }

    override fun onSavingBank(destination: List<String>?, infoName: String?) {
        savingBank(destination?.first() ?: "", destination?.get(1) ?: "", infoName ?: "")
    }

    override fun onContinueTransfer(destination: List<String>?, infoName: String?) {
        isContinueTransfer = true
        savingBank(destination?.first() ?: "", destination?.get(1) ?: "", infoName ?: "")
    }

    private fun savingBank(bankCode: String, accNum: String, accName: String) {
        viewModel.onTriggerEvent(
            LocalBankEvent.SavingBankAcc(bankCode = bankCode, bankAccNum = accNum, bankAccName = accName)
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(LocalBankEvent.GetBankSaved)
    }
}