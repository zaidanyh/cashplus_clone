package com.pasukanlangit.id.core.utils

import android.content.res.Resources
import androidx.annotation.DimenRes

fun Resources.getRawDimensionInDp(@DimenRes dimenResId: Int): Int {
    return (getDimension(dimenResId) / displayMetrics.density).toInt()
}