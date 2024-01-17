package com.pasukanlangit.id.ui_downline_home.mintasaldoqr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pasukanlangit.id.core.extensions.setIDRCurrencyInput
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.ui_downline_home.databinding.FragmentEnterNominalBinding

class EnterNominalFragment : Fragment() {

    private var _binding: FragmentEnterNominalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEnterNominalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGenerate.setOnClickListener {
            val inputNominal = binding.inputNominal.text.toString().replace(",", "")

            if(inputNominal.isEmpty()){
                Toast.makeText(requireContext(), "Masukkan nominal terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            findNavController().navigate(EnterNominalFragmentDirections.actionEnterNominalFragmentToQRResultFragment(
                nominal = inputNominal
            ))
        }

        binding.inputNominal.setIDRCurrencyInput()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}