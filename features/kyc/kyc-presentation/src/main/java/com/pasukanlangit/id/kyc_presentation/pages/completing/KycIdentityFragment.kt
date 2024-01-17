package com.pasukanlangit.id.kyc_presentation.pages.completing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pasukanlangit.id.kyc_presentation.ImagePickerType
import com.pasukanlangit.id.kyc_presentation.databinding.FragmentKycIdentityBinding
import com.pasukanlangit.id.kyc_presentation.imagePickerIDIntent

class KycIdentityFragment : Fragment() {

    private var _binding: FragmentKycIdentityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKycIdentityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnTakePhoto.setOnClickListener {
                val intent = imagePickerIDIntent(requireActivity(), ImagePickerType.CAMERA_ONLY)
                (activity as CompletingKYCActivity).takePhotoIdCard.launch(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}