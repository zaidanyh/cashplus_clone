package com.pasukanlangit.id.travelling.ship.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.travelling.ship.R
import com.pasukanlangit.id.travelling.ship.databinding.FragmentShipPassengerBinding
import com.pasukanlangit.id.travelling.ship.temp.ShipPassengers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ShipPassengerFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentShipPassengerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InitialShipViewModel by sharedViewModel()

    private var male: Int = 1
    private var female: Int = 0
    private var infant: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShipPassengerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnMaleIncrease.setOnClickListener { increases("male") }
            btnFemaleIncrease.setOnClickListener { increases("female") }
            btnIncreaseInfant.setOnClickListener { increases("infant") }

            btnMaleDecrease.setOnClickListener { decreases("male") }
            btnFemaleDecrease.setOnClickListener { decreases("female") }
            btnDecreaseInfant.setOnClickListener { decreases("infant") }
            btnConfirm.setOnClickListener {
                viewModel.onTriggerEvents(
                    ShipEvents.SetPassengers(
                        ShipPassengers(
                            male = this@ShipPassengerFragment.male,
                            female = this@ShipPassengerFragment.female,
                            infant = this@ShipPassengerFragment.infant
                        )
                    )
                )
                this@ShipPassengerFragment.dismiss()
            }
        }
        collectDataPassengers()
    }

    private fun increases(type: String) {
        when(type) {
            "male" -> {
                male += 1
                binding.tvMaleTotal.text = male.toString()
                binding.btnMaleDecrease.isVisible = true
            }
            "female" -> {
                female += 1
                binding.tvFemaleTotal.text = female.toString()
                binding.btnFemaleDecrease.isVisible = true
            }
            "infant" -> {
                infant += 1
                binding.tvInfantTotal.text = infant.toString()
                binding.btnDecreaseInfant.isVisible = true
            }
        }
    }

    private fun decreases(type: String) {
        when(type) {
            "male" -> {
                male -= 1
                if (male <= 1) binding.btnMaleDecrease.isVisible = false
                binding.tvMaleTotal.text = male.toString()
            }
            "female" -> {
                female -= 1
                if (female <= 0) binding.btnFemaleDecrease.isVisible = false
                binding.tvFemaleTotal.text = female.toString()
            }
            "infant" -> {
                infant -= 1
                if (infant <= 0) binding.btnDecreaseInfant.isVisible = false
                binding.tvInfantTotal.text = infant.toString()
            }
        }
    }

    private fun collectDataPassengers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.passengers.collectLatest {
                        with(binding) {
                            male = it?.male ?: 1
                            female = it?.female ?: 0
                            infant = it?.infant ?: 0
                            tvMaleTotal.text = (it?.male ?: 1).toString()
                            tvFemaleTotal.text = (it?.female ?: 0).toString()
                            tvInfantTotal.text = (it?.infant ?: 0).toString()
                            btnMaleDecrease.isVisible = male > 1
                            btnFemaleDecrease.isVisible = female > 0
                            btnDecreaseInfant.isVisible = infant > 0
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