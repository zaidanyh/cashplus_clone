package com.pasukanlangit.cashplus.ui.pages.others

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentChooseRecapitulationBinding
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.RECAPITULATION_DEPOSIT_PATH
import com.pasukanlangit.id.core.RECAPITULATION_PROFIT_PATH

class ChooseRecapitulationFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChooseRecapitulationBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseRecapitulationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            wrapperRecapTrans.setOnClickListener {
                activity?.startActivity(
                    ModuleRoute.internalIntent(requireContext(), RECAPITULATION_PROFIT_PATH)
                )
                dismiss()
            }
            wrapperRecapDeposit.setOnClickListener {
                activity?.startActivity(
                    ModuleRoute.internalIntent(requireContext(), RECAPITULATION_DEPOSIT_PATH)
                )
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}