package com.pasukanlangit.id.cash_transfer.presentation.local.bank_add

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.FragmentBankListBottomSheetBinding
import com.pasukanlangit.id.cash_transfer.domain.model.LocalBankSavedResponse
import com.pasukanlangit.id.core.utils.parcelableArrayList

class BankListBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBankListBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBankListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setOnApplyWindowInsetsListener { _, windowInsets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                binding.root.setPadding(0, 0, 0, imeHeight)
                windowInsets.getInsets(WindowInsets.Type.ime() or WindowInsets.Type.systemGestures())
            }
            windowInsets
        }

        val bankSaved = arguments?.parcelableArrayList<LocalBankSavedResponse>(BANK_SAVED_LIST_KEY)
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_bank_list) as NavHostFragment
        val bundle = Bundle().apply {
            putParcelableArrayList(BANK_SAVED_LIST_KEY, bankSaved)
        }
        navController = navHostFragment.findNavController()
        navController.setGraph(R.navigation.bank_graph, bundle)

        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }
    }

    fun setBtnBackIsActive(value: Boolean, title: String) {
        with(binding) {
            btnBack.isVisible = value
            txtTitle.text = title
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val BANK_SAVED_LIST_KEY = "bank_saved_list_key"
        @JvmStatic
        fun newInstance(savedBank: ArrayList<LocalBankSavedResponse>? = null) =
            BankListBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(BANK_SAVED_LIST_KEY, savedBank)
                }
            }
    }
}