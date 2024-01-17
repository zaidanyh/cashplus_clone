package com.pasukanlangit.id.core.presentation.components

import android.content.Context
import android.graphics.ColorFilter
import android.util.AttributeSet
import androidx.annotation.Nullable
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.textfield.TextInputLayout

class CustomTextInputLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {

    override fun setError(error: CharSequence?) {
        val defaultColorFilter = getBackgroundDefaultColorFilter()
        super.setError(error)
        //Reset EditText's background color to default.
        updateBackgroundColorFilter(defaultColorFilter)
    }

    override fun drawableStateChanged() {
        val defaultColorFilter = getBackgroundDefaultColorFilter()
        super.drawableStateChanged()
        //Reset EditText's background color to default.
        updateBackgroundColorFilter(defaultColorFilter)
    }

    private fun updateBackgroundColorFilter(colorFilter: ColorFilter?) {
        if (editText != null && editText!!.background != null) editText!!.background.colorFilter =
            colorFilter
    }

    @Nullable
    private fun getBackgroundDefaultColorFilter(): ColorFilter? {
        var defaultColorFilter: ColorFilter? = null
        if (editText != null && editText!!.background != null) defaultColorFilter =
            DrawableCompat.getColorFilter(
                editText!!.background
            )
        return defaultColorFilter
    }
}