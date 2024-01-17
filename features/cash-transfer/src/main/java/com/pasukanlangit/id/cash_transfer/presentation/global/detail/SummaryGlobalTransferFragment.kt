package com.pasukanlangit.id.cash_transfer.presentation.global.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.FragmentSummaryGlobalTransferBinding
import com.pasukanlangit.id.cash_transfer.domain.model.TransferBillData
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SummaryGlobalTransferFragment(
    private val event: OnButtonSendClickListener
) : BottomSheetDialogFragment() {

    private var _binding: FragmentSummaryGlobalTransferBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailGlobalTransferViewModel by sharedViewModel()

    private var totalPay: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.isCancelable = false
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryGlobalTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val priceTotal = arguments?.getString(ARG_RESULT_PRICE_KEY)
        val fee = arguments?.getString(ARG_RESULT_FEE_KEY)
        val billData = arguments?.parcelable<TransferBillData>(ARG_RESULT_TAG_TRANSACTION_KEY)
        viewModel.onTriggerEvents(DetailGlobalTransferEvents.CheckBalance)
        with(binding) {
            icClose.setOnClickListener {
                event.onButtonCloseClicked()
                this@SummaryGlobalTransferFragment.dismiss()
            }
            tvBeneficiaryName.text = billData?.name
            tvBeneficiaryAccountData.text = getString(R.string.beneficiary_num_name_format, billData?.bank_acc_num, billData?.bankName)
            tvAmountTransfer.text = getNumberRupiah(billData?.bill?.toIntOrNull())

            val serviceFee = (fee?.toDoubleOrNull()?.plus(billData?.adminFee?.toDouble() ?: 0.0)) ?: 0.0
            tvFeeMore.text = getNumberRupiah(serviceFee)

            totalPay = priceTotal?.toDoubleOrNull() ?: 0.0
            tvTotalTransfer.text = getNumberRupiah(totalPay)
            btnSend.setOnClickListener {
                event.onButtonSendClicked()
                this@SummaryGlobalTransferFragment.dismiss()
            }
        }
        collectData()
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.balanceLoading.collectLatest {
                        binding.progressBar.isVisible = it
                        if (it) {
                            this@SummaryGlobalTransferFragment.isCancelable = false
                        }
                    }
                }
                launch {
                    viewModel.balance.collectLatest { response ->
                        with(binding) {
                            btnSend.isEnabled = response?.balance != null
                            this@SummaryGlobalTransferFragment.isCancelable = response?.balance == null
                            if (response?.balance != null) {
                                tvBalance.text = getNumberRupiah(response.balance)
                                btnSend.isEnabled = response.balance >= totalPay
                                txtLessBalance.isVisible = response.balance < totalPay
                                return@collectLatest
                            }
                        }
                    }
                }
                launch {
                    viewModel.balanceError.collectLatest { message ->
                        message.peek()?.let { info ->
                            this@SummaryGlobalTransferFragment.isCancelable = true
                            val toast = Toast.makeText(requireActivity(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvents(DetailGlobalTransferEvents.RemoveBalanceError)
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

    interface OnButtonSendClickListener {
        fun onButtonCloseClicked()
        fun onButtonSendClicked()
    }

    companion object {

        private const val ARG_RESULT_PRICE_KEY = "arg_amount_transfer_key"
        private const val ARG_RESULT_FEE_KEY = "arg_result_tag_transaction_key"
        private const val ARG_RESULT_TAG_TRANSACTION_KEY = "arg_result_tag_transaction"

        @JvmStatic
        fun newInstance(
            price: String?, fee: String?, resultTag: TransferBillData?,
            event: OnButtonSendClickListener
        ) = SummaryGlobalTransferFragment(event).apply {
                arguments = Bundle().apply {
                    putString(ARG_RESULT_PRICE_KEY, price)
                    putString(ARG_RESULT_FEE_KEY, fee)
                    putParcelable(ARG_RESULT_TAG_TRANSACTION_KEY, resultTag)
                }
            }
    }
}