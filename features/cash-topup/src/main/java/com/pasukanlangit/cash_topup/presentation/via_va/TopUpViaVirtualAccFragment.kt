package com.pasukanlangit.cash_topup.presentation.via_va

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
import com.pasukanlangit.cash_topup.databinding.FragmentTopUpViaVirtualAccBinding
import com.pasukanlangit.cash_topup.presentation.InitialTopUpActivity
import com.pasukanlangit.cash_topup.presentation.MenuPaymentAdapter
import com.pasukanlangit.cash_topup.presentation.NewTopUpViewModel
import com.pasukanlangit.cash_topup.presentation.via_merchant.ViaMerchantResultFragment
import com.pasukanlangit.cash_topup.utils.MenuTopUp
import com.pasukanlangit.cash_topup.utils.MenuViaTopUpPayment
import com.pasukanlangit.cash_topup.utils.TopUpUtils.getMenuPayment
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.CoreUtils.showCoreComingSoon
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopUpViaVirtualAccFragment : Fragment() {

    private var _binding: FragmentTopUpViaVirtualAccBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: NewTopUpViewModel by sharedViewModel()
    private val viewModel: ViaVirtualAccountViewModel by viewModel()

    private lateinit var imgUrl: String
    private lateinit var bankName: String
    private lateinit var amount: String
    private lateinit var menuTopUp: MenuViaTopUpPayment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpViaVirtualAccBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectDataViaVA()
        with(binding.rvTopUpViaVa) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = MenuPaymentAdapter(getMenuPayment(MenuTopUp.VIRTUAL_ACCOUNT.value)) { menuTopUp ->
                amount = (activity as InitialTopUpActivity).getNominal()
                this@TopUpViaVirtualAccFragment.menuTopUp = menuTopUp

                if (validateAmount(amount)) {
                    viewModel.onTriggerEvent(
                        ViaVirtualAccountEvent.TopUpViaVA(
                            bankCode = menuTopUp.bankCode, amount = amount,
                            paymentMethod = menuTopUp.payMethodCode.toString(), isFlipAccept = false
                        )
                    )
                }
            }
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    private fun collectDataViaVA() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.flipVA.collectLatest { response ->
                        if (response != null) {
                            binding.rvTopUpViaVa.adapter = ViaVAFlipListAdapter(response) { menuTopUp ->
                                val amount = (activity as InitialTopUpActivity).getNominal()
                                this@TopUpViaVirtualAccFragment.bankName = menuTopUp.bankName
                                this@TopUpViaVirtualAccFragment.imgUrl = menuTopUp.imgUrl.toString()

                                if (validateAmount(amount)) {
                                    viewModel.onTriggerEvent(
                                        ViaVirtualAccountEvent.TopUpViaVA(
                                            bankCode = menuTopUp.bankCode, amount = amount, isFlipAccept = true
                                        )
                                    )
                                }
                            }
                            return@collectLatest
                        }
                    }
                }

                launch {
                    viewModel.viaVALoading.collectLatest {
                        (activity as InitialTopUpActivity).setIsLoading(it)
                    }
                }

                launch {
                    viewModel.viaVAFlipAccept.collectLatest { response ->
                        if (response != null) {
                            ViaVAResultFlipAcceptFragment.newInstance(
                                bankName = bankName, imgUrl = imgUrl, viaVAResult = response
                            ).show(requireActivity().supportFragmentManager, "Via VA Result")
                        }
                    }
                }
                launch {
                    viewModel.viaVANicePay.collectLatest { response ->
                        if (response != null) {
                            ViaMerchantResultFragment.newInstance(
                                nominalTopUp = amount, menuTopUp = menuTopUp, viaVAResult = response
                            ).show(requireActivity().supportFragmentManager, "Via VA Nicepay Result")
                        }
                    }
                }

                launch {
                    viewModel.viaVAError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.contains("payment_gateway", ignoreCase = true)) {
                                showCoreComingSoon(requireContext(), requireActivity().supportFragmentManager)
                                viewModel.onTriggerEvent(ViaVirtualAccountEvent.RemoveViaVAError)
                                return@collectLatest
                            }
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(ViaVirtualAccountEvent.RemoveViaVAError)
                        }
                    }
                }
            }
        }
    }

    private fun validateAmount(amount: String): Boolean {
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
            return false
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
            return false
        }
        if (amount.toInt() < 10000) {
            (activity as InitialTopUpActivity).setErrorEditText(getString(R.string.top_up_balance_error_minimal))
            return false
        }
        if (amount.toInt() > 100000000) {
            (activity as InitialTopUpActivity).setErrorEditText(getString(R.string.top_up_balance_error_maximal, getNumberRupiah(100000000)))
            return false
        }
        if (amount.toInt() % 10000 != 0) {
            (activity as InitialTopUpActivity).setErrorEditText(getString(R.string.top_up_balance_error_multiple))
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}