package com.pasukanlangit.id.cash_transfer.presentation.local.bank_add

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.FragmentListBankBinding
import com.pasukanlangit.id.cash_transfer.domain.model.LocalBankSaved
import com.pasukanlangit.id.cash_transfer.domain.model.LocalBankSavedResponse
import com.pasukanlangit.id.cash_transfer.presentation.local.LocalBankEvent
import com.pasukanlangit.id.cash_transfer.presentation.local.LocalBankViewModel
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.InputUtil.inputAlphabetFilter
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.core.utils.parcelableArrayList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ListBankFragment : Fragment() {

    private var _binding: FragmentListBankBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocalBankViewModel by sharedViewModel()

    private lateinit var bankListAdapter: BankListAdapter
    private lateinit var bankSaved: LocalBankSaved

    private var parent: Fragment? = null
    private var isShownClose = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBankBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parent = (parentFragment as NavHostFragment).parentFragment
        val argBankSaved = arguments?.parcelableArrayList<LocalBankSavedResponse>("bank_saved_list_key")
        bankSaved = LocalBankSaved()
        bankSaved.addAll(argBankSaved?.toList() ?: emptyList())
        viewModel.onTriggerEvent(LocalBankEvent.GetBankList)

        (parent as BankListBottomSheetFragment).setBtnBackIsActive(false, requireContext().getString(R.string.choose_bank_destination))
        setupRecyclerView()
        binding.edtSearchBank.filters = arrayOf(InputFilter.LengthFilter(16), inputAlphabetFilter)
        binding.edtSearchBank.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (::bankListAdapter.isInitialized) bankListAdapter.filter.filter(s.toString())
                if (!s.isNullOrEmpty()) {
                    isShownClose = true
                    binding.edtSearchBank.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_close, 0)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edtSearchBank.setOnTouchListener { _, event ->
            val drawableEnd = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.edtSearchBank.right - binding.edtSearchBank.compoundDrawables[drawableEnd].bounds.width())) {
                    if (isShownClose) {
                        with(binding) {
                            edtSearchBank.text?.clear()
                            edtSearchBank.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_search, 0)
                            KeyboardUtil.closeKeyboardDialog(requireContext(), edtSearchBank)
                            edtSearchBank.clearFocus()
                            isShownClose = !isShownClose
                            return@setOnTouchListener true
                        }
                    }
                }
            }
            false
        }
        collectBankList()
    }

    private fun setupRecyclerView() {
        bankListAdapter = BankListAdapter { bank ->
            KeyboardUtil.closeKeyboardDialog(requireContext(), binding.edtSearchBank)
            val extras = FragmentNavigatorExtras(binding.root to "find_bank")
            findNavController().navigate(ListBankFragmentDirections.listBankToRegisBank(bank, bankSaved), extras)
        }
        with(binding.rvBankList) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bankListAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    private fun collectBankList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.bankListLoading.collectLatest {
                        binding.progressBar.isVisible = it
                    }
                }
                launch {
                    viewModel.bankList.collectLatest { response ->
                        if (response != null) bankListAdapter.setBankList(response)
                        bankListAdapter.notifyDataSetChanged()
                    }
                }
                launch {
                    viewModel.bankListError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(LocalBankEvent.RemoveBankListError)
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