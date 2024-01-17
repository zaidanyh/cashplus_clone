package com.pasukanlangit.cashplus.ui.register

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.messaging.FirebaseMessaging
import com.pasukanlangit.cashplus.MainActivityNavComp
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityRegisterOtpBinding
import com.pasukanlangit.cashplus.domain.model.LoginRequest
import com.pasukanlangit.cashplus.domain.model.RegisterRequest
import com.pasukanlangit.cashplus.domain.model.ResetPINRequest
import com.pasukanlangit.cashplus.domain.model.ResetPasswordRequest
import com.pasukanlangit.cashplus.model.request_body.*
import com.pasukanlangit.cashplus.ui.login.LoginActivity
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.login.OptionOTPFragment
import com.pasukanlangit.cashplus.ui.login.ResetPwViewModel
import com.pasukanlangit.cashplus.ui.pages.others.settings.pin.ChangePinWithoutLastViewModel
import com.pasukanlangit.cashplus.utils.MethodSendOTP
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.LoginViewModel
import com.pasukanlangit.cashplus.view_model.RegisterViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.InputUtil.toCapitalize
import com.pasukanlangit.id.core.utils.KeyboardUtil.hideSoftKeyboard
import com.pasukanlangit.id.core.utils.KeyboardUtil.openSoftKeyboard
import com.pasukanlangit.id.core.utils.TIME_SHOW_NOTIF
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.domain.model.NotifType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnterOTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterOtpBinding
    private val loginVM: LoginViewModel by viewModel()
    private val registerVM: RegisterViewModel by viewModel()
    private val resetPassVM: ResetPwViewModel by viewModel()
    private val resetPinVM: ChangePinWithoutLastViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private lateinit var countdownTimer: CountDownTimer
    private var login: LoginRequest? = null
    private var register: RegisterRequest? = null
    private var resetPw: ResetPasswordRequest? = null
    private var resetPIN: ResetPINRequest? = null

    private var stateCountDown: Boolean = true
    private var isLoggedIn: Boolean = false
    private var accessToken: String? = null

    private val optionOTP = OptionOTPFragment(
        via = { via ->
            openSoftKeyboard(this, binding.inputCodeWhatsapp)
            setMethodKey(via)
            onResendVerifyCode(via = via)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterOtpBinding.inflate(layoutInflater)
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

        login = intent.parcelable(KEY_LOGIN_REQUEST)
        register = intent.parcelable(KEY_REGISTER_REQUEST)
        resetPw = intent.parcelable(KEY_RESET_PW_REQUEST)
        resetPIN = intent.parcelable(KEY_RESET_PIN_REQUEST)
        isLoggedIn = intent.getBooleanExtra(KEY_IS_LOGGED_IN, false)
        accessToken = intent.getStringExtra(KEY_ACCESS_TOKEN_LOGGED_IN)
        val methodOtp = intent.getStringExtra(KEY_METHOD_OTP)
        val otpLink = intent.getStringExtra(OTP_LINK_KEY)

        initCountDownTimer()
        if (methodOtp == MethodSendOTP.EMAIL.value)
            Toast.makeText(this@EnterOTPActivity, getString(R.string.otp_via_email_sent), Toast.LENGTH_SHORT).show()
        setMethodKey(methodOtp)
        with(binding) {
            appBar.setOnBackPressed { onFinishActivity() }
            openSoftKeyboard(this@EnterOTPActivity, inputCodeWhatsapp)
            if (!otpLink.isNullOrEmpty()) {
                openWhatsApp(otpLink)
            }

            inputCodeWhatsapp.setOtpCompletionListener {
                when {
                    login != null -> {
                        login?.let { data ->
                            loginVM.loginOtpCode(
                                ResetPwCodeRequest(it, data.phone, data.uuid)
                            )
                        }
                    }
                    register != null -> {
                        register?.let { data ->
                            registerVM.registerOtpCode(
                                ResetPwCodeRequest(it, data.phoneNumber, data.uuid)
                            )
                        }
                    }
                    resetPw != null -> {
                        resetPw?.let { data ->
                            resetPassVM.resetPasswordCode(
                                ResetPwCodeRequest(it, data.phoneNumber, data.uuid)
                            )
                        }
                    }
                    resetPIN != null -> {
                        resetPIN?.let { data ->
                            resetPinVM.sendOtpChangePin(
                                ResetPinOtpCodeRequest(it, data.uuid),
                                accessToken ?: ""
                            )
                        }
                    }
                }
            }
            btnChangeMethodOtp.paintFlags = btnChangeMethodOtp.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            btnChangeMethodOtp.setOnClickListener {
                hideSoftKeyboard(this@EnterOTPActivity)
                optionOTP.show(supportFragmentManager, optionOTP.tag)
            }
            btnResendCode.setOnClickListener {
                onResendVerifyCode()
            }
        }

        collectData()
        observeRegisterOTP()
        observeResetPwOTP()
        observeResetPINOTP()
    }

    private fun initCountDownTimer() {
        countdownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val durationSecond = millisUntilFinished / 1000
                binding.btnResendCode.text = getString(
                    R.string.waiting_expired,
                    (durationSecond % 3600) / 60,
                    (durationSecond % 60)
                )
            }

            override fun onFinish() {
                stateCountDown = true
                binding.btnResendCode.text = getString(R.string.resend)
                binding.btnResendCode.isEnabled = stateCountDown
                binding.btnResendCode.setTextColor(Color.parseColor("#0A57FF"))
            }
        }
    }

    private fun setMethodKey(methodOtp: String?) {
        with(binding) {
            tvMethodOtp.text = methodOtp?.toCapitalize()
            when(methodOtp) {
                MethodSendOTP.EMAIL.value -> {
                    tvMethodOtp.setTextColor(Color.parseColor("#D81600"))
                    tvDescMethodOtp.text = getString(R.string.label_otp_via_email)
                }
                MethodSendOTP.WHATSAPP.value -> {
                    tvMethodOtp.setTextColor(Color.parseColor("#16A34A"))
                    tvDescMethodOtp.text = getString(R.string.label_warning_otp)
                }
                MethodSendOTP.SMS.value -> {
                    tvMethodOtp.setTextColor(Color.parseColor("#0F766E"))
                    tvDescMethodOtp.text = getString(R.string.label_warning_otp)
                }
            }
        }
    }

    private fun onResendVerifyCode(via: String? = null) {
        when {
            login != null -> {
                if (!via.isNullOrEmpty()) login = login?.copy(via = via)
                login?.let { loginVM.loginByOtp(it) }
            }
            register != null -> {
                if (!via.isNullOrEmpty()) register = register?.copy(via = via)
                register?.let { registerVM.register(it) }
            }
            resetPw != null -> {
                if (!via.isNullOrEmpty()) resetPw = resetPw?.copy(via = via)
                resetPw?.let { resetPassVM.resetPassword(it) }
            }
            resetPIN != null -> {
                if (!via.isNullOrEmpty()) resetPIN = resetPIN?.copy(via = via)
                resetPIN?.let { resetPinVM.resetPIN(it, accessToken ?: "") }
            }
        }
        stateCountDown = false
        binding.btnResendCode.isEnabled = stateCountDown
        binding.btnResendCode.setTextColor(Color.parseColor("#590A57FF"))

        countdownTimer.start()
        binding.inputCodeWhatsapp.text?.clear()
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    loginVM.otpLoading.collectLatest {
                        binding.loading.isVisible = it
                        stateCountDown = !it
                    }
                }
                launch {
                    loginVM.otpLogin.collectLatest { response ->
                        if (response != null) {
                            if (!response.x_access_token.isNullOrEmpty()) {
                                sharedPref.setUUID(login?.uuid ?: "")
                                sharedPref.setAccessToken(response.x_access_token)

                                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val token = task.result
                                        Log.d("FCM", token)

                                        loginVM.updateFcm(
                                            UpdateFirebaseTokenRequest(sharedPref.getUUID() ?: "", token),
                                            response.x_access_token
                                        )
                                        return@addOnCompleteListener
                                    }
                                }
                            }
                        }
                    }
                }
                launch {
                    loginVM.otpError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val menusAllFragment = ButtomSheetNotif(info, NotifType.NOTIF_ERROR)
                            menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                            delay(300)
                            loginVM.removeOtpVerifyError()
                            binding.inputCodeWhatsapp.text?.clear()
                        }
                    }
                }

                launch {
                    loginVM.updateFcmLoading.collectLatest {
                        binding.loading.isVisible = it
                        stateCountDown = !it
                    }
                }
                launch {
                    loginVM.updateFcm.collectLatest {
                        if (it == true) {
                            finishAffinity()
                            startActivity(Intent(this@EnterOTPActivity, MainActivityNavComp::class.java))
                        }
                    }
                }
                launch {
                    loginVM.updateFcmError.collectLatest { message ->
                        message.peek()?.let { info ->
                            showModalDialog(info)
                        }
                    }
                }

