package com.pasukanlangit.cashplus.ui.ewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.github.razir.progressbutton.hideProgress
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentDirectProductPurchasesBinding
import com.pasukanlangit.cashplus.domain.model.UnitPriceResponse
import com.pasukanlangit.cashplus.utils.MyUtils.httpToHttps
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.SaldoNotEnoughNotif
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DirectProductPurchasesFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDirectProductPurchasesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DirectProductPurchasesViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var result: UnitPriceResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDirectProductPurchasesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uuid = sharedPref.getUUID()
        val accessToken = sharedPref.getAccessToken()
        val phoneNumber = arguments?.getString(PHONE_NUMBER_DESTINATION_KEY)
        val imageUrl = arguments?.getString(IMAGE_URL_PRODUCT_KEY)
        result = arguments?.parcelable(RESULT_CALCULATE_UNIT_KEY)

        with(binding) {
            Glide.with(requireContext())
                .load(imageUrl?.httpToHttps())
                .into(imgProduct)
            tvNameCustomer.text = result?.name
            tvCustomerNumber.text = phoneNumber
            tvProductPurchase.text = getString(R.string.purchase_dana_format, result?.qty)
            tvPrice.text = getNumberRupiah(result?.price?.toIntOrNull())
            tvAdminFee.text = if (result?.admin.isNullOrEmpty() || result?.admin?.toIntOrNull() == 0)
                getString(R.string.admin_free)
            else getNumberRupiah(result?.admin?.toIntOrNull())
            tvTotalPayment.text = getNumberRupiah(result?.totalPrice?.toIntOrNull())
            btnPay.setUpToProgressButton(viewLifecycleOwner)
            btnPay.setOnClickListener {
                viewModel.getBalance(uuid, accessToken)
            }
        }
        collectDataBalance()
    }

    private fun collectDataBalance() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.balanceLoading.collectLatest {
                        binding.btnPay.isEnabled = !it
                        if (it) {
                            binding.btnPay.showLoading()
                            return@collectLatest
                        }
                        binding.btnPay.hideProgress(getString(R.string.pay))
                    }
                }
                launch {
                    viewModel.balance.collectLatest { response ->
                        if (response != null) {
                            if (response.balance!! < (result?.totalPrice?.toIntOrNull() ?: 0)) {
                                val balanceNotEnough = SaldoNotEnoughNotif()
                                balanceNotEnough.show(requireActivity().supportFragmentManager, balanceNotEnough.tag)
                                dismiss()
                                return@collectLatest
                            }
                            (activity as InputManualActivity).enterPIN
                                .show(requireActivity().supportFragmentManager, "Enter PIN")
                        }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeHeadMessage()
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

        private const val PHONE_NUMBER_DESTINATION_KEY = "phone_number_destination_key"
        private const val IMAGE_URL_PRODUCT_KEY = "image_url_product_key"
        private const val RESULT_CALCULATE_UNIT_KEY = "result_calculate_unit_key"

        @JvmStatic
        fun newInstance(phoneNumber: String?, imgUrl: String, result: UnitPriceResponse?) =
            DirectProductPurchasesFragment().apply {
                arguments = Bundle().apply {
                    putString(PHONE_NUMBER_DESTINATION_KEY, phoneNumber)
                    putString(IMAGE_URL_PRODUCT_KEY, imgUrl)
                    putParcelable(RESULT_CALCULATE_UNIT_KEY, result)
                }
            }
    }
}