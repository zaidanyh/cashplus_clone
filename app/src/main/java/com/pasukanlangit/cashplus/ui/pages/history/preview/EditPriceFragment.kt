package com.pasukanlangit.cashplus.ui.pages.history.preview

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentEditPriceBinding
import com.pasukanlangit.cashplus.ui.pages.history.HistoryDetailActivity
import com.pasukanlangit.cashplus.ui.pages.history.HistoryDetailViewModel
import com.pasukanlangit.id.core.extensions.setOnlyNumberAllowed
import com.pasukanlangit.id.core.utils.getDecimalFormattedString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPriceFragment(
    private val event: OnButtonSetPriceClickListener
) : BottomSheetDialogFragment() {

    private var _binding: FragmentEditPriceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryDetailViewModel by viewModel()

    private var uuid: String? = null
    private var accessToken: String? = null
    private var sellingPriceState = false
    private var defaultPrice: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPriceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productCode = arguments?.getString(PRODUCT_CODE_KEY)
        defaultPrice = arguments?.getString(PRICE_DEFAULT_KEY)
        val isTrxPay = arguments?.getBoolean(IS_TRANSACTION_PAY_KEY)
        uuid = (activity as HistoryDetailActivity).sharedPref.getUUID()
        accessToken = (activity as HistoryDetailActivity).sharedPref.getAccessToken()
        if (productCode?.contains("unit", ignoreCase = true) == false)
            viewModel.getSellingPrice(uuid = uuid, codeProduct = productCode.toString(), accessToken = accessToken)
        with(binding) {
            if (productCode?.contains("unit", ignoreCase = true) == true)
                edtSellingPrice.setText(defaultPrice)
            if (isTrxPay == true) {
                txtSellingPrice.text = getString(R.string.service_fee)
                edtSellingPrice.hint = getString(R.string.input_fee_service)
                txtDescSellingPrice.text = getString(R.string.desc_service_fee)
            }
            edtSellingPrice.setOnlyNumberAllowed()
            edtSellingPrice.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString()
                    if (value.isEmpty()) {
                        inputSellingPrice.isErrorEnabled = true
                        inputSellingPrice.error = getString(
                            R.string.input_required,
                            if (isTrxPay == true) getString(R.string.service_fee)
                            else getString(R.string.selling_price)
                        )
                        sellingPriceState = false
                        return
                    }
                    if (value.replace(",", "").length > 9) {
                        inputSellingPrice.isErrorEnabled = true
                        inputSellingPrice.error = getString(R.string.top_up_balance_error_out_of_capacity)
                        sellingPriceState = false
                        return
                    }
                    inputSellingPrice.isErrorEnabled = false
                    inputSellingPrice.error = null
                    sellingPriceState = true
                }
                override fun afterTextChanged(s: Editable?) {
                    btnActionStruck.isEnabled = sellingPriceState
                    try {
                        edtSellingPrice.removeTextChangedListener(this)
                        val value = edtSellingPrice.text.toString()
                        if (value.isNotEmpty()) {
                            if (value.startsWith("0")) {
                                edtSellingPrice.setText("")
                            }
                            val str = edtSellingPrice.text.toString().replace(",", "")
                            if (value.isNotEmpty()) edtSellingPrice.setText(getDecimalFormattedString(str))
                            edtSellingPrice.setSelection(edtSellingPrice.text.toString().length)
                        }
                        edtSellingPrice.addTextChangedListener(this)
                        return
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        edtSellingPrice.addTextChangedListener(this)
                    }
                }
            })
            btnActionStruck.setOnClickListener {
                val price = edtSellingPrice.text.toString().replace(",", "")
                event.onButtonSetPriceClicked(price)
                this@EditPriceFragment.dismiss()
            }
        }
        collectSellingPrice()
    }

    private fun collectSellingPrice() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.sellingLoading.collectLatest {
                        with(binding) {
                            this@EditPriceFragment.isCancelable = !it
                            edtSellingPrice.isEnabled = !it
                            btnActionStruck.isEnabled = !it
                        }
                    }
                }
                launch {
                    viewModel.sellingPrice.collectLatest { response ->
                        if (response != null) binding.edtSellingPrice.setText(response.outletPrice)
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.contains("tidak ditemukan") || info.contains("Setting price")) {
                                binding.edtSellingPrice.setText(defaultPrice)
                                viewModel.removePrintError()
                                return@collectLatest
                            }
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removePrintError()
                        }
                    }
                }
            }
        }
    }

    interface OnButtonSetPriceClickListener {
        fun onButtonSetPriceClicked(newPrice: String)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val PRODUCT_CODE_KEY = "product_code_key"
        private const val PRICE_DEFAULT_KEY = "price_default_key"
        private const val IS_TRANSACTION_PAY_KEY = "is_transaction_pay_key"

        @JvmStatic
        fun newInstance(productCode: String, defaultPrice: String?, isTrxPay: Boolean = false, event: OnButtonSetPriceClickListener) =
            EditPriceFragment(event).apply {
                arguments = Bundle().apply {
                    putString(PRODUCT_CODE_KEY, productCode)
                    putString(PRICE_DEFAULT_KEY, defaultPrice)
                    putBoolean(IS_TRANSACTION_PAY_KEY, isTrxPay)
                }
            }
    }
}