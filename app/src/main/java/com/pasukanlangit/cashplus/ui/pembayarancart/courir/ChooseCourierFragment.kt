package com.pasukanlangit.cashplus.ui.pembayarancart.courir

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentChooseCourirBinding

class ChooseCourierFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChooseCourirBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseCourirBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment_container_courier) as NavHostFragment
        navController = navHostFragment.findNavController()

        binding.btnBack.setOnClickListener {
            navController.popBackStack(R.id.typeCourierFragment, false)
        }
    }

   fun setBtnBackIsActive(value: Boolean){
       binding.btnBack.isVisible = value
   }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}