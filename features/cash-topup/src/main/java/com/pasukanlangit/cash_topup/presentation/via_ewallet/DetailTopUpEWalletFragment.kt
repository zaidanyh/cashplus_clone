package com.pasukanlangit.cash_topup.presentation.via_ewallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.FragmentDetailTopUpEWalletBinding
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailTopUpEWalletFragment : Fragment() {

    private var _binding: FragmentDetailTopUpEWalletBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViaEWalletViewModel by sharedViewModel()
    private val args: DetailTopUpEWalletFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailTopUpEWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuTopUp = args.topUpEWalletData
        binding.imgEWallet.setImageResource(menuTopUp.img)
        binding.btnPay.setUpToProgressButton(viewLifecycleOwner)
        binding.btnPay.setOnClickListener {
            findNavController().navigate(DetailTopUpEWalletFragmentDirections.actionDetailToProcessingTopUp(args.topUpEWalletData))
        }
        collectData()
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.amountTopUp.collectLatest { binding.tvNominal.text = getNumberRupiah(it?.toIntOrNull()) }
                }
                launch {
                    viewModel.adminCost.collectLatest { binding.tvAdmin.text = getNumberRupiah(it?.toIntOrNull()) }
                }
                launch {
                    viewModel.isReadyPay.collectLatest { state ->
                        with(binding) {
                            btnPay.isEnabled = state
                            val amount = viewModel.amountTopUp.value
                            val adminCost = viewModel.adminCost.value
                            val adminPercentage = adminCost?.toDoubleOrNull()?.div(amount?.toDoubleOrNull() ?: 0.0)?.times(100)
                            val total = amount?.toIntOrNull()?.plus(adminCost?.toIntOrNull() ?: 0) ?: 0
                            tvAdminPercentage.text = getString(R.string.percentage_admin_fee, adminPercentage)
                            tvTotal.text = getNumberRupiah(total)
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