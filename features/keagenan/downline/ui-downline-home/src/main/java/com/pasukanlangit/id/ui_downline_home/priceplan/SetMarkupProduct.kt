package com.pasukanlangit.id.ui_downline_home.priceplan

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.FragmentSetMarkupProductBinding
import com.pasukanlangit.id.ui_downline_home.priceplan.findproduct.FindProductEvent
import com.pasukanlangit.id.ui_downline_home.priceplan.findproduct.FindProductViewModel
import com.pasukanlangit.id.ui_downline_home.priceplan.product.ProductPricePlanEvent
import com.pasukanlangit.id.ui_downline_home.priceplan.product.ProductPricePlanViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import com.pasukanlangit.id.core.R as RCore

class SetMarkupProduct : BottomSheetDialogFragment() {

    private var _binding: FragmentSetMarkupProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductPricePlanViewModel by sharedViewModel()
    private val foundViewModel: FindProductViewModel by sharedViewModel()

    private var markupCode: String? = null
    private var markupDesc: String? = null
    private var productCode: String? = null
    private var positionIndex: Int? = null
    private var category: String? = null
    private var markupValue: String? = null
    private var isFoundProduct: Boolean? = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetMarkupProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int = RCore.style.DialogStyleResizeKeyboardCore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        markupCode = arguments?.getString(MARKUP_CODE_KEY)
        markupDesc = arguments?.getString(MARKUP_DESC_KEY)
        productCode = arguments?.getString(PRODUCT_CODE_KEY)
        positionIndex = arguments?.getInt(POSITION_INDEX_KEY)
        category = arguments?.getString(CATEGORY_KEY)
        markupValue = arguments?.getString(MARKUP_PRODUCT_KEY)
        isFoundProduct = arguments?.getBoolean(IS_FOUND_PRODUCT_KEY)
        with(binding) {
            iconClose.setOnClickListener { dismiss() }
            txtLabel.text = getString(R.string.desc_set_markup_product_price_plan, productCode, markupCode?.replace("_", " "))
            edtSetMarkup.filters = arrayOf(InputFilter.LengthFilter(4))
            edtSetMarkup.setText(markupValue ?: "")
            btnSubmit.setOnClickListener {
                it.isEnabled = false
                KeyboardUtil.closeKeyboardDialog(requireContext(), edtSetMarkup)
                submitMarkup()
            }
        }
    }

    private fun submitMarkup() {
        val newMarkup = binding.edtSetMarkup.text.toString().trim()
        if (newMarkup.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.markup_value_required), Toast.LENGTH_SHORT).show()
            binding.btnSubmit.isEnabled = true
            return
        }
        if (isFoundProduct == true) {
            foundViewModel.onTriggerEvent(
                FindProductEvent.AddProductMarkup(
                    productCode.toString(), newMarkup,
                    category.toString(), positionIndex
                )
            )
            this.dismiss()
            return
        }
        viewModel.onTriggerEvent(
            ProductPricePlanEvent.ChangeMarkupProduct(
                markupCode = markupCode.toString(), markupDesc = markupDesc.toString(),
                productCode = productCode.toString(), newMarkup
            )
        )
        this.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MARKUP_CODE_KEY = "markup_code_key"
        private const val MARKUP_DESC_KEY = "markup_desc_key"
        private const val PRODUCT_CODE_KEY = "product_code_key"
        private const val POSITION_INDEX_KEY = "position_index_key"
        private const val CATEGORY_KEY = "category_key"
        private const val MARKUP_PRODUCT_KEY = "markup_product_key"
        private const val IS_FOUND_PRODUCT_KEY = "is_product_list_key"

        fun newInstance(
            markupCode: String, markupDesc: String, productCode: String, position: Int? = null,
            category: String? = null, markupValue: String? = null, isFoundProduct: Boolean = false
        ): SetMarkupProduct {
            return SetMarkupProduct().apply {
                arguments = Bundle().apply {
                    putString(MARKUP_CODE_KEY, markupCode)
                    putString(MARKUP_DESC_KEY, markupDesc)
                    putString(PRODUCT_CODE_KEY, productCode)
                    putInt(POSITION_INDEX_KEY, position ?: -1)
                    putString(CATEGORY_KEY, category)
                    putString(MARKUP_PRODUCT_KEY, markupValue)
                    putBoolean(IS_FOUND_PRODUCT_KEY, isFoundProduct)
                }
            }
        }
    }
}