package com.pasukanlangit.cashplus.ui.onlinestore

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.pasukanlangit.id.core.utils.DrawablePosition
import com.pasukanlangit.cashplus.databinding.FragmentProductSearchBinding
import com.pasukanlangit.cashplus.model.response.CategoryProductDataItem
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.AllProductEcommerceViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductSearchFragment : Fragment() {

    private var _binding: FragmentProductSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllProductEcommerceViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCategoriesProduct()

        observeCategoryProduct()

        with(binding){
            KeyboardUtil.openSoftKeyboard(requireContext(), edtSearch)

            edtSearch.setOnEditorActionListener { _, actionId, _ ->
                val searchText = edtSearch.text.toString()
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    sendResultSearchWithText(searchText)
                }
                false
            }

            edtSearch.setOnTouchListener { _, event ->
                if(event.action == MotionEvent.ACTION_UP) {
                    val searchText = edtSearch.text.toString()
                    if(event.rawX >= (edtSearch.right - edtSearch.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                        // your action here
                        sendResultSearchWithText(searchText)
                        return@setOnTouchListener true
                    }
                }
                false
            }
        }
    }

    private fun observeCategoryProduct() {
        viewModel.categories.observe(viewLifecycleOwner){
            with(binding){
                rvKategori.isVisible = it.status == Status.SUCCESS
                rvKategoriShimmer.isVisible = it.status == Status.LOADING
            }
            when (it.status) {
                Status.SUCCESS -> {
                    binding.rvKategoriShimmer.stopShimmer()
                    if (it.data != null) {
                        setUpRvCategory(it.data.categories)
                    }
                }
                Status.LOADING -> {
                    binding.rvKategoriShimmer.startShimmer()
                }
                Status.ERROR -> {
                    val menusAllFragment = ButtomSheetNotif(it.message, com.pasukanlangit.id.library_core.domain.model.NotifType.NOTIF_ERROR)
                    menusAllFragment.show(childFragmentManager, menusAllFragment.tag)

                    binding.rvKategoriShimmer.stopShimmer()
                }
            }
        }
    }



    private fun setUpRvCategory(categories: List<CategoryProductDataItem>) {
        val categoriesName = categories.map { it.name }
        val spanCount = 2
        val maxLengthTextSpan = 10

        val categoriesSorted = mutableListOf<String>()
        val categoriesLong = categoriesName.filter { it.length > maxLengthTextSpan }.toMutableList()
        val categoriesShort = categoriesName.filter { it.length <= maxLengthTextSpan }.toMutableList()

        var indexCategoryShort = 1
        categoriesShort.forEachIndexed { index, s ->
            if(categoriesShort.size % 2 == 1 && index == categoriesShort.size-1){
                return@forEachIndexed
            }
            categoriesSorted.add(s)
            indexCategoryShort++

            if(indexCategoryShort == 5){
                categoriesLong.firstOrNull()?.let { categoryLongFirst ->
                    categoriesSorted.add(categoryLongFirst)
                    categoriesLong.removeAt(0)
                }
                indexCategoryShort = 1
            }
        }

        categoriesLong.forEach { category ->
            categoriesSorted.add(category)
        }

        if(categoriesShort.size % 2 == 1 ){
            categoriesShort.lastOrNull()?.let { categoryLast ->
                categoriesSorted.add(categoryLast)
            }
        }


//
//        var index = 0
//        val categoriesStorted = mutableListOf<String>()
//        while(index < categoriesName.size){
//            val name = categoriesName[index]
//            if(index % 2 == 0 && index+1 != categoriesName.size){
//                if(name.length <= maxLengthTextSpan && categoriesName[index+1].length > maxLengthTextSpan){
//                    categoriesStorted.add(categoriesName[index+1])
//                }
//            }
//            categoriesStorted.add(name)
//            index++
//        }


//        categoriesName.forEachIndexed { index, s ->
//             if(index % 2 == 0 && index+1 != categoriesName.size){
//                 if(s.length <= maxLengthTextSpan && categoriesName[index+1].length > maxLengthTextSpan){
//                     Collections.swap(categoriesName, index, index+1)
//                 }
//             }
//        }
//        val categoriesLength = categoriesName.size
//        if(categoriesLength >= 2) {
//            if(categoriesName[categoriesLength - 2].length < maxLengthTextSpan){
//                Collections.swap(categoriesName, categoriesLength-2, categoriesLength-1)
//            }
//        }

        binding.rvKategori.apply {
            adapter = CategoryProductAdapter(categoriesSorted){ hint ->
                findNavController().navigate(ProductSearchFragmentDirections.actionProductSearchFragmentToAllProductFragment(
                    TypeSearchProduct.SEARCH_BY_CATEGORY, hint
                ))
            }
            val gridLayout = GridLayoutManager(requireContext(), spanCount)
            gridLayout.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    val lengthText = categoriesSorted[position].length
                    return if(lengthText > maxLengthTextSpan){
                        spanCount
                    }else {
                        1
                    }
                }

            }
            layoutManager = gridLayout
            if(itemDecorationCount < 1) addItemDecoration(ArbitraryGridDecoration(20, spanCount))
        }
    }

    private fun sendResultSearchWithText(text: String) {
        findNavController().navigate(ProductSearchFragmentDirections.actionProductSearchFragmentToAllProductFragment(
           if(text.isNotEmpty()) TypeSearchProduct.SEARCH_BY_KEYWORD else TypeSearchProduct.NONE, text
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}