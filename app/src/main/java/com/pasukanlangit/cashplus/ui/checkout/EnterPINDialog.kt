package com.pasukanlangit.cashplus.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentEnterPINDialogBinding
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.ui.pages.others.settings.pin.ChangePinActivity
import com.pasukanlangit.cashplus.utils.MyUtils.goToMainAndSendMessage
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.library_core.domain.model.NotifType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class EnterPINDialog : DialogFragment() {

    private var _binding: FragmentEnterPINDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionPayViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var destination: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterPINDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uuid = sharedPref.getUUID()
        val accessToken = sharedPref.getAccessToken()
        destination = arguments?.getString(DESTINATION_KEY)
        val productCode = arguments?.getString(PRODUCT_CODE_KEY)

        with(binding) {
            tvResetPin.setOnClickListener {
                val intent = Intent(requireContext(), ChangePinActivity::class.java)
                startActivity(intent)
            }
            KeyboardUtil.openSoftKeyboard(requireContext(), inputOtpTransaction)

            inputOtpTransaction.setOtpCompletionListener {
                if (uuid != null && accessToken != null) {
                    val transactRequest = TransactionRequest(
                        uuid = uuid,
                        kode_produk = productCode ?: "",
                        dest = destination ?: "",
                        pin = it
                    )
                    KeyboardUtil.closeKeyboardDialog(requireContext(), inputOtpTransaction)
                    viewModel.sendTransaction(transactRequest, accessToken)
                }
            }
        }

        observePayBill()
    }

    private fun observePayBill() {
        viewModel.responseTransaction.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                when(response.status) {
                    Status.LOADING -> {
                        binding.loadingEnterPin.isVisible = true
                        this.isCancelable = false
                    }
                    Status.SUCCESS -> {
                        binding.loadingEnterPin.isVisible = false
                        this.isCancelable = true

                        val message = getString(R.string.pay_format_successfully, response.data?.shortDsc, destination)
                        goToMainAndSendMessage(
                            context = requireContext(), message = message,
                            notifType = NotifType.NOTIF_TRX_SUCCESS
                        )
                    }
                    Status.ERROR -> {
                        binding.loadingEnterPin.isVisible = false
                        binding.tvError.isVisible = true
                        this.isCancelable = true

                        response.data?.let { trx ->
                            var status = getString(R.string.failed)
                            if(trx.rc == "68") status = getString(R.string.pending)
                            val message = "Pembelian ${trx.dsc?.lowercase(Locale.ROOT)} ke ${trx.dsc?.replace(Regex("\\D"), "")} $status. ${
                                trx.rc_msg?.lowercase()
                            }"
                            if(trx.rc == "68") {
                                goToMainAndSendMessage(
                                    requireContext(),
                                    trx.msg ?: "",
                                    NotifType.NOTIF_PENDING_OR_PROCESS
                                )
                                return@observe
                            }
                            if (trx.rc_msg?.contains("pin", ignoreCase = true) == true) {
                                binding.tvError.text = getString(R.string.state_wrong_pin)
                                binding.inputOtpTransaction.text?.clear()
                                KeyboardUtil.openSoftKeyboard(requireContext(), binding.inputOtpTransaction)
                                return@observe
                            } else binding.tvError.text = message
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

        private const val DESTINATION_KEY = "destination_key"
        private const val PRODUCT_CODE_KEY = "product_code_key"

        @JvmStatic
        fun newInstance(destination: String?, productCode: String?) =
            EnterPINDialog().apply {
                arguments = Bundle().apply {
                    putString(DESTINATION_KEY, destination)
                    putString(PRODUCT_CODE_KEY, productCode)
                }
            }
    }
}