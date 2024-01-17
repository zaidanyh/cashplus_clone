package com.pasukanlangit.cashplus.ui.pembayaran_pln

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.adapter.PulsaAdapter
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.FragmentTopUpTokenPLNBinding
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.TopUpPLNViewModel
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.InputUtil.inputNumericFilter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopUpTokenPLNFragment : Fragment() {

    private var _binding: FragmentTopUpTokenPLNBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TopUpPLNViewModel by viewModel()
    private val sharedPrefDataSource : SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null
    private var destination: String? = null
    private var productSelected: ProductModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTopUpTokenPLNBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uuid = sharedPrefDataSource.getUUID()
        accessToken = sharedPrefDataSource.getAccessToken()

        with(binding) {
            edtNumberPlnToken.filters = arrayOf(InputFilter.LengthFilter(16), inputNumericFilter)
            rvTokenpln.layoutManager = GridLayoutManager(activity, 2)
            rvTokenpln.addItemDecoration(CashplusItemDecoration(24))

            if (uuid != null && accessToken != null) {
                val productRequest = ProductRequest(
                    uuid = uuid ?: "",
                    category = "#TOKEN",
                    kode_provider = "PLN",
                    is_faktur = "",
                    is_active = "1"
                )

                viewModel.getProductPln(productRequest, accessToken ?: "")
            }
        }
        observeTokenPLN()
        observeDetailToken()
    }

    private fun observeTokenPLN() {
        viewModel.topUpPln.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    with(binding) {
                        rvTokenplnShimmer.isVisible = false
                        rvTokenplnShimmer.stopShimmer()
                        rvTokenpln.isVisible = true

                        if (response.data != null) {
                            rvTokenpln.adapter =
                                response.data.data?.sortedBy { sort -> sort.price.toInt() }?.let { data ->
                                    PulsaAdapter(data) { pln ->
                                        destination = edtNumberPlnToken.text.toString().trim()

                                        if (!destination.isNullOrEmpty()) {
                                            productSelected = pln
                                            val transactionRequest = TransactionRequest(
                                                uuid = uuid ?: "",
                                                kode_produk = "CEKTOKEN",
                                                dest = destination ?: "",
                                                pin = ""
                                            )
                                            viewModel.getInfoDetailToken(transactionRequest, accessToken ?: "")
                                            return@PulsaAdapter
                                        }
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
                                            .show(requireActivity().supportFragmentManager, "Customer Id Is Empty")
                                    }
                                }
                        }
                    }
                }
                Status.LOADING -> {
                    with(binding) {
                        rvTokenplnShimmer.isVisible = true
                        rvTokenplnShimmer.startShimmer()
                        rvTokenpln.isVisible = false
                    }
                }
                Status.ERROR -> {
                    with(binding) {
                        rvTokenplnShimmer.isVisible = true
                        rvTokenplnShimmer.startShimmer()
                        rvTokenpln.isVisible = false

                        showError(response.message.toString())
                    }
                }
            }
        }
    }

    private fun observeDetailToken() {
        viewModel.tokenInfoDetail.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                when(response.status) {
                    Status.LOADING -> binding.progressBar.isVisible = true
                    Status.SUCCESS -> {
                        binding.progressBar.isVisible = false

                        DetailTopUpTokenBottomSheet.newInstance(
                            destination,
                            productSelected,
                            response.data,
                            R.drawable.icon_pln_svg
                        ).show(requireActivity().supportFragmentManager, "Detail Cek Token")
                    }
                    Status.ERROR -> {
                        binding.progressBar.isVisible = false
                        showError(response.message.toString())
                    }
                }
            }
        }
    }

    private fun showError(message: String) {
        GenericModalDialogCashplus.Builder()
            .title(getString(R.string.something_wrong))
            .image(R.drawable.illustration_verify)
            .description(
                if (message.contains("format salah", ignoreCase = true) || message.contains("tidak dikenal", ignoreCase = true))
                    getString(R.string.any_not_found, getString(R.string.customer_id))
                else if (message.contains("diroute", ignoreCase = true))
                    getString(R.string.product_is_unavailable_contact_cs_please)
                else message
            )
            .buttonNegative(
                NegativeButtonAction(
                    btnLabel = getString(R.string.close),
                    backgroundButton = R.drawable.bg_transparent_border_primary_rounded_16,
                    buttonTextColor = Color.parseColor("#1570EF"),
                    setClickOnDismiss = true
                )
            ).show(childFragmentManager, "show error")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}