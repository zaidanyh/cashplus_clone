package com.pasukanlangit.cash_topup.presentation.via_merchant

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
import com.pasukanlangit.cash_topup.databinding.FragmentTopUpViaMerchantBinding
import com.pasukanlangit.cash_topup.presentation.InitialTopUpActivity
import com.pasukanlangit.cash_topup.presentation.MenuPaymentAdapter
import com.pasukanlangit.cash_topup.utils.MenuTopUp
import com.pasukanlangit.cash_topup.utils.MenuViaTopUpPayment
import com.pasukanlangit.cash_topup.utils.TopUpUtils.getMenuPayment
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopUpViaMerchantFragment : Fragment() {

    private var _binding: FragmentTopUpViaMerchantBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViaMerchantViewModel by viewModel()

    private lateinit var amount: String
    private lateinit var menuTopUp: MenuViaTopUpPayment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpViaMerchantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuPaymentAdapter = MenuPaymentAdapter(getMenuPayment(MenuTopUp.MERCHANT.value)) { menu ->
            amount = (activity as InitialTopUpActivity).getNominal()
            this.menuTopUp = menu
            if (amount.length > 10) {
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
            if (amount.toInt() > 1000000) {
                (activity as InitialTopUpActivity).setErrorEditText(getString(R.string.top_up_balance_error_maximal, getNumberRupiah(1000000)))
                return@MenuPaymentAdapter
            }
            if (amount.toInt() % 10000 != 0) {
                (activity as InitialTopUpActivity).setErrorEditText(getString(R.string.top_up_balance_error_multiple))
                return@MenuPaymentAdapter
            }
            viewModel.onTriggerEvent(
                ViaMerchantEvent.TopUpViaMerchant(
                    bankMitraCode = menu.bankCode, payMethod = menu.payMethodCode.toString(),
                    amount = amount
                )
            )
        }
        with(binding.rvTopUpViaMerchant) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = menuPaymentAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
        collectDataViaMerchant()
    }

    private fun collectDataViaMerchant() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.viaMerchantLoading.collectLatest {
                        (activity as InitialTopUpActivity).setIsLoading(it)
                    }
                }
                launch {
                    viewModel.viaMerchant.collectLatest { response ->
                        if (response != null) {
                            ViaMerchantResultFragment.newInstance(
                                nominalTopUp = amount, menuTopUp = menuTopUp,
                                viaVAResult = response
                            ).show(requireActivity().supportFragmentManager, "Via Merchant Result")
                        }
                    }
                }
                launch {
                    viewModel.viaMerchantError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(ViaMerchantEvent.RemoveViaMerchantError)
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