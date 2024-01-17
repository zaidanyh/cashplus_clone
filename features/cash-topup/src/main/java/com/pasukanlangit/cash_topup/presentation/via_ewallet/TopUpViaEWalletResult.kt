package com.pasukanlangit.cash_topup.presentation.via_ewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.FragmentTopUpViaEWalletResultBinding
import com.pasukanlangit.cash_topup.utils.MenuViaTopUpPayment
import com.pasukanlangit.id.core.utils.parcelable

class TopUpViaEWalletResult : BottomSheetDialogFragment() {

    private var _binding: FragmentTopUpViaEWalletResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpViaEWalletResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val amount = arguments?.getString(AMOUNT_TOP_UP_KEY)
        val menuTopUp = arguments?.parcelable<MenuViaTopUpPayment>(REQUEST_TOP_UP_VIA_E_WALLET_KEY)

        val bundle = Bundle().apply {
            putString(InputNumberEWalletFragment.AMOUNT_TOP_UP_VALUE_KEY, amount)
            putParcelable(InputNumberEWalletFragment.REQUEST_TOP_UP_VIA_E_WALLET_KEY, menuTopUp)
        }
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_e_wallet_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.top_up_ewallet_graph, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val AMOUNT_TOP_UP_KEY = "amount_top_up_key"
        private const val REQUEST_TOP_UP_VIA_E_WALLET_KEY = "request_top_up_via_e_wallet_key"

        fun newInstance(amount: String, menuTopUp: MenuViaTopUpPayment) =
            TopUpViaEWalletResult().apply {
                arguments = Bundle().apply {
                    putString(AMOUNT_TOP_UP_KEY, amount)
                    putParcelable(REQUEST_TOP_UP_VIA_E_WALLET_KEY, menuTopUp)
                }
            }
    }
}