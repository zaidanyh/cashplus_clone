package com.pasukanlangit.cashplus.ui.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentSecondRegisterBinding
import com.pasukanlangit.cashplus.domain.model.RegisterRequest
import com.pasukanlangit.cashplus.ui.login.OptionOTPFragment
import com.pasukanlangit.cashplus.utils.MethodSendOTP
import com.pasukanlangit.cashplus.view_model.RegisterViewModel
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyWords
import com.pasukanlangit.id.core.utils.InputUtil.inputAlphabetFilter
import com.pasukanlangit.id.core.utils.InputUtil.isValidEmail
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SecondRegisterFragment : Fragment() {

    private var _binding: FragmentSecondRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by sharedViewModel()
    private val args: SecondRegisterFragmentArgs by navArgs()

    private var stateName: Boolean = false
    private var stateKeagenan: Boolean = false
    private var stateTermCond: Boolean = false
    private var stateEmail: Boolean = false

    private var registerRequest: RegisterRequest? = null

    private val optionOTP = OptionOTPFragment(
        via = { via ->
            registerRequest = registerRequest?.copy(via = via)
            registerRequest?.let { viewModel.register(it) }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnSendOtp.setUpToProgressButton(viewLifecycleOwner)
            btnSendOtp.setOnClickListener { onNextClicked() }
            edtNameReg.filters = arrayOf(InputFilter.LengthFilter(24), inputAlphabetFilter)
            edtNameReg.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString().trim()
                    if(input.isEmpty()){
                        inputNameReg.error = getString(R.string.input_required, getString(R.string.account_name))
                        stateName = false
                        return
                    }

                    if(!input.checkIsOnlyWords()) {
                        inputNameReg.error = getString(R.string.input_check_only_word_error, getString(R.string.account_name))
                        stateName = false
                        return
                    }
                    inputNameReg.error = null
                    stateName = true
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSendOtp.isEnabled = stateName && stateKeagenan && stateEmail && stateTermCond
                }
            })

            edtOwnerNameReg.filters = arrayOf(InputFilter.LengthFilter(32), inputAlphabetFilter)
            edtOwnerNameReg.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString().trim()
                    if(input.isEmpty()){
                        inputOwnerNameReg.isErrorEnabled = true
                        inputOwnerNameReg.error = getString(R.string.input_required, getString(R.string.name_owner))
                        stateKeagenan = false
                        return
                    }
                    if(!input.checkIsOnlyWords()) {
                        inputOwnerNameReg.isErrorEnabled = true
                        inputOwnerNameReg.error = getString(R.string.input_check_only_word_error, getString(R.string.owner_name))
                        stateKeagenan = false
                        return
                    }
                    inputOwnerNameReg.isErrorEnabled = false
                    inputOwnerNameReg.error = null
                    stateKeagenan = true
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSendOtp.isEnabled = stateName && stateKeagenan && stateEmail && stateTermCond
                }
            })

            edtEmailReg.filters = arrayOf(InputFilter.LengthFilter(64))
            edtEmailReg.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString().trim()
                    if (input.isEmpty()) {
                        inputEmailReg.error = getString(R.string.input_email_is_empty)
                        stateEmail = false
                        return
                    }
                    if(!input.isValidEmail()) {
                        inputEmailReg.error = getString(R.string.input_email_not_valid)
                        stateEmail = false
                        return
                    }
                    stateEmail = true
                    inputEmailReg.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSendOtp.isEnabled = stateName && stateKeagenan && stateEmail && stateTermCond
                }
            })

            checkboxTermReg.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    checkboxTermReg.error = getString(R.string.approve_term_condition)
                    stateTermCond = false
                } else {
                    stateTermCond = true
                    checkboxTermReg.error = null
                }
                btnSendOtp.isEnabled = stateName && stateKeagenan && stateEmail && stateTermCond
            }
        }
        collectDataRegister()
    }

    private fun onNextClicked() {
        with(binding) {
            val fullName = edtNameReg.text.toString().trim()
            val ownerName = edtOwnerNameReg.text.toString().trim()
            val email = edtEmailReg.text.toString().trim()

            registerRequest = args.firstToSecond.copy(
                fullName = fullName, email = email, ownerName = ownerName
            )
            optionOTP.show(requireActivity().supportFragmentManager, optionOTP.tag)
        }
    }

    private fun collectDataRegister() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.registerLoading.collectLatest {
                        with(binding.btnSendOtp) {
                            if (it) {
                                isEnabled = false
                                showLoading()
                                return@collectLatest
                            }
                            hideProgress(getString(R.string.send_otp))
                            isEnabled = stateName && stateKeagenan && stateEmail && stateTermCond
                        }
                    }
                }
                launch {
                    viewModel.register.collectLatest { response ->
                        if (response != null) {
                            val intent = Intent(activity, EnterOTPActivity::class.java).apply {
                                putExtra(EnterOTPActivity.KEY_REGISTER_REQUEST, registerRequest)
                                putExtra(EnterOTPActivity.KEY_METHOD_OTP, registerRequest?.via)
                            }
                            if (registerRequest?.via == MethodSendOTP.WHATSAPP.value && !response.otpLink.isNullOrEmpty())
                                intent.putExtra(EnterOTPActivity.OTP_LINK_KEY, response.otpLink)
                            (activity as RegisterActivity).startActivityToOTPActivity.launch(intent)
                        }
                    }
                }
                launch {
                    viewModel.registerError.collectLatest { message ->
                        message.peek()?.let { info ->
                            Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT).show()
//                            showModalError(info)
                            viewModel.removeRegisterError()
                        }
                    }
                }
            }
        }
    }

    private fun showModalError(message: String) {
        GenericModalDialogCashplus.Builder()
            .title(getString(R.string.something_wrong))
            .image(R.drawable.illustration_error)
            .description(message)
            .buttonPositive(
                if (message.contains("nomor rujukan", ignoreCase = true) || message.contains("referral", ignoreCase = true))
                    PositiveButtonAction(
                        btnLabel = getString(R.string.change_referral_code),
                        backgroundButton = R.drawable.bg_emerald600_rounded_12,
                        buttonTextColor = Color.parseColor("#F8FAFC"),
                        onBtnClicked = {
                            (activity as RegisterActivity).wrapperFirstOnClick()
                        }
                    )
                else null
            )
            .buttonNegative(
                NegativeButtonAction(
                    btnLabel = getString(R.string.close),
                    backgroundButton = R.drawable.bg_blue50_rounded_12,
                    buttonTextColor = Color.parseColor("#1570EF"),
                    setClickOnDismiss = true
                )
            )
            .show(childFragmentManager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}