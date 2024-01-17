package com.pasukanlangit.cashplus.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentThirdRegisterBinding
import com.pasukanlangit.cashplus.model.request_body.ChangePinRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.RegisterViewModel
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.domain.model.NotifType
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ThirdRegisterFragment : Fragment() {

    private var _binding: FragmentThirdRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by sharedViewModel()
    private val args: ThirdRegisterFragmentArgs by navArgs()

    private var stateSetPIN: Boolean = false
    private var stateConfirmPIN: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThirdRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as RegisterActivity).finishing()
        with(binding) {
            btnSave.setUpToProgressButton(viewLifecycleOwner)
            btnSave.setOnClickListener { onSavePIN() }
            edtSetPin.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputSetPin.error = getString(R.string.input_required, getString(R.string.pin))
                        stateSetPIN = false
                        return
                    }
                    if (input.length < 6) {
                        inputSetPin.error = getString(R.string.input_pin_min_length)
                        stateSetPIN = false
                        return
                    }

                    stateSetPIN = true
                    inputSetPin.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    binding.btnSave.isEnabled = stateSetPIN && stateConfirmPIN
                }
            })

            edtConfirmPin.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    val setPIN = edtSetPin.text.toString().trim()
                    if (input.isEmpty()) {
                        inputConfirmPin.error = getString(R.string.input_required, getString(R.string.confirm_new_pin))
                        stateConfirmPIN = false
                        return
                    }
                    if (setPIN != input) {
                        inputConfirmPin.error = getString(R.string.input_confirm_new_pin_not_same)
                        stateConfirmPIN = false
                        return
                    }

                    stateConfirmPIN = true
                    inputConfirmPin.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    binding.btnSave.isEnabled = stateSetPIN && stateConfirmPIN
                }
            })
        }

        observeChangePIN()
    }

    private fun onSavePIN() {
        val newPIN = binding.edtSetPin.text.toString().trim()

        if (args.secondToThird.isNotEmpty()) {
            val changePinRequest = ChangePinRequest(args.secondToThird.first(), "123456", newPIN)
            viewModel.createPIN(changePinRequest, args.secondToThird.last())
        }
    }

    private fun observeChangePIN() {
        viewModel.createPIN.observe(viewLifecycleOwner) { response ->
            when(response.status) {
                Status.LOADING -> binding.btnSave.showLoading()
                Status.SUCCESS -> {
                    binding.btnSave.hideProgress(context?.getString(R.string.simpan))

                    viewModel.setStateThird(true)
                    findNavController().navigate(ThirdRegisterFragmentDirections.actionThirdToFourth())
                }
                Status.ERROR -> {
                    binding.btnSave.hideProgress(context?.getString(R.string.simpan))

                    val menusAllFragment = ButtomSheetNotif(response.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(requireActivity().supportFragmentManager, menusAllFragment.tag)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}