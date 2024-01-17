package com.pasukanlangit.cashplus.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityResetPasswordPublicBinding
import com.pasukanlangit.cashplus.domain.model.ResetPasswordRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.register.EnterOTPActivity
import com.pasukanlangit.cashplus.utils.MethodSendOTP
import com.pasukanlangit.cashplus.utils.MyUtils.createTransactionID
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyNumber
import com.pasukanlangit.id.core.utils.InputUtil.passwordValidate
import com.pasukanlangit.id.core.utils.InputUtil.validPrefixNumber
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.domain.model.NotifType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetPasswordPublicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordPublicBinding
    private val viewModel: ResetPwViewModel by viewModel()

    private lateinit var resetPassRequest: ResetPasswordRequest
    private var stateMsidn: Boolean = false
    private var statePw: Boolean = false
    private var statePwConfirm: Boolean = false
    private var stateBack: Boolean = true

    private val optionOTP = OptionOTPFragment(
        via = { via ->
            resetPassRequest = resetPassRequest.copy(via = via)
            viewModel.resetPassword(resetPassRequest)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordPublicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) { onFinishActivity() }
        } else {
            onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() { onFinishActivity() }
            })
        }

        with(binding){
            appBar.setOnBackPressed { onFinishActivity() }
            btnReset.setUpToProgressButton(this@ResetPasswordPublicActivity)
            btnReset.setOnClickListener { onButtonResetClicked() }
            edtMsidn.filters = arrayOf(InputFilter.LengthFilter(16))
            edtMsidn.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputMsidn.error = getString(R.string.input_phone_number_first)
                        stateMsidn = false
                        return
                    }
                    if (input.length < 9) {
                        inputMsidn.error = getString(R.string.input_min_length_required, getString(R.string.phone_number), 9)
                        stateMsidn = false
                        return
                    }
                    if (!input.checkIsOnlyNumber()) {
                        inputMsidn.error = getString(R.string.number_or_code_not_valid)
                        stateMsidn = false
                        return
                    }
                    stateMsidn = true
                    inputMsidn.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnReset.isEnabled = stateMsidn && statePw && statePwConfirm
                }
            })

            edtNewPassword.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    val result = passwordValidate(this@ResetPasswordPublicActivity, input)
                    if (input.isEmpty()) {
                        inputNewPassword.error = getString(R.string.input_password_required)
                        statePw = false
                        return
                    }
                    if (result.isError) {
                        inputNewPassword.error = result.message
                        statePw = false
                        return
                    }
                    statePw = true
                    inputNewPassword.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnReset.isEnabled = stateMsidn && statePw && statePwConfirm
                }
            })

            edtPasswordConfimation.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    val password = edtNewPassword.text.toString()
                    if (input.isEmpty()) {
                        inputPasswordConfimation.error = getString(R.string.input_confirm_password_required)
                        statePwConfirm = false
                        return
                    }
                    if (password != input) {
                        inputPasswordConfimation.error = getString(R.string.input_confirm_password_not_same)
                        statePwConfirm = false
                        return
                    }
                    statePwConfirm = true
                    inputPasswordConfimation.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnReset.isEnabled = stateMsidn && statePw && statePwConfirm
                }
            })
        }
        collectDataResetPass()
    }

    private fun onButtonResetClicked() {
        with(binding) {
            btnReset.isEnabled = false
            val uuid = createTransactionID()
            val phoneNumber = edtMsidn.text.toString()
            val newPassword = edtNewPassword.text.toString()
            val confirmPassword = edtPasswordConfimation.text.toString()

            if (!phoneNumber.validPrefixNumber()) {
                inputMsidn.error = getString(R.string.any_input_is_not_valid, getString(R.string.phone_number))
                return
            }
            if (newPassword != confirmPassword) {
                inputPasswordConfimation.error = getString(R.string.input_confirm_password_not_same)
                return
            }
            resetPassRequest = ResetPasswordRequest(uuid, phoneNumber, newPassword, "")
            optionOTP.show(supportFragmentManager, optionOTP.tag)
        }
    }

    private fun collectDataResetPass() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.resetPassLoading.collectLatest {
                        stateBack = !it
                        if (it) {
                            binding.btnReset.showLoading()
                            return@collectLatest
                        }
                        binding.btnReset.hideProgress(getString(R.string.reset))
                    }
                }
                launch {
                    viewModel.resetPass.collectLatest { response ->
                        binding.btnReset.isEnabled = stateMsidn && statePw && statePwConfirm
                        val intent = Intent(this@ResetPasswordPublicActivity, EnterOTPActivity::class.java).apply {
                            putExtra(EnterOTPActivity.KEY_RESET_PW_REQUEST, resetPassRequest)
                            putExtra(EnterOTPActivity.KEY_METHOD_OTP, resetPassRequest.via)
                        }
                        if (resetPassRequest.via == MethodSendOTP.WHATSAPP.value && !response?.otpLink.isNullOrEmpty())
                            intent.putExtra(EnterOTPActivity.OTP_LINK_KEY, response?.otpLink)
                        startActivity(intent)
                    }
                }
                launch {
                    viewModel.resetPassError.collectLatest { message ->
                        binding.btnReset.isEnabled = stateMsidn && statePw && statePwConfirm
                        message.peek()?.let { info ->
                            val menusAllFragment = ButtomSheetNotif(info, NotifType.NOTIF_ERROR)
                            menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                            delay(300)
                            viewModel.removeResetPassError()
                        }
                    }
                }
            }
        }
    }

    private fun onFinishActivity() {
        if (!stateBack) return
        else finish()
    }
}