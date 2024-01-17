package com.pasukanlangit.cashplus.ui.pembayaran_bulanan

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentPembayaranMultifinanceBinding
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.PembayaranBulananViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.InputUtil.inputCharNumberFilter
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.utils.Constants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PembayaranMultiFinanceFragment : Fragment() {

    private var _binding: FragmentPembayaranMultifinanceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PembayaranBulananViewModel by viewModel()
    private val sharedPrefDataSource: SharedPrefDataSource by inject()

    private lateinit var productsModel: List<ProductModel>
    private var destination: String? = null
    private var productSelected: ProductModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPembayaranMultifinanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uuid = sharedPrefDataSource.getUUID()
        val token = sharedPrefDataSource.getAccessToken()

        if (uuid != null && token != null) {
            val productRequest = ProductRequest(
                uuid = uuid,
                category = "#PEMBAYARAN",
                kode_provider = "MULTI_FINANCE",
                is_faktur = "",
                is_active = "1"
            )

            viewModel.getProductFinance(productRequest, token)
        }

        viewModel.tagihanFinance.observe(viewLifecycleOwner) { response ->
            if (response.status == Status.SUCCESS) {
                val listProvider = arrayListOf(Constants.NO_PROVIDER)
                if (!response.data?.data.isNullOrEmpty()) {
                    productsModel = response.data?.data!!
                    listProvider.clear()
                    response.data.data.forEach { product ->
                        listProvider.add(product.dsc)
                    }
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner_small_selected, listProvider)
                adapter.setDropDownViewResource(R.layout.item_spinner_small)
                binding.financeListSpinner.adapter = adapter
            }
        }

        with(binding) {
            edtNomerMultifinance.filters = arrayOf(InputFilter.LengthFilter(30), inputCharNumberFilter)
            btnCheckFinance.setUpToProgressButton(viewLifecycleOwner)
            btnCheckFinance.setOnClickListener {
                val operator = financeListSpinner.selectedItem.toString()
                val itemSelected = financeListSpinner.selectedItemPosition
                val dest = edtNomerMultifinance.text.toString().trim()

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

                destination = dest
                productSelected = productsModel[itemSelected]
                viewModel.payBill(
                    TransactionRequest(
                        uuid = uuid ?: "", kode_produk = productSelected?.kode_produk?.replaceRange(0, 3, "TAG").toString(),
                        dest = destination ?: "", pin = ""
                    ), accessToken = token ?: ""
                )
            }
        }

        observeTAGMultifinance()
    }

    private fun observeTAGMultifinance() {
        viewModel.billTrx.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                when(response.status) {
                    Status.LOADING -> binding.btnCheckFinance.showLoading()
                    Status.SUCCESS -> {
                        binding.btnCheckFinance.hideProgress(getString(R.string.check))

                        BillDetailFragment.newInstance(
                            destination, response.data,
                            productSelected?.img_url_2?.ifEmpty { productSelected?.img_url },
                            R.drawable.ic_tab_menu_multi_finance
                        ).show(childFragmentManager, "Bill Detail")
                    }
                    Status.ERROR -> {
                        binding.btnCheckFinance.hideProgress(getString(R.string.check))

                        GenericModalDialogCashplus.Builder()
                            .title(getString(R.string.something_wrong))
                            .image(R.drawable.illustration_login_error)
                            .description(
                                if (
                                    response.message?.contains("format salah", ignoreCase = true) == true ||
                                    response.message?.contains("tidak dikenal", ignoreCase = true) == true ||
                                    response.message?.contains("general error", ignoreCase = true) == true
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}