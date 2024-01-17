package com.pasukanlangit.id.core.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CashplusItemDecoration @JvmOverloads constructor(
    private val space: Int = 0,
    private var displayedMode: Int = -1
): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val layoutManager = parent.layoutManager
        val itemCount = state.itemCount
        setSpacingForDirection(outRect, layoutManager, position, itemCount)
    }

    private fun setSpacingForDirection(
        outRect: Rect,
        layoutManager: RecyclerView.LayoutManager?,
        position: Int,
        itemCount: Int
    ) {
        if (displayedMode == -1) {
            displayedMode = resolveDisplayMode(layoutManager)
        }

        when(displayedMode) {
            HORIZONTAL -> with(outRect) {
                left = if (position == 0) space*4 else space
                right = if (position == itemCount -1) space*4 else space
                top = space
                bottom = space
            }
            GRID -> if (layoutManager is GridLayoutManager) {
                val spanSize = layoutManager.spanCount
                with(outRect) {
                    left = if (position % spanSize == 0) space else space/2
                    right = if (position % spanSize == spanSize -1) space else space/2
                    top = space/2
                    bottom = space/2
                }
            }
            VERTICAL -> with(outRect) {
                left = space
                right = space
                top = if (position == 0) space else 0
                bottom = space
            }
        }
    }

    private fun resolveDisplayMode(layoutManager: RecyclerView.LayoutManager?): Int {
        if (layoutManager is GridLayoutManager) return GRID
        if (layoutManager?.canScrollHorizontally() == true) return HORIZONTAL
        return VERTICAL
    }

    companion object {
        private const val HORIZONTAL = 0
        private const val VERTICAL = 1
        private const val GRID = 2
    }
}