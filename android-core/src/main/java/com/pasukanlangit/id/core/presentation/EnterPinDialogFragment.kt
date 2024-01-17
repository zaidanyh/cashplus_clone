package com.pasukanlangit.id.core.presentation

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.pasukanlangit.id.core.R
import com.pasukanlangit.id.core.databinding.FragmentEnterPinDialogBinding
import com.pasukanlangit.id.core.utils.KeyboardUtil.closeKeyboardDialog
import com.pasukanlangit.id.core.utils.KeyboardUtil.hideSoftKeyboard
import com.pasukanlangit.id.core.utils.KeyboardUtil.openSoftKeyboard

class EnterPinDialogFragment(
    private val onEnterPinCompleted: (String) -> Unit
) : DialogFragment() {

    private var _binding: FragmentEnterPinDialogBinding? = null
    private val binding get() = _binding!!

    override fun show(manager: FragmentManager, tag: String?) {
        if (isShown) return

        super.show(manager, tag)
        isShown = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //make dialog fullscreen
        setStyle(STYLE_NORMAL, R.style.FullScreenEnterPinDialogNoClose)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterPinDialogBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            openSoftKeyboard(requireContext(), inputOtpTransaction)
            inputOtpTransaction.setOtpCompletionListener {
                onEnterPinCompleted(it)
                closeKeyboardDialog(requireContext(), inputOtpTransaction)
            }
        }
    }

    fun setLoading(value: Boolean) {
        if(isVisible) binding.loadingEnterPin.isVisible = value
    }

    fun clearTextOnDialog() {
        binding.inputOtpTransaction.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        isShown = false
        hideSoftKeyboard(requireContext())
        binding.inputOtpTransaction.text = null
        super.onDismiss(dialog)
    }

    companion object {
        var isShown: Boolean = false
    }
}
