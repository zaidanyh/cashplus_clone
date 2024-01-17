package com.pasukanlangit.id.cash_transfer.presentation.global.find

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.FragmentFindCountryBinding
import com.pasukanlangit.id.cash_transfer.domain.model.FetchCountryResponse
import com.pasukanlangit.id.cash_transfer.presentation.global.beneficiary.BeneficiaryAccountActivity
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.InputUtil.inputAlphabetFilter
import com.pasukanlangit.id.core.utils.KeyboardUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FindCountryFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFindCountryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FindBankCountryViewModel by viewModel()

    private lateinit var findCountryAdapter: FindCountryAdapter

    private var isShownClose = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindCountryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        viewModel.onTriggerEvents(FindBankCountryEvents.GetCountries)
        with(binding) {
            edtFindDestCountry.filters = arrayOf(InputFilter.LengthFilter(16), inputAlphabetFilter)
            edtFindDestCountry.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (::findCountryAdapter.isInitialized) findCountryAdapter.filter.filter(s.toString())
                    if (!s.isNullOrEmpty()) {
                        isShownClose = true
                        edtFindDestCountry.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_close, 0)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            edtFindDestCountry.setOnTouchListener { _, event ->
                val drawableEnd = 2
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edtFindDestCountry.right - edtFindDestCountry.compoundDrawables[drawableEnd].bounds.width())) {
                        if (isShownClose) {
                            edtFindDestCountry.text?.clear()
                            edtFindDestCountry.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_search, 0)
                            KeyboardUtil.closeKeyboardDialog(requireContext(), edtFindDestCountry)
                            isShownClose = !isShownClose
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }
        }
        collectDataCountries()
    }

    private fun setupRecyclerView() {
        findCountryAdapter = FindCountryAdapter()
        with(binding.rvCountries) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = findCountryAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
        findCountryAdapter.setOnItemClickListener(object: FindCountryAdapter.OnItemClickListener {
            override fun onItemClicked(item: FetchCountryResponse) {
                startActivity(
                    Intent(requireContext(), BeneficiaryAccountActivity::class.java).apply {
                        putExtra(BeneficiaryAccountActivity.COUNTRY_CODE_SELECTED_RESULT_KEY, item)
                    }
                )
                this@FindCountryFragment.dismiss()
            }
        })
    }

    private fun collectDataCountries() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateLoading.collectLatest {
                        binding.progressbar.isVisible = it
                        binding.gapBottom.isVisible = it
                    }
                }
                launch {
                    viewModel.countries.collectLatest { response ->
                        if (response != null) findCountryAdapter.setCountries(response)
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvents(FindBankCountryEvents.RemoveHeadMessageError)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}