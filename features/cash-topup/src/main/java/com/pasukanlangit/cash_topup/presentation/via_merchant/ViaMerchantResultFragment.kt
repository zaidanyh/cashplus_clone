package com.pasukanlangit.cash_topup.presentation.via_merchant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.FragmentViaVAResultBinding
import com.pasukanlangit.cash_topup.domain.model.TopUpViaVAResult
import com.pasukanlangit.cash_topup.utils.MenuViaTopUpPayment
import com.pasukanlangit.id.core.utils.CoreUtils.copyClipboard
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class ViaMerchantResultFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentViaVAResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViaVAResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nominal = arguments?.getString(NOMINAL_TOP_UP_KEY)
        val menuTopUp = arguments?.parcelable<MenuViaTopUpPayment>(REQUEST_TOP_UP_VIA_MERCHANT_KEY)
        val vaResult = arguments?.parcelable<TopUpViaVAResult>(TOP_UP_VIA_MERCHANT_RESULT_KEY)
        with(binding) {
            menuTopUp?.let { menu ->
                if (menu.img == -1) ivPayment.isVisible = false
                else ivPayment.setImageResource(menu.img)
                txtTransfer.text = getString(R.string.transfer_va, menu.payName)

                if (menu.payName == "Alfamart") txtTransfer.text = getString(R.string.pay_code_alfa)

                tvNominalTopupConfirm.text = getNumberRupiah(nominal?.toIntOrNull())
                tvAdminCost.text= getNumberRupiah(vaResult?.adminCost?.toIntOrNull())
                val totalTransfer = nominal?.toIntOrNull()?.plus(vaResult?.adminCost?.toIntOrNull() ?: 0)
                tvTotalTopup.text = getNumberRupiah(totalTransfer)
                tvRek.text = vaResult?.vaNumber

                btnCopyNorek.setOnClickListener {
                    copyClipboard(requireContext(), vaResult?.vaNumber)
                }

                btnCopyTotal.setOnClickListener {
                    copyClipboard(requireContext(), totalTransfer.toString())
                }
                btnClose.setOnClickListener { dismiss() }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val NOMINAL_TOP_UP_KEY = "nominal_top_up_key"
        private const val REQUEST_TOP_UP_VIA_MERCHANT_KEY = "request_top_up_via_merchant_key"
        private const val TOP_UP_VIA_MERCHANT_RESULT_KEY = "top_up_via_merchant_result_key"

        @JvmStatic
        fun newInstance(nominalTopUp: String, menuTopUp: MenuViaTopUpPayment, viaVAResult: TopUpViaVAResult?) =
            ViaMerchantResultFragment().apply {
                arguments = Bundle().apply {
                    putString(NOMINAL_TOP_UP_KEY, nominalTopUp)
                    putParcelable(REQUEST_TOP_UP_VIA_MERCHANT_KEY, menuTopUp)
                    putParcelable(TOP_UP_VIA_MERCHANT_RESULT_KEY, viaVAResult)
                }
            }
    }
}