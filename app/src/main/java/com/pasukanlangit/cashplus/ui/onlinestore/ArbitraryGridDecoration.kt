package com.pasukanlangit.cashplus.ui.onlinestore

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.ViewCompat

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * source: https://stackoverflow.com/questions/45660968/recyclerview-itemdecoration-spacing-and-span
 * custom width item decoration
 * */
class ArbitraryGridDecoration(val space: Int, private val NUMBER_OF_COLUMNS: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        addSpaceToView(outRect, parent.getChildAdapterPosition(view), parent)
    }
//    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
//        super.getItemOffsets(outRect, view, parent, state)
//
//        addSpaceToView(outRect, parent?.getChildAdapterPosition(view), parent)
//    }

    private fun addSpaceToView(outRect: Rect?, position: Int?, parent: RecyclerView?) {
        if (position == null || parent == null)
            return

        val grid = parent.layoutManager as GridLayoutManager
        val spanSize = grid.spanSizeLookup.getSpanSize(position)

        if (spanSize == NUMBER_OF_COLUMNS) {
            outRect?.right = space
        } else {
            var allSpanSize = 0
            for (i: Int in IntRange(0, position)) {
                allSpanSize += grid.spanSizeLookup.getSpanSize(i)
            }
            val currentModuloResult = allSpanSize % NUMBER_OF_COLUMNS
            if (currentModuloResult == 0) {
                outRect?.right = space
            }
        }
        outRect?.left = space
        outRect?.top = space
    }
}

internal class GridSpanDecoration(private val padding: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val gridLayoutManager = parent.layoutManager as GridLayoutManager?
        val spanCount = gridLayoutManager!!.spanCount
        val params = view.layoutParams as GridLayoutManager.LayoutParams
        val spanIndex = params.spanIndex
        val spanSize = params.spanSize

        // If it is in column 0 you apply the full offset on the start side, else only half
        if (spanIndex == 0) {
            outRect.left = padding
        } else {
            outRect.left = padding / 2
        }

        // If spanIndex + spanSize equals spanCount (it occupies the last column) you apply the full offset on the end, else only half.
        if (spanIndex + spanSize == spanCount) {
            outRect.right = padding
        } else {
            outRect.right = padding / 2
        }

        // just add some vertical padding as well
        outRect.top = padding / 2
        outRect.bottom = padding / 2
        if (isLayoutRTL(parent)) {
            val tmp = outRect.left
            outRect.left = outRect.right
            outRect.right = tmp
        }
    }

    companion object {
        @SuppressLint("NewApi", "WrongConstant")
        private fun isLayoutRTL(parent: RecyclerView): Boolean {
            return parent.layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL
        }
    }
}