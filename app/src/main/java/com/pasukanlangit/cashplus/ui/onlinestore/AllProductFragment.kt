package com.pasukanlangit.cashplus.ui.onlinestore

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.pasukanlangit.cashplus.databinding.FragmentAllProductBinding
import com.pasukanlangit.id.core.utils.GridSpacingItemDecoration
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.cashplus.view_model.AllProductEcommerceViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class TypeSearchProduct {
    SEARCH_BY_KEYWORD,
    SEARCH_BY_CATEGORY,
    NONE
}

class AllProductFragment : Fragment(), AllProductPagingAdapter.ProductStoreEvent {

    private var _binding: FragmentAllProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllProductEcommerceViewModel by viewModel()
    private val sharedPref : SharedPrefDataSource by inject()
    private val args: AllProductFragmentArgs by navArgs()
    private lateinit var mAdapter : AllProductPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAllProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecycler()

        with(binding){
            edtSearch.setOnClickListener {
                findNavController().navigate(AllProductFragmentDirections.actionAllProductFragmentToProductSearchFragment())
            }

            val token = sharedPref.getAccessToken()
            val uuid = sharedPref.getUUID()

            if (token != null && uuid != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        val typeSearch = args.typeSearch
                        val hint = args.textHint

                        binding.tvCategory.isVisible = typeSearch == TypeSearchProduct.SEARCH_BY_CATEGORY

                        when(typeSearch){
                            TypeSearchProduct.SEARCH_BY_KEYWORD -> {
                                binding.edtSearch.setText(hint)
                                viewModel.searchProductPagging(token,uuid, hint).collectLatest {
                                    if(::mAdapter.isInitialized) mAdapter.submitData(it)
                                }
                            }
                            TypeSearchProduct.SEARCH_BY_CATEGORY -> {
                                binding.tvCategory.text = hint
                                viewModel.getProductsPaging(token, uuid, hint).collectLatest {
                                    if(::mAdapter.isInitialized) mAdapter.submitData(it)
                                }
                            }
                            TypeSearchProduct.NONE -> {
                                viewModel.getProductsPaging(token, uuid, null).collectLatest {
                                    if(::mAdapter.isInitialized) mAdapter.submitData(it)
                                }
                            }
                        }
                    }
                }
            }

            //for first load
            mAdapter.addLoadStateListener { loadState ->
                if(loadState.refresh is LoadState.Loading){
                    rvProdukEcommerceAll.visibility = View.INVISIBLE
                    rvProdukEcommerceAllShimmer.visibility = View.VISIBLE
                    rvProdukEcommerceAllShimmer.startShimmer()
                }else if(loadState.refresh is LoadState.NotLoading){
                    rvProdukEcommerceAll.visibility = View.VISIBLE
                    rvProdukEcommerceAllShimmer.visibility = View.INVISIBLE
                    rvProdukEcommerceAllShimmer.stopShimmer()
                }
            }
        }
    }

    private fun setUpRecycler() {
        //Note: for centering footer in paging -> https://stackoverflow.com/questions/65291291/android-loadstateadapter-not-centered-inside-recyclerview-gridlayout
        mAdapter = AllProductPagingAdapter(this)
        binding.rvProdukEcommerceAll.apply {
            val gridLayoutManager = GridLayoutManager(requireContext(), 2)
            val headerAdapter = PagingLoadStateAdapter()
            val footerAdapter = PagingLoadStateAdapter()
            val concatAdapter = mAdapter.withLoadStateHeaderAndFooter(
                header = headerAdapter,
                footer = footerAdapter
            )

            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    return if (position == 0 && headerAdapter.itemCount > 0) {
                        // if it is the first position and we have a header,
                        2
                    } else if (position == concatAdapter.itemCount - 1 && footerAdapter.itemCount > 0){
                        // if it is the last position and we have a footer
                        2
                    } else {
                        1
                    }
                }
            }

            layoutManager = gridLayoutManager
            adapter = concatAdapter
            addItemDecoration(GridSpacingItemDecoration(8))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickRoot(productStoreDataItem: ProductStoreDataItem) {
        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
        intent.putExtra(ProductDetailActivity.KEY_IMG_PRODUCT, productStoreDataItem)
        startActivity(intent)
    }
}