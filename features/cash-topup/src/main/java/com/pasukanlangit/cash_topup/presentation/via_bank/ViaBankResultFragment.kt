package com.pasukanlangit.cash_topup.presentation.via_bank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.FragmentViaBankResultBinding
import com.pasukanlangit.cash_topup.domain.model.TopUpViaBankResponse
import com.pasukanlangit.id.core.utils.CoreUtils.copyClipboard
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class ViaBankResultFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentViaBankResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViaBankResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nominal = arguments?.getString(NOMINAL_TOP_UP_KEY)
        val imgBank = arguments?.getInt(IMAGE_TOP_UP_VIA_BANK_KEY) ?: -1
        val bankResult = arguments?.parcelable<TopUpViaBankResponse>(TOP_UP_VIA_BANK_RESULT_KEY)

        with(binding) {
            if (imgBank == -1) ivPayment.isVisible = false
            else ivPayment.setImageResource(imgBank)

            tvNominalTopupConfirm.text = getNumberRupiah(nominal?.toIntOrNull())
            tvUnixCode.text = bankResult?.uniqueId
            tvTotalTopup.text = getNumberRupiah(bankResult?.amount?.toIntOrNull())
            tvRek.text = bankResult?.bankAccNum
            tvNameAn.text = getString(R.string.on_behalf_of_format, bankResult?.bankAccName)

            btnCopyNorek.setOnClickListener {
                copyClipboard(requireContext(), bankResult?.bankAccNum)
            }

            btnCopyTotal.setOnClickListener {
                copyClipboard(requireContext(), bankResult?.amount)
            }

            btnClose.setOnClickListener { dismiss() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val NOMINAL_TOP_UP_KEY = "nominal_top_up_key"
        private const val IMAGE_TOP_UP_VIA_BANK_KEY = "image_top_up_via_bank_key"
        private const val TOP_UP_VIA_BANK_RESULT_KEY = "top_up_via_bank_result_key"

        @JvmStatic
        fun newInstance(nominalTopUp: String, imgSrc: Int, viaBankResult: TopUpViaBankResponse?) =
            ViaBankResultFragment().apply {
                arguments = Bundle().apply {
                    putString(NOMINAL_TOP_UP_KEY, nominalTopUp)
                    putInt(IMAGE_TOP_UP_VIA_BANK_KEY, imgSrc)
                    putParcelable(TOP_UP_VIA_BANK_RESULT_KEY, viaBankResult)
                }
            }
    }
}