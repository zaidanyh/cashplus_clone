package com.pasukanlangit.cashplus.ui.pembayaran_bulanan

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.FragmentPembayaranTelkomBinding
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.PembayaranBulananViewModel
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.utils.Constants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PembayaranTelkomFragment : Fragment() {

    private var _binding: FragmentPembayaranTelkomBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PembayaranBulananViewModel by viewModel()
    private val sharedPrefDataSource : SharedPrefDataSource by inject()

    private lateinit var productsModel: List<ProductModel>
    private var productSelected: ProductModel? = null
    private var destination: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPembayaranTelkomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uuid = sharedPrefDataSource.getUUID()
        val token = sharedPrefDataSource.getAccessToken()

        if(uuid != null && token != null){
            val productRequest = ProductRequest(
                uuid = uuid,
                category = "#PEMBAYARAN",
                kode_provider = "TELKOM",
                is_faktur = "",
                is_active = "1"
            )

            viewModel.getProductTelkom(productRequest, token)
        }

        viewModel.tagihanTelkom.observe(viewLifecycleOwner) { response ->
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
                binding.telkomListSpinner.adapter = adapter
            }
        }

        with(binding) {
            btnCheckTelkom.setUpToProgressButton(viewLifecycleOwner)
            btnCheckTelkom.setOnClickListener {
                val operator = telkomListSpinner.selectedItem.toString()
                val itemSelected = telkomListSpinner.selectedItemPosition
                val dest = edtNomerTelkom.text.toString().trim()

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

        observeTAGTelkom()
    }

    private fun observeTAGTelkom() {
        viewModel.billTrx.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                when(response.status) {
                    Status.LOADING -> binding.btnCheckTelkom.showLoading()
                    Status.SUCCESS -> {
                        binding.btnCheckTelkom.hideProgress(getString(R.string.check))

                        BillDetailFragment.newInstance(
                            destination, response.data,
                            productSelected?.img_url_2?.ifEmpty { productSelected?.img_url },
                            R.drawable.ic_tab_menu_tellkom
                        ).show(childFragmentManager, "Bill Detail")
                    }
                    Status.ERROR -> {
                        binding.btnCheckTelkom.hideProgress(getString(R.string.check))

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}