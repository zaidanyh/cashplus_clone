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
import com.pasukanlangit.cashplus.databinding.FragmentPembayaranBPJSBinding
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.PembayaranBulananViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.utils.Constants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PembayaranBPJSFragment : Fragment() {

    private var _binding: FragmentPembayaranBPJSBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PembayaranBulananViewModel by viewModel()
    private val sharedPred: SharedPrefDataSource by inject()

    private var destination: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPembayaranBPJSBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uuid = sharedPred.getUUID()
        val accessToken = sharedPred.getAccessToken()

        with(binding) {
            periodebpjsListSpinner.prompt = "Pilih Jumlah"

            val listPeriodeBayar = listOf(
                "1 Bulan", "2 Bulan", "3 Bulan", "4 Bulan",
                "5 Bulan", "6 Bulan", "7 Bulan", "8 Bulan",
                "9 Bulan", "10 Bulan", "11 Bulan", "12 Bulan"
            )

            val adapter: ArrayAdapter<String> = ArrayAdapter(
                requireActivity(),
                R.layout.item_spinner_small_selected, listPeriodeBayar
            )
            adapter.setDropDownViewResource(R.layout.item_spinner_small)
            periodebpjsListSpinner.adapter = adapter

            btnCheckBpjs.setUpToProgressButton(viewLifecycleOwner)
            btnCheckBpjs.setOnClickListener {
                val originPeriod = periodebpjsListSpinner.selectedItem.toString()
                val dest = edtNomerBpjs.text.toString().trim()

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
                if (originPeriod.isEmpty()) {
                    GenericModalDialogCashplus.Builder()
                        .title(getString(R.string.something_wrong))
                        .image(R.drawable.illustration_empty)
                        .description(Constants.NO_PERIOD)
                        .buttonNegative(
                            NegativeButtonAction(
                                btnLabel = getString(R.string.close),
                                backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                buttonTextColor = Color.parseColor("#0A57FF"),
                                setClickOnDismiss = true
                            )
                        )
                        .show(requireActivity().supportFragmentManager, "No_province_available")
                    return@setOnClickListener
                }

                val period = originPeriod.split(" ").first()
                destination = "$dest-$period"
                viewModel.payBill(
                    TransactionRequest(
                        uuid = uuid ?: "", kode_produk = "TAGBPJSKS",
                        dest = destination ?: "", pin = ""
                    ), accessToken = accessToken ?: ""
                )
            }
        }

        observeTAGBPJSKS()
    }

    private fun observeTAGBPJSKS() {
        viewModel.billTrx.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                when(response.status) {
                    Status.LOADING -> binding.btnCheckBpjs.showLoading()
                    Status.SUCCESS -> {
                        binding.btnCheckBpjs.hideProgress(getString(R.string.check))

                        BillDetailFragment.newInstance(
                            destination, response.data,
                            "", R.drawable.ic_tab_menu_bpjs
                        ).show(childFragmentManager, "Bill Detail")
                    }
                    Status.ERROR -> {
                        binding.btnCheckBpjs.hideProgress(getString(R.string.check))

                        GenericModalDialogCashplus.Builder()
                            .title(getString(R.string.something_wrong))
                            .image(R.drawable.illustration_login_error)
                            .description(
                                if (
                                    response.message?.contains("format salah", ignoreCase = true) == true ||
                                    response.message?.contains("tidak dikenal", ignoreCase = true) == true
                                ) getString(R.string.any_not_found, getString(R.string.bpjs_number))
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