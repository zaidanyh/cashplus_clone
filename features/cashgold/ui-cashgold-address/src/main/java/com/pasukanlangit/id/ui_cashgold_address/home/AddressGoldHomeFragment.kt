package com.pasukanlangit.id.ui_cashgold_address.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.id.core.utils.putExtra
import com.pasukanlangit.id.ui_cashgold_address.addupdate.AddUpdateAddressActivity
import com.pasukanlangit.id.ui_cashgold_address.addupdate.AddUpdateAddressType
import com.pasukanlangit.id.ui_cashgold_address.addupdate.AddressParcelize
import com.pasukanlangit.id.ui_cashgold_address.databinding.FragmentAddressGoldHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddressGoldHomeFragment : Fragment() {

    private var _binding: FragmentAddressGoldHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddressGoldHoveViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddressGoldHomeBinding.inflate(layoutInflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddAddress.setOnClickListener {
            startActivity(Intent(requireContext(), AddUpdateAddressActivity::class.java))
        }

        setUpRvAddress()
        collectState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(AddressGoldHomeEvent.GetAddressList)
    }

    private fun setUpRvAddress() {
        binding.rvAddress.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun collectState() {
        collectStateLoading()
        collectStateError()
        collectAddressList()
    }

    private fun collectAddressList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stateAddressList.collectLatest {
                    it?.let { address ->
                        binding.note.isVisible = address.isEmpty()
                        binding.rvAddress.adapter = AddressAdapter(address){
                            when(it){
                                is AddressAdapterEvent.DeleteAddress -> {
                                    viewModel.onTriggerEvent(AddressGoldHomeEvent.DeleteAddress(it.id))
                                }
                                is AddressAdapterEvent.UpdateAddressFull -> {
                                    startActivity(
                                        Intent(requireContext(), AddUpdateAddressActivity::class.java)
                                            .putExtra(AddUpdateAddressType.TYPE_UPDATE)
                                            .putExtra(AddUpdateAddressActivity.KEY_ADDRESS, AddressParcelize(
                                                id = it.address.id,
                                                kodepos = it.address.kodepos,
                                                alamat = it.address.alamat,
                                                provinsi = it.address.provinsi,
                                                kabupaten = it.address.kabupaten,
                                                kecamatan = it.address.kecamatan,
                                                kelurahan = it.address.kelurahan,
                                                isDefault = it.address.isDefault
                                            )
                                            )
                                    )
                                }
                                is AddressAdapterEvent.UpdateToMainAddress -> {
                                    viewModel.onTriggerEvent(AddressGoldHomeEvent.UpdateAddress(
                                        id = it.address.id,
                                        zipCode = it.address.kodepos,
                                        address = it.address.alamat,
                                        province = it.address.provinsi,
                                        city = it.address.kabupaten,
                                        district = it.address.kecamatan,
                                        village = it.address.kelurahan,
                                        isMain = true
                                    ))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun collectStateError() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stateError.collectLatest { message ->
                    message.peek()?.let { info ->
                        val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                        toast.show()
                        delay(toast.duration.toLong() + 500L)
                        viewModel.onTriggerEvent(AddressGoldHomeEvent.RemoveHeadQueue)
                    }
                }
            }
        }
    }

    private fun collectStateLoading() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stateLoading.collectLatest { isLoading ->
                    binding.loading.isVisible = isLoading
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}