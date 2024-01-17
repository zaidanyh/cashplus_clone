package com.pasukanlangit.cashplus.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.google.firebase.messaging.FirebaseMessaging
import com.pasukanlangit.cashplus.MainActivityNavComp
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityLoginBinding
import com.pasukanlangit.cashplus.domain.model.LoginRequest
import com.pasukanlangit.cashplus.model.request_body.UpdateFirebaseTokenRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.register.EnterOTPActivity
import com.pasukanlangit.cashplus.ui.register.RegisterActivity
import com.pasukanlangit.cashplus.utils.MethodSendOTP
import com.pasukanlangit.cashplus.utils.MyUtils.createTransactionID
import com.pasukanlangit.cashplus.utils.MyUtils.generateMd5
import com.pasukanlangit.cashplus.view_model.LoginViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyNumber
import com.pasukanlangit.id.core.utils.InputUtil.validPrefixNumber
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity(), ButtomSheetNotif.BottomSheetEventLogin {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private lateinit var loginRequest: LoginRequest
    private lateinit var uuid: String
    private var isLoginWithPw: Boolean = false

    private var stateMsidn: Boolean = false
    private var statePassword: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            edtMsidnMasuk.filters = arrayOf(InputFilter.LengthFilter(16))
            btnLogin.setUpToProgressButton(this@LoginActivity)
            btnMethodLogin.setOnClickListener {
                isLoginWithPw = !isLoginWithPw
                txtPassword.isVisible = isLoginWithPw
                inputPwMasuk.isVisible = isLoginWithPw
                btnForgotPw.isInvisible = !isLoginWithPw
                edtPwMasuk.text?.clear()
                btnLogin.isEnabled = if (isLoginWithPw) stateMsidn && statePassword else stateMsidn
                animationLayout()
            }
            btnForgotPw.setOnClickListener {
                startActivity(Intent(this@LoginActivity, ResetPasswordPublicActivity::class.java))
            }
            btnLogin.setOnClickListener {
                btnLogin.isEnabled = false
                processLogin()
            }

            btnDaftar.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            edtMsidnMasuk.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputMsidnMasuk.error = getString(R.string.input_phone_number_first)
                        stateMsidn = false
                        return
                    }

                    if (input.length < 9) {
                        inputMsidnMasuk.error = getString(R.string.input_min_length_required, getString(R.string.phone_number), 9)
                        stateMsidn = false
                        return
                    }

                    if(!input.checkIsOnlyNumber()) {
                        inputMsidnMasuk.error = getString(R.string.number_or_code_not_valid)
                        stateMsidn = false
                        return
                    }
                    stateMsidn = true
                    inputMsidnMasuk.error = null
                    inputMsidnMasuk.isErrorEnabled = false
                }
                override fun afterTextChanged(s: Editable?) {
                    btnLogin.isEnabled = if (isLoginWithPw) stateMsidn && statePassword else stateMsidn
                }
            })
            edtPwMasuk.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputPwMasuk.error = getString(R.string.input_password_required)
                        statePassword = false
                        return
                    }
                    statePassword = true
                    inputPwMasuk.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnLogin.isEnabled = if (isLoginWithPw) stateMsidn && statePassword else stateMsidn
                }
            })
        }
        collectData()
    }

    private fun animationLayout() {
        val animation = AnimationUtils.loadAnimation(this,
            if (isLoginWithPw) R.anim.translate_fade_in_anim
            else R.anim.translate_fade_out_anim
        )
        animation.startOffset = 40
        with(binding) {
            txtPassword.startAnimation(animation)
            inputPwMasuk.startAnimation(animation)
            btnForgotPw.startAnimation(animation)
            btnMethodLogin.text = if (isLoginWithPw) getString(R.string.login_without_password)
                else getString(R.string.login_with_password)
        }
    }

    private fun processLogin() {
        with(binding) {
            val msidn = edtMsidnMasuk.text.toString().trim()
            val password = edtPwMasuk.text.toString().trim()

            KeyboardUtil.hideSoftKeyboard(this@LoginActivity)
            if (!msidn.validPrefixNumber()) {
                inputMsidnMasuk.error = getString(R.string.any_input_is_not_valid, getString(R.string.phone_number))
                return
            }

            uuid = createTransactionID()
            loginRequest = LoginRequest(uuid, msidn, "")
            if (isLoginWithPw) {
                val signature = generateMd5("$password$msidn")
                viewModel.login(loginRequest, signature)
                return
            }
            loginRequest = loginRequest.copy(via = MethodSendOTP.EMAIL.value)
            viewModel.loginByOtp(loginRequest)
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // COLLECT DATA LOGIN
                launch {
                    viewModel.loginLoading.collectLatest {
                        if (it) {
                            binding.btnLogin.showLoading()
                            return@collectLatest
                        }
                        binding.btnLogin.hideProgress(getString(R.string.login))
                    }
                }
                launch {
                    viewModel.login.collectLatest { response ->
                        sharedPref.setUUID(uuid)
                        sharedPref.setAccessToken(response?.x_access_token)

                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result
                                Log.d("FCM", token)

                                viewModel.updateFcm(
                                    UpdateFirebaseTokenRequest(uuid, token),
                                    response?.x_access_token.toString()
                                )
                                return@addOnCompleteListener
                            }
                        }
                    }
                }
                launch {
                    viewModel.loginByOtp.collectLatest { response ->
                        binding.btnLogin.isEnabled = if (isLoginWithPw) stateMsidn && statePassword else stateMsidn
                        val intent = Intent(this@LoginActivity, EnterOTPActivity::class.java).apply {
                            putExtra(EnterOTPActivity.KEY_LOGIN_REQUEST, loginRequest)
                            putExtra(EnterOTPActivity.KEY_METHOD_OTP, loginRequest.via)
                        }
                        sharedPref.setUUID(loginRequest.uuid)
                        sharedPref.setPhoneNumber(loginRequest.phone)
                        if (loginRequest.via == MethodSendOTP.WHATSAPP.value && !response?.otpLink.isNullOrEmpty())
                            intent.putExtra(EnterOTPActivity.OTP_LINK_KEY, response?.otpLink)
                        startActivity(intent)
                    }
                }
                launch {
                    viewModel.loginError.collectLatest { message ->
                        message.peek()?.let { info ->
                            binding.btnLogin.isEnabled = if (isLoginWithPw) stateMsidn && statePassword else stateMsidn
                            showModalDialog(info)
                        }
                    }
                }

                // COLLECT DATA UPDATE FCM
                launch {
                    viewModel.updateFcmLoading.collectLatest {
                        if (it) {
                            binding.btnLogin.showLoading()
                            return@collectLatest
                        }
                        binding.btnLogin.hideProgress(getString(R.string.login))
                    }
                }
                launch {
                    viewModel.updateFcm.collectLatest {
                        binding.btnLogin.isEnabled = if (isLoginWithPw) stateMsidn && statePassword else stateMsidn
                        if (it == true) navigateToMain()
                    }
                }
                launch {
                    viewModel.updateFcmError.collectLatest { message ->
                        message.peek()?.let {
                            binding.btnLogin.isEnabled = if (isLoginWithPw) stateMsidn && statePassword else stateMsidn
                            Toast.makeText(this@LoginActivity, getString(R.string.update_token_fcm_failed), Toast.LENGTH_SHORT).show()
                            viewModel.removeUpdateFCMError()
                            navigateToMain()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToMain() {
        finishAffinity()
        startActivity(Intent(this@LoginActivity, MainActivityNavComp::class.java))
    }

    private fun showModalDialog(message: String) {
        GenericModalDialogCashplus.Builder()
            .title(getString(R.string.something_wrong))
            .image(
                if ("""(?i)(email)""".toRegex().containsMatchIn(message)) R.drawable.illustration_login_error
                else R.drawable.illustration_error
            )
            .description(
                if ("""(?i)(email)""".toRegex().containsMatchIn(message))
                    getString(R.string.email_not_set_error)
                else message
            )
            .buttonNegative(
                NegativeButtonAction(
                    btnLabel =
                    if ("""(?i)(email)""".toRegex().containsMatchIn(message)) getString(R.string.try_via_whatsapp)
                    else getString(R.string.finish),
                    backgroundButton = if ("""(?i)(email)""".toRegex().containsMatchIn(message)) R.drawable.bg_green600_rounded_20
                    else R.drawable.bg_primary_rounded_20,
                    buttonTextColor = Color.parseColor("#FFFFFF"),
                    onBtnClicked = {
                        viewModel.removeLoginError()
                        if ("""(?i)(email)""".toRegex().containsMatchIn(message)) {
                            loginRequest = loginRequest.copy(via = MethodSendOTP.WHATSAPP.value)
                            viewModel.loginByOtp(loginRequest)
                        }
                        return@NegativeButtonAction
                    }
                )
            ).show(supportFragmentManager)
    }

    override fun resetPassword() {
        startActivity(Intent(this, ResetPasswordPublicActivity::class.java))
    }
}