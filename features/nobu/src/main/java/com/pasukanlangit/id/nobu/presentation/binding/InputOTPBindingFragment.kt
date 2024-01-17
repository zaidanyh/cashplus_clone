package com.pasukanlangit.id.nobu.presentation.binding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.FragmentInputOTPBindingBinding
import com.pasukanlangit.id.nobu.presentation.StateEvent
import com.pasukanlangit.id.nobu.presentation.StateViewModel
import com.pasukanlangit.id.nobu.presentation.WebviewNobuActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class InputOTPBindingFragment : DialogFragment() {

    private var _binding: FragmentInputOTPBindingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StateViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.fullScreenEnterPINOrOTP)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputOTPBindingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val phoneNumber = arguments?.getString(ARG_PHONE_NUMBER_KEY)
        with(binding) {
            txtDesc.text = getString(R.string.send_otp_desc, phoneNumber)
            KeyboardUtil.openSoftKeyboard(requireContext(), inputOtp)
            inputOtp.setOtpCompletionListener {
                KeyboardUtil.closeKeyboardDialog(requireContext(), inputOtp)
                viewModel.onTriggerEvent(StateEvent.VerifyOtp(it))
                collectVerifyOtp()
            }
        }
    }

    private fun collectVerifyOtp() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.verifyOtp.collectLatest { response ->
                        if (response != null) {
                            val intent = Intent(requireActivity(), WebviewNobuActivity::class.java).apply {
                                putExtra(WebviewNobuActivity.URL_WEBVIEW_KEY, response.qParamsURL)
                            }
                            this@InputOTPBindingFragment.dismiss()
                            activity?.startActivity(intent)
                            activity?.finish()
                        }
                    }
                }

                launch {
                    viewModel.stateLoading.collectLatest { binding.progressBar.isVisible = it }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        if (message.isNotEmpty()) {
                            binding.inputOtp.text?.clear()
                            message.peek()?.let { info ->
                                val toast =
                                    Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong() + 500L)
                                viewModel.onTriggerEvent(StateEvent.RemoveHeadMessage)
                                this@InputOTPBindingFragment.dismiss()
                            }
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
        private const val ARG_PHONE_NUMBER_KEY = "arg_phone_number_key"

        @JvmStatic
        fun newInstance(phoneNumber: String?) =
            InputOTPBindingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PHONE_NUMBER_KEY, phoneNumber)
                }
            }
    }
}