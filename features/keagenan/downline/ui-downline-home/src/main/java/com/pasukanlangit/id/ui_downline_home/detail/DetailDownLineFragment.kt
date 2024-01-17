package com.pasukanlangit.id.ui_downline_home.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.core.*
import com.pasukanlangit.id.core.presentation.MutationSumDetPageType
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.putExtra
import com.pasukanlangit.id.ui_downline_home.databinding.FragmentDetailDownLineBinding
import com.pasukanlangit.id.ui_downline_home.subdownline.SubDownLineActivity

class DetailDownLineFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDetailDownLineBinding? = null
    private val binding get() = _binding!!

    private var isFromSubDownLine = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailDownLineBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconClose.setOnClickListener { dismiss() }

        arguments?.getBoolean(KEY_IS_FROM_SUB_DOWN_LINE)?.let { isFromSubDownLine = it }
        arguments?.parcelable<DownLineDetail>(KEY_DOWN_LINE_ITEM)?.let { downline ->
            with(binding){
                tvIsActive.isVisible = downline.isActive
                tvIsNotActive.isVisible = !downline.isActive
                tvName.text = downline.accountName
                tvPhone.text = downline.phoneNumber
                tvAddress.text = downline.address
                tvSaldo.text = downline.balance
                tvOwnerName.text= downline.ownerName
                tvTrxCount.text = downline.trxCount
                tvMarkupCount.text = downline.markup
                tvDownlineCount.text = downline.downLineCount
                buttonCheckSubdownline.isVisible =
                    (downline.downLineCount.toIntOrNull() ?: 0) > 0 && !isFromSubDownLine

                buttonCheckMutation.setOnClickListener {
                    startActivity(
                        ModuleRoute.internalIntent(requireContext(), MUTASI_SUMMARY_DETAIL_DOWNLINE_PATH).apply {
                            putExtra(MUTASI_SUMMARY_DETAIL_DOWNLINE_KEY_PHONE_NUMBER, downline.phoneNumber)
                            putExtra(MUTASI_SUMMARY_DETAIL_DOWNLINE_KEY_NAME, downline.accountName)
                            putExtra(MUTASI_SUMMARY_DETAIL_DOWNLINE_KEY_IS_FROM_SUBDOWNLINE, isFromSubDownLine)
                            putExtra(MutationSumDetPageType.CEK_MUTATION)
                        }
                    )
                    dismiss()
                }
                buttonCheckSubdownline.setOnClickListener {
                    startActivity(
                        Intent(requireContext(), SubDownLineActivity::class.java).apply {
                            putExtra(SubDownLineActivity.KEY_NAME_PARENT, downline.accountName)
                            putExtra(SubDownLineActivity.KEY_PHONE_PARENT, downline.phoneNumber)
                        }
                    )
                    dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_DOWN_LINE_ITEM = "KEY_DOWN_LINE_ITEM"
        private const val KEY_IS_FROM_SUB_DOWN_LINE = "KEY_IS_FROM_SUB_DOWN_LINE"
        //The activity is being restored from an instance state bundle. Part of the restore operation is recreating its fragments and call 0-args constructor.
        //best practice to instance fragment: https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment#:~:text=1229-,If%20Android%20decides%20to%20recreate%20your%20Fragment%20later%2C%20it%27s%20going,after%20a%20Fragment%20is%20recreated%20by%20Android%20is%20to%20pass,-a%20bundle%20to
        fun newInstance(
            downLineDetail: DownLineDetail,
            isFromSubDownLine: Boolean = false
        ) : DetailDownLineFragment {
            return DetailDownLineFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_DOWN_LINE_ITEM, downLineDetail)
                    putBoolean(KEY_IS_FROM_SUB_DOWN_LINE, isFromSubDownLine)
                }
            }
        }
    }
}