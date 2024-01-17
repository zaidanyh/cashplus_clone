package com.pasukanlangit.id.nobu.presentation.creation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.nobu.databinding.FragmentOTPActivationBinding
import com.pasukanlangit.id.nobu.presentation.WebviewNobuActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class OTPActivationFragment : Fragment() {

    private var _binding: FragmentOTPActivationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ActivationViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOTPActivationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as QrisActivationActivity).finishing()
        with(binding) {
            KeyboardUtil.openSoftKeyboard(requireContext(), inputOtp)
            inputOtp.setOtpCompletionListener {
                KeyboardUtil.closeKeyboardDialog(requireContext(), inputOtp)
                viewModel.verifyOtp(it)
                collectVerifyOtp()
            }
        }
    }

    private fun collectVerifyOtp() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE LOADING
                launch {
                    viewModel.stateLoading.collectLatest {
                        binding.progressBar.isVisible = it
                    }
                }
                // STATE VERIFY OTP
                launch {
                    viewModel.verifyOtp.collectLatest { response ->
                        if (response != null) {
                            val intent = Intent(requireActivity(), WebviewNobuActivity::class.java).apply {
                                putExtra(WebviewNobuActivity.URL_WEBVIEW_KEY, response.qParamsURL)
                            }
                            activity?.startActivity(intent)
                            activity?.finish()
                        }
                    }
                }
                //STATE ERROR
                launch {
                    viewModel.stateError.collectLatest { message ->
                        if (message.isNotEmpty()) {
                            binding.inputOtp.text?.clear()
                            message.peek()?.let { info ->
                                val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong() + 500L)
                                viewModel.removeHeadQueue()
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
}