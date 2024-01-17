package com.pasukanlangit.id.nobu.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.FragmentConfirmBackDialogBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ConfirmBackDialogFragment : DialogFragment() {

    private var _binding: FragmentConfirmBackDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StateViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenEnterPinDialogNoClose)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmBackDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isBinding = arguments?.getBoolean(ARG_IS_ACCOUNT_BINDING)
        val isUnBind = arguments?.getBoolean(ARG_IS_UNBIND_ACCOUNT)

        with(binding) {
            if (isBinding == true) {
                txtConfirm.text = getString(R.string.cancel_binding)
                btnConfirm.text = getString(R.string.confirm_cancel)
            }
            else if (isBinding == false && isUnBind == true) {
                txtConfirm.text = getString(R.string.unbind_confirm)
                btnConfirm.text = getString(R.string.confirm_unbind)
            }
            else {
                txtConfirm.text = getString(R.string.cancel_account_creation)
                btnConfirm.text = getString(R.string.confirm_cancel)
            }

            btnConfirm.setOnClickListener {
                if (isBinding == false && isUnBind == true) {
                    viewModel.onTriggerEvent(StateEvent.UnBindAccount)
                    this@ConfirmBackDialogFragment.dismiss()
                } else {
                    activity?.finish()
                }
            }
            btnCancel.setOnClickListener { this@ConfirmBackDialogFragment.dismiss() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IS_ACCOUNT_BINDING = "arg_is_account_binding"
        private const val ARG_IS_UNBIND_ACCOUNT = "arg_is_unbind_account"

        @JvmStatic
        fun newInstance(isAccountBinding: Boolean = false, isUnbind: Boolean = false) =
            ConfirmBackDialogFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_ACCOUNT_BINDING, isAccountBinding)
                    putBoolean(ARG_IS_UNBIND_ACCOUNT, isUnbind)
                }
            }
    }
}