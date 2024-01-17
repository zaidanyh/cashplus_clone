package com.pasukanlangit.id.ui_downline_home.mintasaldoqr

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.presentation.EnterPinDialogFragment
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.ActivityScanQrconfirmationBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanQRConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanQrconfirmationBinding
    private val viewModel: ScanQRConfirmationViewModel by viewModel()

    private val enterPinDialogPay: EnterPinDialogFragment =
        EnterPinDialogFragment(
            onEnterPinCompleted = {
                viewModel.onTriggerEvent(ScanQrConfirmationEvent.OnSubmitPin(pin = it))
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrconfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appbar.setOnBackPressed {
            finish()
        }

        binding.btnNext.setUpToProgressButton(this)
        binding.btnNext.setOnClickListener {
            enterPinDialogPay.show(supportFragmentManager, null)
        }

        intent.parcelable<DataScanResult>(KEY_DATA_QR)?.let { data ->
            binding.apply {
                tvName.text = data.full_name
                tvTujuan.text = data.dest
                tvJumlahTf.text = getNumberRupiah(data.amount)

                //set ID QR
                viewModel.onTriggerEvent(ScanQrConfirmationEvent.SetIdQr(data.id))

                btnNext.isEnabled = true
            }
        }

        collectState()
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                //LOADING STATE
                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        enterPinDialogPay.setLoading(isLoading)
                        binding.btnNext.isEnabled = !isLoading

                        if(isLoading){
                            binding.btnNext.showLoading()
                        }else{
                            binding.btnNext.hideProgress(getString(R.string.next))
                        }
                    }
                }

                //ERROR STATE
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            enterPinDialogPay.dismiss()
                            val toast =
                                Toast.makeText(this@ScanQRConfirmationActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong() + 500L)
                            viewModel.onTriggerEvent(ScanQrConfirmationEvent.RemoveHeadMessage)
                        }
                    }
                }

                //SUCCESS STATE
                launch {
                    viewModel.response.collectLatest {
                        it?.let { resp ->
                            enterPinDialogPay.dismiss()
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.done))
                                .description(resp.message)
                                .image(R.drawable.ilustration_warning)
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.close),
                                        onBtnClicked =  {
                                            this@ScanQRConfirmationActivity.finish()
                                        }
                                    )
                                )
                                .show(supportFragmentManager)
                        }
                    }
                }
            }
        }
    }


    companion object {
        const val KEY_DATA_QR = "KEY_DATA_QR"
    }
}