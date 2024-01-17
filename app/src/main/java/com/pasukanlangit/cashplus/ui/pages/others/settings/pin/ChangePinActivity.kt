package com.pasukanlangit.cashplus.ui.pages.others.settings.pin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.ActivityChangePinBinding
import com.pasukanlangit.cashplus.model.request_body.ChangePinRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.cashplus.utils.*
import com.pasukanlangit.cashplus.view_model.OtherChangePinViewModel
import com.pasukanlangit.id.core.utils.TIME_SHOW_NOTIF
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePinBinding
    private val otherChangePinViewModel : OtherChangePinViewModel by viewModel()
    private val sharedPrefDataSource : SharedPrefDataSource by inject()

    private var stateCurrPIN: Boolean = false
    private var stateNewPIN: Boolean = false
    private var stateConfPIN: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            appBar.setOnBackPressed { finish() }
            btnSavePin.setUpToProgressButton(this@ChangePinActivity)
            forgotCurrentPin.setOnClickListener {
                startActivity(Intent(this@ChangePinActivity, ChangePinWithoutLastPinActivity::class.java))
            }
            edtPrevPin.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if(input.isEmpty()){
                        inputPrevPin.error = getString(R.string.input_pin_currently_required)
                        stateCurrPIN = false
                        return
                    }
                    stateCurrPIN = true
                    inputPrevPin.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSavePin.isEnabled = stateCurrPIN && stateNewPIN && stateConfPIN
                }
            })
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
                    btnSavePin.isEnabled = stateCurrPIN && stateNewPIN && stateConfPIN
                }
            })
            edtConfirmNewPin.addTextChangedListener(object: TextWatcher {
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
                    btnSavePin.isEnabled = stateCurrPIN && stateNewPIN && stateConfPIN
                }
            })
            btnSavePin.setOnClickListener {
                btnSavePin.isEnabled = false
                val uuid = sharedPrefDataSource.getUUID()
                val token = sharedPrefDataSource.getAccessToken()

                if (uuid != null && token != null) {
                    val previousPin = edtPrevPin.text.toString().trim()
                    val newPin = edtNewPin.text.toString().trim()

                    val changePinRequest = ChangePinRequest(
                        uuid,
                        previousPin,
                        newPin
                    )
                    otherChangePinViewModel.changePin(changePinRequest, token)
                }
            }
        }

        observeChangePIN()
    }

    private fun observeChangePIN() {
        otherChangePinViewModel.user.observe(this@ChangePinActivity) {
            when (it.status) {
                Status.LOADING -> binding.btnSavePin.showLoading()
                Status.SUCCESS -> {
                    binding.btnSavePin.hideProgress(getString(R.string.save))
                    binding.btnSavePin.isEnabled = stateCurrPIN && stateNewPIN && stateConfPIN
                    if (it.data != null) {
                        val menusAllFragment =
                            ButtomSheetNotif(getString(R.string.change_pin_successfully), NotifType.NOTIF_SUCCESS)
                        menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)

                        lifecycleScope.launch {
                            delay(TIME_SHOW_NOTIF.div(2))
                            finish()
                        }
                    }
                }
                Status.ERROR -> {
                    binding.btnSavePin.hideProgress(getString(R.string.save))
                    binding.btnSavePin.isEnabled = stateCurrPIN && stateNewPIN && stateConfPIN

                    val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                }
            }
        }
    }
}