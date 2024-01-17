package com.pasukanlangit.id.ui_cashgold_buy.register

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.KYC_INIT_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.ui_cashgold_buy.R
import com.pasukanlangit.id.ui_cashgold_buy.databinding.ActivityRegisterCashGoldBinding
import com.pasukanlangit.id.ui_core.components.GenericCashGoldModalDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterCashGoldActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterCashGoldBinding
    private val viewModel: RegisterCashGoldViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterCashGoldBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            appbar.setIconBackPressed { finish() }
            btnRegister.setUpToProgressButton(this@RegisterCashGoldActivity)
            inputNpwp.onDoneIME { processRegister() }
            btnRegister.setOnClickListener { processRegister() }
        }

        collectState()
    }

    private fun processRegister() {
        with(binding){
            try {
                val name = inputUser.getValue()
                val phoneNumber = inputPhoneNumber.getValue()
                val email = inputEmail.getValue()
                val identityNumber = inputKtp.getValue()
                val taxNumber = inputNpwp.getValue().ifEmpty { null }

                viewModel.onTriggerEvent(RegisterCashGoldEvent.RegisterUser(
                    name = name,
                    phoneNumber = phoneNumber,
                    email = email,
                    identityNumber = identityNumber,
                    taxNumber = taxNumber
                ))
            }catch (e: Exception){
                Toast.makeText(this@RegisterCashGoldActivity, e.message ?: "Unkown Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun collectState() {
        collectLoadingStatus()
        collectErrorMessage()
        collectProfile()
        collectData()
    }

    private fun collectProfile() {
        lifecycleScope.launch {
            viewModel.profile.collectLatest {
                it?.let { profile ->
                    with(binding){
                        inputEmail.setValue(profile.email)
                        inputPhoneNumber.setValue(profile.phone)
                        inputUser.setValue(profile.fullName)
                    }
                }
            }
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            viewModel.isUpdated.collectLatest { isRegisteredAndUpdate ->
                isRegisteredAndUpdate?.let { isSuccess ->
                    if(isSuccess){
                        GenericCashGoldModalDialog.Builder()
                            .title("Sukses")
                            .description("Selamat pendaftaran anda sukses, silahkan lengkapi data KYC untuk menggunakan fitur kami")
                            .image(R.drawable.ilustration_warning)
                            .buttonPositive(
                                PositiveButtonAction(
                                    btnLabel = "KYC Upload",
                                    onBtnClicked = {
                                        startActivity(ModuleRoute.internalIntent(this@RegisterCashGoldActivity, KYC_INIT_PATH))
                                    }
                                )
                            )
                            .buttonNegative(
                                NegativeButtonAction(
                                    btnLabel = "Nanti Saja",
                                    onBtnClicked = {
                                        this@RegisterCashGoldActivity.finish()
                                    }
                                )
                            )
                            .show(supportFragmentManager)
                    }else {
                        Toast.makeText(this@RegisterCashGoldActivity, "Pendaftaran Gagal, Silahkan daftar ulang", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun collectErrorMessage() {
        lifecycleScope.launch {
            viewModel.stateError.collectLatest { errorMessage ->
                errorMessage.peek()?.let { info ->
                    val toast = Toast.makeText(this@RegisterCashGoldActivity, info, Toast.LENGTH_SHORT)
                    toast.show()
                    delay(toast.duration.toLong() + 500L)
                    viewModel.onTriggerEvent(RegisterCashGoldEvent.RemoveHeadQueue)
                }
            }
        }
    }

    private fun collectLoadingStatus() {
        lifecycleScope.launch {
            viewModel.isLoadingButton.collectLatest { isLoading ->
                binding.btnRegister.isEnabled = !isLoading
                if(isLoading){
                    binding.btnRegister.showLoading()
                }else{
                    binding.btnRegister.hideProgress(getString(R.string.register))
                }
            }
        }
    }
}