package com.pasukanlangit.cashplus.ui.pembayaran_pln

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentTopUpTagihanPLNBinding
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.ui.pembayaran_bulanan.BillDetailFragment
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.TopUpPLNViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.InputUtil.inputNumericFilter
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopUpTagihanPLNFragment : Fragment() {

    private var _binding: FragmentTopUpTagihanPLNBinding? = null
    private val binding get() = _binding!!
    private val viewModel : TopUpPLNViewModel by viewModel()
    private val sharedPrefDataSource: SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null
    private var destination: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTopUpTagihanPLNBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uuid = sharedPrefDataSource.getUUID()
        accessToken = sharedPrefDataSource.getAccessToken()

        with(binding) {
            edtPlnNumber.filters = arrayOf(InputFilter.LengthFilter(16), inputNumericFilter)
            btnCheckPln.setUpToProgressButton(viewLifecycleOwner)
            btnCheckPln.setOnClickListener {
                val dest = edtPlnNumber.text.toString().trim()
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

                destination = dest
                if (!uuid.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
                    val transactionRequest = TransactionRequest(
                        uuid = uuid ?: "",
                        kode_produk = "TAGPLN",
                        dest = destination ?: "",
                        pin = ""
                    )
                    viewModel.getProductTagihanPln(transactionRequest, accessToken ?: "")
                }
            }
        }
        observeTAGBillPLN()
    }

    private fun observeTAGBillPLN() {
        viewModel.topUpTagihanPln.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                when(response.status) {
                    Status.LOADING -> binding.btnCheckPln.showLoading()
                    Status.SUCCESS -> {
                        binding.btnCheckPln.hideProgress(getString(R.string.check))

                        BillDetailFragment.newInstance(
                            destination, response.data,
                            "", R.drawable.icon_pln_svg
                        ).show(requireActivity().supportFragmentManager, "Detail Bill")
                    }
                    Status.ERROR -> {
                        binding.btnCheckPln.hideProgress(getString(R.string.check))

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


