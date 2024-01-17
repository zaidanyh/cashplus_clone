package com.pasukanlangit.id.ui_downline_home.priceplan.product

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
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
import com.pasukanlangit.id.core.utils.DrawablePosition.DRAWABLE_RIGHT
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.domain_downline.model.MarkupProductResponse
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.ActivityProductPricePlanBinding
import com.pasukanlangit.id.ui_downline_home.priceplan.PricePlanActivity
import com.pasukanlangit.id.ui_downline_home.priceplan.SetMarkupProduct
import com.pasukanlangit.id.ui_downline_home.priceplan.findproduct.SearchProductFragment
import com.pasukanlangit.id.ui_downline_home.utils.toListProductMarkup
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductPricePlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductPricePlanBinding
    private val viewModel: ProductPricePlanViewModel by viewModel()

    private lateinit var productPricePlanAdapter: ProductPricePlanAdapter

    private val products = mutableListOf<MarkupProductResponse>()
    private var codeMarkup: String? = null
    private var descMarkup: String? = null
    private var mainMarkup: String? = null
    private var codeProduct: String? = null

    private var isShownClose = false
    private var isDeleteProduct = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductPricePlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, binding.root.rootView)
        wic.isAppearanceLightStatusBars = false

        codeMarkup = intent.getStringExtra(PricePlanActivity.MARKUP_PLAN_KEY)
        descMarkup = intent.getStringExtra(PricePlanActivity.MARKUP_DESC_KEY)
        mainMarkup = intent.getStringExtra(PricePlanActivity.MAIN_MARKUP_VALUE_KEY)
        setUpRecyclerView()
        with(binding) {
            imgBack.setOnClickListener {
                finish()
            }
            txtDescIsEmpty.text = getString(R.string.product_havent_added_desc, codeMarkup?.replace("_", " "))
            edtSearchProduct.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (::productPricePlanAdapter.isInitialized) productPricePlanAdapter.filter.filter(s.toString())
                    if (!s.isNullOrEmpty()) {
                        isShownClose = true
                        edtSearchProduct.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_close, 0)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            edtSearchProduct.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edtSearchProduct.right - edtSearchProduct.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        if (isShownClose) {
                            edtSearchProduct.text?.clear()
                            edtSearchProduct.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_search, 0)
                            KeyboardUtil.closeKeyboardDialog(this@ProductPricePlanActivity, edtSearchProduct)
                            edtSearchProduct.clearFocus()
                            isShownClose = !isShownClose
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }
            btnAddNow.setOnClickListener {
                SearchProductFragment
                    .newInstance(markupCode = codeMarkup, markupDesc = descMarkup, mainMarkup = mainMarkup)
                    .show(supportFragmentManager, "Search Product")
            }
            btnAddProduct.setOnClickListener {
                SearchProductFragment
                    .newInstance(markupCode = codeMarkup, markupDesc = descMarkup, mainMarkup = mainMarkup)
                    .show(supportFragmentManager, "Search Product")
            }
        }

        collectDataProduct()
    }

    private fun setUpRecyclerView() {
        productPricePlanAdapter = ProductPricePlanAdapter()
        with(binding.rvProductPricePlan) {
            layoutManager = LinearLayoutManager(this@ProductPricePlanActivity)
            adapter = productPricePlanAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
        productPricePlanAdapter.setOnClickListener(object: ProductPricePlanAdapter.SetOnItemClickListener {
            override fun onItemDeleteClicked(product: MarkupProductResponse) {
                GenericConfirmDialogFragment.Builder()
                    .title(getString(R.string.delete_product_price_plan))
                    .description(getString(R.string.delete_product_price_plan_confirm, product.kodeProduct, codeMarkup?.replace("_", " ")))
                    .buttonPositive(
                        PositiveButton(
                            backgroundButton = R.drawable.bg_red600_rounded_12,
                            buttonText = getString(R.string.delete),
                            buttonTextColor = Color.parseColor("#FFFFFF"),
                            onBtnAction = {
                                codeProduct = product.kodeProduct
                                isDeleteProduct = true
                                viewModel.onTriggerEvent(
                                    ProductPricePlanEvent.DeleteMarkupProduct(
                                        markupCode = codeMarkup.toString(), markupDesc = descMarkup.toString(),
                                        productCode = product.kodeProduct, products.toListProductMarkup()
                                    )
                                )
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

            override fun onItemSetMarkupClick(codeProduct: String) {
                this@ProductPricePlanActivity.codeProduct = codeProduct
                isDeleteProduct = false
                SetMarkupProduct.newInstance(
                    markupCode = codeMarkup.toString(), markupDesc = descMarkup.toString(),
                    productCode = codeProduct
                ).show(supportFragmentManager, "Set Markup Product")
            }
        })
    }

    private fun collectDataProduct() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collectLatest {
                        binding.progressBar.isVisible = it
                    }
                }
                launch {
                    viewModel.productPricePlan.collectLatest {
                        it?.let { data -> bindToView(data) }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(
                                    this@ProductPricePlanActivity,
                                    info,
                                    Toast.LENGTH_SHORT
                                )
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(ProductPricePlanEvent.RemoveHeadMessage)
                        }
                    }
                }


                launch {
                    viewModel.loadingCreate.collectLatest { binding.progressBar.isVisible = it }
                }
                launch {
                    viewModel.createReplace.collectLatest {
                        if (it) {
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.done))
                                .image(R.drawable.illustration_success2)
                                .description(
                                    if (isDeleteProduct)
                                        getString(R.string.delete_product_price_plan_successfully, codeProduct, codeMarkup?.replace("_", " "))
                                    else
                                        getString(R.string.edit_product_price_plan_successfully, codeProduct, codeMarkup?.replace("_", " "))
                                )
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.close),
                                        onBtnClicked = {
                                            viewModel.onTriggerEvent(ProductPricePlanEvent.GetProductPricePlan(codeMarkup = codeMarkup.toString()))
                                        }
                                    )
                                )
                                .show(supportFragmentManager, "Pop up Create Replace")
                        }
                    }
                }
                launch {
                    viewModel.errorCreate.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(
                                    this@ProductPricePlanActivity,
                                    info,
                                    Toast.LENGTH_SHORT
                                )
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(ProductPricePlanEvent.RemoveMessageChangeMarkup)
                        }
                    }
                }
            }
        }
    }

    private fun bindToView(products: List<MarkupProductResponse>) {
        with(binding) {
            if (products.isEmpty()) {
                wrapperProductIsEmpty.isVisible = true
                wrapperProductIsAvailable.isVisible = false
                return
            }
            wrapperProductIsEmpty.isVisible = false
            wrapperProductIsAvailable.isVisible = true
            productPricePlanAdapter.setProductPricePlan(products)
            this@ProductPricePlanActivity.products.clear()
            this@ProductPricePlanActivity.products.addAll(products)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(ProductPricePlanEvent.GetProductPricePlan(codeMarkup = codeMarkup.toString()))
    }
}