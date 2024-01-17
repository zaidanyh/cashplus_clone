package com.pasukanlangit.id.cash_transfer.presentation.local.transfer

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.FragmentDetailTransferBottomSheetBinding
import com.pasukanlangit.id.cash_transfer.domain.model.TransferBillData
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailTransferBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDetailTransferBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransferViewModel by sharedViewModel()

    private var totalPay: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailTransferBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUrl = arguments?.getString(ARG_IMAGE_URL)
        val destination = arguments?.getString(ARG_DESTINATION)
        val feeOptional = arguments?.getString(ARG_FEE_OPTIONAL)
        val billData = arguments?.parcelable<TransferBillData>(ARG_INFO_TRANSFER)

        viewModel.onEventState(TransferStateEvent.CheckBalance)
        with(binding) {
            icClose.setOnClickListener { this@DetailTransferBottomSheetFragment.dismiss() }
            val destinationSplit = destination?.split("-")
            Glide.with(requireContext())
                .load(imageUrl)
                .into(imgBank)
            tvBankName.text = billData?.name
            tvBankNum.text = destinationSplit?.get(1)
            tvNominalTransfer.text = getNumberRupiah(billData?.bill?.toIntOrNull())
            tvAdminCost.text = if (billData?.bill == "0") getString(R.string.admin_free) else getNumberRupiah(billData?.adminFee?.toIntOrNull())
            val feeOptionalNum = feeOptional?.toIntOrNull()
            if (feeOptionalNum!! < 0) {
                txtFeeMore.text = getString(R.string.discount_fee)
                tvFeeMore.setTextColor(Color.parseColor("#F97316"))
                tvFeeMore.text = getNumberRupiah(feeOptional.replace("-", "").toInt())
            }
            if (feeOptionalNum > 0) {
                tvFeeMore.text = getNumberRupiah(feeOptionalNum)
            }
            totalPay = billData?.total?.toIntOrNull()?.plus(feeOptionalNum)
            tvTotalTransfer.text = getNumberRupiah(totalPay)
            btnSend.setOnClickListener {
                (activity as TransferActivity).enterPIN.show(
                    requireActivity().supportFragmentManager, "enter_pin_for_pay"
                )
            }
        }
        collectBalance()
    }

    private fun collectBalance() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.balanceLoading.collectLatest {
                        binding.progressBar.isVisible = it
                        if (it) {
                            this@DetailTransferBottomSheetFragment.isCancelable = false
                        }
                    }
                }
                launch {
                    viewModel.balance.collectLatest { response ->
                        with(binding) {
                            btnSend.isEnabled = response?.balance != null
                            this@DetailTransferBottomSheetFragment.isCancelable = response?.balance == null
                            if (response?.balance != null) {
                                tvSaldo.text = getNumberRupiah(response.balance)
                                btnSend.isEnabled = response.balance >= totalPay?.toDouble()!!
                                txtLessBalance.isVisible = response.balance < totalPay?.toDouble()!!
                                return@collectLatest
                            }
                        }
                    }
                }
                launch {
                    viewModel.balanceError.collectLatest { message ->
                        message.peek()?.let { info ->
                            this@DetailTransferBottomSheetFragment.isCancelable = true
                            val toast = Toast.makeText(requireActivity(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onEventState(TransferStateEvent.RemoveMessageBalance)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IMAGE_URL = "arg_image_url"
        private const val ARG_DESTINATION = "arg_destination"
        private const val ARG_FEE_OPTIONAL = "arg_fee_optional"
        private const val ARG_INFO_TRANSFER = "arg_info_transfer"

        @JvmStatic
        fun newInstance(imgUrl: String?, destination: String?, fee: String?, info: TransferBillData?) =
            DetailTransferBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE_URL, imgUrl)
                    putString(ARG_DESTINATION, destination)
                    putString(ARG_FEE_OPTIONAL, fee)
                    putParcelable(ARG_INFO_TRANSFER, info)
                }
            }
    }
}