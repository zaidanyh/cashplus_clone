package com.pasukanlangit.id.kyc_presentation.pages.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pasukanlangit.id.core.MAIN_ACTIVITY_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.kyc_presentation.databinding.LayoutKycApprovedBinding

class EKycApprovedFragment: Fragment() {

    private var _binding: LayoutKycApprovedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutKycApprovedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackToHome.setOnClickListener {
            activity?.finishAffinity()
            activity?.startActivity(
                ModuleRoute.internalIntent(requireContext(), MAIN_ACTIVITY_PATH)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}