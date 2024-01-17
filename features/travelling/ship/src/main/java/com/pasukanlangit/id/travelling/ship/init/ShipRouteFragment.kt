package com.pasukanlangit.id.travelling.ship.init

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
import com.pasukanlangit.id.travelling.ship.R
import com.pasukanlangit.id.travelling.ship.databinding.FragmentShipRouteBinding
import com.pasukanlangit.id.travelling.ship.temp.ShipRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ShipRouteFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentShipRouteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InitialShipViewModel by sharedViewModel()

    private var isDestination: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShipRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDestination = arguments?.getBoolean(IS_DESTINATION_KEY) ?: false
        with(binding) {
            rvHarborList.isVisible = false
            tvTitle.text = if (isDestination) getString(R.string.harbor_arrival)
            else getString(R.string.harbor_departure)
            edtInputHarborName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?,start: Int,count: Int,after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().length > 2) {
                        viewModel.searchInput.value = s.toString()
                        viewModel.onTriggerEvents(ShipEvents.HarborsShip)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            with(rvHarborList) {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(CashplusItemDecoration(24))
            }
        }
        collectHarborList()
    }

    private fun collectHarborList() {
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
                                viewModel.onTriggerEvents(ShipEvents.RemoveHeadMessage)
                            }
                        }
                    }
                }
                launch {
                    viewModel.harbors.collectLatest {
                        if (it?.rc == "30") {
                            binding.rvHarborList.isVisible = false
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        } else if (it?.rc == "00") {
                            with(binding) {
                                rvHarborList.adapter =
                                    it.data?.let { harbors ->
                                        ShipRouteAdapter(harbors) { harbor ->
                                            if (isDestination) viewModel.onTriggerEvents(
                                                ShipEvents.SetDestination(
                                                    ShipRoute(harbor.code, harbor.name)
                                                )
                                            )
                                            else viewModel.onTriggerEvents(
                                                ShipEvents.SetDeparture(
                                                    ShipRoute(harbor.code, harbor.name)
                                                )
                                            )
                                            rvHarborList.isVisible = false
                                            edtInputHarborName.text?.clear()
                                            this@ShipRouteFragment.dismiss()
                                        }
                                    }
                                rvHarborList.isVisible = true
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
            ShipRouteFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_DESTINATION_KEY, isDestination)
                }
            }
    }
}