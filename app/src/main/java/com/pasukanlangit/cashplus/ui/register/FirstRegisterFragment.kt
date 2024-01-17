package com.pasukanlangit.cashplus.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentFirstRegisterBinding
import com.pasukanlangit.cashplus.domain.model.RegisterRequest
import com.pasukanlangit.cashplus.utils.MyUtils.createTransactionID
import com.pasukanlangit.cashplus.view_model.RegisterViewModel
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyNumber
import com.pasukanlangit.id.core.utils.InputUtil.validPrefixNumber
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FirstRegisterFragment : Fragment() {

    private var _binding: FragmentFirstRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by sharedViewModel()

    private var stateMsidn: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnNext.setOnClickListener { onNextClicked() }
            edtMsidnReg.filters = arrayOf(InputFilter.LengthFilter(16))
            edtMsidnReg.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputMsidnReg.error = getString(R.string.input_phone_number_first)
                        stateMsidn = false
                        return
                    }
                    if (input.length < 9) {
                        inputMsidnReg.error = getString(R.string.input_min_length_required, getString(R.string.phone_number), 9)
                        stateMsidn = false
                        return
                    }
                    if (!input.checkIsOnlyNumber()) {
                        inputMsidnReg.error = getString(R.string.number_or_code_not_valid)
                        stateMsidn = false
                        return
                    }

                    stateMsidn = true
                    inputMsidnReg.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnNext.isEnabled = stateMsidn
                }
            })
            edtReferral.filters = arrayOf(InputFilter.LengthFilter(16))
            edtReferral.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    txtMakeSure.isVisible = input.isNotEmpty()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun onNextClicked() {
        with(binding) {
            val msidn = edtMsidnReg.text.toString().trim()
            val uuid = createTransactionID()
            val referralCode = edtReferral.text.toString().trim()

            if (!msidn.validPrefixNumber()) {
                inputMsidnReg.error = getString(R.string.any_input_is_not_valid, getString(R.string.phone_number))
                return
            }
            val registerRequest = RegisterRequest(
                uuid = uuid, phoneNumber = msidn, fullName = "",
                email = "", referral = referralCode, ownerName = "",
                via = ""
            )
            viewModel.setStateFirst(true)
            findNavController().navigate(FirstRegisterFragmentDirections.actionFirstToSecond(registerRequest))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}