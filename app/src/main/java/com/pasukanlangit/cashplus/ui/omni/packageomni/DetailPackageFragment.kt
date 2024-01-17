package com.pasukanlangit.cashplus.ui.omni.packageomni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentDetailPackageBinding
import com.pasukanlangit.cashplus.domain.model.ResponsePackage
import com.pasukanlangit.cashplus.model.response.TransactionTAGResponse
import com.pasukanlangit.cashplus.utils.MyUtils.convertToNormalNumber
import com.pasukanlangit.id.core.utils.SaldoNotEnoughNotif
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah

class DetailPackageFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDetailPackageBinding? = null
    private val binding get() = _binding!!

    private lateinit var packageAdapter: DetailPackageAdapter
    private var packaged: ResponsePackage? = null
    private var tagTransaction: TransactionTAGResponse? = null
    private var balance: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPackageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        packaged = arguments?.parcelable(RESPONSE_PACKAGE_KEY)
        tagTransaction = arguments?.parcelable(RESPONSE_TRANSACTION_TAG_KEY)
        balance = arguments?.getDouble(ACCOUNT_BALANCE_KEY)

        setUpRecyclerView()
        val fee = tagTransaction?.fee?.toInt()
        val adminFee = fee?.plus(tagTransaction?.billData?.admin?.toInt() ?: 0)
            ?: tagTransaction?.billData?.admin?.toInt()
        packageAdapter.setBonuses(packaged?.bonuses)
        with(binding) {
            tvPackageName.text = packaged?.name
            tvAdminCost.text = if (adminFee == 0) getString(R.string.admin_free)
            else getNumberRupiah(adminFee)
            val total = tagTransaction?.price?.toIntOrNull() ?: tagTransaction?.billData?.total?.toIntOrNull()
            tvTotalPrice.text = getNumberRupiah(total)
            btnBuy.setUpToProgressButton(viewLifecycleOwner)
            btnBuy.setOnClickListener {
                if (balance!! < convertToNormalNumber(tvTotalPrice.text.toString())) {
                    val notifModal = SaldoNotEnoughNotif()
                    notifModal.show(childFragmentManager,notifModal.tag)
                    return@setOnClickListener
                }
                (activity as PackageOmniActivity).enterPIN
                    .show(requireActivity().supportFragmentManager, "Enter PIN for Pay")
            }
        }
    }

    private fun setUpRecyclerView() {
        packageAdapter = DetailPackageAdapter()
        with(binding.rvDetailPackage) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = packageAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val RESPONSE_PACKAGE_KEY = "response_package_key"
        private const val RESPONSE_TRANSACTION_TAG_KEY = "response_transaction_tag_key"
        private const val ACCOUNT_BALANCE_KEY = "account_balance_key"

        @JvmStatic
        fun newInstance(packaged: ResponsePackage, tag: TransactionTAGResponse, balance: Double) =
            DetailPackageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(RESPONSE_PACKAGE_KEY, packaged)
                    putParcelable(RESPONSE_TRANSACTION_TAG_KEY, tag)
                    putDouble(ACCOUNT_BALANCE_KEY, balance)
                }
            }
    }
}