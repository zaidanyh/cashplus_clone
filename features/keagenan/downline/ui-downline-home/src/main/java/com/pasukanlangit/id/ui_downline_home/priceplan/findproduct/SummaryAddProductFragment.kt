package com.pasukanlangit.id.ui_downline_home.priceplan.findproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.FragmentSummaryAddProductBinding
import com.pasukanlangit.id.ui_downline_home.utils.SummaryAddProduct

class SummaryAddProductFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSummaryAddProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
        this.isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val products = arguments?.parcelable<SummaryAddProduct>(PRODUCTS_SUMMARY_KEY)
        with(binding.rvSummaryProduct) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = products?.dataSummary?.let { SummaryAddProductAdapter(it) }
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PRODUCTS_SUMMARY_KEY = "products_summary_key"

        @JvmStatic
        fun newInstance(products: SummaryAddProduct) =
            SummaryAddProductFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PRODUCTS_SUMMARY_KEY, products)
                }
            }
    }
}