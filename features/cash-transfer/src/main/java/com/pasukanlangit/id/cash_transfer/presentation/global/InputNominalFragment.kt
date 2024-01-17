package com.pasukanlangit.id.cash_transfer.presentation.global

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.FragmentInputNominalBinding
import com.pasukanlangit.id.cash_transfer.domain.model.GlobalBankSavedResponse
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.getDecimalFormattedString
import com.pasukanlangit.id.core.utils.parcelable

class InputNominalFragment(
    private val event: OnButtonNextClickListener
) : BottomSheetDialogFragment() {

    private var _binding: FragmentInputNominalBinding? = null
    private val binding get() = _binding!!

    private var isEnabledButton = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputNominalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val globalBank = arguments?.parcelable<GlobalBankSavedResponse>(VALUE_GLOBAL_BANK_KEY)
        this.isCancelable = false
        globalBank?.let { value ->
            with(binding) {
                icClose.setOnClickListener { dismiss() }
                Glide.with(requireContext())
                    .load(value.countryImgUrl)
                    .error(R.drawable.ic_image_default)
                    .into(imgCountryDest)
                tvCurrencyDest.text = value.currency
                edtNominalTransfer.onTextChangedListenerFilter()
                edtNominalTransfer.setText(arguments?.getString(AMOUNT_TRANSFER_KEY))
                btnNext.text = if (arguments?.getBoolean(IS_CHANGE_KEY, false) == true) getString(R.string.submit)
                else getString(R.string.next)
                btnNext.setOnClickListener {
                    val nominal = edtNominalTransfer.text.toString().trim().replace(",", "")
                    if (nominal.toBigInteger() > (20000000).toBigInteger()) {
                        GenericModalDialogCashplus.Builder()
                            .title(getString(R.string.something_wrong))
                            .image(R.drawable.illustration_error)
                            .description(getString(R.string.top_up_balance_error_out_of_capacity))
                            .buttonNegative(
                                NegativeButtonAction(
                                    btnLabel = getString(R.string.close),
                                    backgroundButton = R.drawable.bg_transparent_border_primary_rounded_16,
                                    buttonTextColor = Color.parseColor("#1570EF"),
                                    setClickOnDismiss = true
                                )
                            ).show(requireActivity().supportFragmentManager, "Error out of capacity")
                        return@setOnClickListener
                    }
                    event.onConversionNominalListener(globalBank, nominal)
                    dismiss()
                }
            }
        }
    }

    private fun EditText.onTextChangedListenerFilter() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val input = s.toString()
                if (input.isEmpty()) {
                    isEnabledButton = false
                    return
                }
                if (input.first() == '0') {
                    isEnabledButton = false
                    return
                }
                isEnabledButton = true
            }
            override fun afterTextChanged(s: Editable) {
                binding.btnNext.isEnabled = isEnabledButton
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

    interface OnButtonNextClickListener {
        fun onConversionNominalListener(item: GlobalBankSavedResponse?, amount: String)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val VALUE_GLOBAL_BANK_KEY = "value_global_bank_key"
        private const val AMOUNT_TRANSFER_KEY = "amount_transfer_key"
        private const val IS_CHANGE_KEY = "is_change_key"

        @JvmStatic
        fun newInstance(
            value: GlobalBankSavedResponse?, amount: String? = null,
            isChange: Boolean = false, event: OnButtonNextClickListener
        ) = InputNominalFragment(event).apply {
                arguments = Bundle().apply {
                    putParcelable(VALUE_GLOBAL_BANK_KEY, value)
                    putString(AMOUNT_TRANSFER_KEY, amount)
                    putBoolean(IS_CHANGE_KEY, isChange)
                }
            }
    }
}