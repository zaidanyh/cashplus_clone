package com.pasukanlangit.cashplus.ui.product

import android.annotation.SuppressLint
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
import com.pasukanlangit.cashplus.databinding.ActivityStatusProductBinding
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.DrawablePosition.DRAWABLE_RIGHT
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.ui_downline_home.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatusProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatusProductBinding
    private val viewModel: StatusProductViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private lateinit var statusProductAdapter: StatusProductAdapter
    private var uuid: String? = null
    private var accessToken: String? = null
    private var isShownClose = false
    private var codeProductChoose: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()
        setUpRecyclerView()
        viewModel.getProductStatus(uuid ?: "", accessToken ?: "")
        with(binding) {
            imgBack.setOnClickListener { finish() }
            swiperRefreshProduct.setOnRefreshListener {
                edtSearch.text?.clear()
                viewModel.getProductStatus(uuid ?: "", accessToken ?: "")
                swiperRefreshProduct.isRefreshing = false
            }
            edtSearch.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (::statusProductAdapter.isInitialized) statusProductAdapter.filter.filter(s.toString())
                    if (!s.isNullOrEmpty()) {
                        isShownClose = true
                        edtSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_close, 0)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            edtSearch.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edtSearch.right - edtSearch.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        if (isShownClose) {
                            edtSearch.text?.clear()
                            edtSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_search, 0)
                            KeyboardUtil.hideSoftKeyboard(this@StatusProductActivity)
                            edtSearch.clearFocus()
                            isShownClose = !isShownClose
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }
        }
        collectData()
    }

    private fun setUpRecyclerView() {
        statusProductAdapter = StatusProductAdapter()
        with(binding.rvStatusProduct) {
            layoutManager = LinearLayoutManager(this@StatusProductActivity)
            adapter = statusProductAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
        statusProductAdapter.setOnItemClickListener(object: StatusProductAdapter.OnItemClickListener {
            override fun onItemClicked(productCode: String, defaultPrice: String, outletPrice: String) {
                codeProductChoose = productCode
                UpdatePriceProductFragment.newInstance(productCode, defaultPrice, outletPrice)
                    .show(supportFragmentManager, "Update price product")
            }
        })
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.productLoading.collectLatest {
                        binding.progressLoading.isVisible = it
                        binding.rvStatusProduct.isVisible = !it
                    }
                }
                launch {
                    viewModel.statusProduct.collectLatest { response ->
                        statusProductAdapter.setProductStatus(response)
                    }
                }
                launch {
                    viewModel.productError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@StatusProductActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeStatusProductMessage()
                        }
                    }
                }

                launch {
                    viewModel.updatePriceLoading.collectLatest { binding.progressLoading.isVisible = it }
                }
                launch {
                    viewModel.updatePrice.collectLatest { response ->
                        if (response != null && response) {
                            binding.edtSearch.text?.clear()
                            statusProductAdapter.notifyDataSetChanged()
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.done))
                                .image(R.drawable.illustration_success2)
                                .description(getString(R.string.update_status_product_successfully, codeProductChoose))
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.close),
                                        backgroundButton = R.drawable.bg_primary_rounded_20,
                                        onBtnClicked = {
                                            viewModel.getProductStatus(uuid ?: "", accessToken ?: "")
                                        }
                                    )
                                )
                                .show(supportFragmentManager, "Update price success")
                        }
                    }
                }
                launch {
                    viewModel.updatePriceError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@StatusProductActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeUpdatePriceMessage()
                        }
                    }
                }
            }
        }
    }
}