package com.pasukanlangit.id.travelling.hotel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.travelling.hotel.databinding.FragmentRoomVisitorBottomSheetBinding
import com.pasukanlangit.id.travelling.hotel.temp.RoomVisitor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RoomVisitorBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentRoomVisitorBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InitialHotelViewModel by sharedViewModel()

    private var room: Int = 1
    private var visitor: Int = 1
    private var child: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomVisitorBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnIncrease.setOnClickListener { increases("room") }
            btnIncreaseVisitor.setOnClickListener { increases("visitor") }
            btnIncreaseChild.setOnClickListener { increases("child") }
            btnDecrease.setOnClickListener { decreases("room") }
            btnDecreaseVisitor.setOnClickListener { decreases("visitor") }
            btnDecreaseChild.setOnClickListener { decreases("child") }
            btnConfirm.setOnClickListener {
                viewModel.onTriggerEvent(
                    HotelEvents.SetRoomVisitor(
                        RoomVisitor(room, visitor, child)
                    )
                )
                this@RoomVisitorBottomSheet.dismiss()
            }
        }
        collectDataRoomVisitor()
    }

    private fun increases(type: String) {
        when(type) {
            "room" -> {
                room += 1
                binding.tvRoomTotal.text = room.toString()
                binding.btnDecrease.isVisible = true
            }
            "visitor" -> {
                visitor += 1
                binding.tvVisitorTotal.text = visitor.toString()
                binding.btnDecreaseVisitor.isVisible = true
            }
            "child" -> {
                child += 1
                binding.tvChildTotal.text = child.toString()
                binding.btnDecreaseChild.isVisible = true
            }
        }
    }

    private fun decreases(type: String) {
        when(type) {
            "room" -> {
                room -= 1
                if (room <= 1) binding.btnDecrease.isVisible = false
                binding.tvRoomTotal.text = room.toString()
            }
            "visitor" -> {
                visitor -= 1
                if (visitor <= 1) binding.btnDecreaseVisitor.isVisible = false
                binding.tvVisitorTotal.text = visitor.toString()
            }
            "child" -> {
                child -= 1
                if (child <= 0) binding.btnDecreaseChild.isVisible = false
                binding.tvChildTotal.text = child.toString()
            }
        }
    }

    private fun collectDataRoomVisitor() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.roomVisitor.collectLatest {
                        room = it?.room ?: 1
                        visitor = it?.visitor ?: 1
                        child = it?.child ?: 0
                        with(binding) {
                            tvRoomTotal.text = (it?.room ?: 1).toString()
                            tvVisitorTotal.text = (it?.visitor ?: 1).toString()
                            tvChildTotal.text = (it?.child ?: 0).toString()
                            btnDecrease.isVisible = room > 1
                            btnDecreaseVisitor.isVisible = visitor > 1
                            btnDecreaseChild.isVisible = child > 0
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