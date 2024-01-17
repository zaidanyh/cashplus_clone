package com.pasukanlangit.id.kyc_presentation.pages.init

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pasukanlangit.id.kyc_presentation.databinding.LayoutKycStartedBinding
import com.pasukanlangit.id.kyc_presentation.pages.completing.CompletingKYCActivity

class EKycStartedFragment : Fragment() {

    private var _binding: LayoutKycStartedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutKycStartedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCompletingData.setOnClickListener {
            startActivity(Intent(requireContext(), CompletingKYCActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}