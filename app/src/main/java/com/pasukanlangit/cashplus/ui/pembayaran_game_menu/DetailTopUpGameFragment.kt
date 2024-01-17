package com.pasukanlangit.cashplus.ui.pembayaran_game_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentDetailTopUpGameBinding
import com.pasukanlangit.cashplus.model.request_body.ProfileRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.id.core.utils.SaldoNotEnoughNotif
import com.pasukanlangit.cashplus.ui.pembayaran_provider.EnterPINProviderFragment
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.PembayaranViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.utils.CategoryProduct
import com.pasukanlangit.id.core.utils.InputUtil.toDoubleNumber
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.library_core.domain.model.NotifType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailTopUpGameFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDetailTopUpGameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PembayaranViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null
    private var balance: Double? = null
    private var customerId: String? = null
    private var productModel: ProductModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailTopUpGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productModel = arguments?.parcelable(KEY_PRODUCT_MODEL)
        customerId = arguments?.getString(KEY_CUSTOMER_ID)

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()
        viewModel.getBalance(ProfileRequest(uuid ?: ""), accessToken ?: "")

        with(binding) {
            var imgUrl = if(productModel?.category == CategoryProduct.GAMES.value) {
                productModel?.img_url_2?.ifEmpty { productModel?.img_url }
            } else {
                productModel?.img_url
            }
            if(imgUrl.isNullOrEmpty()) imgUrl =
                MyUtils.getAvaImagePlaceholder(productModel?.kode_provider?.replace("_", " ").toString())

            Glide.with(requireContext())
                .load(imgUrl)
                .circleCrop()
                .into(ivProduct)

            val total = if (productModel?.admin.isNullOrEmpty()) productModel?.price?.toIntOrNull()
            else productModel?.admin?.toIntOrNull()?.plus(productModel?.price?.toIntOrNull() ?: 0)

            tvCustomerId.text = customerId
            tvNamePackage.text = productModel?.dsc
            tvPrice.text = getNumberRupiah(productModel?.price?.toIntOrNull())
            tvAdminFee.text = if (productModel?.admin.isNullOrEmpty() || productModel?.admin?.toIntOrNull() == 0)
                getString(R.string.admin_free)
            else getNumberRupiah(productModel?.admin?.toIntOrNull())
            tvTotalPayment.text = getNumberRupiah(total)
            btnPay.setOnClickListener { onPayProcess() }
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

    private fun onPayProcess() {
        if (balance!! < (productModel?.price?.toDoubleNumber() ?: 0.0)) {
            val notifModal = SaldoNotEnoughNotif()
            notifModal.show(requireActivity().supportFragmentManager,null)
        } else {
            EnterPINProviderFragment.newInstance(
                productCode = productModel?.kode_produk,
                destination = customerId
            ).show(requireActivity().supportFragmentManager, "Enter PIN Provider")
            this.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val KEY_PRODUCT_MODEL = "key_product_model"
        private const val KEY_CUSTOMER_ID = "key_customer_id"

        @JvmStatic
        fun newInstance(productModel: ProductModel, customerId: String) =
            DetailTopUpGameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_PRODUCT_MODEL, productModel)
                    putString(KEY_CUSTOMER_ID, customerId)
                }
            }
    }
}