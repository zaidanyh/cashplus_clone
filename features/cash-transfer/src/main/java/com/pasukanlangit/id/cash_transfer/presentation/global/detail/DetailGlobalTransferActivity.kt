package com.pasukanlangit.id.cash_transfer.presentation.global.detail

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.github.razir.progressbutton.hideProgress
import com.makeramen.roundedimageview.RoundedImageView
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.ActivityDetailGlobalTransferBinding
import com.pasukanlangit.id.cash_transfer.domain.model.FetchCountryResponse
import com.pasukanlangit.id.cash_transfer.domain.model.GlobalBankSavedResponse
import com.pasukanlangit.id.cash_transfer.presentation.global.InputNominalFragment
import com.pasukanlangit.id.cash_transfer.presentation.global.beneficiary.BeneficiaryAccountActivity
import com.pasukanlangit.id.core.presentation.EnterPinDialogFragment
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiahWithoutRp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.NumberFormat

class DetailGlobalTransferActivity : AppCompatActivity(), InputNominalFragment.OnButtonNextClickListener, SummaryGlobalTransferFragment.OnButtonSendClickListener {

    private lateinit var binding: ActivityDetailGlobalTransferBinding
    private val viewModel: DetailGlobalTransferViewModel by viewModel()

    private lateinit var enterPinDialog: EnterPinDialogFragment

    private var destAcc: GlobalBankSavedResponse? = null
    private var amount: String? = null
    private var uniQNumber: String? = null
    private var quoteId: String = ""
    private var quoteIdForTrans: String = ""
    private var totalTransfer: String = ""
    private var stateLoading = false
    private var stateAvailable = false

