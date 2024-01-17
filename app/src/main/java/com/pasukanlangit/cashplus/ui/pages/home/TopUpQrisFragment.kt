package com.pasukanlangit.cashplus.ui.pages.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentTopUpQrisBinding
import com.pasukanlangit.id.cash_transfer.presentation.local.transfer.NominalTransferAdapter
import com.pasukanlangit.id.core.utils.getDecimalFormattedString
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class TopUpQrisFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTopUpQrisBinding? = null
    private val binding get() = _binding!!

    private lateinit var nominalTransferAdapter: NominalTransferAdapter
    private val nominalTransfer = listOf("50,000", "100,000", "200,000", "500,000", "1,000,000", "2,000,000")

    private var stateNominal = false
    private var balance: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpQrisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        balance = (arguments?.getDouble(ARG_BALANCE_CASHPLUS) ?: 0).toInt()
        setupNominalList()

        with(binding) {
            icClose.setOnClickListener { dismiss() }
            tvSaldo.text = getNumberRupiah(balance)
            edtNominal.onTextChangedListenerFilter()
            btnSend.setOnClickListener {
                val nominalTopUp = edtNominal.text.trim().toString().replace(",", "")
                EnterPINTopUpQrisFragment
                    .newInstance(nominalTopUp)
                    .show(requireActivity().supportFragmentManager, "Enter PIN Top UP QRIS")
                this@TopUpQrisFragment.dismiss()
            }
        }
    }

    private fun setupNominalList() {
        nominalTransferAdapter = NominalTransferAdapter()
        with(binding.rvNominal) {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = nominalTransferAdapter
        }
        nominalTransferAdapter.setNominalList(nominalTransfer)
        nominalTransferAdapter.setOnItemClickListener(object: NominalTransferAdapter.OnItemClickListener {
            override fun onItemClicked(item: String) {
                binding.edtNominal.setText(item)
            }
        })
    }

    private fun EditText.onTextChangedListenerFilter() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val input = s.toString()
                if (input.isEmpty()) {
                    binding.inputNominalTransfer.error = "Nominal transfer perlu diisi"
                    stateNominal = false
                    return
                }
                if (balance < input.replace(",", "").toInt()) {
                    binding.inputNominalTransfer.error = "Saldo anda tidak mencukupi"
                    stateNominal = false
                    return
                }
                stateNominal = true
                binding.inputNominalTransfer.error = null
            }
            override fun afterTextChanged(s: Editable) {
                binding.btnSend.isEnabled = stateNominal
                val editText = this@onTextChangedListenerFilter
                try {
                    editText.removeTextChangedListener(this)
                    val value = editText.text.toString()
                    if (value != "") {
                        if (value.startsWith(".")) {
                            editText.setText("0.")
                        }
                        if (value.startsWith("0") && !value.startsWith("0.")) {
                            editText.setText("")
                        }
                        val str: String = editText.text.toString().replace(",", "")
                        if (value.isNotEmpty()) editText.setText(getDecimalFormattedString(str))
                        editText.setSelection(editText.text.toString().length)
                        nominalTransferAdapter.setOnItemClickSelected(getDecimalFormattedString(str))
                    }
                    editText.addTextChangedListener(this)
                    return
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    editText.addTextChangedListener(this)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_BALANCE_CASHPLUS = "arg_balance_cashplus"

        @JvmStatic
        fun newInstance(balance: Double) =
            TopUpQrisFragment().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_BALANCE_CASHPLUS, balance)
                }
            }
    }
}