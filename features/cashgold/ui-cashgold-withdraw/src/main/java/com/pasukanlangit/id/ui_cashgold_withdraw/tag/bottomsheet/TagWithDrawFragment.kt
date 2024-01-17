package com.pasukanlangit.id.ui_cashgold_withdraw.tag.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.ui_cashgold_withdraw.databinding.FragmentTagWithDrawBinding
import com.pasukanlangit.id.ui_cashgold_withdraw.tag.TagWithDrawViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.pasukanlangit.id.ui_core.R
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TagWithDrawFragment(
    private val onButtonPayClicked: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentTagWithDrawBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TagWithDrawViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTagWithDrawBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBuy.setOnClickListener {
            onButtonPayClicked()
        }

        collectState()
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trxTagResult.collectLatest {
                    it?.let { trxData ->
                        with(binding){
                            val isDiscount = trxData.fee.toInt() < 0

                            if(isDiscount){
                                labelAppsFee.text =  getString(R.string.label_diskon)
                            }else{
                                labelAppsFee.text =  getString(R.string.label_apps_fee)
                            }
                            tvProduct.text = trxData.name
                            tvItem.text = trxData.gramBuy
                            tvAdmin.text = trxData.admin
                            tvQty.text = trxData.qty
                            tvPrice.text = trxData.withdrawData?.certificateFee
                            tvAppsFee.text = trxData.feeCurrency
                            tvTotal.text = trxData.priceTotalCurrency

                            tvAsuransi.text = trxData.withdrawData?.assuranceFee
                            tvSendFee.text = trxData.withdrawData?.sendFee
                        }
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}