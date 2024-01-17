package com.pasukanlangit.id.ui_downline_home.priceplan

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.id.core.presentation.components.*
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.ActivityPricePlanBinding
import com.pasukanlangit.id.ui_downline_home.priceplan.findproduct.ProductFindedActivity
import com.pasukanlangit.id.ui_downline_home.priceplan.product.ProductPricePlanActivity
import com.pasukanlangit.id.ui_downline_home.utils.MarkupPlanParcelable
import com.pasukanlangit.id.ui_downline_home.utils.SetMarkupDataClass
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class PricePlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPricePlanBinding
    private val viewModel: PricePlanViewModel by viewModel()

    private lateinit var pricePlanAdapter: PricePlanAdapter
    private var markupPlans = arrayListOf<MarkupPlanParcelable>()
    private var markupDataClass: SetMarkupDataClass? = null
    private var downLinePhone: String? = null
    private var downLineName: String? = null
    private var markupPlan: String? = null
    private var mainMarkupValue: String? = null
    private var isShownClose = false

    private var codeMarkupForView: String? = null
    private var keywordSearch = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPricePlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        downLinePhone = intent.getStringExtra(PHONE_NUMBER_DOWNLINE_KEY)
        downLineName = intent.getStringExtra(NAME_DOWNLINE_KEY)
        markupPlan = intent.getStringExtra(MARKUP_PLAN_KEY)
        mainMarkupValue = intent.getStringExtra(MAIN_MARKUP_VALUE_KEY)
        setUpRecyclerView()
        with(binding) {
            imgBack.setOnClickListener { finish() }
            swipeRefreshPricePlan.setOnRefreshListener {
                viewModel.onTriggerEvent(PricePlanEvent.GetListPricePlan)
                swipeRefreshPricePlan.isRefreshing = false
            }
            edtSearchPricePlan.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().isNotEmpty()) {
                        isShownClose = true
                        edtSearchPricePlan.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_close, 0)
                    } else {
                        isShownClose = false
                        edtSearchPricePlan.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_search, 0)
                    }
                    keywordSearch = s.toString().trim()
                    viewModel.keywordInput.value = keywordSearch
                    viewModel.onTriggerEvent(PricePlanEvent.GetListPricePlan)
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            edtSearchPricePlan.setOnTouchListener { _, event ->
                val drawableEnd = 2
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (binding.edtSearchPricePlan.right - binding.edtSearchPricePlan.compoundDrawables[drawableEnd].bounds.width())) {
                        if (isShownClose) {
                            edtSearchPricePlan.text?.clear()
                            edtSearchPricePlan.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_search, 0)
                            KeyboardUtil.closeKeyboardDialog(this@PricePlanActivity, edtSearchPricePlan)
                            edtSearchPricePlan.clearFocus()
                            isShownClose = !isShownClose
                            keywordSearch = edtSearchPricePlan.text.toString().trim()
                            viewModel.keywordInput.value = keywordSearch
                            viewModel.onTriggerEvent(PricePlanEvent.GetListPricePlan)
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }
            btnCreatePricePlan.setOnClickListener {
                CreatePricePlanFragment.newInstance().show(supportFragmentManager, "CREATE PRICE PLAN")
            }
            btnCreatePricePlan2.setOnClickListener {
                CreatePricePlanFragment.newInstance(
                    markupPlans = markupPlans
                ).show(supportFragmentManager, "CREATE PRICE PLAN")
            }
            rvPricePlan.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (abs(scrollY - oldScrollY) > 15) {
                    if (scrollY > oldScrollY) {
                        slideDown(btnCreatePricePlan2)
                        return@setOnScrollChangeListener
                    }
                    slideUp(btnCreatePricePlan2)
                }
            }
        }
        collectPricePlans()
    }

    private fun setUpRecyclerView() {
        pricePlanAdapter = PricePlanAdapter()
        with(binding.rvPricePlan) {
            layoutManager = LinearLayoutManager(this@PricePlanActivity)
            adapter = pricePlanAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
        pricePlanAdapter.setOnItemClickListener(object: PricePlanAdapter.OnItemClickListener {
            override fun onApplyClicked(codeMarkup: String) {
                this@PricePlanActivity.codeMarkupForView = codeMarkup
                if (codeMarkup == markupPlan) {
                    GenericConfirmDialogFragment.Builder()
                        .title(getString(R.string.confirm))
                        .description(getString(R.string.confirm_unapply_markup_plan, markupPlan?.replace("_", " "), downLineName))
                        .buttonPositive(
                            PositiveButton(
                                backgroundButton = R.drawable.bg_red600_rounded_12,
                                buttonText = getString(R.string.unapply),
                                buttonTextColor = Color.parseColor("#FFFFFF"),
                                onBtnAction = {
                                    viewModel.onTriggerEvent(PricePlanEvent.UnApplyMarkupPlan(downLinePhone.toString()))
                                }
                            )
                        )
                        .buttonNegative(
                            NegativeButton(
                                backgroundButton = R.drawable.bg_grey_slate100_rounded_12,
                                buttonText = getString(R.string.no),
                                buttonTextColor = Color.parseColor("#334155"),
                                actionDismiss = true
                            )
                        )
                        .show(supportFragmentManager, "Confirm Unapply Price Plan")
                    return
                }
                viewModel.onTriggerEvent(PricePlanEvent.ApplyMarkupPlan(codeMarkup, downLinePhone.toString()))
            }

            override fun onDeleteClicked(codeMarkup: String) {
                this@PricePlanActivity.codeMarkupForView = codeMarkup
                GenericConfirmDialogFragment.Builder()
                    .title(getString(R.string.delete_price_plan))
                    .description(getString(R.string.delete_price_plan_confirm, codeMarkup.replace("_", " ")))
                    .buttonPositive(
                        PositiveButton(
                            backgroundButton = R.drawable.bg_red600_rounded_12,
                            buttonText = getString(R.string.delete),
                            buttonTextColor = Color.parseColor("#FFFFFF"),
                            onBtnAction = {
                                viewModel.onTriggerEvent(PricePlanEvent.DeleteMarkupPlan(codeMarkup))
                            }
                        )
                    )
                    .buttonNegative(
                        NegativeButton(
                            backgroundButton = R.drawable.bg_grey_slate100_rounded_12,
                            buttonText = getString(R.string.close),
                            buttonTextColor = Color.parseColor("#334155"),
                            actionDismiss = true
                        )
                    ).show(supportFragmentManager, "Confirm Delete Price Plan")
            }

            override fun onEditClicked(markupPlan: MarkupPlanParcelable) {
                codeMarkupForView = markupPlan.codeMarkupPlan
                CreatePricePlanFragment.newInstance(
                    markupCode = markupPlan.codeMarkupPlan, desc = markupPlan.description
                ).show(supportFragmentManager, "REPLACE PRICE PLAN")
            }

            override fun onSetMarkupProductClicked(markupPlan: MarkupPlanParcelable) {
                startActivity(
                    Intent(this@PricePlanActivity, ProductPricePlanActivity::class.java).apply {
                        putExtra(MARKUP_PLAN_KEY, markupPlan.codeMarkupPlan)
                        putExtra(MARKUP_DESC_KEY, markupPlan.description)
                        putExtra(MAIN_MARKUP_VALUE_KEY, mainMarkupValue)
                    }
                )
            }
        })
    }

    private fun collectPricePlans() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE GET LIST PRICE PLAN
                launch {
                    viewModel.isLoading.collectLatest {
                        binding.progressLoading.isVisible = it
                    }
                }
                launch {
                    viewModel.pricePlans.collectLatest {
                        it?.let { data -> bindPricePlan(data) }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@PricePlanActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(PricePlanEvent.RemoveListPricePlanMessage)
                        }
                    }
                }

                // STATE CREATE PRICE PLAN
                launch {
                    viewModel.loadingCreate.collectLatest { binding.progressLoading.isVisible = it }
                }
                launch {
                    viewModel.createReplaceMarkup.collectLatest {
                        if (it != null && it) {
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.done))
                                .image(R.drawable.illustration_success2)
                                .description(
                                    if (!codeMarkupForView.isNullOrEmpty())
                                        getString(R.string.edit_price_plan_successfully, codeMarkupForView?.replace("_", " "))
                                    else getString(
                                        R.string.create_price_plan_successfully,
                                        markupDataClass?.markupCode?.replace("_", " ")
                                    )
                                )
                                .buttonPositive(
                                    if (!codeMarkupForView.isNullOrEmpty()) null
                                    else
                                        PositiveButtonAction(
                                            btnLabel = getString(R.string.next_set_markup),
                                            backgroundButton = R.drawable.bg_emerald600_rounded_12,
                                            buttonTextColor = Color.parseColor("#FFFFFF"),
                                            onBtnClicked = {
                                                startActivity(
                                                    Intent(this@PricePlanActivity, ProductFindedActivity::class.java).apply {
                                                        putExtra(MARKUP_PLAN_KEY, markupDataClass?.markupCode)
                                                        putExtra(MARKUP_DESC_KEY, markupDataClass?.description)
                                                        putExtra(MAIN_MARKUP_VALUE_KEY, mainMarkupValue)
                                                    }
                                                )
                                                markupDataClass = null
                                            }
                                        )
                                )
                                .buttonNegative(
                                    NegativeButtonAction(
                                        btnLabel = getString(R.string.close),
                                        backgroundButton = R.drawable.bg_primary_rounded_12,
                                        buttonTextColor = Color.parseColor("#FFFFFF"),
                                        onBtnClicked = {
                                            codeMarkupForView = null
                                            viewModel.onTriggerEvent(PricePlanEvent.GetListPricePlan)
                                        }
                                    )
                                ).show(supportFragmentManager, "Create/Replace Price Plan Successfully")
                        }
                    }
                }
                launch {
                    viewModel.errorCreate.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@PricePlanActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(PricePlanEvent.RemoveCreateMessage)
                        }
                    }
                }

                // STATE APPLY PRICE PLAN
                launch {
                    viewModel.loadingApply.collectLatest { binding.progressLoading.isVisible = it }
                }
                launch {
                    viewModel.applyMarkupPlan.collectLatest {
                        if (it != null && it) {
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.done))
                                .image(R.drawable.illustration_success2)
                                .description(getString(R.string.apply_price_plan_successfully, codeMarkupForView?.replace("_", " "), downLineName))
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.close),
                                        onBtnClicked = {
                                            finish()
                                        }
                                    )
                                ).show(supportFragmentManager, "Apply Price Plan Successfully")
                        }
                    }
                }
                launch {
                    viewModel.errorApply.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@PricePlanActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(PricePlanEvent.RemoveApplyMessage)
                        }
                    }
                }

                // STATE UNAPPLY PRICE PLAN
                launch {
                    viewModel.loadingUnApply.collectLatest { binding.progressLoading.isVisible = it }
                }
                launch {
                    viewModel.unApplyMarkupPlan.collectLatest {
                        if (it != null && it)
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.done))
                                .image(R.drawable.illustration_success2)
                                .description(getString(R.string.unapply_price_plan_successfully, codeMarkupForView?.replace("_", " "), downLineName))
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.close),
                                        onBtnClicked = {
                                            finish()
                                        }
                                    )
                                ).show(supportFragmentManager, "UnApply Price Plan Successfully")
                    }
                }
                launch {
                    viewModel.errorUnApply.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@PricePlanActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(PricePlanEvent.RemoveUnApplyMessage)
                        }
                    }
                }

                // STATE DELETE PRICE PLAN
                launch {
                    viewModel.loadingDelete.collectLatest { binding.progressLoading.isVisible = it }
                }
                launch {
                    viewModel.deleteMarkupPlan.collectLatest {
                        if (it != null && it) {
                            viewModel.onTriggerEvent(PricePlanEvent.GetListPricePlan)
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.done))
                                .image(R.drawable.illustration_success2)
                                .description(getString(R.string.delete_price_plan_successfully, codeMarkupForView?.replace("_", " ")))
                                .buttonNegative(
                                    NegativeButtonAction(
                                        btnLabel = getString(R.string.close),
                                        backgroundButton = R.drawable.bg_primary_rounded_12,
                                        buttonTextColor = Color.parseColor("#FFFFFF"),
                                        onBtnClicked = {
                                            codeMarkupForView = null
                                        }
                                    )
                                ).show(supportFragmentManager, "Delete Price Plan Successfully")
                        }
                    }
                }
                launch {
                    viewModel.errorDelete.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@PricePlanActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(PricePlanEvent.RemoveDeleteMessage)
                        }
                    }
                }

                launch {
                    viewModel.markupDataClass.collectLatest {
                        if (it != null)
                            markupDataClass = SetMarkupDataClass(it.markupCode, it.description)
                    }
                }
            }
        }
    }

    private fun bindPricePlan(pricePlans: List<MarkupPlanParcelable>) {
        markupPlans.clear()
        markupPlans.addAll(pricePlans)
        with(binding) {
            edtSearchPricePlan.isVisible = markupPlans.isNotEmpty() || keywordSearch.isNotEmpty()
            wrapperAvailable.isVisible = markupPlans.isNotEmpty()
            wrapperEmptyState.isVisible = markupPlans.isEmpty()
            btnCreatePricePlan.isVisible = markupPlans.isEmpty() && keywordSearch.isEmpty()
            if (markupPlans.isEmpty() && keywordSearch.isNotEmpty()) {
                txtEmptyState.text = getString(R.string.search_not_found)
                txtEmptyStateDesc.text = getString(R.string.search_price_plan_not_found_desc)
            }
            if (markupPlans.isNotEmpty())
                pricePlanAdapter.setPricePlanList(markupPlans, markupPlan.toString())
        }
    }

    private fun slideDown(view: View) {
        val height = view.height
        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, height.toFloat() * 2.5f).apply {
            duration = 300
            start()
        }
    }

    private fun slideUp(view: View) {
        val height = view.height
        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, height.toFloat() * 2.5f, 0f).apply {
            duration = 300
            start()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(PricePlanEvent.GetListPricePlan)
    }

    companion object {
        const val PHONE_NUMBER_DOWNLINE_KEY = "phone_number_downline_key"
        const val MARKUP_PLAN_KEY = "markup_plan_key"
        const val NAME_DOWNLINE_KEY = "name_downline_key"
        const val MARKUP_DESC_KEY = "MARKUP_DES_KEY"
        const val MAIN_MARKUP_VALUE_KEY = "main_markup_value_key"
    }
}