package com.pasukanlangit.id.kyc_presentation.pages.init

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.kyc_presentation.R
import com.pasukanlangit.id.kyc_presentation.databinding.ActivityStartedEKYCBinding
import com.pasukanlangit.id.kyc_presentation.pages.completing.CompletingKYCActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class StartedEKYCActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartedEKYCBinding
    private val viewModel: KYCViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartedEKYCBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            appBar.setOnBackPressed { finish() }
        }
        collectState()
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateLoading.collectLatest {
                        binding.progressBar.isVisible = it
                    }
                }

                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                GenericModalDialogCashplus.Builder()
                                    .title(getString(R.string.something_wrong))
                                    .image(R.drawable.ilustration_warning)
                                    .description(info)
                                    .buttonNegative(
                                        NegativeButtonAction(
                                            btnLabel = getString(R.string.close),
                                            onBtnClicked = {
                                                viewModel.onTriggerEvent(EKycEvents.RemoveHeadMessage)
                                            }
                                        )
                                    )
                            }
                        }
                    }
                }

                launch {
                    viewModel.checkStatus.collectLatest { status ->
                        binding.progressBar.isVisible = false
                        when(status) {
                            is EKycStatus.None ->
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.container_fragment_status, EKycStartedFragment())
                                    .commit()
                            is EKycStatus.IdCardUploaded -> {
                                delay(300)
                                startActivity(
                                    Intent(this@StartedEKYCActivity, CompletingKYCActivity::class.java).apply {
                                        putExtra(CompletingKYCActivity.IS_ID_CARD_UPLOADED_STATE, true)
                                    }
                                )
                                finish()
                            }
                            is EKycStatus.UploadSelfie -> {
                                delay(300)
                                startActivity(
                                    Intent(this@StartedEKYCActivity, CompletingKYCActivity::class.java).apply {
                                        putExtra(CompletingKYCActivity.IS_UPLOAD_SELFIE_STATE, true)
                                    }
                                )
                                finish()
                            }
                            is EKycStatus.SelfieUploaded -> {
                                delay(300)
                                startActivity(
                                    Intent(this@StartedEKYCActivity, CompletingKYCActivity::class.java).apply {
                                        putExtra(CompletingKYCActivity.IS_SELFIE_UPLOADED_STATE, true)
                                    }
                                )
                                finish()
                            }
                            is EKycStatus.Waiting -> {
                                delay(300)
                                startActivity(
                                    Intent(this@StartedEKYCActivity, CompletingKYCActivity::class.java).apply {
                                        putExtra(CompletingKYCActivity.IS_WAITING_EKYC_STATE, true)
                                    }
                                )
                                finish()
                            }
                            is EKycStatus.Rejected -> {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.container_fragment_status, EKycRejectedFragment())
                                    .commit()
                            }
                            is EKycStatus.Approved ->
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.container_fragment_status, EKycApprovedFragment())
                                    .commit()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(EKycEvents.CheckEKycStatus)
    }
}