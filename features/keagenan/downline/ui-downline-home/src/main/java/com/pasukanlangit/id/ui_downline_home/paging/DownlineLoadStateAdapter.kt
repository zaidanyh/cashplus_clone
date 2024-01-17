package com.pasukanlangit.id.ui_downline_home.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.ui_downline_home.databinding.ItemDownlineLayoutShimmerBinding

class DownlineLoadStateAdapter: LoadStateAdapter<DownlineLoadStateAdapter.PagingViewHolder>() {

    inner class PagingViewHolder(val binding: ItemDownlineLayoutShimmerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            with(binding) {
                loadingShimmer.isVisible = loadState is LoadState.Loading
                loadingShimmer.startShimmer()
            }
        }
    }

    override fun onBindViewHolder(holder: PagingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): PagingViewHolder {
        return PagingViewHolder(ItemDownlineLayoutShimmerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
}