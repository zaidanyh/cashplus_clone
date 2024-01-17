package com.pasukanlangit.id.core.extensions

import android.text.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.pasukanlangit.id.core.utils.getDecimalFormattedString


fun EditText.onDone(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
//            return@setOnEditorActionListener true
        }
        false
    }
}

fun EditText.setOnlyNumberAllowed() {
    this.inputType =  InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_CLASS_NUMBER
    val regex = Regex("^[-]*$")
    val filter = InputFilter { source, _, _, _, _, _ ->
        return@InputFilter when {
            source?.matches(regex) == true -> ""
            else -> null
        }
    }
    this.filters = arrayOf(filter)
}

fun EditText.setIDRCurrencyInput() {
    this.setOnlyNumberAllowed()
    this.setRupiahListener()
}

fun EditText.setRupiahListener() {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }
        override fun afterTextChanged(s: Editable) {
            try {
                this@setRupiahListener.removeTextChangedListener(this)
                val value = this@setRupiahListener.text.toString()
                if (value.isNotEmpty()) {
                    if (value.startsWith("0")) {
                        this@setRupiahListener.setText("")
                    }
                    val str: String = this@setRupiahListener.text.toString().replace(",", "")
                    if (value.isNotEmpty())  this@setRupiahListener.setText(getDecimalFormattedString(str))
                    this@setRupiahListener.setSelection(this@setRupiahListener.text.toString().length)
                }
                this@setRupiahListener.addTextChangedListener(this)
                return
            } catch (ex: Exception) {
                ex.printStackTrace()
                this@setRupiahListener.addTextChangedListener(this)
            }
        }
    })
}

class InputFilterMinMax : InputFilter {
    private var min: Int
    private var max: Int

    constructor(min: Int, max: Int) {
        this.min = min
        this.max = max
    }

    constructor(min: String, max: String) {
        this.min = min.toInt()
        this.max = max.toInt()
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toInt()
            if (isInRange(min, max, input)) return null
        } catch (_: NumberFormatException) {
        }
        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}