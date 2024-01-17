package com.pasukanlangit.id.core.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtil {

    fun hideSoftKeyboard(context: Context) {
        val inputMethodManager: InputMethodManager? = context.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as? InputMethodManager

        inputMethodManager?.hideSoftInputFromWindow(
            (context as Activity).currentFocus?.windowToken, 0
        )
    }

    fun openSoftKeyboard(context: Context, inputView: View) {
        SoftInputService(context, inputView).show()
    }

//    fun openKeyboardDialog(view: View) {
//        view.post {
//            view.requestFocus()
//            val imm: InputMethodManager = view.context
//                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
//        }
//    }

    fun closeKeyboardDialog(context: Context, theEditText: View) {
        theEditText.clearFocus()
        SoftInputService.hide(context, theEditText.windowToken)
//        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    class SoftInputService(private val context: Context?,private val targetView: View?) :
        Runnable {

        private val handler: Handler = Handler(Looper.getMainLooper())

        override fun run() {
            if (context == null || targetView == null) {
                return
            }
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (!targetView.isFocusable || !targetView.isFocusableInTouchMode) {
                Log.d(
                    TAG,
                    "focusable = " + targetView.isFocusable + ", focusableInTouchMode = " + targetView.isFocusableInTouchMode
                )
                return
            } else if (!targetView.requestFocus()) {
                Log.d(TAG, "Cannot focus on view")
                post()
            } else if (!imm.showSoftInput(targetView, InputMethodManager.SHOW_IMPLICIT)) {
                Log.d(TAG, "Unable to show keyboard")
                post()
            }
        }

        fun show() {
            handler.post(this)
        }

        private fun post() {
            handler.postDelayed(this, INTERVAL_MS)
        }

        companion object {
            private val TAG = SoftInputService::class.java.simpleName
            private const val INTERVAL_MS = 100L
            fun hide(context: Context, windowToekn: IBinder?) {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(windowToekn, 0)
            }
        }
    }
}