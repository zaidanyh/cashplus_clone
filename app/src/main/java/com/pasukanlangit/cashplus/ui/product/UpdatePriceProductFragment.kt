package com.pasukanlangit.cashplus.ui.product

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentUpdatePriceProductBinding
import com.pasukanlangit.cashplus.domain.model.UpdateSellingPriceRequest
import com.pasukanlangit.cashplus.utils.MyUtils.convertToNormalNumber
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.getDecimalFormattedString
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UpdatePriceProductFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentUpdatePriceProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatusProductViewModel by sharedViewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null

    private var stateInputChangePrice = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdatePriceProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val codeProduct = arguments?.getString(CODE_PRODUCT_KEY_UPDATE)
        val defaultPrice = arguments?.getString(DEFAULT_PRICE_KEY)
        val outletPrice = arguments?.getString(OUTLET_PRICE_KEY)
        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()

        with(binding) {
            imgCloseDialog.setOnClickListener { this@UpdatePriceProductFragment.dismiss() }
            edtUpdatePrice.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        stateInputChangePrice = false
                        inputUpdatePrice.error = getString(R.string.input_required, getString(R.string.outlet_price))
                        return
                    }

                    stateInputChangePrice = true
                    inputUpdatePrice.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSaveChangePrice.isEnabled = stateInputChangePrice
                    try {
                        edtUpdatePrice.removeTextChangedListener(this)
                        val value = edtUpdatePrice.text.toString()
                        if (value.isNotEmpty()) {
                            if (value.startsWith(".")) edtUpdatePrice.setText("0.")
                            if (value.startsWith("0") && !value.startsWith("0."))
                                edtUpdatePrice.setText("")
                            val newValue = edtUpdatePrice.text.toString().replace(",", "")
                            if (value.isNotEmpty()) edtUpdatePrice.setText(getDecimalFormattedString(newValue))
                            edtUpdatePrice.setSelection(edtUpdatePrice.text.toString().length)
                        }
                        edtUpdatePrice.addTextChangedListener(this)
                        return
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("InvalidFormat", e.message.toString())
                        edtUpdatePrice.addTextChangedListener(this)
                    }
                }
            })
            edtUpdatePrice.setText(outletPrice)
            btnSaveChangePrice.setOnClickListener {
                val newPrice = convertToNormalNumber(edtUpdatePrice.text.toString())
                if (newPrice.toInt() < defaultPrice?.toIntOrNull()!!) {
                    Toast.makeText(requireContext(), getString(R.string.selling_price_not_allowed_capital_price), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val request = UpdateSellingPriceRequest(
                    uuid = uuid ?: "", productCode = codeProduct ?: "", newPrice = newPrice.toString()
                )
                viewModel.updateSellingPrice(request = request, accessToken = accessToken ?: "")
                this@UpdatePriceProductFragment.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val CODE_PRODUCT_KEY_UPDATE = "code_product_key_update"
        private const val DEFAULT_PRICE_KEY = "default_price_key"
        private const val OUTLET_PRICE_KEY = "outlet_price_key"

        @JvmStatic
        fun newInstance(codeProduct: String, defaultPrice: String, outletPrice: String) =
            UpdatePriceProductFragment().apply {
                arguments = Bundle().apply {
                    putString(CODE_PRODUCT_KEY_UPDATE, codeProduct)
                    putString(DEFAULT_PRICE_KEY, defaultPrice)
                    putString(OUTLET_PRICE_KEY, outletPrice)
                }
            }
    }
}