package com.pasukanlangit.id.kyc_presentation.pages.init

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pasukanlangit.id.kyc_presentation.R
import com.pasukanlangit.id.kyc_presentation.databinding.LayoutKycRejectedBinding
import com.pasukanlangit.id.kyc_presentation.pages.completing.CompletingKYCActivity

class EKycRejectedFragment: Fragment() {

    private var _binding: LayoutKycRejectedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutKycRejectedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnRepeatVerify.setOnClickListener {
                startActivity(Intent(requireContext(), CompletingKYCActivity::class.java))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}