package com.pasukanlangit.cashplus.ui.pages.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentChooseTopUpBinding
import com.pasukanlangit.id.core.CASHPLUS_BALANCE_TOP_UP
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.STATE_REQUEST_FLIP_ACCEPT_KEY

class ChooseTopUpFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChooseTopUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseTopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stateIsBinded = arguments?.getBoolean(ARG_STATE_IS_BINDED)
        val stateIsFlipAccept = arguments?.getBoolean(ARG_STATE_IS_FLIP_ACCEPT) ?: false
        val balance = arguments?.getDouble(ARG_BALANCE_CASHPLUS)

        with(binding) {
            icClose.setOnClickListener { this@ChooseTopUpFragment.dismiss() }
            btnTopUpCashplus.setOnClickListener {
                startActivity(
                    ModuleRoute.internalIntent(requireContext(), CASHPLUS_BALANCE_TOP_UP).apply {
                        putExtra(STATE_REQUEST_FLIP_ACCEPT_KEY, stateIsFlipAccept)
                    }
                )
                this@ChooseTopUpFragment.dismiss()
            }
            btnTopUpQris.setOnClickListener {
                if (stateIsBinded == true) {
                    TopUpQrisFragment.newInstance(balance!!)
                        .show(requireActivity().supportFragmentManager, "Top Up QRIS")
                    this@ChooseTopUpFragment.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Akun anda belum terhubung ke nobu, silahkan lakukan penghubungan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_STATE_IS_BINDED = "arg_state_is_binded"
        private const val ARG_STATE_IS_FLIP_ACCEPT = "arg_state_is_flip_accept"
        private const val ARG_BALANCE_CASHPLUS = "arg_balance_cashplus"

        @JvmStatic
        fun newInstance(isBinded: Boolean = false, isFlipAccept: Boolean, balance: Double) =
            ChooseTopUpFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_STATE_IS_BINDED, isBinded)
                    putBoolean(ARG_STATE_IS_FLIP_ACCEPT, isFlipAccept)
                    putDouble(ARG_BALANCE_CASHPLUS, balance)
                }
            }
    }
}