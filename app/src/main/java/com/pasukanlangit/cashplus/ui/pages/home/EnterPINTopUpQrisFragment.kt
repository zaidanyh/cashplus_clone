package com.pasukanlangit.cashplus.ui.pages.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentEnterPINTopUpQrisBinding
import com.pasukanlangit.cashplus.model.request_body.TopUpQrisRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.view_model.MainViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.core.utils.TIME_SHOW_NOTIF
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.library_core.domain.model.NotifType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnterPINTopUpQrisFragment : DialogFragment() {

    private var _binding: FragmentEnterPINTopUpQrisBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var nominalTopUp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterPINTopUpQrisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uuid = sharedPref.getUUID()
        val accessToken = sharedPref.getAccessToken()
        nominalTopUp = arguments?.getString(ARG_NOMINAL_TOP_UP)

        with(binding) {
            KeyboardUtil.openSoftKeyboard(requireContext(), inputPinTransaction)
            inputPinTransaction.setOtpCompletionListener {
                if (!uuid.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
                    viewModel.topUpQris(
                        TopUpQrisRequest(uuid, nominalTopUp ?: "", it), accessToken
                    )
                }
                KeyboardUtil.closeKeyboardDialog(requireContext(), inputPinTransaction)
            }
        }
        collectTopUpQris()
    }

    private fun collectTopUpQris() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.topUpQrisLoading.collectLatest {
                        this@EnterPINTopUpQrisFragment.isCancelable = !it
                        binding.progressBar.isVisible = it
                    }
                }
                launch {
                    viewModel.topUpQris.collectLatest {
                        if (it != null) {
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.transfer_success))
                                .image(R.drawable.illustration_success2)
                                .description(getString(R.string.topup_qris_success, getNumberRupiah(nominalTopUp?.toIntOrNull())))
                            this@EnterPINTopUpQrisFragment.dismiss()
                        }
                    }
                }
                launch {
                    viewModel.topUpQrisError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val buttomSheetNotif = ButtomSheetNotif(info, NotifType.NOTIF_ERROR)
                            buttomSheetNotif.show(requireActivity().supportFragmentManager, buttomSheetNotif.tag)
                            delay(TIME_SHOW_NOTIF/2)
                            viewModel.removeTopUpQrisError()
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
        private const val ARG_NOMINAL_TOP_UP = "arg_nominal_top_up"

        @JvmStatic
        fun newInstance(nominalTopUp: String?) =
            EnterPINTopUpQrisFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NOMINAL_TOP_UP, nominalTopUp)
                }
            }
    }
}