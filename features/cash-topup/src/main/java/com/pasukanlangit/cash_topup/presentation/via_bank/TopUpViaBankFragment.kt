package com.pasukanlangit.cash_topup.presentation.via_bank

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.FragmentTopUpViaBankBinding
import com.pasukanlangit.cash_topup.presentation.InitialTopUpActivity
import com.pasukanlangit.cash_topup.presentation.MenuPaymentAdapter
import com.pasukanlangit.cash_topup.utils.MenuTopUp
import com.pasukanlangit.cash_topup.utils.TopUpUtils.getMenuPayment
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class TopUpViaBankFragment : Fragment() {

    private var _binding: FragmentTopUpViaBankBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViaBankViewModel by viewModel()

    private lateinit var amount: String
    private var imgSource by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpViaBankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuPaymentAdapter = MenuPaymentAdapter(getMenuPayment(MenuTopUp.BANK_TRANSFER.value)) { menuTopUp ->
            amount = (activity as InitialTopUpActivity).getNominal()
            imgSource = menuTopUp.img
            if (amount.length > 11) {
                GenericModalDialogCashplus.Builder()
                    .title(getString(R.string.something_wrong))
                    .image(R.raw.cashplus_gagal, true)
                    .description(getString(R.string.top_up_balance_error_out_of_capacity))
                    .buttonNegative(
                        NegativeButtonAction(
                            btnLabel = getString(R.string.close),
                            setClickOnDismiss = true
                        )
                    ).show(requireActivity().supportFragmentManager)
                return@MenuPaymentAdapter
            }

            if (amount.isEmpty()) {
                GenericModalDialogCashplus.Builder()
                    .title(getString(R.string.something_wrong))
                    .image(R.drawable.illustration_empty)
                    .description(getString(R.string.top_up_balance_error_required))
                    .buttonNegative(
                        NegativeButtonAction(
                            btnLabel = getString(R.string.close),
                            backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                            buttonTextColor = Color.parseColor("#0A57FF"),
                            setClickOnDismiss = true
                        )
                    )
                    .show(requireActivity().supportFragmentManager, "Customer Id Is Empty")
                return@MenuPaymentAdapter
            }
            if (amount.toInt() < 10000) {
                (activity as InitialTopUpActivity).setErrorEditText(getString(R.string.top_up_balance_error_minimal))
                return@MenuPaymentAdapter
            }
            if (amount.toInt() > 100000000) {
                (activity as InitialTopUpActivity).setErrorEditText(getString(R.string.top_up_balance_error_maximal, getNumberRupiah(100000000)))
                return@MenuPaymentAdapter
            }
            if (amount.toInt() % 10000 != 0) {
                (activity as InitialTopUpActivity).setErrorEditText(getString(R.string.top_up_balance_error_multiple))
                return@MenuPaymentAdapter
            }
            viewModel.onTriggerEvent(ViaBankEvent.TopUpViaBank(menuTopUp.bankCode, amount))
        }
        with(binding.rvTopUpViaBank) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = menuPaymentAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }

        collectDataViaBank()
    }

    private fun collectDataViaBank() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.viaBankLoading.collectLatest {
                        (activity as InitialTopUpActivity).setIsLoading(it)
                    }
                }
                launch {
                    viewModel.viaBank.collectLatest { response ->
                        if (response != null) {
                            ViaBankResultFragment.newInstance(
                                nominalTopUp = amount, imgSrc = imgSource, viaBankResult = response
                            ).show(requireActivity().supportFragmentManager, "Via Bank Result")
                        }
                    }
                }
                launch {
                    viewModel.viaBankError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(ViaBankEvent.RemoveViaBankError)
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