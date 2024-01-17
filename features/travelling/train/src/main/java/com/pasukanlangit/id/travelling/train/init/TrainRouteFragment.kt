package com.pasukanlangit.id.travelling.train.init

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
import com.pasukanlangit.id.travelling.train.R
import com.pasukanlangit.id.travelling.train.databinding.FragmentTrainRouteBinding
import com.pasukanlangit.id.travelling.train.temp.TrainRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TrainRouteFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTrainRouteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InitialTrainViewModel by sharedViewModel()

    private var isDestination: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDestination = arguments?.getBoolean(IS_DESTINATION_KEY) ?: false
        with(binding) {
            rvStationList.isVisible = false
            tvTitle.text = if (isDestination) getString(R.string.train_destination)
                else getString(R.string.train_departure)
            edtInputStationName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?,start: Int,count: Int,after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().length > 2) {
                        viewModel.searchInput.value = s.toString()
                        viewModel.onTriggerEvents(TrainEvents.StationsList)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            with(rvStationList) {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(CashplusItemDecoration(24))
            }
        }
        collectStationList()
    }

    private fun collectStationList() {
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
                                viewModel.onTriggerEvents(TrainEvents.RemoveHeadMessage)
                            }
                        }
                    }
                }
                launch {
                    viewModel.stations.collectLatest {
                        if (it?.rc == "30") {
                            binding.rvStationList.isVisible = false
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        } else if (it?.rc == "00") {
                            with(binding) {
                                rvStationList.adapter =
                                    it.data?.let { stations ->
                                        TrainRouteAdapter(stations) { station ->
                                            if (isDestination) viewModel.onTriggerEvents(
                                                TrainEvents.SetDestination(
                                                    TrainRoute(station.code, station.location)
                                                )
                                            )
                                            else viewModel.onTriggerEvents(
                                                TrainEvents.SetDeparture(
                                                    TrainRoute(station.code, station.location)
                                                )
                                            )
                                            rvStationList.isVisible = false
                                            edtInputStationName.text?.clear()
                                            this@TrainRouteFragment.dismiss()
                                        }
                                    }
                                rvStationList.isVisible = true
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
            TrainRouteFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_DESTINATION_KEY, isDestination)
                }
            }
    }
}