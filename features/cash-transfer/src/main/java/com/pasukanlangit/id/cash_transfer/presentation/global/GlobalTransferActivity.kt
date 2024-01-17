package com.pasukanlangit.id.cash_transfer.presentation.global

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.ActivityGlobalTransferBinding
import com.pasukanlangit.id.cash_transfer.domain.model.GlobalBankSavedResponse
import com.pasukanlangit.id.cash_transfer.presentation.global.detail.DetailGlobalTransferActivity
import com.pasukanlangit.id.cash_transfer.presentation.global.find.FindCountryFragment
import com.pasukanlangit.id.core.presentation.components.GenericConfirmDialogFragment
import com.pasukanlangit.id.core.presentation.components.NegativeButton
import com.pasukanlangit.id.core.presentation.components.PositiveButton
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.InputUtil.inputCharNumberFilter
import com.pasukanlangit.id.core.utils.KeyboardUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GlobalTransferActivity : AppCompatActivity(), InputNominalFragment.OnButtonNextClickListener {

    private lateinit var binding: ActivityGlobalTransferBinding
    private val viewModel: GlobalBankViewModel by viewModel()

    private lateinit var initialAdapter: GlobalBankSavedAdapter
    private var isShownClose = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlobalTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        setupRecyclerView()
        with(binding) {
            imgBack.setOnClickListener { finish() }
            edtSearchGlobalBankSaved.filters = arrayOf(InputFilter.LengthFilter(24), inputCharNumberFilter)
            edtSearchGlobalBankSaved.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (::initialAdapter.isInitialized) initialAdapter.filter.filter(s.toString())
                    if (!s.isNullOrEmpty()) {
                        isShownClose = true
                        edtSearchGlobalBankSaved.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_close, 0)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            edtSearchGlobalBankSaved.setOnTouchListener { _, event ->
                val drawableEnd = 2
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (binding.edtSearchGlobalBankSaved.right - binding.edtSearchGlobalBankSaved.compoundDrawables[drawableEnd].bounds.width())) {
                        if (isShownClose) {
                            with(binding) {
                                edtSearchGlobalBankSaved.text?.clear()
                                edtSearchGlobalBankSaved.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_search, 0)
                                KeyboardUtil.closeKeyboardDialog(this@GlobalTransferActivity, edtSearchGlobalBankSaved)
                                isShownClose = !isShownClose
                                return@setOnTouchListener true
                            }
                        }
                    }
                }
                false
            }
            btnCreateNow.setOnClickListener {
                FindCountryFragment().show(supportFragmentManager, "Find Banks Country")
            }
            btnAddList.setOnClickListener {
                FindCountryFragment().show(supportFragmentManager, "Find Banks Country")
            }
        }
        collectDataBankSaved()
    }

    private fun setupRecyclerView() {
        initialAdapter = GlobalBankSavedAdapter()
        with(binding.rvGlobalBankSaved) {
            layoutManager = LinearLayoutManager(this@GlobalTransferActivity)
            adapter = initialAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
        initialAdapter.setOnItemClickListener(object: GlobalBankSavedAdapter.OnItemClickListener {
            override fun onItemClicked(item: GlobalBankSavedResponse) {
                InputNominalFragment.newInstance(value = item, event = this@GlobalTransferActivity)
                    .show(supportFragmentManager, "Input Amount Transfer")
            }

            override fun onDeleteItemClicked(bankCode: String, bankAccNum: String) {
                GenericConfirmDialogFragment.Builder()
                    .title(getString(R.string.confirm))
                    .description(getString(R.string.confirm_delete_bank_acc))
                    .buttonPositive(
                        PositiveButton(
                            backgroundButton = R.drawable.bg_red600_rounded_12,
                            buttonText = getString(R.string.yes),
                            buttonTextColor = Color.parseColor("#F8FAFC"),
                            onBtnAction = {
                                viewModel.onTriggerEvent(
                                    GlobalBankEvent.DeleteGlobalBankSaved(
                                        bankCode = bankCode, bankAccNum = bankAccNum
                                    )
                                )
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
                    )
                    .show(supportFragmentManager)
            }
        })
    }

    private fun collectDataBankSaved() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.globalBankLoading.collectLatest { binding.progressBar.isVisible = it }
                }
                launch {
                    viewModel.globalBank.collectLatest {
                        it?.let { response ->
                            with(binding) {
                                wrapperEmptyGlobalBankSaved.isVisible = response.isEmpty()
                                wrapperListGlobalBank.isVisible = response.isNotEmpty()
                                lineBreaker.isVisible = response.isNotEmpty()
                                btnAddList.isVisible = response.isNotEmpty()
                                if (response.isNotEmpty()) {
                                    initialAdapter.setGlobalBankSaved(response)
                                    if (response.size > 4) {
                                        val params = binding.wrapperListGlobalBank.layoutParams as ConstraintLayout.LayoutParams
                                        params.height = 0
                                        params.bottomToTop = binding.lineBreaker.id
                                        binding.wrapperListGlobalBank.requestLayout()
                                    }
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.globalBankError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@GlobalTransferActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(GlobalBankEvent.RemoveGlobalBankError)
                        }
                    }
                }

                launch {
                    viewModel.deleteGlobalBankLoading.collectLatest { binding.progressBar.isVisible = it }
                }
                launch {
                    viewModel.deleteGlobalBank.collectLatest { response ->
                        if (response != null) {
                            viewModel.onTriggerEvent(GlobalBankEvent.GlobalBankSaved)
                        }
                    }
                }
                launch {
                    viewModel.deleteGlobalBankError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@GlobalTransferActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(GlobalBankEvent.RemoveDeleteGlobalBankSaved)
                        }
                    }
                }
            }
        }
    }

    override fun onConversionNominalListener(item: GlobalBankSavedResponse?, amount: String) {
        startActivity(
            Intent(this, DetailGlobalTransferActivity::class.java).apply {
                putExtra(DetailGlobalTransferActivity.DESTINATION_ACCOUNT_KEY, item)
                putExtra(DetailGlobalTransferActivity.AMOUNT_TRANSFER_KEY, amount)
            }
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(GlobalBankEvent.GlobalBankSaved)
    }
}