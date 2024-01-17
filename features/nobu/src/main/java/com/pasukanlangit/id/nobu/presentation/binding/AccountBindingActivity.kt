package com.pasukanlangit.id.nobu.presentation.binding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.ActivityAccountBindingBinding
import com.pasukanlangit.id.nobu.presentation.ConfirmBackDialogFragment
import com.pasukanlangit.id.nobu.presentation.StateEvent
import com.pasukanlangit.id.nobu.presentation.StateViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountBindingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBindingBinding
    private val viewModel: StateViewModel by viewModel()

    private var fullName: String? = null
    private var email: String? = null
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBindingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackDialog()
            }
        })

        viewModel.onTriggerEvent(StateEvent.FetchProfile)
        with(binding) {
            appBar.setOnBackPressed { onBackDialog() }
            btnSubmit.setUpToProgressButton(this@AccountBindingActivity)
            btnChange.setOnClickListener {
                ChangeDataFragment().show(supportFragmentManager, "Change data binding")
            }
            btnSubmit.setOnClickListener {
                viewModel.onTriggerEvent(StateEvent.AccountBinding(phoneNumber ?: "", email ?: "", fullName ?: ""))
            }
        }
        collectProfile()
        collectBindingResponse()
    }

    private fun collectProfile() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE DATA
                launch {
                    viewModel.profile.collectLatest { data ->
                        if (data != null) {
                            binding.tvPhoneNumber.text = data.phones?.first()?.phone
                            viewModel.onTriggerEvent(StateEvent.SetDataBinding(
                                data.phones?.first()?.phone.toString(),
                                data.email, data.full_name)
                            )
                        }
                    }
                }
                // STATE LOADING
                launch {
                    viewModel.stateLoading.collectLatest { binding.progressBar.isVisible = it }
                }
                // STATE ERROR
                launch {
                    viewModel.stateError.collectLatest { message ->
                        if (message.isNotEmpty()) {
                            message.peek()?.let { info ->
                                val toast =
                                    Toast.makeText(this@AccountBindingActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong() + 500L)
                                viewModel.onTriggerEvent(StateEvent.RemoveHeadMessage)
                            }
                        }
                    }
                }

                // STATE COLLECT DATA
                launch { viewModel.name.collectLatest { fullName = it } }
                launch {
                    viewModel.email.collectLatest {
                        if (it.isNullOrEmpty()) {
                            with(binding) {
                                wrapperWarning.isVisible = true
                                btnSubmit.isEnabled = false
                            }
                        } else {
                            with(binding) {
                                wrapperWarning.isVisible = false
                                btnSubmit.isEnabled = true
                            }
                            email = it
                        }
                    }
                }
                launch {
                    viewModel.phone.collectLatest {
                        phoneNumber = it
                        binding.tvPhoneNumber.text = it
                    }
                }
            }
        }
    }

    private fun collectBindingResponse() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE RESPONSE BINDING
                launch {
                    viewModel.accountBinding.collectLatest {
                        if (it != null) {
                            InputOTPBindingFragment.newInstance(
                                phoneNumber
                            ).show(supportFragmentManager, "Input Otp Binding")
                        }
                    }
                }
                // STATE IS LOADING
                launch {
                    viewModel.stateLoadingBinding.collectLatest { stateLoadingBind(it) }
                }

                // STATE ERROR
                launch {
                    viewModel.stateBindingError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@AccountBindingActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(StateEvent.RemoveBindingHeadMessage)
                        }
                    }
                }
            }
        }
    }

    private fun stateLoadingBind(isLoading: Boolean?) {
        with(binding.btnSubmit) {
            if (isLoading != null) {
                isEnabled = !isLoading
                if (isLoading) showLoading()
                else hideProgress(getString(R.string.continuing))
            }
        }
    }

    private fun onBackDialog() {
        ConfirmBackDialogFragment.newInstance(
            isAccountBinding = true
        ).show(supportFragmentManager, "Leave Binding")
    }
}