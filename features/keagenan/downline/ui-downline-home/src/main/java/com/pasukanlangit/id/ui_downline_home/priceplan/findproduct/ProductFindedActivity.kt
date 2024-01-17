package com.pasukanlangit.id.ui_downline_home.priceplan.findproduct

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.extensions.onDone
import com.pasukanlangit.id.core.presentation.components.*
import com.pasukanlangit.id.core.utils.*
import com.pasukanlangit.id.domain_downline.model.MarkupProductResponse
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.ActivityProductFindedBinding
import com.pasukanlangit.id.ui_downline_home.priceplan.PricePlanActivity
import com.pasukanlangit.id.ui_downline_home.priceplan.SetMarkupProduct
import com.pasukanlangit.id.ui_downline_home.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductFindedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductFindedBinding
    private val viewModel: FindProductViewModel by viewModel()

    private var productsFound: MarkupProductResponseParcel? = null
    private var inputSearch: String? = null
    private var markupCode: String? = null
    private var markupDesc: String? = null
    private var mainMarkup: String? = null
    private val summaryAdd = mutableListOf<SummaryProduct>()

    private lateinit var productFoundPricePlanAdapter: ProductPricePlanFoundAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductFindedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onActivityFinish()
            }
        })

        markupCode =  intent.getStringExtra(PricePlanActivity.MARKUP_PLAN_KEY)
        markupDesc = intent.getStringExtra(PricePlanActivity.MARKUP_DESC_KEY)
        mainMarkup = intent.getStringExtra(PricePlanActivity.MAIN_MARKUP_VALUE_KEY)
        val keywordSearch = intent.getStringExtra(KEYWORD_SEARCH_KEY)
        inputSearch = keywordSearch
        productsFound = intent.parcelable(LIST_PRODUCT_FOUND_KEY)
        setupRecyclerView()
        with(binding) {
            imgBack.setOnClickListener { finish() }
            if (keywordSearch.isNullOrEmpty()) txtProductFound.text = getString(R.string.find_product_please)
            swipeRefreshMarkup.setOnRefreshListener {
                viewModel.onTriggerEvent(
                    FindProductEvent.GetProductPricePlanBySearch(
                        codeMarkup = markupCode.toString(), keyword = inputSearch.toString(),
                        mainMarkup = mainMarkup.toString()
                    )
                )
                swipeRefreshMarkup.isRefreshing = false
            }
            edtSearchProduct.setText(keywordSearch ?: "")
            edtSearchProduct.setOnClickListener {
                edtSearchProduct.isFocusable = true
                edtSearchProduct.isFocusableInTouchMode = true
                KeyboardUtil.openSoftKeyboard(this@ProductFindedActivity, edtSearchProduct)
            }
            edtSearchProduct.onDone {
                inputSearch = edtSearchProduct.text.toString().trim()
                edtSearchProduct.isFocusable = false
                if (!inputSearch.isNullOrEmpty())
                    viewModel.onTriggerEvent(
                        FindProductEvent.GetProductPricePlanBySearch(
                            codeMarkup = markupCode.toString(), keyword = inputSearch.toString(),
                            mainMarkup = mainMarkup.toString()
                        )
                    )
            }
            btnSearch.setOnClickListener {
                inputSearch = edtSearchProduct.text.toString().trim()
                if (inputSearch.isNullOrEmpty()) {
                    return@setOnClickListener
                }
                KeyboardUtil.closeKeyboardDialog(this@ProductFindedActivity, edtSearchProduct)
                viewModel.onTriggerEvent(
                    FindProductEvent.GetProductPricePlanBySearch(
                        codeMarkup = markupCode.toString(), keyword = inputSearch.toString(),
                        mainMarkup = mainMarkup.toString()
                    )
                )
            }
            btnSummary.setOnClickListener {
                SummaryAddProductFragment.newInstance(
                    summaryAdd.toSummaryAddProduct()
                ).show(supportFragmentManager, "Summary add product")
            }
            btnSaveChange.setUpToProgressButton(this@ProductFindedActivity)
            btnSaveChange.setOnClickListener {
                edtSearchProduct.isFocusable = false
                btnSaveChange.isEnabled = false
                viewModel.onTriggerEvent(
                    FindProductEvent.CreateReplace(
                        markupCode = markupCode.toString(), description = markupDesc.toString(),
                        products = summaryAdd.toListProductMarkupRequest()
                    )
                )
            }
        }
        collectProductPricePlanFound()
    }

    private fun setupRecyclerView() {
        productFoundPricePlanAdapter = ProductPricePlanFoundAdapter()
        with(binding.rvProductMarkup) {
            layoutManager = LinearLayoutManager(this@ProductFindedActivity)
            adapter = productFoundPricePlanAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
        if (!productsFound?.dataProduct.isNullOrEmpty()) {
            productFoundPricePlanAdapter.setProducts(productsFound?.toListMarkupProductResponse())
            productFoundPricePlanAdapter.notifyDataSetChanged()
        }
        productFoundPricePlanAdapter.setOnItemClickListener(object: ProductPricePlanFoundAdapter.OnItemClickListener {
            override fun onAddClicked(productCode: String, category: String, position: Int) {
                SetMarkupProduct.newInstance(
                    markupCode = markupCode.toString(),
                    markupDesc = markupDesc.toString(),
                    productCode = productCode,
                    position = position,
                    category = category,
                    isFoundProduct = true
                ).show(supportFragmentManager, "Set Markup Product By Search")
            }
            override fun onCancelClicked(productCode: String) {
                viewModel.onTriggerEvent(FindProductEvent.RemoveMarkupProduct(productCode))
            }
            override fun onTextChangeListener(productCode: String, newMarkup: String) {
                viewModel.onTriggerEvent(
                    FindProductEvent.ChangeProductMarkup(
                        productCode = productCode, markupValue = newMarkup
                    )
                )
            }
        })
    }

    private fun collectProductPricePlanFound() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loadingSearch.collectLatest { binding.progressBar.isVisible = it }
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
                                Toast.makeText(this@ProductFindedActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(FindProductEvent.RemoveHeadMessageSearch)
                        }
                    }
                }
                launch {
                    viewModel.addMarkupProduct.collectLatest { data ->
                        with(binding) {
                            btnSaveChange.isEnabled = data.isNotEmpty()
                            wrapperSummary.isVisible = data.isNotEmpty()
                            summaryAdd.clear()
                            summaryAdd.addAll(data)

                            tvCountProductAdded.text = getString(R.string.count_product_added, data.size.toString())
                            productFoundPricePlanAdapter.setIsAddingProduct(data)
                            productFoundPricePlanAdapter.notifyDataSetChanged()
                        }
                    }
                }

                // Add Markup Product
                launch {
                    viewModel.loadingCreate.collectLatest {
                        if (it) {
                            binding.btnSaveChange.showLoading()
                            return@collectLatest
                        }
                        binding.btnSaveChange.hideProgress(getString(R.string.save_changes))
                    }
                }
                launch {
                    viewModel.createReplaceMarkup.collectLatest {
                        if (it != null && it) {
                            binding.btnSaveChange.isEnabled = it
                            viewModel.onTriggerEvent(FindProductEvent.ClearProductMarkup)
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.setting_markup_successfully))
                                .image(R.drawable.illustration_success2)
                                .description(getString(R.string.add_product_create_price_plan_successfully, markupCode?.replace("_", " ")))
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.close),
                                        onBtnClicked = {
                                            finish()
                                        }
                                    )
                                )
                                .show(supportFragmentManager, "Add Markup Product Successfully")
                        }
                    }
                }
                launch {
                    viewModel.errorCreate.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@ProductFindedActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(FindProductEvent.RemoveCreateMessage)
                        }
                    }
                }
            }
        }
    }

    private fun bindToView(productFound: List<MarkupProductResponse>) {
        with(binding) {
            if (productFound.isEmpty()) {
                txtProductFound.text = getString(R.string.search_product_not_found)
                txtProductFound.setTextColor(Color.parseColor("#FF3822"))
                return
            }
            txtProductFound.text = getString(R.string.search_founded)
            productFoundPricePlanAdapter.setProducts(productFound)
            productFoundPricePlanAdapter.notifyDataSetChanged()
        }
    }

    private fun onActivityFinish() {
        GenericConfirmDialogFragment.Builder()
            .title(getString(R.string.confirm))
            .description(getString(R.string.confirm_close_layout))
            .buttonPositive(
                PositiveButton(
                    backgroundButton = R.drawable.bg_red600_rounded_12,
                    buttonText = getString(R.string.sign_out),
                    buttonTextColor = Color.parseColor("#FFFFFF"),
                    onBtnAction = {
                        viewModel.onTriggerEvent(FindProductEvent.ClearProductMarkup)
                        finish()
                    }
                )
            )
            .buttonNegative(
                NegativeButton(
                    backgroundButton = R.drawable.bg_red50_rounded_12,
                    buttonText = getString(R.string.cancel),
                    buttonTextColor = Color.parseColor("#FF6150"),
                    actionDismiss = true
                )
            ).show(supportFragmentManager, "Leave Activity")
    }

    companion object {
        const val LIST_PRODUCT_FOUND_KEY = "list_product_found_key"
        const val KEYWORD_SEARCH_KEY = "keyword_search_key"
    }
}