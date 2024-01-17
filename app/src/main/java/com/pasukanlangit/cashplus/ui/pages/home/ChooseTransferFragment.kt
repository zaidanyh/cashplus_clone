package com.pasukanlangit.cashplus.ui.pages.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentChooseTransferBinding
import com.pasukanlangit.id.core.GLOBAL_TRANSFER_PATH
import com.pasukanlangit.id.core.LOCAL_TRANSFER_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.utils.CoreUtils.showCoreComingSoon

class ChooseTransferFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChooseTransferBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isDisabled = arguments?.getBoolean(ARG_DISABLED_FOREIGN_BANK_KEY, false)
        with(binding) {
            layoutLocalBank.setOnClickListener {
                startActivity(
                    ModuleRoute.internalIntent(requireActivity(), LOCAL_TRANSFER_PATH)
                )
                this@ChooseTransferFragment.dismiss()
            }
            layoutGlobalBank.setOnClickListener {
                if (isDisabled == true) {
                    showCoreComingSoon(requireContext(), childFragmentManager)
                    return@setOnClickListener
                }
                startActivity(
                    ModuleRoute.internalIntent(requireActivity(), GLOBAL_TRANSFER_PATH)
                )
                this@ChooseTransferFragment.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_DISABLED_FOREIGN_BANK_KEY = "arg_disabled_foreign_bank_key"

        @JvmStatic
        fun newInstance(state: Boolean) =
            ChooseTransferFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_DISABLED_FOREIGN_BANK_KEY, state)
                }
            }
    }
}