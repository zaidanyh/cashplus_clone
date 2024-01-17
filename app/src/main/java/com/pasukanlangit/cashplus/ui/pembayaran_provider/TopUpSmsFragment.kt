package com.pasukanlangit.cashplus.ui.pembayaran_provider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.adapter.DataProductAdapter
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.FragmentTopUpSmsBinding
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.TopupProviderViewModel
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TopUpSmsFragment : Fragment() {

    private var _binding: FragmentTopUpSmsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TopupProviderViewModel by sharedViewModel()
    private val sharedPrefDataSource: SharedPrefDataSource by inject()

    private lateinit var oprAdapter: ArrayAdapter<String>
    private var currentNumber: String = ""
    private val operatorNameList = arrayListOf<String>()
    private var onItemSelected = 0
    private var isGetProduct = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpSmsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uuid = sharedPrefDataSource.getUUID()
        val accessToken = sharedPrefDataSource.getAccessToken()

        with(binding) {
            rvSms.layoutManager = LinearLayoutManager(activity)
            rvSms.addItemDecoration(CashplusItemDecoration(24))

            viewModel.numberProvider.observe(viewLifecycleOwner) {
                val phoneNumber = it.first
                val state = it.second
                currentNumber = phoneNumber

                if (phoneNumber.length > 5) {
                    val productRequest = ProductRequest(
                        uuid = uuid ?: "", category = "#PAKETSMS",
                        kode_provider = "", is_faktur = "",
                        is_active = "1", opr_name = "",
                        prefix = if (phoneNumber.substring(0, 2) == "62")
                            phoneNumber.replaceRange(0, 2, "0")
                        else phoneNumber.substring(0, 6)
                    )
                    if (state) {
                        operatorNameList.clear()
                        operatorNameList.add(getString(R.string.all_category))
                        onItemSelected = 0
                        viewModel.getProductSms(productRequest, accessToken ?: "")
                    } else if (!isGetProduct) {
                        isGetProduct = true
                        operatorNameList.clear()
                        operatorNameList.add(getString(R.string.all_category))
                        onItemSelected = 0
                        viewModel.getProductSms(productRequest, accessToken ?: "")
                    }
                    return@observe
                }
                isGetProduct = false
                rvSms.isVisible = false
                (activity as TopUpProviderActivity).setOperatorLogo(null)
                if (operatorNameList.isNotEmpty()) {
                    operatorNameList.clear()
                    operatorNameList.add(getString(R.string.all_category))
                    onItemSelected = 0
                    oprListSpinner.setSelection(0)
                    oprListSpinner.isVisible = false
                }
            }

            oprListSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (++onItemSelected > 1) {
                        val oprNameSelected = oprListSpinner.selectedItem.toString()
                        val productRequest = ProductRequest(
                            uuid = uuid ?: "",
                            category = "#PAKETSMS",
                            kode_provider = "",
                            is_faktur = "",
                            is_active = "1",
                            opr_name = if (oprNameSelected == getString(R.string.all_category)) "" else oprNameSelected,
                            prefix = currentNumber
                        )
                        viewModel.getProductSms(productRequest, accessToken ?: "")
                        return
                    }
                    oprListSpinner.setSelection(0)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        observeProduct()
    }

    private fun observeProduct() {
        viewModel.productSms.observe(viewLifecycleOwner) {
            with(binding) {
                when (it.status) {
                    Status.SUCCESS -> {
                        rvSmsShimmer.isVisible = false
                        rvSmsShimmer.stopShimmer()

                        if (it.data != null) {
                            val distinctProduct = it.data.data?.distinctBy { data -> data.kode_provider }
                            if (!distinctProduct.isNullOrEmpty()) {
                                if (distinctProduct.size > 1) {
                                    val products = mutableListOf<ProductModel>()
                                    it.data.data.forEach { data ->
                                        if (distinctProduct.first().kode_provider == data.kode_provider)
                                            products.add(data)
                                    }
                                    bindToView(products.sortedBy { it.price.toInt() })
                                    return@observe
                                }
                            }
                            it.data.data?.sortedBy { sort -> sort.price.toInt() }
                                ?.let { bindToView(it) }
                        }

                    }
                    Status.LOADING -> {
                        rvSmsShimmer.visibility = View.VISIBLE
                        rvSmsShimmer.startShimmer()
                        rvSms.visibility = View.GONE
                        oprListSpinner.isVisible = false
                    }
                    Status.ERROR -> {
                        val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                        menusAllFragment.show(childFragmentManager, menusAllFragment.tag)

                        rvSmsShimmer.visibility = View.GONE
                        rvSmsShimmer.stopShimmer()
                        rvSms.visibility = View.VISIBLE
                        oprListSpinner.isVisible = true
                    }
                }
            }
        }
    }

    private fun bindToView(product: List<ProductModel>) {
        with(binding) {
            if (product.isEmpty()) {
                wrapperIsEmpty.isVisible = true
                rvSms.isVisible = false
                oprListSpinner.isVisible = false
                return
            }
            wrapperIsEmpty.isVisible = false
            rvSms.isVisible = true
            oprListSpinner.isVisible = true
            if (operatorNameList.size == 1) {
                val oprList = product.distinctBy { data -> data.opr_name }
                oprList.forEach { opr ->
                    operatorNameList.add(opr.opr_name)
                }
                oprAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner_small_selected, operatorNameList)
                oprAdapter.setDropDownViewResource(R.layout.item_spinner_small)
                oprListSpinner.adapter = oprAdapter
            }
            val dataFirst = product.getOrNull(0)
            if (dataFirst != null) (activity as TopUpProviderActivity).setOperatorLogo(
                dataFirst.img_url_2.ifEmpty { dataFirst.img_url }
            )

            rvSms.adapter = DataProductAdapter(product) { paketSms ->
                DetailTopUpFragment.newInstance(
                    productModel = paketSms,
                    phoneNumber = currentNumber
                ).show(requireActivity().supportFragmentManager, "Detail Top Up SMS")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}