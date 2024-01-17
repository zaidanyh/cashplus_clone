package com.pasukanlangit.id.nobu.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.FragmentDialogChooseBinding
import com.pasukanlangit.id.nobu.presentation.binding.AccountBindingActivity
import com.pasukanlangit.id.nobu.presentation.creation.QrisActivationActivity

class DialogChooseFragment : DialogFragment() {

    private var _binding: FragmentDialogChooseBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenEnterPinDialogNoClose)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogChooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnHave.setOnClickListener {
                activity?.startActivity(Intent(requireActivity(), AccountBindingActivity::class.java))
                activity?.finish()
            }
            btnNotHave.setOnClickListener {
                activity?.startActivity(Intent(requireActivity(), QrisActivationActivity::class.java))
                activity?.finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}