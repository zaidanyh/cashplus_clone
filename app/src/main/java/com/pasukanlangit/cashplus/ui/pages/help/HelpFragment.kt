package com.pasukanlangit.cashplus.ui.pages.help

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pasukanlangit.cashplus.databinding.FragmentHelpBinding
import com.pasukanlangit.cashplus.utils.MyUtils

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            wrapperWa.setOnClickListener {
                MyUtils.callCustomerService(requireContext())
            }
            wrapperTelegram.setOnClickListener {
                MyUtils.callCustomerServiceTelegram(requireContext())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}