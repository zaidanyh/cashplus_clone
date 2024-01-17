package com.pasukanlangit.cashplus.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pasukanlangit.cashplus.MainActivityNavComp
import com.pasukanlangit.cashplus.databinding.FragmentFourthRegisterBinding

class FourthRegisterFragment : Fragment() {

    private var _binding: FragmentFourthRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFourthRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFinishing.setOnClickListener {
            activity?.finishAffinity()
            startActivity(Intent(activity, MainActivityNavComp::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}