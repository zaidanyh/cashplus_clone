package com.pasukanlangit.id.core.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior


class CollapseLayoutBehavior<V : ViewGroup>(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<V>(context, attrs) {
    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: V,
        dependency: View
    ): Boolean {
        if (isBottomSheet(dependency)) {
            val behavior =
                (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior as BottomSheetBehavior<*>?
            val peekHeight = behavior!!.peekHeight
            // The default peek height is -1, which
            // gets resolved to a 16:9 ratio with the parent
            val actualPeek =
                if (peekHeight >= 0) peekHeight else (parent.height * 1.0 / 16.0 * 9.0).toInt()
            if (dependency.top >= actualPeek) {
                // Only perform translations when the
                // view is between "hidden" and "collapsed" states
                val dy: Int = dependency.top - parent.height
                child.translationY = (dy / 2).toFloat()
//                ViewCompat.setTranslationY(child, (dy / 2).toFloat())
                return true
            }
        }
        return false
    }

    companion object {
        private fun isBottomSheet(@NonNull view: View): Boolean {
            val lp: ViewGroup.LayoutParams = view.layoutParams
            return if (lp is CoordinatorLayout.LayoutParams) {
                lp.behavior is BottomSheetBehavior<*>
            } else false
        }
    }
}