    private val changeBeneficiaryAcc = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            destAcc = result?.data?.parcelable(BeneficiaryAccountActivity.BENEFICIARY_ACCOUNT_VALUE_KEY)
            destAcc?.let {
                with(binding) {
                    tvBankName.text = it.bankName
                    tvBeneficiaryAccountNumber.text = it.bankAccNum
                    tvBeneficiaryName.text = it.bankAccName
                    tvBeneficiaryAddress.text = it.address
                    tvBeneficiaryCity.text = it.city
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailGlobalTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        destAcc = intent.parcelable(DESTINATION_ACCOUNT_KEY)
        amount = intent.getStringExtra(AMOUNT_TRANSFER_KEY)
        enterPinDialog = EnterPinDialogFragment(
            onEnterPinCompleted = {
                viewModel.onTriggerEvents(
                    DetailGlobalTransferEvents.TransferTransaction(
                        productCode = "PAYFBANK", dest = quoteIdForTrans, pin = it, reqId = uniQNumber
                    )
                )
            }
        )
        viewModel.onTriggerEvents(
            DetailGlobalTransferEvents.GetRateConversion(currency = destAcc?.currency.toString(), amount = amount.toString())
        )
        with(binding) {
            imgBack.setOnClickListener { finish() }
            tvNominalDest.text = getNumberRupiahWithoutRp(amount?.toIntOrNull())
            destAcc?.let { dest ->
                setImageSource(dest.countryImgUrl.toString(), imgCountryFlag)
                tvCountryName.text = dest.countryName
                tvCurrencyCode.text = getString(R.string.two_format_with_brackets, dest.currency, dest.currencyDsc)

                setImageSource(dest.countryImgUrl.toString(), imgCountryDest)
                tvCurrencyDest.text = dest.currency
                txtExchangeRate.text = getString(R.string.exchange_rate_format, dest.currency)

                tvBankName.text = dest.bankName
                tvBeneficiaryAccountNumber.text = dest.bankAccNum
                tvBeneficiaryName.text = dest.bankAccName
                tvBeneficiaryAddress.text = dest.address
                tvBeneficiaryCity.text = dest.city
            }
            btnChangeNominal.setOnClickListener {
                val edtAmount = tvNominalDest.text.toString().toNormalNumber()
                InputNominalFragment.newInstance(
                    value = destAcc, amount = edtAmount, isChange = true,
                    event = this@DetailGlobalTransferActivity
                ).show(supportFragmentManager, "Change Amount Transfer")
            }
            btnChangeReceiver.setOnClickListener {
                val country = FetchCountryResponse(
                    codeCountry = destAcc?.countryCode ?: "", nameCountry = destAcc?.countryName ?: "",
                    currency = destAcc?.currency ?: "", currencyDsc = "", imgUrl = destAcc?.countryImgUrl ?: ""
                )
                changeBeneficiaryAcc.launch(
                    Intent(this@DetailGlobalTransferActivity, BeneficiaryAccountActivity::class.java).apply {
                        putExtra(BeneficiaryAccountActivity.COUNTRY_CODE_SELECTED_RESULT_KEY, country)
                        putExtra(BeneficiaryAccountActivity.BENEFICIARY_ACCOUNT_VALUE_KEY, destAcc)
                        putExtra(BeneficiaryAccountActivity.IS_CHANGE_BENEFICIARY_ACCOUNT_KEY, true)
                    }
                )
            }
            btnSend.setUpToProgressButton(this@DetailGlobalTransferActivity)
            btnSend.setOnClickListener {
                if (totalTransfer.toBigInteger() > (25000000).toBigInteger()) {
                    showModalError("server mitra")
                    return@setOnClickListener
                }
                uniQNumber = (1..100).random().toString()
                viewModel.onTriggerEvents(
                    DetailGlobalTransferEvents.GlobalBankCreateTrans(
                        quoteId = quoteId, relationCode = destAcc?.relationship ?: "", nationCode = destAcc?.nationCode ?: "",
                        bankCode = destAcc?.bankCode ?: "", bankAccNum = destAcc?.bankAccNum ?: "", bankAccName = destAcc?.bankAccName ?: "",
                        countryCode = destAcc?.countryCode ?: "", address = destAcc?.address ?: "", city = destAcc?.city ?: "",
                        reqId = uniQNumber
                    )
                )
            }
        }

        collectData()
    }

    private fun setImageSource(imgUrl: String, source: RoundedImageView) {
        Glide.with(this)
            .load(imgUrl)
            .error(R.drawable.ic_image_default)
            .skipMemoryCache(true)
            .into(source)
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateLoading.collectLatest {
                        stateLoading = !it
                        binding.progressBar.isVisible = it
                        binding.btnSend.isEnabled = stateLoading && stateAvailable
                    }
                }
                launch {
                    viewModel.rateConversion.collectLatest { response ->
                        if (response != null) {
                            stateAvailable = true
                            quoteId = response.quoteId
                            totalTransfer = response.idrAmount

                            val usdIdrRate = response.usdIdrRate.toDouble().plus(response.kursMarkup.toDouble())
                            val exchangeRate = usdIdrRate.div(response.rate)
                            with(binding) {
                                tvNominalIdr.text = getNumberRupiahWithoutRp(response.amountSend.toBigIntegerOrNull())
                                tvExchangeRateNominal.text = exchangeRate.newFormatNumber()
                                tvTransferFee.text = getString(R.string.idr_format, getNumberRupiahWithoutRp(response.adminTotal.toIntOrNull()))
                                tvValueTransfer.text = getString(R.string.idr_format, getNumberRupiahWithoutRp(response.idrAmount.toBigIntegerOrNull()))
                                btnSend.isEnabled = stateLoading && stateAvailable
                            }
                        }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            stateAvailable = false
                            with(binding) {
                                tvExchangeRateNominal.text = ""
                                tvNominalIdr.text = ""
                                tvValueTransfer.text = ""
                                btnSend.isEnabled = stateLoading && stateAvailable
                            }
                            showModalError(info)
                            viewModel.onTriggerEvents(DetailGlobalTransferEvents.RemoveHeadMessageError)
                        }
                    }
                }

