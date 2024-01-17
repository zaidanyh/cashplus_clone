package com.pasukanlangit.id.kyc_presentation.pages.completing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pasukanlangit.id.core.MAIN_ACTIVITY_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.kyc_presentation.databinding.FragmentKycDoneBinding

class KycDoneFragment : Fragment() {

    private var _binding: FragmentKycDoneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKycDoneBinding.inflate(inflater, container, false)
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