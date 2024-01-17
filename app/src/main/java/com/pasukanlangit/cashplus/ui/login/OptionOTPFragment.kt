package com.pasukanlangit.cashplus.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentOptionOTPBinding
import com.pasukanlangit.cashplus.utils.MethodSendOTP

class OptionOTPFragment(
    private val via: (String) -> Unit
): BottomSheetDialogFragment() {

    private var _binding: FragmentOptionOTPBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOptionOTPBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            wrapperViaWhatsapp.setOnClickListener {
                via(MethodSendOTP.WHATSAPP.value)
                this@OptionOTPFragment.dismiss()
            }
            wrapperViaEmail.setOnClickListener {
                via(MethodSendOTP.EMAIL.value)
                this@OptionOTPFragment.dismiss()
            }
            wrapperViaMessage.setOnClickListener {
                Toast.makeText(requireContext(), getString(R.string.method_message_not_available), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
//                via(MethodSendOTP.SMS.value)
//                this@OptionOTPFragment.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}