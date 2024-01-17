package com.pasukanlangit.cashplus.ui.pages.others.settings.email

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.MainActivityNavComp
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentChangeEmailBinding
import com.pasukanlangit.cashplus.view_model.MainViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.presentation.components.GenericConfirmDialogFragment
import com.pasukanlangit.id.core.presentation.components.NegativeButton
import com.pasukanlangit.id.core.presentation.components.PositiveButton
import com.pasukanlangit.id.core.utils.InputUtil.isValidEmail
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChangeEmailFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChangeEmailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by sharedViewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var stateEmail = false
    private var isChangeEmail = false

    private var uuid: String? = null
    private var accessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()
        val email = arguments?.getString(EMAIL_ACCOUNT_KEY)
        stateEmail = !email.isNullOrEmpty()

        with(binding) {
            tvEmail.text = if (email.isNullOrEmpty()) getString(R.string.email_not_filled) else email
            tvEmail.setTextColor(if (email.isNullOrEmpty()) Color.parseColor("#DBA800") else Color.parseColor("#1E293B"))
            edtEmail.setText(email)
            edtEmail.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        layoutInputEmail.error = getString(R.string.input_email_is_empty)
                        stateEmail = false
                        return
                    }
                    if(!input.isValidEmail()) {
                        layoutInputEmail.error = getString(R.string.input_email_not_valid)
                        stateEmail = false
                        return
                    }
                    stateEmail = true
                    layoutInputEmail.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnChangeEmail.isEnabled = stateEmail
                }
            })
            btnChangeEmail.text = if (email.isNullOrEmpty()) getString(R.string.adding_email)
                else getString(R.string.change_email)
            btnChangeEmail.setOnClickListener {
                stateChangeEmail()
                isChangeEmail = true
            }
        }
    }

    private fun stateChangeEmail() {
        with(binding) {
            if (isChangeEmail) {
                GenericConfirmDialogFragment.Builder()
                    .title(getString(R.string.confirm))
                    .description(getString(R.string.confirm_change_email))
                    .buttonPositive(
                        PositiveButton(
                            backgroundButton = R.drawable.bg_primary_rounded_12,
                            buttonText = getString(R.string.yes_right),
                            buttonTextColor = Color.parseColor("#FFFFFF"),
                            onBtnAction = {
                                this@ChangeEmailFragment.dismiss()
                                viewModel.updateEmail(
                                    uuid = uuid ?: "",
                                    email = edtEmail.text.toString().trim(),
                                    accessToken = accessToken ?: ""
                                )
                            }
                        )
                    )
                    .buttonNegative(
                        NegativeButton(
                            backgroundButton = R.drawable.bg_blue50_rounded_12,
                            buttonText = getString(R.string.cancel),
                            buttonTextColor = Color.parseColor("#0A57FF"),
                            actionDismiss = true
                        )
                    )
                    .show(requireActivity().supportFragmentManager, "Confirm Change Email")
                return
            }
            txtEmail.text = getString(R.string.email_address)
            tvEmail.isVisible = false
            layoutInputEmail.isVisible = true
            btnChangeEmail.text = getString(R.string.save_changes)
            btnChangeEmail.isEnabled = stateEmail
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        (activity as MainActivityNavComp).stateIsOpenEmailConfig = false
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val EMAIL_ACCOUNT_KEY = "email_account_key"

        @JvmStatic
        fun newInstance(email: String?) =
            ChangeEmailFragment().apply {
                arguments = Bundle().apply {
                    putString(EMAIL_ACCOUNT_KEY, email)
                }
            }
    }
}