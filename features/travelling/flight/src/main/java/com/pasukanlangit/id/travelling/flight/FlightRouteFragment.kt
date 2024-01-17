package com.pasukanlangit.id.travelling.flight

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
import com.pasukanlangit.id.travelling.flight.databinding.FragmentFlightRouteBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.travelling.flight.temp.FlightRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FlightRouteFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFlightRouteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FlightViewModel by sharedViewModel()

    private var isDestination: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlightRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDestination = arguments?.getBoolean(IS_DESTINATION_KEY) ?: false
        with(binding) {
            rvAirportList.isVisible = false
            tvTitle.text = if (isDestination) getString(R.string.destination_airport)
            else getString(R.string.departure_airport)
            edtInputAirportName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().length > 2) {
                        viewModel.searchInput.value = s.toString()
                        viewModel.onTriggerEvent(FlightEvents.AirportList)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            with(rvAirportList) {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(CashplusItemDecoration(24))
            }
        }

        collectAirportList()
    }

    private fun collectAirportList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateLoading.collectLatest { binding.progressBar.isVisible = it }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.isNotEmpty()) {
                                val toast =
                                    Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvent(FlightEvents.RemoveHeadMessage)
                            }
                        }
                    }
                }
                launch {
                    viewModel.airports.collectLatest {
                        if (it?.rc == "30") {
                            binding.rvAirportList.isVisible = false
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        } else if (it?.rc == "00") {
                            with(binding) {
                                rvAirportList.adapter =
                                    it.data?.let { airports ->
                                        FlightRouteAdapter(airports, isDestination) { airport ->
                                            if (isDestination) viewModel.onTriggerEvent(
                                                FlightEvents.SetDestination(
                                                    FlightRoute(airport.code, airport.city, airport.airport)
                                                )
                                            )
                                            else viewModel.onTriggerEvent(
                                                FlightEvents.SetDeparture(
                                                    FlightRoute(airport.code, airport.city, airport.airport)
                                                )
                                            )
                                            rvAirportList.isVisible = false
                                            edtInputAirportName.text?.clear()
                                            this@FlightRouteFragment.dismiss()
                                        }
                                    }
                                rvAirportList.isVisible = true
                            }
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

    companion object {
        private const val IS_DESTINATION_KEY = "is_destination_key"

        @JvmStatic
        fun newInstance(isDestination: Boolean = false) =
            FlightRouteFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_DESTINATION_KEY, isDestination)
                }
            }
    }
}