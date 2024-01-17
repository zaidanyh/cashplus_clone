package com.pasukanlangit.id.travelling.hotel

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.travelling.hotel.databinding.FragmentFindCityBottomSheetBinding
import com.pasukanlangit.id.travelling.hotel.temp.CitySelected
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FindCityBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFindCityBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InitialHotelViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindCityBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            edtInputAirportName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().length > 2) {
                        viewModel.searchInput.value = s.toString()
                        viewModel.onTriggerEvent(HotelEvents.CityList)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            with(rvAirportList) {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(CashplusItemDecoration(24))
            }
        }
        collectCities()
    }

    private fun collectCities() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // CITIES
                launch {
                    viewModel.cityList.collectLatest {
                        if (it?.rc == "30") {
                            binding.rvAirportList.isVisible = false
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        } else if (it?.rc == "00") {
                            with(binding) {
                                rvAirportList.adapter =
                                    it.data?.let { hotels ->
                                        FindCityAdapter(hotels) { city ->
                                            viewModel.onTriggerEvent(
                                                HotelEvents.SetCitySelected(
                                                    CitySelected(city.hotelCityName, city.hotelCityCode)
                                                )
                                            )
                                            rvAirportList.isVisible = false
                                            edtInputAirportName.text?.clear()
                                            this@FindCityBottomSheetFragment.dismiss()
                                        }
                                    }
                                rvAirportList.isVisible = true
                            }
                        }
                    }
                }

                // LOADING
                launch {
                    viewModel.stateLoading.collectLatest { binding.progressBar.isVisible = it }
                }

                // ERROR
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                            }
                            viewModel.onTriggerEvent(HotelEvents.RemoveHeadMessage)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}