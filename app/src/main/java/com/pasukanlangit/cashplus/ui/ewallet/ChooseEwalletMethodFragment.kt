package com.pasukanlangit.cashplus.ui.ewallet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentChooseEwalletMethodBinding
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.parcelable

class ChooseEwalletMethodFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChooseEwalletMethodBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseEwalletMethodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = arguments?.parcelable<ProductModel>(PRODUCT_PURCHASES_KEY)
        val providerCode = arguments?.getString(PROVIDER_CODE_KEY)
        with(binding) {
            iconClose.setOnClickListener { dismiss() }
            wrapperChoosePackage.setOnClickListener {
                startActivity(
                    Intent(requireContext(), EWalletActivity::class.java).apply {
                        putExtra(EWalletActivity.KEY_PROVIDER_CODE, providerCode)
                    }
                )
                dismiss()
            }
            wrapperInputManual.setOnClickListener {
                if (product != null) {
                    startActivity(
                        Intent(requireActivity(), InputManualActivity::class.java).apply {
                            putExtra(InputManualActivity.PRODUCT_PURCHASE_MANUAL_KEY, product)
                        }
                    )
                    dismiss()
                    return@setOnClickListener
                }
                GenericModalDialogCashplus.Builder()
                    .title(getString(R.string.something_wrong))
                    .image(R.drawable.illustration_error)
                    .description(getString(R.string.product_is_unavailable_contact_cs_please))
                    .buttonNegative(
                        NegativeButtonAction(
                            btnLabel = getString(R.string.close),
                            backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                            buttonTextColor = Color.parseColor("#0A57FF"),
                            setClickOnDismiss = true
                        )
                    )
                    .show(requireActivity().supportFragmentManager, "Customer Id Is Empty")
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PRODUCT_PURCHASES_KEY = "product_purchases_key"
        private const val PROVIDER_CODE_KEY = "provider_code_key"

        @JvmStatic
        fun newInstance(product: ProductModel?, providerCode: String) =
            ChooseEwalletMethodFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PRODUCT_PURCHASES_KEY, product)
                    putString(PROVIDER_CODE_KEY, providerCode)
                }
            }
    }
}