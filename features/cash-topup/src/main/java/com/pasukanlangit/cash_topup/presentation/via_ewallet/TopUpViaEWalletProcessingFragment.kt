package com.pasukanlangit.cash_topup.presentation.via_ewallet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.FragmentTopUpViaEWalletProcessingBinding
import com.pasukanlangit.id.core.MAIN_ACTIVITY_FORWARDING_TO_HISTORY
import com.pasukanlangit.id.core.MAIN_ACTIVITY_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TopUpViaEWalletProcessingFragment : Fragment() {

    private var _binding: FragmentTopUpViaEWalletProcessingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViaEWalletViewModel by sharedViewModel()
    private val args: TopUpViaEWalletProcessingFragmentArgs by navArgs()

    private var countdownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpViaEWalletProcessingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectDataViaEWallet()
        with(binding) {
            imgEWallet.setImageResource(args.processingTopUpViaEWallet.img)
            tvDesc.text = getString(R.string.top_up_via_e_wallet_processing, args.processingTopUpViaEWallet.payName)
            countdownTimer = object: CountDownTimer(58000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    tvTime.text = "${millisUntilFinished / 1000}"
                }

                override fun onFinish() {
                    closeParentDialog()
                }
            }
        }
    }

    private fun collectDataViaEWallet() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isReadyPay.collectLatest {
                        val amount = viewModel.amountTopUp.value
                        val adminCost = viewModel.adminCost.value
                        val billingPhone = viewModel.billingPhone.value
                        val total = amount?.toIntOrNull()?.plus(adminCost?.toIntOrNull() ?: 0) ?: 0
                        viewModel.onTriggerEvent(
                            ViaEWalletEvent.TopUpViaEWallet(
                                bankMitraCode = args.processingTopUpViaEWallet.bankCode,
                                payMethod = args.processingTopUpViaEWallet.payMethodCode.toString(),
                                amount = total.toString(), billingPhone = billingPhone.toString()
                            )
                        )
                    }
                }
                launch {
                    viewModel.viaEWalletLoading.collectLatest {
                        if (it) {
                            countdownTimer?.start()
                            return@collectLatest
                        }
                    }
                }
                launch {
                    viewModel.viaEWallet.collectLatest { response ->
                        if (response != null) {
                            countdownTimer?.cancel()
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.top_up_successfully))
                                .image(R.drawable.illustration_success2)
                                .description(response.message)
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.check_history),
                                        backgroundButton = R.drawable.bg_primary_rounded_20,
                                        buttonTextColor = Color.parseColor("#F1F5F9"),
                                        onBtnClicked = {
                                            activity?.finishAffinity()
                                            activity?.startActivity(
                                                ModuleRoute.internalIntent(requireContext(), MAIN_ACTIVITY_PATH).apply {
                                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    putExtra(MAIN_ACTIVITY_FORWARDING_TO_HISTORY, true)
                                                }
                                            )
                                        }
                                    )
                                )
                                .show(childFragmentManager)
                            closeParentDialog(true)
                        }
                    }
                }
                launch {
                    viewModel.viaEWalletError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) countdownTimer?.cancel()
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(ViaEWalletEvent.RemoveTopUpViaEWalletError)
                            closeParentDialog(true)
                        }
                    }
                }
            }
        }
    }

    private fun closeParentDialog(isNeedDelay: Boolean = false) {
        if (isNeedDelay) {
            lifecycleScope.launch {
                delay(2400)
                ((parentFragment as NavHostFragment?)?.parentFragment as TopUpViaEWalletResult).dismiss()
            }
            return
        }
        ((parentFragment as NavHostFragment?)?.parentFragment as TopUpViaEWalletResult).dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer?.cancel()
        countdownTimer = null
        _binding = null
    }
}