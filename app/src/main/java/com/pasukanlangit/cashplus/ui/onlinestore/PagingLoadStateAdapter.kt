package com.pasukanlangit.cashplus.ui.onlinestore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemPagingLoadingBinding

class PagingLoadStateAdapter : LoadStateAdapter<PagingLoadStateAdapter.PagingViewHolder>(){

    class PagingViewHolder(val binding: ItemPagingLoadingBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            with(binding) {
                loadingShimmer.isVisible = loadState is LoadState.Loading
                loadingShimmer.startShimmer()
            }
        }
    }

    override fun onBindViewHolder(
        holder: PagingViewHolder,
        loadState: LoadState
    ) = holder.bind(loadState)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PagingViewHolder {
        return PagingViewHolder(ItemPagingLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
}