//                OTP Whatsapp 2 way
                launch {
                    registerVM.registerLoading.collectLatest { binding.loading.isVisible = it }
                }
                launch {
                    registerVM.register.collectLatest { response ->
                        if (response != null) {
                            if (!response.otpLink.isNullOrEmpty()) {
                                openWhatsApp(response.otpLink)
                                return@collectLatest
                            }
                            if (response.otpLink.isNullOrEmpty() && register?.via == MethodSendOTP.EMAIL.value) {
                                Toast.makeText(this@EnterOTPActivity, getString(R.string.otp_via_email_sent), Toast.LENGTH_SHORT).show()
                                return@collectLatest
                            }
                            Toast.makeText(this@EnterOTPActivity, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                launch {
                    loginVM.loginLoading.collectLatest { binding.loading.isVisible = it }
                }
                launch {
                    loginVM.loginByOtp.collectLatest { response ->
                        if (response != null) {
                            if (!response.otpLink.isNullOrEmpty()) {
                                openWhatsApp(response.otpLink)
                                return@collectLatest
                            }
                            if (response.otpLink.isNullOrEmpty() && login?.via == MethodSendOTP.EMAIL.value) {
                                Toast.makeText(this@EnterOTPActivity, getString(R.string.otp_via_email_sent), Toast.LENGTH_SHORT).show()
                                return@collectLatest
                            }
                            Toast.makeText(this@EnterOTPActivity, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                launch {
                    resetPassVM.resetPassLoading.collectLatest { binding.loading.isVisible = it }
                }
                launch {
                    resetPassVM.resetPass.collectLatest { response ->
                        if (response != null) {
                            if (!response.otpLink.isNullOrEmpty()) {
                                openWhatsApp(response.otpLink)
                                return@collectLatest
                            }
                            if (response.otpLink.isNullOrEmpty() && resetPw?.via == MethodSendOTP.EMAIL.value) {
                                Toast.makeText(this@EnterOTPActivity, getString(R.string.otp_via_email_sent), Toast.LENGTH_SHORT).show()
                                return@collectLatest
                            }
                            Toast.makeText(this@EnterOTPActivity, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                launch {
                    resetPinVM.resetPinLoading.collectLatest { binding.loading.isVisible = it }
                }
                launch {
                    resetPinVM.resetPIN.collectLatest { response ->
                        if (response != null) {
                            if (!response.otpLink.isNullOrEmpty()) {
                                openWhatsApp(response.otpLink)
                                return@collectLatest
                            }
                            if (response.otpLink.isNullOrEmpty() && resetPIN?.via == MethodSendOTP.EMAIL.value) {
                                Toast.makeText(this@EnterOTPActivity, getString(R.string.otp_via_email_sent), Toast.LENGTH_SHORT).show()
                                return@collectLatest
                            }
                            Toast.makeText(this@EnterOTPActivity, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun observeRegisterOTP() {
        registerVM.otpRegisterResponse.observe(this) { response ->
            when(response.status) {
                Status.LOADING -> {
                    stateCountDown = false
                    binding.loading.isVisible = true
                }
                Status.SUCCESS -> {
                    stateCountDown = true
                    binding.loading.isVisible = false

                    register?.let {
                        response.data?.let { data ->
                            if (!data.x_access_token.isNullOrEmpty()) {
                                sharedPref.setUUID(it.uuid)
                                sharedPref.setAccessToken(data.x_access_token)
                            }
                            val intent = Intent()
                            intent.putExtra(KEY_RESULT_OTP, arrayListOf(it.uuid, data.x_access_token))
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }
                }
                Status.ERROR -> {
                    stateCountDown = true
                    binding.loading.isVisible = false
                    binding.inputCodeWhatsapp.text?.clear()

                    val menusAllFragment = ButtomSheetNotif(response.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                }
            }
        }
    }

    private fun observeResetPwOTP() {
        resetPassVM.resetPwCode.observe(this){
            when (it.status) {
                Status.LOADING -> {
                    stateCountDown = false
                    binding.loading.isVisible = true
                }
                Status.SUCCESS -> {
                    stateCountDown = true
                    binding.loading.isVisible = false

                    if (it.data != null) {
                        val menusAllFragment = ButtomSheetNotif(
                            getString(R.string.password_change_successfully),
                            NotifType.NOTIF_SUCCESS
                        )
                        menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)

                        if (isLoggedIn) sharedPref.deleteAuth()
                        lifecycleScope.launch {
                            delay(TIME_SHOW_NOTIF)
                            finishAffinity()
                            startActivity(Intent(this@EnterOTPActivity, LoginActivity::class.java))
                        }
                    }
                }
                Status.ERROR -> {
                    stateCountDown = true
                    binding.loading.isVisible = false
                    binding.inputCodeWhatsapp.text?.clear()

                    val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                }
            }
        }
    }

    private fun observeResetPINOTP() {
        resetPinVM.resetPINOTP.observe(this) { response ->
            when(response.status) {
                Status.LOADING -> {
                    stateCountDown = false
                    binding.loading.isVisible = true
                }
                Status.SUCCESS -> {
                    stateCountDown = true
                    binding.loading.isVisible = false

                    val menusAllFragment = ButtomSheetNotif(getString(R.string.pin_change_successfully), NotifType.NOTIF_SUCCESS)
                    menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)

                    lifecycleScope.launch {
                        delay(TIME_SHOW_NOTIF)
                        finishAffinity()
                        val intent = Intent(this@EnterOTPActivity, MainActivityNavComp::class.java).apply {
                            putExtra(MainActivityNavComp.FORWARDING_TO_OTHER, true)
                        }
                        startActivity(intent)
                    }
                }
                Status.ERROR -> {
                    stateCountDown = true
                    binding.loading.isVisible = false
                    binding.inputCodeWhatsapp.text?.clear()

                    val menusAllFragment = ButtomSheetNotif(response.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                }
            }
        }
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
            .buttonPositive(
                PositiveButtonAction(
                    btnLabel =
                    if ("""(?i)(email)""".toRegex().containsMatchIn(message)) getString(R.string.try_via_whatsapp)
                    else getString(R.string.finish),
                    backgroundButton = if ("""(?i)(email)""".toRegex().containsMatchIn(message)) R.drawable.bg_green600_rounded_20
                    else R.drawable.bg_primary_rounded_20,
                    onBtnClicked = {
                        loginVM.removeLoginError()
                        if ("""(?i)(email)""".toRegex().containsMatchIn(message)) {
                            login = login?.copy(via = MethodSendOTP.WHATSAPP.value)
                            login?.let { loginVM.loginByOtp(it) }
                        }
                        return@PositiveButtonAction
                    }
                )
            )
            .show(supportFragmentManager)
    }

    private fun openWhatsApp(otpLink: String?) {
        if (isWhatsAppInstalled()) {
            lifecycleScope.launch {
                delay(300)
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(otpLink))
                )
            }
            return
        }
        Toast.makeText(this@EnterOTPActivity, getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT).show()
    }

    private fun isWhatsAppInstalled(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                packageManager.getPackageInfo("com.whatsapp", PackageManager.PackageInfoFlags.of(0))
            else @Suppress("DEPRECATION") packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun onFinishActivity() {
        if (!stateCountDown) return
        else finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Destroying", "Activity Enter OTP has been destroy")
    }

    companion object {
        const val KEY_LOGIN_REQUEST = "key_login_request"
        const val KEY_REGISTER_REQUEST = "key_register_request"
        const val KEY_RESET_PW_REQUEST = "key_reset_pw_request"
        const val KEY_RESET_PIN_REQUEST = "key_reset_pin_request"
        const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        const val KEY_ACCESS_TOKEN_LOGGED_IN = "key_access_token_logged_in"
        const val KEY_RESULT_OTP = "key_result_otp"
        const val KEY_METHOD_OTP = "key_method_otp"
        const val OTP_LINK_KEY = "otp_lnk_key"
    }
}