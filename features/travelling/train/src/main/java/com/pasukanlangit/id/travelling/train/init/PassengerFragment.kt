package com.pasukanlangit.id.travelling.train.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.travelling.train.R
import com.pasukanlangit.id.travelling.train.databinding.FragmentPassengerBinding
import com.pasukanlangit.id.travelling.train.temp.Passengers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PassengerFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPassengerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InitialTrainViewModel by sharedViewModel()

    private var adult: Int = 1
    private var infant: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPassengerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnIncrease.setOnClickListener { increases("adult") }
            btnIncreaseInfant.setOnClickListener { increases("infant") }

            btnDecrease.setOnClickListener { decreases("adult") }
            btnDecreaseInfant.setOnClickListener { decreases("infant") }
            btnConfirm.setOnClickListener {
                viewModel.onTriggerEvents(
                    TrainEvents.SetPassengers(
                        Passengers(adult = this@PassengerFragment.adult, infant = this@PassengerFragment.infant)
                    )
                )
                this@PassengerFragment.dismiss()
            }
        }
        collectDataPassengers()
    }

    private fun increases(type: String) {
        when(type) {
            "adult" -> {
                adult += 1
                binding.tvAdultTotal.text = adult.toString()
                binding.btnDecrease.isVisible = true
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
            "adult" -> {
                adult -= 1
                if (adult <= 1) binding.btnDecrease.isVisible = false
                binding.tvAdultTotal.text = adult.toString()
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
                            adult = it?.adult ?: 1
                            infant = it?.infant ?: 0
                            tvAdultTotal.text = (it?.adult ?: 1).toString()
                            tvInfantTotal.text = (it?.infant ?: 0).toString()
                            btnDecrease.isVisible = adult > 1
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