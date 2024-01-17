package com.pasukanlangit.id.travelling.ship.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.travelling.ship.R
import com.pasukanlangit.id.travelling.ship.databinding.FragmentMoreInfoScheduleBinding
import com.pasukanlangit.id.travelling.ship.temp.ShipSchedules

class MoreInfoScheduleFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentMoreInfoScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreInfoScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val schedule = arguments?.parcelable<ShipSchedules>(SHIP_SCHEDULE_KEY)
        with(binding) {
            tvPrice.text = getNumberRupiah(schedule?.basicFare)
            tvServiceFee.text = getNumberRupiah(schedule?.admin)
            tvTotalFee.text = getNumberRupiah(schedule?.price)
            tvSeatMaleIsAvailable.isEnabled = schedule?.maleSeat?.contains(getString(R.string.available), ignoreCase = true) == true
            tvSeatMaleIsAvailable.text = schedule?.maleSeat
            tvSeatFemaleIsAvailable.isEnabled = schedule?.femaleSeat?.contains(getString(R.string.available), ignoreCase = true) == true
            tvSeatFemaleIsAvailable.text = schedule?.femaleSeat
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SHIP_SCHEDULE_KEY = "ship_schedule_key"

        @JvmStatic
        fun newInstance(schedule: ShipSchedules) =
            MoreInfoScheduleFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SHIP_SCHEDULE_KEY, schedule)
                }
            }
    }
}