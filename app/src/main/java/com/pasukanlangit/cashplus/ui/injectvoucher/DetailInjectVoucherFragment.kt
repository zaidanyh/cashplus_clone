package com.pasukanlangit.cashplus.ui.injectvoucher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentDetailInjectVoucherBinding
import com.pasukanlangit.cashplus.domain.model.DataInject
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.MyUtils.convertToNormalNumber
import com.pasukanlangit.id.core.utils.SaldoNotEnoughNotif
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.parcelableArrayList
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class DetailInjectVoucherFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDetailInjectVoucherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailInjectVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = arguments?.parcelable<ProductModel>(PRODUCT_INJECT_VOUCHER_KEY)
        val balance = arguments?.getDouble(BALANCE_CURRENTLY_KEY)
        val serialsNumber = arguments?.parcelableArrayList<DataInject>(SERIALS_NUMBER_KEY)

        val amountVoucher = serialsNumber?.size ?: 1
        val totalFee = product?.price?.toIntOrNull()?.times(amountVoucher)
        with(binding) {
            tvProductInjectVoucher.text = product?.dsc
            tvPriceInjectVoucher.text = getNumberRupiah(product?.price?.toIntOrNull())
            tvAmountInjectVoucher.text = amountVoucher.toString()
            tvTotalPayment.text = getNumberRupiah(totalFee)
            btnPay.setOnClickListener {
                if (balance!! < convertToNormalNumber(tvTotalPayment.text.toString())) {
                    val notifModal = SaldoNotEnoughNotif()
                    notifModal.show(childFragmentManager, notifModal.tag)
                    return@setOnClickListener
                }
                (activity as InjectVoucherActivity).enterPIN
                    .show(requireActivity().supportFragmentManager, "Enter PIN")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val PRODUCT_INJECT_VOUCHER_KEY = "product_inject_voucher_key"
        private const val BALANCE_CURRENTLY_KEY = "balance_currently_key"
        private const val SERIALS_NUMBER_KEY = "serials_number_key"

        @JvmStatic
        fun newInstance(product: ProductModel?, balance: Double?, serialNumbers: ArrayList<DataInject>) =
            DetailInjectVoucherFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PRODUCT_INJECT_VOUCHER_KEY, product)
                    putDouble(BALANCE_CURRENTLY_KEY, balance!!)
                    putParcelableArrayList(SERIALS_NUMBER_KEY, serialNumbers)
                }
            }
    }
}