package com.pasukanlangit.cashplus.ui.pages.others.settings.password

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.ActivityChangePasswordBinding
import com.pasukanlangit.cashplus.model.request_body.ChangePasswordRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.utils.InputUtil.passwordValidate
import com.pasukanlangit.id.core.utils.TIME_SHOW_NOTIF
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel: ChangePassViewModel by viewModel()
    private val sharedPrefDataSource : SharedPrefDataSource by inject()

    private var stateCurrPw: Boolean = false
    private var stateNewPw: Boolean = false
    private var stateConfPw: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phoneNumber = intent.getStringExtra(KEY_PHONE_NUMBER)

        with(binding){
            appBar.setOnBackPressed { finish() }
            forgotCurrentPass.setOnClickListener {
                startActivity(
                    Intent(this@ChangePasswordActivity, ResetPasswordActivity::class.java)
                        .apply {
                            putExtra(ResetPasswordActivity.KEY_PHONE_NUMBER, phoneNumber)
                        }
                )
            }

            btnSavePin.setUpToProgressButton(this@ChangePasswordActivity)
            btnSavePin.setOnClickListener {
                val uuid = sharedPrefDataSource.getUUID()
                val token = sharedPrefDataSource.getAccessToken()

                if (uuid != null && token != null) {
                    val previousPass = edtPrevPass.text.toString().trim()
                    val newPass = edtNewPass.text.toString().trim()

                    val request = ChangePasswordRequest(
                        newPass = newPass,
                        uuid = uuid,
                        oldPass = previousPass
                    )
                    viewModel.changePassword(request, token)
                }
            }

            edtPrevPass.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if(input.isEmpty()){
                        inputPrevPass.error = "Sandi lama perlu diisi"
                        stateCurrPw = false
                        return
                    }

                    stateCurrPw = true
                    inputPrevPass.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    binding.btnSavePin.isEnabled = stateCurrPw && stateNewPw && stateConfPw
                }
            })

            edtNewPass.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    val validatePassword = passwordValidate(this@ChangePasswordActivity, input)

                    if(input.isEmpty()){
                        inputNewPass.error = "Sandi baru perlu diisi"
                        stateNewPw = false
                        return
                    }
                    if(validatePassword.isError){
                        inputNewPass.error = validatePassword.message
                        stateNewPw = false
                        return
                    }

                    stateNewPw = true
                    inputNewPass.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    binding.btnSavePin.isEnabled = stateCurrPw && stateNewPw && stateConfPw
                }
            })

            edtConfirmNewPass.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    val password = edtNewPass.text.toString()
                    if(input.isEmpty()){
                        inputConfirmNewPass.error = "Konfirmasi sandi perlu diisi"
                        stateConfPw = false
                        return
                    }
                    if(password != input){
                        inputConfirmNewPass.error = "Kata sandi tidak sama"
                        stateConfPw = false
                        return
                    }

                    stateConfPw = true
                    inputConfirmNewPass.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    binding.btnSavePin.isEnabled = stateCurrPw && stateNewPw && stateConfPw
                }
            })
        }

        observePasswordChange()
    }

    private fun observePasswordChange() {
        viewModel.pass.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.btnSavePin.hideProgress("Simpan")
                    if (it.data != null) {
                        val menusAllFragment =
                            ButtomSheetNotif("Password berhasil diubah", NotifType.NOTIF_SUCCESS)
                        menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)

                        lifecycleScope.launch {
                            delay(TIME_SHOW_NOTIF.div(2))
                            finish()
                        }
                    }
                }
                Status.LOADING -> {
                    binding.btnSavePin.showLoading()
                }
                Status.ERROR -> {
                    val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                    binding.btnSavePin.hideProgress("Simpan")
                }
            }
        }
    }

    companion object {
        const val KEY_PHONE_NUMBER = "KEY_PHONE_NUMBER"
    }

}