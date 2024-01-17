package com.pasukanlangit.cashplus.ui.pages.others.settings.password

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
import com.pasukanlangit.cashplus.databinding.ActivityResetPasswordBinding
import com.pasukanlangit.cashplus.domain.model.ResetPasswordRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.login.OptionOTPFragment
import com.pasukanlangit.cashplus.ui.login.ResetPwViewModel
import com.pasukanlangit.cashplus.ui.register.EnterOTPActivity
import com.pasukanlangit.cashplus.utils.MethodSendOTP
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.InputUtil.passwordValidate
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.domain.model.NotifType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private val viewModel : ResetPwViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private lateinit var resetPasswordRequest: ResetPasswordRequest

    private var accessToken: String? = null
    private var phoneNumber: String? = null
    private var stateNewPw: Boolean = false
    private var stateConfPw: Boolean = false

    private var optionOTP = OptionOTPFragment(
        via = { via ->
            resetPasswordRequest = resetPasswordRequest.copy(via = via)
            viewModel.resetPassword(resetPasswordRequest)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumber = intent.getStringExtra(KEY_PHONE_NUMBER)
        val uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()

        with(binding){
            appBar.setOnBackPressed { finish() }

            edtNewPw.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    val validatePassword = passwordValidate(this@ResetPasswordActivity, input)

                    if(input.isEmpty()) {
                        inputNewPw.error = getString(R.string.input_password_required)
                        stateNewPw = false
                        return
                    }
                    if(validatePassword.isError) {
                        inputNewPw.error = validatePassword.message
                        stateNewPw = false
                        return
                    }

                    stateNewPw = true
                    inputNewPw.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSavePw.isEnabled = stateNewPw && stateConfPw
                }
            })

            edtNewPwConfirm.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    val password = edtNewPw.text.toString()
                    if(input.isEmpty()){
                        inputNewPwConfirm.error = "Konfirmasi sandi perlu diisi"
                        stateConfPw = false
                        return
                    }
                    if(password != input){
                        inputNewPwConfirm.error = "Kata sandi tidak sama"
                        stateConfPw = false
                        return
                    }

                    stateConfPw = true
                    inputNewPwConfirm.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSavePw.isEnabled = stateNewPw && stateConfPw
                }
            })
            btnSavePw.setOnClickListener {
                btnSavePw.isEnabled = false
                val inputPw = edtNewPw.text.toString().trim()

                resetPasswordRequest = ResetPasswordRequest(
                    uuid = uuid ?: "", phoneNumber = phoneNumber ?: "",
                    newPassword = inputPw, via = ""
                )
                optionOTP.show(supportFragmentManager, optionOTP.tag)
            }
        }
        collectDataResetPassword()
    }

    private fun collectDataResetPassword() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.resetPassLoading.collectLatest {
                        if (it) {
                            binding.btnSavePw.showLoading()
                            return@collectLatest
                        }
                        binding.btnSavePw.hideProgress(getString(R.string.save))
                    }
                }
                launch {
                    viewModel.resetPass.collectLatest { response ->
                        binding.btnSavePw.isEnabled = stateNewPw && stateConfPw
                        val intent = Intent(this@ResetPasswordActivity, EnterOTPActivity::class.java).apply {
                            putExtra(EnterOTPActivity.KEY_RESET_PW_REQUEST, resetPasswordRequest)
                            putExtra(EnterOTPActivity.KEY_METHOD_OTP, resetPasswordRequest.via)
                            putExtra(EnterOTPActivity.KEY_IS_LOGGED_IN, true)
                        }
                        if (resetPasswordRequest.via == MethodSendOTP.WHATSAPP.value && !response?.otpLink.isNullOrEmpty())
                            intent.putExtra(EnterOTPActivity.OTP_LINK_KEY, response?.otpLink)
                        startActivity(intent)
                    }
                }
                launch {
                    viewModel.resetPassError.collectLatest { message ->
                        binding.btnSavePw.isEnabled = stateNewPw && stateConfPw
                        message.peek()?.let { info ->
                            val menusAllFragment = ButtomSheetNotif(info, NotifType.NOTIF_ERROR)
                            menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                            delay(300)
                            viewModel.removeResetPassError()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_PHONE_NUMBER = "KEY_PHONE_NUMBER"
    }
}