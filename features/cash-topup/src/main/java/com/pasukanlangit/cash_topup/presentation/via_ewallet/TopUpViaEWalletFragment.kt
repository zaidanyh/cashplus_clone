package com.pasukanlangit.cash_topup.presentation.via_ewallet

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.FragmentTopUpViaEWalletBinding
import com.pasukanlangit.cash_topup.presentation.InitialTopUpActivity
import com.pasukanlangit.cash_topup.presentation.MenuPaymentAdapter
import com.pasukanlangit.cash_topup.utils.MenuTopUp
import com.pasukanlangit.cash_topup.utils.TopUpUtils.getMenuPayment
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class TopUpViaEWalletFragment : Fragment() {

    private var _binding: FragmentTopUpViaEWalletBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpViaEWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuPaymentAdapter = MenuPaymentAdapter(getMenuPayment(MenuTopUp.EWALLET.value)) { menu ->
            val amount = (activity as InitialTopUpActivity).getNominal()
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
            if (amount.toInt() > 10000000) {
                (activity as InitialTopUpActivity).setErrorEditText(getString(R.string.top_up_balance_error_maximal, getNumberRupiah(10000000)))
                return@MenuPaymentAdapter
            }
            if (amount.toInt() % 10000 != 0) {
                (activity as InitialTopUpActivity).setErrorEditText(getString(R.string.top_up_balance_error_multiple))
                return@MenuPaymentAdapter
            }
            TopUpViaEWalletResult.newInstance(
                amount = amount, menuTopUp = menu
            ).show(requireActivity().supportFragmentManager, "Request Top Up Via EWallet")
        }
        with(binding.rvTopUpViaEWallet) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = menuPaymentAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}