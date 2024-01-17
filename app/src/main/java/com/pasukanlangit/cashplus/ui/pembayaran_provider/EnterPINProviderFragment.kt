package com.pasukanlangit.cashplus.ui.pembayaran_provider

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.pasukanlangit.cashplus.MainActivityNavComp
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentEnterPINProviderBinding
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.pages.others.settings.pin.ChangePinWithoutLastPinActivity
import com.pasukanlangit.cashplus.utils.MyUtils.goToMainAndSendMessage
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.TransactionViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.library_core.domain.model.NotifType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnterPINProviderFragment : DialogFragment(), ButtomSheetNotif.BottomSheetEvent {

    private var _binding: FragmentEnterPINProviderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null
    private var codeProduct: String? = null
    private var destination: String? = null

    private lateinit var trxId : String
    private lateinit var lastPinEntered : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogMax)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterPINProviderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()
        codeProduct = arguments?.getString(KEY_PRODUCT_CODE)
        destination = arguments?.getString(KEY_DESTINATION)

        with(binding) {
            KeyboardUtil.openSoftKeyboard(requireContext(), inputOtpTransaction)
            inputOtpTransaction.setOtpCompletionListener {
                lastPinEntered = it
                viewModel.sendTransaction(
                    TransactionRequest(uuid ?: "", codeProduct ?: "", destination ?: "", it),
                    accessToken ?: ""
                )
                KeyboardUtil.closeKeyboardDialog(requireContext(), inputOtpTransaction)
            }
            btnForgotPw.setOnClickListener {
                activity?.startActivity(Intent(activity, ChangePinWithoutLastPinActivity::class.java))
            }
        }
        observeTransaction()
    }

    private fun observeTransaction() {
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

                        response.data?.let { trx ->
                            trx.rcMsg?.let { rcMsg ->
                                if (rcMsg.contains("SUDAH ADA DENGAN STATUS")) {
                                    trxId = trx.trxId ?: ""
                                    val menusAllFragment = ButtomSheetNotif(getString(R.string.new_transaction_confirmation), NotifType.NOTIF_TRX_HAS_BEEN_COMPLETED,this)
                                    menusAllFragment.show(childFragmentManager,menusAllFragment.tag)
                                    return@observe
                                }
                            }

                            val message = getString(R.string.buying_format_successfully, trx.desc?.lowercase(), trx.dest?.replace(Regex("\\D"), ""))
                            if (codeProduct?.contains("pln", ignoreCase = true) == true) {
                                val token = trx.sn?.split("/")?.first()
                                goToMainAndSendMessage(
                                    context = requireContext(), message = message, notifType = NotifType.NOTIF_TRX_SUCCESS,
                                    additionalToken = token
                                )
                                return@observe
                            }
                            goToMainAndSendMessage(requireContext(), message, NotifType.NOTIF_TRX_SUCCESS)
                        }
                    }
                    Status.ERROR -> {
                        binding.loadingEnterPin.isVisible = false
                        binding.tvError.isVisible = true
                        this.isCancelable = true

                        response.data?.let { trx ->
                            var status = getString(R.string.failed)
                            if(trx.rc == "68") status = getString(R.string.pending)
                            val message = "Pembelian ${trx.desc?.lowercase()} ke ${trx.dest?.replace(Regex("\\D"), "")} $status. ${
                                trx.rcMsg?.lowercase()
                            }"
                            trx.rcMsg?.let { rcMessage ->
                                if (rcMessage.contains("SUDAH ADA DENGAN STATUS")) {
                                    trxId = trx.trxId ?: ""
                                    val menusAllFragment = ButtomSheetNotif(getString(R.string.new_transaction_confirmation), NotifType.NOTIF_TRX_HAS_BEEN_COMPLETED,this)
                                    menusAllFragment.show(childFragmentManager,menusAllFragment.tag)
                                    return@observe
                                }
                            }
                            if(trx.rc == "68") {
                                goToMainAndSendMessage(
                                    requireContext(),
                                    trx.msg ?: "",
                                    NotifType.NOTIF_PENDING_OR_PROCESS
                                )
                                return@observe
                            }
                            if (trx.rcMsg?.contains("pin", ignoreCase = true) == true) {
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

    override fun onRepeatOrder() {
        val randomInt = (1..100).random()
        if (::trxId.isInitialized && ::lastPinEntered.isInitialized) {
            viewModel.sendTransaction(
                TransactionRequest(
                    uuid = uuid ?: "", kode_produk = codeProduct ?: "", dest = destination ?: "",
                    pin = lastPinEntered, reqId = randomInt.toString()),
                accessToken ?: ""
            )
        }
    }

    override fun onCancelOrder() {
        Toast.makeText(requireActivity(),getString(R.string.canceling_order), Toast.LENGTH_LONG).show()
        lifecycleScope.launch {
            delay(2000)
            activity?.startActivity(Intent(activity, MainActivityNavComp::class.java))
            activity?.finishAffinity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val KEY_PRODUCT_CODE = "key_product_code"
        private const val KEY_DESTINATION = "key_destination"

        @JvmStatic
        fun newInstance(productCode: String?, destination: String?) =
            EnterPINProviderFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_PRODUCT_CODE, productCode)
                    putString(KEY_DESTINATION, destination)
                }
            }
    }
}