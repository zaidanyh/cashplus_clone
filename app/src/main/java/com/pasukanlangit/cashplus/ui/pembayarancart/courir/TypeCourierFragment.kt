package com.pasukanlangit.cashplus.ui.pembayarancart.courir

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.databinding.FragmentTypeCorierBinding

class TypeCourierFragment : Fragment() {

    private var _binding: FragmentTypeCorierBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTypeCorierBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent: Fragment? = (parentFragment as NavHostFragment).parentFragment
        (parent as ChooseCourierFragment).setBtnBackIsActive(false)

        setUpRv()
    }

    private fun setUpRv() {
        with(binding.rvCourier){
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CourierListAdapter(getCouriers()){
                val action = TypeCourierFragmentDirections.actionTypeCourierFragmentToListCourirByTypeFragment(it)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}