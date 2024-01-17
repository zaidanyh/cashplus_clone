package com.pasukanlangit.id.ui_downline_transfersaldo

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.presentation.EnterPinDialogFragment
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.InputUtil.toCapitalize
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.ui_downline_transfersaldo.databinding.ActivityDetailTransferAgentBinding
import com.pasukanlangit.id.ui_downline_transfersaldo.model.ListDownlinePhone
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailTransferAgentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTransferAgentBinding
    private val viewModel: TransferSaldoAgenViewModel by viewModel()

    private var dataDownLine: ListDownlinePhone? = null
    private lateinit var enterPinDialog: EnterPinDialogFragment
    private var balanceTransfer: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransferAgentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataDownLine = intent.parcelable(DATA_DOWNLINE_CHOOSED_KEY)
        balanceTransfer = intent.getStringExtra(BALANCE_TRANSFER_KEY)

        enterPinDialog = EnterPinDialogFragment(
            onEnterPinCompleted = {
                viewModel.onTriggerEvent(
                    TransferBalanceEvent.TransferBalanceDownLine(
                        pin = it,
                        dest = dataDownLine?.phoneNumber,
                        nominal = balanceTransfer
                    )
                )
            }
        )

        viewModel.onTriggerEvent(TransferBalanceEvent.CheckBalance)
        with(binding) {
            appbar.setOnBackPressed { finish() }
            val imageUrl = dataDownLine?.img_url?.ifEmpty {
                "https://ui-avatars.com/api/?name=${dataDownLine?.name}&size=300&rounded=true&background=0A57FF&color=ffffff&bold=true"
            }
            Glide.with(this@DetailTransferAgentActivity)
                .load(imageUrl)
                .circleCrop()
                .into(imgDownline)
            tvNameDownline.text = dataDownLine?.name.toString().toCapitalize()
            tvPhoneDownline.text = dataDownLine?.phoneNumber
            tvNominalTransfer.text = getNumberRupiah(balanceTransfer?.toInt())
            btnSubmit.setUpToProgressButton(this@DetailTransferAgentActivity)
            btnSubmit.setOnClickListener {
                enterPinDialog.show(supportFragmentManager, enterPinDialog.tag)
            }
        }

        collectState()
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE SUCCESS MESSAGE
                launch {
                    viewModel.messageSuccess.collectLatest { message ->
                        if (message != null) {
                            enterPinDialog.dismiss()
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.done))
                                .description(
                                    getString(
                                        R.string.transfer_deposit_downline_successfully,
                                        getNumberRupiah(balanceTransfer?.toIntOrNull()),
                                        dataDownLine?.phoneNumber
                                    )
                                )
                                .image(R.drawable.ilustration_warning)
                                .buttonNegative(
                                    NegativeButtonAction(
                                        btnLabel = getString(R.string.close),
                                        onBtnClicked = {
                                            this@DetailTransferAgentActivity.finish()
                                        }
                                    )
                                )
                                .show(supportFragmentManager)
                        }
                    }
                }
                launch {
                    viewModel.isLoadingButton.collectLatest {
                        binding.btnSubmit.isEnabled = !it
                        enterPinDialog.setLoading(it)

                        if (it) {
                            binding.btnSubmit.showLoading()
                            return@collectLatest
                        }
                        binding.btnSubmit.hideProgress(getString(R.string.send))
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.contains("pin", ignoreCase = true)) enterPinDialog.clearTextOnDialog()
                            else enterPinDialog.dismiss()

                            if (info.contains("Transaksi sudah ada", ignoreCase = true)) {
                                viewModel.onTriggerEvent(TransferBalanceEvent.RemoveHeadQueue)
                                GenericModalDialogCashplus.Builder()
                                    .title(getString(R.string.something_wrong))
                                    .image(R.drawable.illustration_error)
                                    .description(getString(R.string.transaction_already_exist, dataDownLine?.phoneNumber, balanceTransfer))
                                    .buttonNegative(
                                        NegativeButtonAction(
                                            btnLabel = getString(R.string.close),
                                            backgroundButton = R.drawable.bg_transparent_border_primary_rounded_12,
                                            buttonTextColor = Color.parseColor("#0A57FF"),
                                            setClickOnDismiss = true
                                        )
                                    )
                                    .show(supportFragmentManager)
                                return@collectLatest
                            }
                            val toast = Toast.makeText(
                                this@DetailTransferAgentActivity,
                                if (info.contains("pin", ignoreCase = true)) getString(R.string.state_wrong_pin)
                                else info,
                                Toast.LENGTH_SHORT
                            )
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(TransferBalanceEvent.RemoveHeadQueue)
                        }
                    }
                }

                launch {
                    viewModel.balanceCheckLoading.collectLatest {
                        binding.progressBar.isVisible = it
                    }
                }
                launch {
                    viewModel.balanceCheck.collectLatest { response ->
                        binding.apply {
                            btnSubmit.isEnabled = response?.balance != null
                            if (response?.balance != null) {
                                tvBalance.text = getNumberRupiah(response.balance)
                                txtLessBalance.isVisible = response.balance!! < balanceTransfer?.toDouble()!!
                                btnSubmit.isEnabled = response.balance!! >= balanceTransfer?.toDouble()!!
                            }
                        }
                    }
                }
                launch {
                    viewModel.balanceCheckError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@DetailTransferAgentActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(TransferBalanceEvent.RemoveCheckBalanceError)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val DATA_DOWNLINE_CHOOSED_KEY = "data_downline_choosed_key"
        const val BALANCE_TRANSFER_KEY = "balance_transfer_key"
    }
}