package com.pasukanlangit.cashplus.ui.pembayaran_bulanan

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentPembayaranPDAMBinding
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.PembayaranBulananViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.DrawablePosition
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.utils.Constants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PembayaranPDAMFragment : Fragment() {

    private var _binding: FragmentPembayaranPDAMBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PembayaranBulananViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private lateinit var productsModel: List<ProductModel>
    private var uuid: String? = null
    private var accessToken: String? = null
    private var productSelected: ProductModel? = null
    private var destination: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPembayaranPDAMBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()

        if(!uuid.isNullOrEmpty() && !accessToken.isNullOrEmpty()){
            val productRequest = ProductRequest(
                uuid = uuid ?: "",
                category = "#PEMBAYARAN",
                kode_provider = "PDAM",
                is_faktur = "",
                is_active = "1"
            )
            viewModel.getProductPDAM(productRequest, accessToken ?: "")
        }

        with(binding) {
            provinceList.setDropDownBackgroundResource(R.drawable.bg_white_border_slate200_rounded_10)
            provinceList.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= binding.provinceList.right - binding.provinceList.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width()
                    ) {
                        binding.provinceList.showDropDown()
                        return@setOnTouchListener true
                    }
                }
                false
            }
            btnCheckPdam.setUpToProgressButton(viewLifecycleOwner)
            btnCheckPdam.setOnClickListener { checkPDAM() }
        }

        observePDAM()
    }

    private fun observePDAM() {
        viewModel.tagihanPDAM.observe(viewLifecycleOwner) { response ->
            if (response.status == Status.SUCCESS) {
                val listProvincesPDAM = arrayListOf(Constants.NO_PROVIDER)
                if (!response.data?.data.isNullOrEmpty()) {
                    productsModel = response.data?.data!!
                    listProvincesPDAM.clear()
                    response.data.data.forEach { product ->
                        listProvincesPDAM.add(product.short_dsc)
                    }
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner_small, listProvincesPDAM)
                binding.provinceList.setAdapter(adapter)
            }
        }
    }

    private fun checkPDAM() {
        with(binding) {
            val provinces = provinceList.text.toString().trim()
            val destinationNumber = edtNomerPdam.text.toString().trim()

            if (destinationNumber.isEmpty()) {
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
                return
            }
            if (provinces == Constants.NO_PROVIDER) {
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
                    .show(requireActivity().supportFragmentManager, "No_province_available")
                return
            }
            try {
                destination = destinationNumber
                productSelected = productsModel.single { product -> product.short_dsc == provinces }
                viewModel.payBill(
                    TransactionRequest(
                        uuid = uuid ?: "", kode_produk = productSelected?.kode_produk?.replaceRange(0, 3, "TAG").toString(),
                        dest = destination ?: "", pin = ""
                    ), accessToken = accessToken ?: ""
                )
            } catch (e: NoSuchElementException) {
                GenericModalDialogCashplus.Builder()
                    .title(getString(R.string.something_wrong))
                    .image(R.drawable.illustration_empty)
                    .description(getString(R.string.any_input_is_not_valid, getString(R.string.region)))
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

        observeTAGPDAM()
    }

    private fun observeTAGPDAM() {
        viewModel.billTrx.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                when(response.status) {
                    Status.LOADING -> binding.btnCheckPdam.showLoading()
                    Status.SUCCESS -> {
                        binding.btnCheckPdam.hideProgress(getString(R.string.check))

                        BillDetailFragment.newInstance(
                            destination, response.data,
                            productSelected?.img_url_2?.ifEmpty { productSelected?.img_url },
                            R.drawable.ic_tab_menu_pdam
                        ).show(childFragmentManager, "Bill Detail")
                    }
                    Status.ERROR -> {
                        binding.btnCheckPdam.hideProgress(getString(R.string.check))

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