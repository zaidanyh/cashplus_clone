package com.pasukanlangit.cashplus.ui.pembayaran_pln

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentDetailTopUpTokenDialogBinding
import com.pasukanlangit.cashplus.model.request_body.ProfileRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.model.response.TransactionTAGResponse
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.id.core.utils.SaldoNotEnoughNotif
import com.pasukanlangit.cashplus.ui.pembayaran_bulanan.BillDetailAdapter
import com.pasukanlangit.cashplus.ui.pembayaran_provider.EnterPINProviderFragment
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.PembayaranViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.InputUtil.toDoubleNumber
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.library_core.domain.model.NotifType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailTopUpTokenBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentDetailTopUpTokenDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PembayaranViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null
    private var balance: Double? = null

    private var destination: String? = null
    private var transactionInfo: TransactionTAGResponse? = null
    private var product: ProductModel? = null
    private var imgLogo: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailTopUpTokenDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        destination = arguments?.getString(DESTINATION_KEY)
        product = arguments?.parcelable(PRODUCT_SELECTED_KEY)
        transactionInfo = arguments?.parcelable(DATA_CHECK_TOKEN_KEY)
        imgLogo = arguments?.getInt(IMAGE_LOGO_KEY) ?: -1

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()
        viewModel.getBalance(ProfileRequest(uuid ?: ""), accessToken ?: "")

        with(binding) {
            val imgProduct = product?.img_url_2?.ifEmpty { product?.img_url }
            Glide.with(requireContext())
                .load(imgProduct)
                .error(imgLogo)
                .circleCrop()
                .into(ivOperator)

            val splitter = transactionInfo?.billData?.info?.split("||") as List<String>
            val splitData = splitter.last().split("|")
            tvCustomerName.text = transactionInfo?.billData?.nama ?: splitData.first().split(":").last().trim()
            with(rvDetailTrx) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = BillDetailAdapter(splitData)
            }
            tvPayTotal.text = getNumberRupiah(product?.price?.toIntOrNull())
            btnPay.setOnClickListener { billPay() }
        }

        observeBalance()
    }

    private fun observeBalance() {
        viewModel.balance.observe(viewLifecycleOwner) { response ->
            when(response.status) {
                Status.LOADING -> {
                    this.isCancelable = false
                }
                Status.SUCCESS -> {
                    this.isCancelable = true
                    balance = response.data?.balance
                }
                Status.ERROR -> {
                    this.isCancelable = true
                    val menusAllFragment = ButtomSheetNotif(response.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(requireActivity().supportFragmentManager,menusAllFragment.tag)
                }
            }
        }
    }

    private fun billPay() {
        if (balance!! < binding.tvPayTotal.text.toString().trim().toDoubleNumber()) {
            val notifModal = SaldoNotEnoughNotif()
            notifModal.show(requireActivity().supportFragmentManager, null)
            return
        }
        EnterPINProviderFragment.newInstance(
            productCode = product?.kode_produk, destination = destination
        ).show(requireActivity().supportFragmentManager, "Enter PIN Pay Bill")
        this.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val DESTINATION_KEY = "destination_key"
        private const val PRODUCT_SELECTED_KEY = "product_selected_key"
        private const val DATA_CHECK_TOKEN_KEY = "data_check_token_key"
        private const val IMAGE_LOGO_KEY = "image_logo_key"

        @JvmStatic
        fun newInstance(destination: String?, product: ProductModel?, data: TransactionTAGResponse?, imgLogo: Int) =
            DetailTopUpTokenBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(DESTINATION_KEY, destination)
                    putParcelable(PRODUCT_SELECTED_KEY, product)
                    putParcelable(DATA_CHECK_TOKEN_KEY, data)
                    putInt(IMAGE_LOGO_KEY, imgLogo)
                }
            }
    }
}