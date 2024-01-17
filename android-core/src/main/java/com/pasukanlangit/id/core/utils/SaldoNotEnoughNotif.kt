package com.pasukanlangit.id.core.utils

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pasukanlangit.id.core.CASHPLUS_BALANCE_TOP_UP
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.R
import com.pasukanlangit.id.core.databinding.FragmentNotifModalBinding

class SaldoNotEnoughNotif: DialogFragment() {

    private var _binding: FragmentNotifModalBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotifModalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.dialog_animation_slide
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){

            btnTopupNotif.setOnClickListener {
                startActivity(
                    ModuleRoute.internalIntent(requireContext(), CASHPLUS_BALANCE_TOP_UP)
                )
                this@SaldoNotEnoughNotif.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}