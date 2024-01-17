package com.pasukanlangit.cashplus.ui.pages.others

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentChangeReferralBinding
import com.pasukanlangit.cashplus.view_model.MainViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.CoreUtils.copyClipboard
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChangeReferralFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChangeReferralBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by sharedViewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var isEditReferral = false
    private var btnActionIsActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeReferralBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val referralCode = arguments?.getString(REFERRAL_CODE_KEY)
        val referralName = arguments?.getString(REFERRAL_NAME_KEY)
        val referralNumber = arguments?.getString(REFERRAL_NUMBER_KEY)
        val isChanged = referralCode != "CPS001"
        with(binding) {
            tvReferralName.text = referralName
            tvReferral.text = referralNumber
            edtReferralCode.filters = arrayOf(InputFilter.LengthFilter(16))
            edtReferralCode.setText(referralNumber)
            btnCopyReferral.setOnClickListener {
                copyClipboard(requireContext(), referralNumber)
            }
            edtReferralCode.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        edtReferralCode.error = getString(R.string.referral_required)
                        btnActionIsActive = false
                        return
                    }
                    if (input.length < 6) {
                        edtReferralCode.error = getString(R.string.number_or_code_not_valid)
                        btnActionIsActive = false
                        return
                    }
                    btnActionIsActive = true
                    edtReferralCode.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnAction.isEnabled = btnActionIsActive
                }
            })
            btnAction.text = if (isChanged)
                getString(R.string.finish)
            else getString(R.string.change)
            btnAction.setOnClickListener {
                if (!isChanged) {
                    isEditReferral = !isEditReferral
                    edtReferralCode.isVisible = isEditReferral
                    tvReferral.isVisible = !isEditReferral
                    tvReferralName.isVisible = !isEditReferral
                    btnCopyReferral.isVisible = !isEditReferral
                    if (isEditReferral) {
                        btnAction.text = getString(R.string.save)
                        return@setOnClickListener
                    }
                    btnAction.text = getString(R.string.change)
                    viewModel.changeReferral(
                        uuid = sharedPref.getUUID() ?: "",
                        referral = edtReferralCode.text.toString().trim(),
                        accessToken = sharedPref.getAccessToken() ?: ""
                    )
                    this@ChangeReferralFragment.dismiss()
                    return@setOnClickListener
                }
                this@ChangeReferralFragment.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val REFERRAL_CODE_KEY = "referral_code_key"
        private const val REFERRAL_NAME_KEY = "referral_name_key"
        private const val REFERRAL_NUMBER_KEY = "referral_number_key"

        @JvmStatic
        fun newInstance(referralCode: String?, referralName: String?, referralNumber: String?) =
            ChangeReferralFragment().apply {
                arguments = Bundle().apply {
                    putString(REFERRAL_CODE_KEY, referralCode)
                    putString(REFERRAL_NAME_KEY, referralName)
                    putString(REFERRAL_NUMBER_KEY, referralNumber)
                }
            }
    }
}