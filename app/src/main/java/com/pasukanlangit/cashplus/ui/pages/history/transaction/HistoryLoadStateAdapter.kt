package com.pasukanlangit.cashplus.ui.pages.history.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.databinding.ItemHistoryTransactionShimmerBinding

class HistoryLoadStateAdapter: LoadStateAdapter<HistoryLoadStateAdapter.HistoryLoadStateViewHolder>() {

    inner class HistoryLoadStateViewHolder(val binding: ItemHistoryTransactionShimmerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            with(binding) {
                shimmerLoading.isVisible = loadState is LoadState.Loading
                shimmerLoading.startShimmer()
            }
        }
    }

    override fun onBindViewHolder(holder: HistoryLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): HistoryLoadStateViewHolder {
        return HistoryLoadStateViewHolder(ItemHistoryTransactionShimmerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }
}