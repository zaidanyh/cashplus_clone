package com.pasukanlangit.id.kyc_presentation.pages.completing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pasukanlangit.id.kyc_presentation.ImagePickerType
import com.pasukanlangit.id.kyc_presentation.databinding.FragmentKycSelfieBinding
import com.pasukanlangit.id.kyc_presentation.imagePickerSelfieIntent

class KycSelfieFragment : Fragment() {

    private var _binding: FragmentKycSelfieBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKycSelfieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTakePhoto.setOnClickListener {
            val intent = imagePickerSelfieIntent(requireActivity(), ImagePickerType.CAMERA_ONLY)
            (activity as CompletingKYCActivity).takePhotoSelfie.launch(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}