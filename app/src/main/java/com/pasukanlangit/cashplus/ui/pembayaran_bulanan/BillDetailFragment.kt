package com.pasukanlangit.cashplus.ui.pembayaran_bulanan

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentBillDetailBinding
import com.pasukanlangit.cashplus.model.request_body.ProfileRequest
import com.pasukanlangit.cashplus.model.response.TransactionTAGResponse
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.checkout.EnterPINDialog
import com.pasukanlangit.id.core.utils.SaldoNotEnoughNotif
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.PembayaranViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.InputUtil.toDoubleNumber
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.library_core.domain.model.NotifType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class BillDetailFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBillDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PembayaranViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null
    private var balance: Double? = null

    private var destination: String? = null
    private var transactionInfo: TransactionTAGResponse? = null
    private var imgUrl: String? = null
    private var imgLogo: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionInfo = arguments?.parcelable(CHECK_BILL_KEY)
        destination = arguments?.getString(DESTINATION_KEY)
        imgUrl = arguments?.getString(IMAGE_URL_KEY)
        imgLogo = arguments?.getInt(IMAGE_LOGO_KEY) ?: -1

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()
        viewModel.getBalance(ProfileRequest(uuid ?: ""), accessToken ?: "")

        with(binding) {
            transactionInfo?.let { trxInfo ->
                if (imgUrl.isNullOrEmpty())
                    ivOperator.setImageResource(imgLogo)
                else Glide.with(requireContext())
                    .load(imgUrl)
                    .error(imgLogo)
                    .into(ivOperator)

                val splitter = trxInfo.billData.info.split("||")
                val splitData = splitter.last().split("|")
                tvCustomerName.text = trxInfo.billData.nama
                with(rvDetailTrx) {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = BillDetailAdapter(splitData)
                }
                txtAdminFeeCost.isVisible = trxInfo.fee != "0"
                tvAdminCostFee.isVisible = trxInfo.fee != "0"
                tvAdminCostFee.text = getNumberRupiah(trxInfo.fee?.replace("-", "")?.toIntOrNull())
                if (trxInfo.fee?.toInt()!! > 0)
                    txtAdminFeeCost.text = getString(R.string.service_fee)
                else if (trxInfo.fee.toInt() < 0) {
                    txtAdminFeeCost.text = getString(R.string.discount_fee)
                    tvAdminCostFee.setTextColor(Color.parseColor("#F97316"))
                }
                tvPayTotal.text = getNumberRupiah(
                    trxInfo.billData.admin.toIntOrNull()
                        ?.plus(trxInfo.fee.toInt())
                        ?.plus(trxInfo.billData.tagihan.toInt())
                )
                btnPay.setOnClickListener { billPay() }
            }
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
        } else {
            EnterPINDialog.newInstance(
                destination = destination,
                productCode = transactionInfo?.kode_produk?.replaceRange(0, 3, "PAY")
            ).show(requireActivity().supportFragmentManager, "Enter PIN Pay Bill")
            this.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activity?.viewModelStore?.clear()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (isShown) return
        super.show(manager, tag)
        isShown = true
    }

    override fun onDismiss(dialog: DialogInterface) {
        isShown = false
        super.onDismiss(dialog)
    }

    companion object {

        private var isShown: Boolean = false
        private const val DESTINATION_KEY = "destination_key"
        private const val CHECK_BILL_KEY = "check_bill_key"
        private const val IMAGE_URL_KEY = "image_url_key"
        private const val IMAGE_LOGO_KEY = "image_logo_key"

        @JvmStatic
        fun newInstance(destination: String?, data: TransactionTAGResponse?, imgUrl: String?, imgLogo: Int) =
            BillDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(DESTINATION_KEY, destination)
                    putParcelable(CHECK_BILL_KEY, data)
                    putString(IMAGE_URL_KEY, imgUrl)
                    putInt(IMAGE_LOGO_KEY, imgLogo)
                }
            }
    }
}