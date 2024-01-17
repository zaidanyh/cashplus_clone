package com.pasukanlangit.id.cash_transfer.presentation.global.beneficiary

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.ActivityBeneficiaryAccountBinding
import com.pasukanlangit.id.cash_transfer.domain.model.*
import com.pasukanlangit.id.cash_transfer.presentation.global.InputNominalFragment
import com.pasukanlangit.id.cash_transfer.presentation.global.detail.DetailGlobalTransferActivity
import com.pasukanlangit.id.core.presentation.components.*
import com.pasukanlangit.id.core.utils.*
import com.pasukanlangit.id.core.utils.InputUtil.inputAllTypeWithSomeSpecialChars
import com.pasukanlangit.id.core.utils.InputUtil.inputAlphabetFilter
import com.pasukanlangit.id.core.utils.InputUtil.inputNumericFilter
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class BeneficiaryAccountActivity : AppCompatActivity(), InputNominalFragment.OnButtonNextClickListener {

    private lateinit var binding: ActivityBeneficiaryAccountBinding
    private val viewModel: BeneficiaryAccountViewModel by viewModel()

    private var country: FetchCountryResponse? = null
    private var beneficiaryAcc: GlobalBankSavedResponse? = null
    private var isChangeBeneficiary = false

    private var banksSaved: List<GlobalBankSavedResponse> = emptyList()
    private var globalBanks: List<FetchBankByCountryResponse> = emptyList()
    private var nationality: List<FetchNationResponse> = emptyList()
    private var relationships: List<FetchRelationshipsResponse> = emptyList()

    private var stateBankName = false
    private var stateAccNum = false
    private var stateAccName = false
    private var stateCity = false
    private var stateAddress = false
    private var stateNationality = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeneficiaryAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        country = intent.parcelable(COUNTRY_CODE_SELECTED_RESULT_KEY)
        beneficiaryAcc = intent.parcelable(BENEFICIARY_ACCOUNT_VALUE_KEY)
        isChangeBeneficiary = intent.getBooleanExtra(IS_CHANGE_BENEFICIARY_ACCOUNT_KEY, false)
        country?.let {
            viewModel.onTriggerEvent(
                BeneficiaryAccountEvents.GetGlobalBanksNationsAndRelations(countryCode = it.codeCountry)
            )
        }
        with(binding) {
            imgBack.setOnClickListener { finish() }
            if (isChangeBeneficiary) {
                titleGlobalTransfer.text = getString(R.string.change_beneficiary_account)
                btnSave.text = getString(R.string.save_changes)
            }
            edtBankName.filters = arrayOf(InputFilter.LengthFilter(80), inputAllTypeWithSomeSpecialChars)
            edtBankName.setDropDownBackgroundResource(R.drawable.bg_white_border_slate200_rounded_10)
            edtBankName.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= edtBankName.right - edtBankName.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width()
                    ) {
                        edtBankName.showDropDown()
                        return@setOnTouchListener true
                    }
                }
                false
            }
            edtBankName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString()
                    if (value.isEmpty()) {
                        inputBankName.error = getString(R.string.input_format_required_first, getString(R.string.bank_name))
                        stateBankName = false
                        return
                    }
                    stateBankName = true
                    inputBankName.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSave.isEnabled = stateBankName && stateAccNum && stateAccName && stateCity && stateAddress && stateNationality
                }
            })
            edtBeneficiaryAccNum.filters = arrayOf(InputFilter.LengthFilter(32), inputNumericFilter)
            edtBeneficiaryAccNum.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString()
                    if (value.isEmpty()) {
                        inputBeneficiaryAccNum.error = getString(R.string.input_format_required_first, getString(R.string.destination_rekening))
                        stateAccNum = false
                        return
                    }
                    stateAccNum = true
                    inputBeneficiaryAccNum.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSave.isEnabled = stateBankName && stateAccNum && stateAccName && stateCity && stateAddress && stateNationality
                }

            })
            edtBeneficiaryName.filters = arrayOf(InputFilter.LengthFilter(48), inputAlphabetFilter)
            edtBeneficiaryName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString()
                    if (value.isEmpty()) {
                        inputBeneficiaryName.error = getString(R.string.input_format_required_first, getString(R.string.beneficiary_name))
                        stateAccName = false
                        return
                    }
                    stateAccName = true
                    inputBeneficiaryName.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSave.isEnabled = stateBankName && stateAccNum && stateAccName && stateCity && stateAddress && stateNationality
                }
            })
            edtBeneficiaryCity.filters = arrayOf(InputFilter.LengthFilter(20), inputAlphabetFilter)
            edtBeneficiaryCity.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString()
                    if (value.isEmpty()) {
                        inputBeneficiaryCity.error = getString(R.string.input_format_required_first, getString(R.string.beneficiary_city))
                        stateCity = false
                        return
                    }
                    stateCity = true
                    inputBeneficiaryCity.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSave.isEnabled = stateBankName && stateAccNum && stateAccName && stateCity && stateAddress && stateNationality
                }
            })
            edtBeneficiaryAddress.filters = arrayOf(InputFilter.LengthFilter(50))
            edtBeneficiaryAddress.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString()
                    if (value.isEmpty()) {
                        inputBeneficiaryAddress.error = getString(R.string.input_format_required_first, getString(R.string.beneficiary_address))
                        stateAddress = false
                        return
                    }
                    stateAddress = true
                    inputBeneficiaryAddress.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSave.isEnabled = stateBankName && stateAccNum && stateAccName && stateCity && stateAddress && stateNationality
                }
            })
            edtNationality.filters = arrayOf(InputFilter.LengthFilter(32), inputAlphabetFilter)
            edtNationality.setDropDownBackgroundResource(R.drawable.bg_white_border_slate200_rounded_10)
            edtNationality.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= edtNationality.right - edtNationality.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width()
                    ) {
                        edtNationality.showDropDown()
                        return@setOnTouchListener true
                    }
                }
                false
            }
            edtNationality.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString()
                    if (value.isEmpty()) {
                        inputNationality.error = getString(R.string.input_format_required_first, getString(R.string.nationality))
                        stateNationality = false
                        return
                    }
                    stateNationality = true
                    inputNationality.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSave.isEnabled = stateBankName && stateAccNum && stateAccName && stateCity && stateAddress && stateNationality
                }
            })
            btnSave.setUpToProgressButton(this@BeneficiaryAccountActivity)
            btnSave.setOnClickListener {
                val itemBankSelected = edtBankName.text.toString().trim()
                val itemNationSelected = edtNationality.text.toString().trim()
                val itemRelationSelected = spinnerRelationship.selectedItem.toString()

                val bankSelected = globalBanks.singleOrNull { bank ->
                    bank.bankName == itemBankSelected
                }
                val nationSelected = nationality.singleOrNull { nation ->
                    nation.nationName == itemNationSelected
                }
                val relationSelected = relationships.singleOrNull { relation ->
                    relation.desc == itemRelationSelected
                }

                if (bankSelected == null) {
                    showModalError(getString(R.string.any_input_is_not_valid, getString(R.string.bank_name)), "Global bank not valid")
                    return@setOnClickListener
                }
                if (nationSelected == null) {
                    showModalError(getString(R.string.any_input_is_not_valid, getString(R.string.nationality)), "Nationality not valid")
                    return@setOnClickListener
                }
                if (relationSelected == null) {
                    showModalError(getString(R.string.any_not_found, getString(R.string.relationship)), "Relationship not found")
                    return@setOnClickListener
                }

                val accNum = edtBeneficiaryAccNum.text.toString().trim()
                val accName = edtBeneficiaryName.text.toString().trim()
                val address = edtBeneficiaryAddress.text.toString().trim()
                val city = edtBeneficiaryCity.text.toString().trim()
                val findBank = banksSaved.find {
                    it.bankCode == bankSelected.bankCode && it.bankAccNum == accNum
                }

                GenericConfirmDialogFragment.Builder()
                    .title(getString(R.string.confirm))
                    .description(getString(R.string.desc_check_detail))
                    .buttonPositive(
                        PositiveButton(
                            backgroundButton = R.drawable.bg_primary_rounded_16,
                            buttonText = getString(R.string.yes),
                            buttonTextColor = Color.parseColor("#F8FAFC"),
                            onBtnAction = {
                                country?.let {
                                    beneficiaryAcc = GlobalBankSavedResponse(
                                        bankCode = bankSelected.bankCode, countryCode = it.codeCountry, relationship = relationSelected.code,
                                        nationCode = nationSelected.nationCode, address = address, city = city, currency = it.currency,
                                        currencyDsc = it.currencyDsc, countryName = it.nameCountry, bankName = bankSelected.bankName,
                                        bankAccNum = accNum, bankAccName = accName, imgUrl = bankSelected.imgUrl?.ifEmpty { "" },
                                        countryImgUrl = it.imgUrl
                                    )
                                }
                                if (isChangeBeneficiary) {
                                    val intent = Intent()
                                    intent.putExtra(BENEFICIARY_ACCOUNT_VALUE_KEY, beneficiaryAcc)
                                    setResult(RESULT_OK, intent)
                                    finish()
                                    return@PositiveButton
                                }

                                if (findBank != null) {
                                    Toast.makeText(this@BeneficiaryAccountActivity, getString(R.string.already_saved), Toast.LENGTH_SHORT).show()
                                    return@PositiveButton
                                }
                                viewModel.onTriggerEvent(
                                    BeneficiaryAccountEvents.SavingGlobalBank(
                                        bankCode = bankSelected.bankCode, bankAccNum = accNum,
                                        bankAccName = accName, relationCode = relationSelected.code,
                                        nationCode = nationSelected.nationCode, addressStreet = address,
                                        addressCity = city
                                    )
                                )
                            }
                        )
                    )
                    .buttonNegative(
                        NegativeButton(
                            backgroundButton = R.drawable.bg_blue50_rounded_16,
                            buttonText = getString(R.string.cancel),
                            buttonTextColor =  Color.parseColor("#0A57FF"),
                            actionDismiss = true
                        )
                    )
                    .show(supportFragmentManager, "Make sure data is correct")
            }

            if (beneficiaryAcc != null && isChangeBeneficiary) {
                edtBeneficiaryAccNum.setText(beneficiaryAcc?.bankAccNum)
                edtBeneficiaryName.setText(beneficiaryAcc?.bankAccName)
                edtBeneficiaryAddress.setText(beneficiaryAcc?.address)
                edtBeneficiaryCity.setText(beneficiaryAcc?.city)
            }
        }
        collectDataGlobalBank()
    }

    private fun showModalError(message: String, tag: String) {
        GenericModalDialogCashplus.Builder()
            .title(getString(R.string.something_wrong))
            .image(R.drawable.illustration_empty)
            .description(message)
            .buttonNegative(
                NegativeButtonAction(
                    btnLabel = getString(R.string.close),
                    backgroundButton = R.drawable.bg_transparent_border_primary_rounded_16,
                    buttonTextColor = Color.parseColor("#0A57FF"),
                    setClickOnDismiss = true
                )
            )
            .show(supportFragmentManager, tag)
    }

    private fun collectDataGlobalBank() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateLoading.collectLatest {
                        with(binding) {
                            imgBack.isEnabled = !it
                            progressBar.isVisible = it
                        }
                    }
                }
                launch {
                    viewModel.globalBanksSaved.collectLatest { response ->
                        if (response != null) banksSaved = response
                    }
                }
                launch {
                    viewModel.globalBanks.collectLatest { response ->
                        val banks = mutableListOf(Constants.NOT_AVAILABLE)
                        if (response != null) {
                            globalBanks = response

                            banks.clear()
                            globalBanks.forEach { bank -> banks.add(bank.bankName) }

                            if (beneficiaryAcc != null) {
                                val bankFounded = globalBanks.singleOrNull { item -> item.bankCode == beneficiaryAcc?.bankCode }
                                if (bankFounded != null) binding.edtBankName.setText(bankFounded.bankName)
                            }
                        }
                        val banksAdapter = ArrayAdapter(this@BeneficiaryAccountActivity, R.layout.item_spinner_small, banks)
                        binding.edtBankName.setAdapter(banksAdapter)
                    }
                }
                launch {
                    viewModel.nations.collectLatest { response ->
                        val nationalities = mutableListOf(Constants.NOT_AVAILABLE)
                        if (response != null) {
                            nationality = response

                            nationalities.clear()
                            nationality.forEach { nation -> nationalities.add(nation.nationName) }

                            if (beneficiaryAcc != null) {
                                val nationFounded = nationality.singleOrNull { item -> item.nationCode == beneficiaryAcc?.nationCode }
                                if (nationFounded != null) binding.edtNationality.setText(nationFounded.nationName)
                            }
                        }
                        val nationAdapter = ArrayAdapter(this@BeneficiaryAccountActivity, R.layout.item_spinner_small, nationalities)
                        binding.edtNationality.setAdapter(nationAdapter)
                    }
                }
                launch {
                    viewModel.relationships.collectLatest { response ->
                        val relations = mutableListOf(getString(R.string.choose_relationship))
                        if (response != null) {
                            relationships = response

                            relationships.forEach { relation -> relations.add(relation.desc) }
                        }
                        val relationAdapter = setDropDownView(this@BeneficiaryAccountActivity, relations)
                        relationAdapter.setDropDownViewResource(R.layout.item_spinner_small)
                        binding.spinnerRelationship.adapter = relationAdapter
                        if (beneficiaryAcc != null) {
                            val relationFounded = relationships.singleOrNull { item -> item.code == beneficiaryAcc?.relationship }
                            if (relationFounded != null) {
                                val index = relationships.indexOf(relationFounded) + 1
                                binding.spinnerRelationship.setSelection(index)
                            }
                        }
                    }
                }

                launch {
                    viewModel.savingGlobalBankLoading.collectLatest {
                        binding.imgBack.isEnabled = !it
                        if (it) {
                            binding.btnSave.isEnabled = false
                            binding.btnSave.showLoading()
                            return@collectLatest
                        }
                        binding.btnSave.isEnabled = stateBankName && stateAccNum && stateAccName && stateCity && stateAddress && stateNationality
                        binding.btnSave.hideProgress(
                            if (isChangeBeneficiary) getString(R.string.save_changes)
                            else getString(R.string.save)
                        )
                    }
                }
                launch {
                    viewModel.savingGlobalBank.collectLatest { response ->
                        if (response != null) {
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.save_successful))
                                .image(R.drawable.illustration_success2)
                                .description(getString(R.string.new_acc_number_saved_successful))
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.transfer_continue),
                                        backgroundButton = R.drawable.bg_primary_rounded_16,
                                        buttonTextColor = Color.parseColor("#F8FAFC"),
                                        onBtnClicked = {
                                            InputNominalFragment.newInstance(value = beneficiaryAcc, event = this@BeneficiaryAccountActivity)
                                                .show(supportFragmentManager, "Input Amount Transfer")
                                        }
                                    )
                                )
                                .buttonNegative(
                                    NegativeButtonAction(
                                        btnLabel = getString(R.string.go_back),
                                        backgroundButton = R.drawable.bg_blue50_rounded_16,
                                        buttonTextColor = Color.parseColor("#0A57FF"),
                                        onBtnClicked = { this@BeneficiaryAccountActivity.finish() }
                                    )
                                ).show(supportFragmentManager, "Saving global bank successful")
                        }
                    }
                }

                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@BeneficiaryAccountActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(BeneficiaryAccountEvents.RemoveHeadMessageError)
                        }
                    }
                }
            }
        }
    }

    override fun onConversionNominalListener(item: GlobalBankSavedResponse?, amount: String) {
        startActivity(
            Intent(this, DetailGlobalTransferActivity::class.java).apply {
                putExtra(DetailGlobalTransferActivity.DESTINATION_ACCOUNT_KEY, item)
                putExtra(DetailGlobalTransferActivity.AMOUNT_TRANSFER_KEY, amount)
            }
        )
        finish()
    }

    companion object {
        const val COUNTRY_CODE_SELECTED_RESULT_KEY = "country_selected_result_key"
        const val BENEFICIARY_ACCOUNT_VALUE_KEY = "beneficiary_account_value_key"
        const val IS_CHANGE_BENEFICIARY_ACCOUNT_KEY = "is_change_beneficiary_account_key"
    }
}