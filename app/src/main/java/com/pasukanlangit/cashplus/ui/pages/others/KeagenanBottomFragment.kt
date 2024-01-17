package com.pasukanlangit.cashplus.ui.pages.others

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentKeagenanBottomBinding
import com.pasukanlangit.id.rebate.presentation.RebateActivity
import com.pasukanlangit.id.ui_downline_home.DownLineActivity
import com.pasukanlangit.id.ui_downline_home.mintasaldoqr.ScanQRAgentActivity
import com.pasukanlangit.id.ui_downline_transfersaldo.TransferSaldoAgenActivity

class KeagenanBottomFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentKeagenanBottomBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentKeagenanBottomBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            iconClose.setOnClickListener {
                dismiss()
            }
            btnDownline.setOnClickListener {
                startActivity(Intent(requireContext(), DownLineActivity::class.java))
                dismiss()
            }
            btnTfSaldo.setOnClickListener {
                startActivity(Intent(requireContext(), TransferSaldoAgenActivity::class.java))
                dismiss()
            }
            btnRebate.setOnClickListener {
                startActivity(Intent(requireContext(), RebateActivity::class.java))
                dismiss()
            }
            btnMintaSaldo.setOnClickListener {
                startActivity(
                    Intent(requireContext(), DownLineActivity::class.java)
                        .putExtra(DownLineActivity.IS_ASKING_FOR_MONEY_KEY, true)
                )
                dismiss()
            }
            btnScanQr.setOnClickListener {
                startActivity(Intent(requireContext(), ScanQRAgentActivity::class.java))
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}