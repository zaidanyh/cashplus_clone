package com.pasukanlangit.id.ui_cashgold_buy.buy

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.KYC_INIT_PATH
import com.pasukanlangit.id.core.MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE
import com.pasukanlangit.id.core.MAIN_ACTIVITY_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.extensions.onDone
import com.pasukanlangit.id.core.extensions.setIDRCurrencyInput
import com.pasukanlangit.id.core.presentation.EnterPinDialogFragment
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.getEnumExtra
import com.pasukanlangit.id.core.utils.putExtra
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.model.LockGoldType
import com.pasukanlangit.id.ui_cashgold_buy.R
import com.pasukanlangit.id.ui_cashgold_buy.buy.bottomsheet.TagGoldFragment
import com.pasukanlangit.id.ui_cashgold_buy.databinding.ActivityBuyCashgoldBinding
import com.pasukanlangit.id.ui_core.components.GenericCashGoldModalDialog
import com.pasukanlangit.id.usecase.TrxType
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
class BuyCashGoldActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuyCashgoldBinding
    private val viewModel: BuyCashGoldViewModel by viewModel()
    private var buyType: LockGoldType = LockGoldType.BY_PRICE
    private var nominalBuy: String? = null

    private val enterPinDialogTag: EnterPinDialogFragment =
        EnterPinDialogFragment(
            onEnterPinCompleted = {
                viewModel.onTriggerEvent(BuyEvent.SendTrxGold(type = TrxType.TAG, pin = it))
            }
        )

    private val enterPinDialogPay: EnterPinDialogFragment =
        EnterPinDialogFragment(
            onEnterPinCompleted = {
                viewModel.onTriggerEvent(BuyEvent.SendTrxGold(type = TrxType.PAY, pin = it))
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyCashgoldBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buyType = intent.getEnumExtra<LockGoldType>() ?: LockGoldType.BY_PRICE
        viewModel.onTriggerEvent(BuyEvent.SetLockGoldType(buyType))

        with(binding){
            btnLock.setUpToProgressButton(this@BuyCashGoldActivity)
            appbar.setIconBackPressed {
                finish()
            }
            btnLock.setOnClickListener {
                processLock()
            }
            edtValue.onDone {
                processLock()
            }
        }

        collectState()
        setUpInput()
    }

    private fun processLock() {
        nominalBuy = binding.edtValue.text.toString().replace(",", "").trim()
        if (!nominalBuy.isNullOrEmpty()) {
            if (nominalBuy?.length!! > 10) {
                GenericModalDialogCashplus.Builder()
                    .title(getString(R.string.something_wrong))
                    .image(R.raw.cashplus_gagal, true)
                    .description(getString(R.string.top_up_balance_error_out_of_capacity))
                    .buttonNegative(
                        NegativeButtonAction(
                            btnLabel = getString(R.string.close),
                            backgroundButton = R.drawable.bg_yellow50_rounded_12,
                            buttonTextColor = Color.parseColor("#FFBE0B"),
                            setClickOnDismiss = true
                        )
                    ).show(supportFragmentManager)
                return
            }
            viewModel.onTriggerEvent(BuyEvent.CheckKyc)
            return
        }
        GenericModalDialogCashplus.Builder()
            .title(getString(R.string.something_wrong))
            .image(R.drawable.illustration_empty)
            .description(
                if (buyType == LockGoldType.BY_PRICE) getString(R.string.nominal_buy_in_rupiah_is_empty)
                else getString(R.string.nominal_buy_in_gram_is_empty)
            )
            .buttonNegative(
                NegativeButtonAction(
                    btnLabel = getString(R.string.close),
                    backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                    buttonTextColor = Color.parseColor("#0A57FF"),
                    setClickOnDismiss = true
                )
            )
            .show(supportFragmentManager, "Customer Id Is Empty")
    }

    private fun setUpInput() {
        with(binding) {
            when(buyType) {
                LockGoldType.BY_PRICE -> {
                    tvLabelBuyCashGold.text = getString(R.string.value_in_rupiah)
                    edtValue.hint = getString(R.string.hint_value_in_price)
                    edtValue.inputType = InputType.TYPE_CLASS_NUMBER
                    edtValue.setIDRCurrencyInput()
                }
                LockGoldType.BY_GRAM -> {
                    tvLabelBuyCashGold.text = getString(R.string.value_in_gram)
                    edtValue.hint = getString(R.string.hint_value_in_gram)
                    edtValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    edtValue.addTextChangedListener(object: TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            viewModel.inputOnChange.value = s.toString()
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    })
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(BuyEvent.GetGoldBalance)
        viewModel.onTriggerEvent(BuyEvent.GetGoldPrice)
        viewModel.onTriggerEvent(BuyEvent.GetSt24Balance)
    }

    private fun collectState() {
        collectGoldBalance()
        collectStateLoading()
        collectStateError()
        collectGoldPrice()
        collectUserBalance()
//        collectActiveInput()
//        collectLeftInput()
//        collectRightInput()
        collectKycStatus()
        collectLockResult()
        collectTrxResult()
    }

    private fun collectTrxResult() {
        //TAG PRODUCT
        lifecycleScope.launch {
            viewModel.trxTagResult.collectLatest {
                it?.let {
                    enterPinDialogTag.dismiss()
                    TagGoldFragment(
                        onButtonPayClicked = {
                            enterPinDialogPay.show(supportFragmentManager, null)
                        }
                    ).show(supportFragmentManager, null)
                }
            }
        }

        //PAY PRODUCT
        lifecycleScope.launch {
            viewModel.trxPayResult.collectLatest {
                it?.let {
                    enterPinDialogPay.dismiss()
                    val message = "Pembelian emas senilai ${it.gramBuy} berhasil"

                    finishAffinity()
                    startActivity(
                        ModuleRoute.internalIntent(this@BuyCashGoldActivity, MAIN_ACTIVITY_PATH)
                            .putExtra(MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE, message)
                            .putExtra(com.pasukanlangit.id.library_core.domain.model.NotifType.NOTIF_TRX_SUCCESS)
                    )
                }
            }
        }
    }

    private fun collectLockResult() {
        lifecycleScope.launch {
            viewModel.lockResult.collectLatest {
               it?.let {
                   enterPinDialogTag.show(supportFragmentManager, null)
               }
            }
        }
    }

    private fun collectKycStatus() {
        lifecycleScope.launch {
            viewModel.kycStatus.collectLatest {
              it?.let { status ->
                  if(!status.isIdentityCardUploaded || !status.isIdentityWithSelfieUploaded || !status.isTaxIdUploaded){
                      showModalDialog("Silahkan lengkapi data KYC untuk dapat menggunakan semua fitur cashgold")
                  }else if(status.isRejected){
                      showModalDialog("Data kyc kamu ditolak, silahkan melakukan verifikasi ulang")
                  }else if(!status.isApproved){
                      GenericCashGoldModalDialog.Builder()
                          .title("Peringatan")
                          .description("KYC Masih dalam proses verifikasi, tunggu beberapa saat ya")
                          .image(R.drawable.ilustration_warning)
                          .show(supportFragmentManager)
                  }
                  else{
                      viewModel.onTriggerEvent(BuyEvent.LockGold(nominalBuy))
                  }
              }
            }
        }
    }

    private fun showModalDialog(desc: String) {
        GenericCashGoldModalDialog.Builder()
            .title("Peringatan")
            .description(desc)
            .image(R.drawable.ilustration_warning)
            .buttonPositive(
                PositiveButtonAction(
                    btnLabel = "KYC Upload",
                    onBtnClicked = {
                        startActivity(ModuleRoute.internalIntent(this@BuyCashGoldActivity, KYC_INIT_PATH))
                    }
                )
            )
            .buttonNegative(
                NegativeButtonAction(
                    btnLabel = "Nanti Saja",
                    setClickOnDismiss = true
//                                  onBtnClicked = {
//                                     viewModel.onTriggerEvent(BuyEvent.LockGold)
//                                  }
                )
            )
            .show(supportFragmentManager)
    }

//    private fun collectRightInput() {
//        lifecycleScope.launch {
//            viewModel.inputRightResult.collectLatest {
//                binding.edtInputRight.setText(it)
//                binding.edtInputRight.setSelection(it?.length ?: 0)
//            }
//        }
//    }
//
//    private fun collectLeftInput() {
//        lifecycleScope.launch {
//            viewModel.inputLeftResult.collectLatest {
//                binding.edtInputLeft.setText(it)
//                binding.edtInputLeft.setSelection(it?.length ?: 0)
//            }
//        }
//    }

//    private fun collectActiveInput() {
//        lifecycleScope.launch {
//            viewModel.lockType.collectLatest {
//                with(binding){
//                    when(it){
//                        LockGoldType.BY_PRICE -> {
//                            labelInputLeft.text = getString(R.string.label_harga_emas)
//                            labelInputRight.text = getString(R.string.label_jumlah_emas)
//
//                            edtInputLeft.hint = getString(R.string.hint_price)
//                            edtInputRight.hint = getString(R.string.hint_gram)
//                        }
//                        LockGoldType.BY_GRAM -> {
//                            labelInputLeft.text = getString(R.string.label_jumlah_emas)
//                            labelInputRight.text =getString(R.string.label_harga_emas)
//
//                            edtInputRight.hint = getString(R.string.hint_price)
//                            edtInputLeft.hint = getString(R.string.hint_gram)
//                        }
//                    }
////                    if(it){
////                        labelInputLeft.text = getString(R.string.label_harga_emas)
////                        labelInputRight.text = getString(R.string.label_jumlah_emas)
////
////                        edtInputLeft.hint = getString(R.string.hint_price)
////                        edtInputRight.hint = getString(R.string.hint_gram)
////                    }else{
////                        labelInputLeft.text = getString(R.string.label_jumlah_emas)
////                        labelInputRight.text =getString(R.string.label_harga_emas)
////
////                        edtInputRight.hint = getString(R.string.hint_price)
////                        edtInputLeft.hint = getString(R.string.hint_gram)
////                    }
//                }
//
//            }
//        }
//    }

    private fun collectUserBalance() {
        lifecycleScope.launch {
            viewModel.stateUserBalance.collectLatest {
                it?.let { userBalance ->
                    binding.tvSaldo.text = userBalance
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun collectGoldPrice() {
        lifecycleScope.launch {
            viewModel.stateGoldPrice.collectLatest {
                binding.tvNote.isVisible = it != null
                it?.let { goldPrice ->
                    binding.tvNote.text = getString(R.string.price_gold_format, goldPrice.priceBuyCurrency)
                }
            }
        }
    }

    private fun collectGoldBalance() {
        lifecycleScope.launch {
            viewModel.stateGoldBalance.collectLatest { goldBalance ->
                goldBalance?.let { balance ->
                    binding.tvSaldoGold.text = balance
                }
            }
        }
    }

    private fun collectStateError() {
        lifecycleScope.launch {
            viewModel.stateError.collectLatest { message ->
                message.peek()?.let { info ->
                    val toast = Toast.makeText(this@BuyCashGoldActivity, info, Toast.LENGTH_SHORT)
                    toast.show()
                    delay(toast.duration.toLong() + 500L)
                    viewModel.onTriggerEvent(BuyEvent.RemoveHeadQueue)
                }
            }
        }
    }

    private fun collectStateLoading() {
        lifecycleScope.launch {
            viewModel.stateLoading.collectLatest { isLoading ->
                binding.loading.isVisible = isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.stateLoadingButton.collectLatest { isLoading ->
                enterPinDialogTag.setLoading(isLoading)
                enterPinDialogPay.setLoading(isLoading)

                binding.btnLock.isEnabled = !isLoading

                if(isLoading){
                    binding.btnLock.showLoading()
                }else{
                    binding.btnLock.hideProgress(getString(R.string.tinjau_pembelian))
                }
            }
        }
    }
}