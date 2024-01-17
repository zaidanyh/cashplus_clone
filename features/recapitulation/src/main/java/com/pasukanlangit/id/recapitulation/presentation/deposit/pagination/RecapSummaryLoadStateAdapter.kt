package com.pasukanlangit.id.recapitulation.presentation.deposit.pagination

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.recapitulation.databinding.ItemRecapDepositLoadStateShimmerBinding

class RecapSummaryLoadStateAdapter: LoadStateAdapter<RecapSummaryLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(val binding: ItemRecapDepositLoadStateShimmerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            with(binding) {
                loadStateShimmer.isVisible = loadState is LoadState.Loading
                if (loadState is LoadState.Loading) {
                    loadStateShimmer.startShimmer()
                    return
                }
                loadStateShimmer.stopShimmer()
            }
        }
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(ItemRecapDepositLoadStateShimmerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
}