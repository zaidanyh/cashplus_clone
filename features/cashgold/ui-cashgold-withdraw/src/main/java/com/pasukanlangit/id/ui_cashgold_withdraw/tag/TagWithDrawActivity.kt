package com.pasukanlangit.id.ui_cashgold_withdraw.tag

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.*
import com.pasukanlangit.id.core.presentation.EnterPinDialogFragment
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.putExtra
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.ui_cashgold_withdraw.R
import com.pasukanlangit.id.ui_cashgold_withdraw.databinding.ActivityTagWithDrawBinding
import com.pasukanlangit.id.ui_cashgold_withdraw.tag.bottomsheet.TagWithDrawFragment
import com.pasukanlangit.id.ui_core.components.GenericCashGoldModalDialog
import com.pasukanlangit.id.usecase.TrxType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TagWithDrawActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTagWithDrawBinding
    private val viewModel: TagWithDrawViewModel by viewModel()

    private val enterPinDialogTag: EnterPinDialogFragment =
        EnterPinDialogFragment(
            onEnterPinCompleted = {
                viewModel.onTriggerEvent(TagWithDrawEvent.SendTrxGold(type = TrxType.TAG, pin = it))
            }
        )

    private val enterPinDialogPay: EnterPinDialogFragment =
        EnterPinDialogFragment(
            onEnterPinCompleted = {
                viewModel.onTriggerEvent(TagWithDrawEvent.SendTrxGold(type = TrxType.PAY, pin = it))
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTagWithDrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appbar.setIconBackPressed { finish() }
        binding.noteAddress.setOnButtonClicked {
            startActivity(ModuleRoute.internalIntent(this, CASHGOD_ADDRESS_HOME_PATH))
        }
        binding.btnChooseAnotherAddress.setOnClickListener {
            startActivity(ModuleRoute.internalIntent(this, CASHGOD_ADDRESS_HOME_PATH))
        }

        binding.btnTinjau.setUpToProgressButton(this)
        binding.btnTinjau.setOnClickListener {
            viewModel.onTriggerEvent(TagWithDrawEvent.CheckIsKtpIsEmpty)
        }

        intent.parcelable<ProductWithDraw>(KEY_PRODUCT_SELECTED)?.let { product ->
            viewModel.onTriggerEvent(TagWithDrawEvent.SetWithDrawProduct(withDrawProduct = product))
            with(binding){
                tvGram.text = product.productDomination
                tvPrice.text = product.fee
                tvQtyItem.text =  getString(R.string.qty_item, product.qty, product.productDomination) //"Pembelian ${product.qty} Barang (${product.productDomination})"
                tvDominationTot.text = product.productDomination
                tvQtyTot.text = product.qty
                tvPriceSertif.text = getNumberRupiah(product.feeRaw.times(product.qty.toDouble()))
            }
        }

        collectState()
    }

    private fun collectState() {
        collectStateLoading()
        collectStateError()
        collectMainAddress()
        collectStateKtpIsEmpty()
        collectStateBook()
        collectIsMainAddressEmpty()
        collectTrxResult()
    }

    private fun collectTrxResult() {
        //TAG PRODUCT
        lifecycleScope.launch {
            viewModel.trxTagResult.collectLatest {
                it?.let {
                    enterPinDialogTag.dismiss()
                    TagWithDrawFragment(
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
                    val message = "Pembayaran withdraw ${it.name} berhasil"

                    finishAffinity()
                    startActivity(
                        ModuleRoute.internalIntent(this@TagWithDrawActivity, MAIN_ACTIVITY_PATH)
                            .putExtra(MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE, message)
                            .putExtra(com.pasukanlangit.id.library_core.domain.model.NotifType.NOTIF_TRX_SUCCESS)
                    )
                }
            }
        }
    }

    private fun collectStateBook() {
        lifecycleScope.launch {
            viewModel.bookWithDrawId.collectLatest {
                it?.let {
                    enterPinDialogTag.show(supportFragmentManager, null)
                }
            }
        }
    }

    private fun collectStateKtpIsEmpty() {
        lifecycleScope.launch {
            viewModel.isKtpNotEmpty.collectLatest {
                it?.let { value ->
                    if(value){
                        viewModel.onTriggerEvent(TagWithDrawEvent.WithDrawBook)
                    }else{
                        GenericCashGoldModalDialog.Builder()
                            .title("Peringatan")
                            .description("Silahkan update nomor KTP dan NPWP untuk dapat melakukan withdraw emas")
                            .image(R.drawable.ilustration_warning)
                            .buttonPositive(
                                PositiveButtonAction(
                                    btnLabel = "Update Profil",
                                    onBtnClicked = {
                                        startActivity(ModuleRoute.internalIntent(this@TagWithDrawActivity, UPDATE_PROFILE_CASHGOLD_PATH))
                                    }
                                )
                            )
                            .buttonNegative(
                                NegativeButtonAction(
                                    btnLabel = "Nanti Saja",
                                    setClickOnDismiss = true
                                )
                            )
                            .show(supportFragmentManager)
                    }
                }
            }
        }
    }

    private fun collectIsMainAddressEmpty() {
        lifecycleScope.launch {
            viewModel.isEmptyMainAddress.collectLatest {
                it?.let { value ->
                    binding.noteAddress.isVisible = value
                    binding.btnTinjau.isEnabled = !value
                    binding.wrapperAddress.isVisible = !value
                }
            }
        }
    }

    private fun collectMainAddress() {
        lifecycleScope.launch {
            viewModel.stateMainAddress.collectLatest {
                it?.let { address ->
                    with(binding){
                        tvVillage.text = address.kelurahan
                        tvAddress.text = address.alamat
                        tvSubAddress.text = getString(R.string.full_address_cashgold,
                                address.kabupaten,
                                address.kecamatan,
                                address.provinsi,
                                address.kodepos
                            )
                    }
                }
            }
        }
    }

    private fun collectStateError() {
        lifecycleScope.launch {
            viewModel.stateError.collectLatest { message ->
                message.peek()?.let { info ->
                    val toast = Toast.makeText(this@TagWithDrawActivity, info, Toast.LENGTH_SHORT)
                    toast.show()
                    delay(toast.duration.toLong() + 500L)
                    viewModel.onTriggerEvent(TagWithDrawEvent.RemoveHeadMessageQueue)
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

                binding.btnTinjau.isEnabled = !isLoading
                if(isLoading){
                    binding.btnTinjau.showLoading()
                }else{
                    binding.btnTinjau.hideProgress(getString(R.string.tinjau ))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(TagWithDrawEvent.GetMainAddress)
    }

    companion object {
        const val KEY_PRODUCT_SELECTED = "KEY_PRODUCT_SELECTED"
    }
}