package com.pasukanlangit.id.ui_downline_home.priceplan.findproduct

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.domain_downline.model.MarkupProductResponse
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.FragmentSearchProductBinding
import com.pasukanlangit.id.ui_downline_home.priceplan.PricePlanActivity
import com.pasukanlangit.id.ui_downline_home.utils.toParcelMarkupProduct
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchProductFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSearchProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FindProductViewModel by viewModel()

    private var codeMarkup: String? = null
    private var markupDesc: String? = null
    private var mainMarkup: String? = null
    private var keywordSearch: String? = null
    private var stateButton: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codeMarkup = arguments?.getString(PricePlanActivity.MARKUP_PLAN_KEY)
        markupDesc = arguments?.getString(PricePlanActivity.MARKUP_DESC_KEY)
        mainMarkup = arguments?.getString(PricePlanActivity.MAIN_MARKUP_VALUE_KEY)
        with(binding) {
            iconClose.setOnClickListener { dismiss() }
            edtSearchProduct.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        edtSearchProduct.error = getString(R.string.incomplete_data)
                        stateButton = false
                        return
                    }
                    stateButton = true
                    edtSearchProduct.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnFindProduct.isEnabled = stateButton
                }
            })
            btnFindProduct.setUpToProgressButton(viewLifecycleOwner)
            btnFindProduct.setOnClickListener {
                keywordSearch = edtSearchProduct.text.toString().trim()
                btnFindProduct.isEnabled = false
                KeyboardUtil.closeKeyboardDialog(requireContext(), edtSearchProduct)
                viewModel.onTriggerEvent(
                    FindProductEvent.GetProductPricePlanBySearch(
                        codeMarkup = codeMarkup.toString(),
                        keyword = edtSearchProduct.text.toString().trim(),
                        mainMarkup = mainMarkup.toString()
                    )
                )
            }
        }
        collectProductBySearch()
    }

    private fun collectProductBySearch() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loadingSearch.collectLatest {
                        if (it) {
                            binding.btnFindProduct.showLoading()
                            return@collectLatest
                        }
                        binding.btnFindProduct.hideProgress(getString(R.string.find_product))
                    }
                }
                launch {
                    viewModel.productSearch.collectLatest {
                        it?.let { data -> bindToView(data) }
                    }
                }
                launch {
                    viewModel.errorProductSearch.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(FindProductEvent.RemoveHeadMessageSearch)
                        }
                    }
                }
            }
        }
    }

    private fun bindToView(products: List<MarkupProductResponse>) {
        with(binding) {
            btnFindProduct.isEnabled = true
            if (products.isEmpty()) {
                txtNotFound.isVisible = true
                return
            }
            this@SearchProductFragment.dismiss()
            startActivity(
                Intent(requireContext(), ProductFindedActivity::class.java).apply {
                    putExtra(PricePlanActivity.MARKUP_PLAN_KEY, codeMarkup)
                    putExtra(PricePlanActivity.MARKUP_DESC_KEY, markupDesc)
                    putExtra(PricePlanActivity.MAIN_MARKUP_VALUE_KEY, mainMarkup)
                    putExtra(ProductFindedActivity.KEYWORD_SEARCH_KEY, keywordSearch)
                    putExtra(ProductFindedActivity.LIST_PRODUCT_FOUND_KEY, products.toParcelMarkupProduct())
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(markupCode: String?, markupDesc: String?, mainMarkup: String?) =
            SearchProductFragment().apply {
                arguments = Bundle().apply {
                    putString(PricePlanActivity.MARKUP_PLAN_KEY, markupCode)
                    putString(PricePlanActivity.MARKUP_DESC_KEY, markupDesc)
                    putString(PricePlanActivity.MAIN_MARKUP_VALUE_KEY, mainMarkup)
                }
            }
    }
}