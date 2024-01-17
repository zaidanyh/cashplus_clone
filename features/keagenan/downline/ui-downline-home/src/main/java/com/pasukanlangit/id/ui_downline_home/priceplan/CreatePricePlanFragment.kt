package com.pasukanlangit.id.ui_downline_home.priceplan

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.view.*
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.core.utils.InputUtil.inputCharNumberFilter
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.core.utils.parcelableArrayList
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.FragmentCreatePricePlanBinding
import com.pasukanlangit.id.ui_downline_home.utils.MarkupPlanParcelable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CreatePricePlanFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCreatePricePlanBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PricePlanViewModel by sharedViewModel()

    private lateinit var markupPlans: List<MarkupPlanParcelable>
    private var markupCode: String? = null
    private var descMarkup: String? = null
    private var isUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                dialog.window?.let { WindowCompat.setDecorFitsSystemWindows(it, false) }
            else {
                @Suppress("DEPRECATION")
                dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = STATE_EXPANDED
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePricePlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.isCancelable = false

        markupPlans = arguments?.parcelableArrayList(MARKUP_PLANS_LIST_KEY) ?: emptyList()
        markupCode = arguments?.getString(MARKUP_PLAN_CODE_KEY)
        descMarkup = arguments?.getString(DESCRIPTION_PLAN_CODE_KEY)

        with(binding) {
            iconClose.setOnClickListener { this@CreatePricePlanFragment.dismiss() }
            edtNamePricePlan.filters = arrayOf(InputFilter.LengthFilter(20), inputCharNumberFilter)
            edtDescriptionPricePlan.filters = arrayOf(InputFilter.LengthFilter(50), inputCharNumberFilter)
            txtNamePriceCannotChanged.isVisible = !markupCode.isNullOrEmpty() && !descMarkup.isNullOrEmpty()
            edtNamePricePlan.isEnabled = markupCode.isNullOrEmpty() && descMarkup.isNullOrEmpty()
            if (!markupCode.isNullOrEmpty() && !descMarkup.isNullOrEmpty()) {
                txtPricePlan.text = getString(R.string.change_price_plan)
                edtNamePricePlan.setText(markupCode)
                edtDescriptionPricePlan.setText(descMarkup)
                isUpdate = true
            } else txtPricePlan.text = getString(R.string.create_price_plan)
            btnSave.setUpToProgressButton(viewLifecycleOwner)
            btnSave.setOnClickListener { onCreateMarkupPlan() }
        }
    }

    private fun onCreateMarkupPlan() {
        with(binding) {
            val codeMarkup = edtNamePricePlan.text.toString().trim()
            val descMarkup = edtDescriptionPricePlan.text.toString().trim()
            if (codeMarkup.isEmpty() || descMarkup.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.incomplete_data), Toast.LENGTH_SHORT).show()
                return
            } else if (codeMarkup.length > 20) {
                Toast.makeText(requireContext(), getString(R.string.input_code_markup_length_error), Toast.LENGTH_SHORT).show()
                return
            } else if (descMarkup.length > 50) {
                Toast.makeText(requireContext(), getString(R.string.input_desc_length_error), Toast.LENGTH_SHORT).show()
                return
            }
            val isExistMarkup = markupPlans.find { it.codeMarkupPlan == codeMarkup.replace(" ", "_").uppercase() }
            if (isExistMarkup != null) {
                Toast.makeText(requireContext(), getString(R.string.price_plan_already_exist, codeMarkup), Toast.LENGTH_SHORT).show()
                return
            }
            KeyboardUtil.hideSoftKeyboard(requireContext())
            viewModel.setMarkupDataClass(codeMarkup, descMarkup)
            viewModel.onTriggerEvent(
                PricePlanEvent.CreateReplaceMarkupPlan(markupCode = codeMarkup, description = descMarkup, isAddProduct = isUpdate)
            )
            this@CreatePricePlanFragment.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MARKUP_PLANS_LIST_KEY = "markup_plans_list_key"
        private const val MARKUP_PLAN_CODE_KEY = "markup_plan_key"
        private const val DESCRIPTION_PLAN_CODE_KEY = "description_plan_code_key"

        @JvmStatic
        fun newInstance(markupPlans: ArrayList<MarkupPlanParcelable>? = null, markupCode: String? = null, desc: String? = null) =
            CreatePricePlanFragment().apply {
                arguments = Bundle().apply {
                    putString(MARKUP_PLAN_CODE_KEY, markupCode)
                    putString(DESCRIPTION_PLAN_CODE_KEY, desc)
                    putParcelableArrayList(MARKUP_PLANS_LIST_KEY, markupPlans)
                }
            }
    }
}