package com.pasukanlangit.id.ui_cashgold_buy.buy.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.ui_cashgold_buy.buy.BuyCashGoldViewModel
import com.pasukanlangit.id.ui_cashgold_buy.databinding.FragmentTagGoldBinding
import com.pasukanlangit.id.ui_core.R
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@FlowPreview
class TagGoldFragment(
    private val onButtonPayClicked: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentTagGoldBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BuyCashGoldViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagGoldBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBuy.setOnClickListener {
            onButtonPayClicked()
            dismiss()
        }
        collectState()
    }

    private fun collectState() {
        collectTrxState()
    }

    @SuppressLint("SetTextI18n")
    private fun collectTrxState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trxTagResult.collectLatest {
                    it?.let { trxData ->
                        with(binding){
                            val isDiscount = trxData.fee.toInt() < 0

                            if(isDiscount){
                                labelAppsFee.text = getString(R.string.label_diskon)
                            }else{
                                labelAppsFee.text = getString(R.string.label_apps_fee)
                            }
                            tvGram.text = trxData.gramBuy
                            tvAdmin.text = trxData.admin
                            tvPriceRupiah.text = trxData.priceRupiah
                            tvAppsFee.text = trxData.feeCurrency
                            tvTotal.text = trxData.priceTotalCurrency

                            tvDiscount.text = trxData.trxGoldData?.discountFee
                            tvPph.text = trxData.trxGoldData?.pph22FeeRupiah
                            tvPpn.text = trxData.trxGoldData?.ppn11FeeRupiah
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


}