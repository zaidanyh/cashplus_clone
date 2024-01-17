package com.pasukanlangit.cashplus.ui.pembayaran_bulanan

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.FragmentPembayaranKreditBinding
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.id.cash_transfer.presentation.local.transfer.NominalTransferAdapter
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.PembayaranBulananViewModel
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.getDecimalFormattedString
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.utils.Constants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PembayaranKreditFragment : Fragment() {

    private var _binding: FragmentPembayaranKreditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PembayaranBulananViewModel by viewModel()
    private val sharedPrefDataSource: SharedPrefDataSource by inject()

    private lateinit var productsModel: List<ProductModel>
    private lateinit var nominalTransferAdapter: NominalTransferAdapter
    private val nominalTransfer = listOf("50,000", "100,000", "200,000", "500,000", "1,000,000", "2,000,000")
    private var stateNominal = false
    private var destination: String? = null
    private var productSelected: ProductModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPembayaranKreditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uuid = sharedPrefDataSource.getUUID()
        val token = sharedPrefDataSource.getAccessToken()

        setupNominalList()
        binding.edtNominal.onTextChangedListenerFilter()
        if (uuid != null && token != null) {
            val productRequest = ProductRequest(
                uuid = uuid,
                category = "#PEMBAYARAN",
                kode_provider = "KARTU_KREDIT",
                is_faktur = "",
                is_active = "1"
            )

            viewModel.getProductCredit(productRequest, token)
        }

        viewModel.tagihanCredit.observe(viewLifecycleOwner) { response ->
            if (response.status == Status.SUCCESS) {
                val listProvider = arrayListOf(Constants.NO_PROVIDER)
                if (!response.data?.data.isNullOrEmpty()) {
                    productsModel = response.data?.data!!
                    listProvider.clear()
                    response.data.data.forEach { product ->
                        listProvider.add(product.short_dsc)
                    }
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner_small_selected, listProvider)
                adapter.setDropDownViewResource(R.layout.item_spinner_small)
                binding.creditListSpinner.adapter = adapter
            }
        }

        with(binding) {
            btnCheckCredit.setUpToProgressButton(viewLifecycleOwner)
            btnCheckCredit.setOnClickListener {
                val operator = creditListSpinner.selectedItem?.toString()
                val itemSelected = creditListSpinner.selectedItemPosition
                val dest = edtNomerKredit.text.toString().trim()
                val nominal = edtNominal.text.toString().trim()

                if (dest.isEmpty()) {
                    GenericModalDialogCashplus.Builder()
                        .title(getString(R.string.something_wrong))
                        .image(R.drawable.illustration_empty)
                        .description(getString(R.string.input_id_first))
                        .buttonNegative(
                            NegativeButtonAction(
                                btnLabel = getString(R.string.close),
                                backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                buttonTextColor = Color.parseColor("#0A57FF"),
                                setClickOnDismiss = true
                            )
                        )
                        .show(requireActivity().supportFragmentManager, "CustomerId_is_empty")
                    return@setOnClickListener
                }
                if (nominal.isEmpty()) {
                    GenericModalDialogCashplus.Builder()
                        .title(getString(R.string.something_wrong))
                        .image(R.drawable.illustration_empty)
                        .description(getString(R.string.input_required, getString(R.string.nominal)))
                        .buttonNegative(
                            NegativeButtonAction(
                                btnLabel = getString(R.string.close),
                                backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                buttonTextColor = Color.parseColor("#0A57FF"),
                                setClickOnDismiss = true
                            )
                        )
                        .show(requireActivity().supportFragmentManager, "CustomerId_is_empty")
                    return@setOnClickListener
                }
                if (operator == Constants.NO_PROVIDER) {
                    GenericModalDialogCashplus.Builder()
                        .title(getString(R.string.something_wrong))
                        .image(R.drawable.illustration_empty)
                        .description(Constants.NO_PROVIDER)
                        .buttonNegative(
                            NegativeButtonAction(
                                btnLabel = getString(R.string.close),
                                backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                buttonTextColor = Color.parseColor("#0A57FF"),
                                setClickOnDismiss = true
                            )
                        )
                        .show(requireActivity().supportFragmentManager, "no_provider_available")
                    return@setOnClickListener
                }

                destination = "$dest-$nominal"
                productSelected = productsModel[itemSelected]
                viewModel.payBill(
                    TransactionRequest(
                        uuid = uuid ?: "", kode_produk = productSelected?.kode_produk?.replaceRange(0, 3, "TAG").toString(),
                        dest = destination ?: "", pin = ""
                    ), accessToken = token ?: ""
                )
            }
        }

        observeTAGCreditCard()
    }

    private fun observeTAGCreditCard() {
        viewModel.billTrx.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                when(response.status) {
                    Status.LOADING -> binding.btnCheckCredit.showLoading()
                    Status.SUCCESS -> {
                        binding.btnCheckCredit.hideProgress(getString(R.string.check))

                        BillDetailFragment.newInstance(
                            destination, response.data,
                            productSelected?.img_url_2?.ifEmpty { productSelected?.img_url },
                            R.drawable.ic_tab_menu_kartu_kredit
                        ).show(childFragmentManager, "Bill Detail")
                    }
                    Status.ERROR -> {
                        binding.btnCheckCredit.hideProgress(getString(R.string.check))

                        GenericModalDialogCashplus.Builder()
                            .title(getString(R.string.something_wrong))
                            .image(R.drawable.illustration_login_error)
                            .description(
                                if (
                                    response.message?.contains("format salah", ignoreCase = true) == true ||
                                    response.message?.contains("tidak dikenal", ignoreCase = true) == true
                                ) getString(R.string.any_not_found, getString(R.string.customer_id))
                                else if (
                                    response.message?.contains("tagihan tidak", ignoreCase = true) == true
                                ) getString(R.string.unavailable_bill)
                                else if (
                                    response.message?.contains("diroute", ignoreCase = true) == true
                                ) getString(R.string.product_is_unavailable_contact_cs_please)
                                else if (
                                    response.message?.contains("terbayar", ignoreCase = true) == true
                                ) getString(R.string.bill_paid)
                                else response.message.toString()
                            )
                            .buttonNegative(
                                NegativeButtonAction(
                                    btnLabel = getString(R.string.close),
                                    backgroundButton = R.drawable.bg_transparent_border_primary_rounded_16,
                                    buttonTextColor = Color.parseColor("#1570EF"),
                                    setClickOnDismiss = true
                                )
                            )
                            .show(childFragmentManager, "wrong format")
                    }
                }
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
                    binding.inputNominal.error = "Nominal transfer perlu diisi"
                    stateNominal = false
                    return
                }
                stateNominal = true
                binding.inputNominal.error = null
            }
            override fun afterTextChanged(s: Editable) {
                binding.btnCheckCredit.isEnabled = stateNominal
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
}