package com.pasukanlangit.cash_topup.presentation.via_others.qris

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.FragmentViaQRISResultBinding
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ViaQRISResultFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentViaQRISResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViaQRISViewModel by viewModel()

    private var totalPay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViaQRISResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nominal = arguments?.getString(AMOUNT_REQUEST_KEY)
        val adminCost = arguments?.getString(ADMIN_COST_RESULT_KEY)
        with(binding) {
            tvNominal.text = getNumberRupiah(nominal?.toIntOrNull())
            tvAdminCost.text = if (adminCost.isNullOrEmpty() || adminCost == "0") getString(R.string.admin_free)
                else getNumberRupiah(adminCost.toIntOrNull())
            totalPay = nominal?.toIntOrNull()?.plus(adminCost?.toIntOrNull() ?: 0) ?: 0
            tvPayTotal.text = getNumberRupiah(totalPay)
            btnGenerateQr.setUpToProgressButton(viewLifecycleOwner)
            btnGenerateQr.setOnClickListener {
                viewModel.onTriggerEvent(ViaQRISEvent.TopUpViaQRIS(amount = totalPay.toString()))
            }
        }

        collectDataQRISResult()
    }

    private fun collectDataQRISResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.viaQRISLoading.collectLatest {
                        this@ViaQRISResultFragment.isCancelable = !it
                        if (it) {
                            binding.btnGenerateQr.showLoading()
                            return@collectLatest
                        }
                        binding.btnGenerateQr.hideProgress(getString(R.string.generate_qr_code))
                    }
                }
                launch {
                    viewModel.viaQRIS.collectLatest { response ->
                        if (response != null) {
                            val fragment = ResultQRISFragment()
                            val bundle = Bundle().apply {
                                putInt(ResultQRISFragment.TOTAL_NOMINAL_TOP_UP, totalPay)
                                putString(ResultQRISFragment.QR_CODE_RESULT_KEY, response.qrUrl)
                            }
                            fragment.arguments = bundle
                            requireActivity().supportFragmentManager.beginTransaction()
                                .add(R.id.fragment_container_top_up, fragment, fragment.tag)
                                .commit()

                            this@ViaQRISResultFragment.dismiss()
                        }
                    }
                }
                launch {
                    viewModel.viaQRISError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(ViaQRISEvent.RemoveTopUpViaQRISError)
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

        private const val AMOUNT_REQUEST_KEY = "amount_request_key"
        private const val ADMIN_COST_RESULT_KEY = "admin_cost_result_key"

        @JvmStatic
        fun newInstance(amount: String, adminCost: String) =
            ViaQRISResultFragment().apply {
                arguments = Bundle().apply {
                    putString(AMOUNT_REQUEST_KEY, amount)
                    putString(ADMIN_COST_RESULT_KEY, adminCost)
                }
            }
    }
}