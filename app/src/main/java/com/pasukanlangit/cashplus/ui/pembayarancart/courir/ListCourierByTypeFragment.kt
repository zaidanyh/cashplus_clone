package com.pasukanlangit.cashplus.ui.pembayarancart.courir

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.FragmentListCourirBTypeBinding
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.cashplus.ui.pembayarancart.PembayaranCartViewModel
import com.pasukanlangit.cashplus.utils.Status
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ListCourierByTypeFragment : Fragment() {

    private var _binding: FragmentListCourirBTypeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PembayaranCartViewModel by sharedViewModel()
    private val sharedPref: SharedPrefDataSource by inject()
    private val args : ListCourierByTypeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListCourirBTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivCourierType.setImageResource(args.courierType.image)

        val token = sharedPref.getAccessToken()
        val uuid = sharedPref.getUUID()

        if(!token.isNullOrEmpty() && !uuid.isNullOrEmpty()){
            viewModel.getCourierPrice(args.courierType.value, uuid, token)
        }

        setUpRecyclerView()
        val parent: Fragment? = (parentFragment as NavHostFragment).parentFragment
        (parent as ChooseCourierFragment).setBtnBackIsActive(true)

        viewModel.courierPrice.observe(viewLifecycleOwner) {
            binding.loading.isVisible = it.status == Status.LOADING
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { courierData ->
                        courierData.getContentIfNotHandled()?.let { data ->
                            binding.rvCourier.isVisible = true
                            binding.rvCourier.adapter = CourierPriceListAdapter(
                                args.courierType.value.uppercase(),
                                data.data[0].costs
                            ) { courierChoosen ->
                                viewModel.setCourierChoosen(courierChoosen)
                                parent.dismiss()
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    val menusAllFragment = ButtomSheetNotif(
                        it.message,
                        NotifType.NOTIF_ERROR
                    )
                    menusAllFragment.show(childFragmentManager, menusAllFragment.tag)
                }
                else -> {}
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvCourier.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}