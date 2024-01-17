package com.pasukanlangit.cash_topup.presentation.via_va

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.FragmentViaVAResultBinding
import com.pasukanlangit.cash_topup.domain.model.FlipAcceptCreateBillResponse
import com.pasukanlangit.id.core.utils.CoreUtils.copyClipboard
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class ViaVAResultFlipAcceptFragment: BottomSheetDialogFragment() {

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

        val bankName = arguments?.getString(BANK_NAME_VIA_VA_KEY)
        val imgUrl = arguments?.getString(IMAGE_TOP_UP_VIA_VA_KEY)
        val vaResult = arguments?.parcelable<FlipAcceptCreateBillResponse>(TOP_UP_VIA_VA_RESULT_KEY)

        with(binding) {
            Glide.with(requireContext())
                .load(imgUrl)
                .into(ivPayment)
            txtTransfer.text = getString(R.string.transfer_va, bankName)
            vaResult?.let { result ->
                tvRek.text = result.vaNumber
                tvNominalTopupConfirm.text = getNumberRupiah(result.depositAmount.toIntOrNull())
                tvAdminCost.text = getNumberRupiah(result.cost.toIntOrNull())
                tvTotalTopup.text = getNumberRupiah(result.amount.toIntOrNull())

                btnCopyNorek.setOnClickListener {
                    copyClipboard(requireContext(), result.vaNumber)
                }

                btnCopyTotal.setOnClickListener {
                    copyClipboard(requireContext(), result.amount)
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

        private const val BANK_NAME_VIA_VA_KEY = "bank_name_via_va_key"
        private const val IMAGE_TOP_UP_VIA_VA_KEY = "image_top_up_via_va_key"
        private const val TOP_UP_VIA_VA_RESULT_KEY = "top_up_via_va_result_key"

        @JvmStatic
        fun newInstance(bankName: String?, imgUrl: String?, viaVAResult: FlipAcceptCreateBillResponse?) =
            ViaVAResultFlipAcceptFragment().apply {
                arguments = Bundle().apply {
                    putString(BANK_NAME_VIA_VA_KEY, bankName)
                    putString(IMAGE_TOP_UP_VIA_VA_KEY, imgUrl)
                    putParcelable(TOP_UP_VIA_VA_RESULT_KEY, viaVAResult)
                }
            }
    }
}