                launch {
                    viewModel.createTransLoading.collectLatest {
                        stateLoading = !it
                        binding.btnSend.isEnabled = stateLoading && stateAvailable
                        if (it) {
                            binding.btnSend.showLoading()
                            return@collectLatest
                        }
                        binding.btnSend.hideProgress(getString(R.string.send))
                    }
                }
                launch {
                    viewModel.createTrans.collectLatest { response ->
                        if (response != null) {
                            quoteIdForTrans = response.quoteId
                            SummaryGlobalTransferFragment.newInstance(
                                price = response.tagResponse.price, fee = response.tagResponse.fee,
                                resultTag = response.tagResponse.transferBillData,
                                event = this@DetailGlobalTransferActivity
                            ).show(supportFragmentManager, "Summary Transaction")
                        }
                    }
                }
                launch {
                    viewModel.createTransError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@DetailGlobalTransferActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvents(DetailGlobalTransferEvents.RemoveCreateTransError)
                        }
                    }
                }

                launch {
                    viewModel.transferTransLoading.collectLatest {
                        enterPinDialog.setLoading(it)
                    }
                }
                launch {
                    viewModel.transferTrans.collectLatest { response ->
                        if (response != null) {
                            enterPinDialog.dismiss()
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.transfer_success))
                                .image(R.drawable.illustration_success2)
                                .description(getString(R.string.global_transfer_successfully))
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.ok),
                                        backgroundButton = R.drawable.bg_blue50_rounded_16,
                                        buttonTextColor = Color.parseColor("#0A57FF"),
                                        onBtnClicked = {
                                            finish()
                                        }
                                    )
                                )
                                .show(supportFragmentManager)
                        }
                    }
                }
                launch {
                    viewModel.transferTransError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@DetailGlobalTransferActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvents(DetailGlobalTransferEvents.RemoveTransferTransError)
                        }
                    }
                }
            }
        }
    }

    private fun Double?.newFormatNumber(): String {
        if (this == null) return "IDR 0"
        return "IDR ${NumberFormat.getCurrencyInstance().format(this).replace("$", "")}"
    }

    private fun String.toNormalNumber(): String = this.filter { it.isDigit() }

    private fun showModalError(message: String) {
        GenericModalDialogCashplus.Builder()
            .title(getString(R.string.something_wrong))
            .image(R.drawable.illustration_login_error)
            .description(
                if (
                    message.contains("server mitra", ignoreCase = true) ||
                    message.contains("partner server", ignoreCase = true)
                ) getString(R.string.limit_transfer_error, destAcc?.currency, destAcc?.currency)
                else message
            )
            .buttonPositive(
                if (
                    message.contains("server mitra", ignoreCase = true) ||
                    message.contains("partner server", ignoreCase = true)
                ) PositiveButtonAction(
                    btnLabel = getString(R.string.yes_change),
                    backgroundButton = R.drawable.bg_emerald600_rounded_12,
                    buttonTextColor = Color.parseColor("#F8FAFC"),
                    onBtnClicked = {
                        val edtAmount = binding.tvNominalDest.text.toString().toNormalNumber()
                        InputNominalFragment.newInstance(
                            value = destAcc, amount = edtAmount, isChange = true,
                            event = this@DetailGlobalTransferActivity
                        ).show(supportFragmentManager, "Change Amount Transfer")
                    }
                )
                else null
            )
            .buttonNegative(
                NegativeButtonAction(
                    btnLabel =
                    if (
                        message.contains("server mitra", ignoreCase = true) ||
                        message.contains("partner server", ignoreCase = true)
                    ) getString(R.string.no)
                    else getString(R.string.close),
                    backgroundButton = R.drawable.bg_blue50_rounded_12,
                    buttonTextColor = Color.parseColor("#1570EF"),
                    setClickOnDismiss = true
                )
            )
            .show(supportFragmentManager)
    }

    override fun onConversionNominalListener(item: GlobalBankSavedResponse?, amount: String) {
        this.amount = amount
        binding.tvNominalDest.text = getNumberRupiahWithoutRp(amount.toIntOrNull())
        viewModel.onTriggerEvents(
            DetailGlobalTransferEvents.GetRateConversion(currency = item?.currency.toString(), amount = amount)
        )
    }

    override fun onButtonCloseClicked() {
        viewModel.onTriggerEvents(
            DetailGlobalTransferEvents.GetRateConversion(
                currency = destAcc?.currency.toString(), amount = amount.toString()
            )
        )
    }

    override fun onButtonSendClicked() {
        enterPinDialog.show(supportFragmentManager, "Enter PIN for Transaction")
    }

    companion object {
        const val DESTINATION_ACCOUNT_KEY = "destination_account_key"
        const val AMOUNT_TRANSFER_KEY = "amount_transfer_key"
    }
}