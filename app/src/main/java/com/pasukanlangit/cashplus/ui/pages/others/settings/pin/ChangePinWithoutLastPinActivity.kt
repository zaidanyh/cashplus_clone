package com.pasukanlangit.cashplus.ui.pages.others.settings.pin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.ActivityChangePinWithoutLastPinBinding
import com.pasukanlangit.cashplus.domain.model.ResetPINRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.login.OptionOTPFragment
import com.pasukanlangit.cashplus.ui.register.EnterOTPActivity
import com.pasukanlangit.cashplus.utils.MethodSendOTP
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.domain.model.NotifType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePinWithoutLastPinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePinWithoutLastPinBinding
    private val viewModel: ChangePinWithoutLastViewModel by viewModel()
    private val sharedPrefDataSource : SharedPrefDataSource by inject()

    private lateinit var resetPINRequest: ResetPINRequest
    private var stateNewPIN: Boolean = false
    private var stateConfPIN: Boolean = false
    private var uuid: String? = null
    private var accessToken: String? = null

    private val optionOTP = OptionOTPFragment(
        via = { via ->
            resetPINRequest = resetPINRequest.copy(via = via)
            viewModel.resetPIN(resetPINRequest, accessToken ?: "")
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePinWithoutLastPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uuid = sharedPrefDataSource.getUUID()
        accessToken = sharedPrefDataSource.getAccessToken()
        with(binding) {
            appBar.setOnBackPressed { finish() }
            btnSavePin.setUpToProgressButton(this@ChangePinWithoutLastPinActivity)
            edtNewPin.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputNewPin.error = getString(R.string.input_new_pin_required)
                        stateNewPIN = false
                        return
                    }
                    if (input.length < 6) {
                        inputNewPin.error = getString(R.string.input_pin_min_length)
                        stateNewPIN = false
                        return
                    }
                    stateNewPIN = true
                    inputNewPin.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSavePin.isEnabled = stateNewPIN && stateConfPIN
                }
            })
            edtNewPinConfirmation.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    val newPIN = edtNewPin.text.toString().trim()
                    if (input.isEmpty()) {
                        inputConfirmNewPin.error = getString(R.string.input_confirm_new_pin_required)
                        stateConfPIN = false
                        return
                    }
                    if (newPIN != input) {
                        inputConfirmNewPin.error = getString(R.string.input_confirm_new_pin_not_same)
                        stateConfPIN = false
                        return
                    }
                    stateConfPIN = true
                    inputConfirmNewPin.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSavePin.isEnabled = stateNewPIN && stateConfPIN
                }
            })
            btnSavePin.setOnClickListener {
                btnSavePin.isEnabled = false
                val newPin = edtNewPin.text.toString().trim()
                resetPINRequest = ResetPINRequest(
                    uuid = uuid ?: "", newPin = newPin, ""
                )
                optionOTP.show(supportFragmentManager, optionOTP.tag)
            }
        }
        collectDataResetPIN()
    }

    private fun collectDataResetPIN() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.resetPinLoading.collectLatest {
                        if (it) {
                            binding.btnSavePin.showLoading()
                            return@collectLatest
                        }
                        binding.btnSavePin.hideProgress(getString(R.string.save))
                    }
                }
                launch {
                    viewModel.resetPIN.collectLatest { response ->
                        binding.btnSavePin.isEnabled = stateNewPIN && stateConfPIN
                        val intent = Intent(this@ChangePinWithoutLastPinActivity, EnterOTPActivity::class.java).apply {
                            putExtra(EnterOTPActivity.KEY_RESET_PIN_REQUEST, resetPINRequest)
                            putExtra(EnterOTPActivity.KEY_METHOD_OTP, resetPINRequest.via)
                            putExtra(EnterOTPActivity.KEY_IS_LOGGED_IN, true)
                            putExtra(EnterOTPActivity.KEY_ACCESS_TOKEN_LOGGED_IN, sharedPrefDataSource.getAccessToken())
                        }
                        if (resetPINRequest.via == MethodSendOTP.WHATSAPP.value && !response?.otpLink.isNullOrEmpty())
                            intent.putExtra(EnterOTPActivity.OTP_LINK_KEY, response?.otpLink)
                        startActivity(intent)
                    }
                }
                launch {
                    viewModel.resetPinError.collectLatest { message ->
                        binding.btnSavePin.isEnabled = stateNewPIN && stateConfPIN
                        message.peek()?.let { info ->
                            val menusAllFragment = ButtomSheetNotif(info, NotifType.NOTIF_ERROR)
                            menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                            delay(300)
                            viewModel.removeResetPINError()
                        }
                    }
                }
            }
        }
    }
}