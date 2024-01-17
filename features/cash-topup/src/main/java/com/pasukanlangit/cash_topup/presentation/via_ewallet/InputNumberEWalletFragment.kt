package com.pasukanlangit.cash_topup.presentation.via_ewallet

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
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.FragmentInputNumberEWalletBinding
import com.pasukanlangit.cash_topup.utils.MenuViaTopUpPayment
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyNumber
import com.pasukanlangit.id.core.utils.InputUtil.validPrefixNumber
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class InputNumberEWalletFragment : Fragment() {

    private var _binding: FragmentInputNumberEWalletBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViaEWalletViewModel by sharedViewModel()

    private var stateButton = false
    private var amount: String? = null
    private var menuTopUp: MenuViaTopUpPayment? = null
    private var billingPhone: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputNumberEWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amount = arguments?.getString(AMOUNT_TOP_UP_VALUE_KEY)
        menuTopUp = arguments?.parcelable(REQUEST_TOP_UP_VIA_E_WALLET_KEY)

        with(binding) {
            menuTopUp?.img?.let { imgEWallet.setImageResource(it) }
            edtInputNumber.filters = arrayOf(InputFilter.LengthFilter(16))
            edtInputNumber.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString()
                    if (value.isEmpty()) {
                        inputPhoneNumber.isErrorEnabled = true
                        inputPhoneNumber.error = getString(R.string.input_phone_number_first)
                        stateButton = false
                        return
                    }
                    if (value.length < 9) {
                        inputPhoneNumber.isErrorEnabled = true
                        inputPhoneNumber.error = getString(R.string.input_min_length_required, getString(R.string.phone_number), 9)
                        stateButton = false
                        return
                    }
                    if (!value.checkIsOnlyNumber()) {
                        inputPhoneNumber.isErrorEnabled = true
                        inputPhoneNumber.error = getString(R.string.number_or_code_not_valid)
                        stateButton = false
                        return
                    }
                    inputPhoneNumber.isErrorEnabled = false
                    inputPhoneNumber.error = null
                    stateButton = true
                }
                override fun afterTextChanged(s: Editable?) {
                    btnNext.isEnabled = stateButton
                }
            })
            btnNext.setUpToProgressButton(viewLifecycleOwner)
            btnNext.setOnClickListener {
                billingPhone = edtInputNumber.text.toString().trim()
                if (billingPhone?.validPrefixNumber() == false) {
                    inputPhoneNumber.isErrorEnabled = true
                    inputPhoneNumber.error = getString(R.string.any_input_is_not_valid, getString(R.string.phone_number))
                    stateButton = false
                    btnNext.isEnabled = stateButton
                    return@setOnClickListener
                }
                viewModel.onTriggerEvent(
                    ViaEWalletEvent.GetCostNicePay(
                        bankMitraCode = menuTopUp?.bankCode.toString(), payMethod = menuTopUp?.payMethodCode.toString(),
                        amount = amount.toString()
                    )
                )
            }
        }
        collectDataCostNicePay()
    }

    private fun collectDataCostNicePay() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getCostNicePayLoading.collectLatest {
                        if (it) {
                            binding.btnNext.isEnabled = false
                            binding.btnNext.showLoading()
                            return@collectLatest
                        }
                        binding.btnNext.hideProgress(getString(R.string.next))
                    }
                }
                launch {
                    viewModel.getCostNicePay.collectLatest { response ->
                        if (response != null) {
                            binding.btnNext.isEnabled = true
                            viewModel.onTriggerEvent(
                                ViaEWalletEvent.SetAmountAndAdminCost(
                                    amount = amount.toString(), adminCost = response.cost,
                                    billingPhone = billingPhone.toString()
                                )
                            )
                            menuTopUp?.let {
                                findNavController().navigate(InputNumberEWalletFragmentDirections.actionInputToDetailTopUp(it))
                            }
                        }
                    }
                }
                launch {
                    viewModel.getCostNicePayError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) binding.btnNext.isEnabled = true
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(ViaEWalletEvent.RemoveGetCostNicePayError)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val AMOUNT_TOP_UP_VALUE_KEY = "amount_top_value_key"
        const val REQUEST_TOP_UP_VIA_E_WALLET_KEY = "request_top_up_via_e_wallet_key"
    }
}