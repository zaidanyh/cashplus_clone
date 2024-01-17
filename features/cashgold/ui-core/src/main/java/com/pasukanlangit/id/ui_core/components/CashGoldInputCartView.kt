package com.pasukanlangit.id.ui_core.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.ui_core.databinding.CashgoldInputCartBinding

@SuppressLint("CustomViewStyleable")
class CashGoldInputCartView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    private val binding = CashgoldInputCartBinding.inflate(LayoutInflater.from(context), this)
    private var stok = Integer.MAX_VALUE
    private var currentQty = 1

    private var onQtyReachMinimum: (() -> Unit)? = null
    private var onQtyReachMaximum: (() -> Unit)? = null
    private var onQtyUpdate: (() -> Unit)? = null


    init {
        init()
    }

    fun setMaxQty(value: Int){
        stok = value
    }

    fun setCurrentQty(value: Int){
        currentQty = value
        binding.edtQty.setText(currentQty.toString())
    }

    fun getQty() = this.currentQty

    fun setEventReachMin(func: () -> Unit){
        onQtyReachMinimum = func
    }

    fun setEventReachMax(func: () -> Unit){
        onQtyReachMaximum = func
    }

    fun setOnQtyUpdate(func: () -> Unit){
        onQtyUpdate = func
    }

    private fun init() {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        binding.btnPlus.setOnClickListener {
            if (currentQty + 1 <= stok) {
                currentQty++
                binding.edtQty.setText(currentQty.toString())

                onQtyUpdate?.invoke()
            }else{
                onQtyReachMaximum?.invoke()
            }
        }

        binding.btnMinus.setOnClickListener {
            if (currentQty - 1 >= 1) {
                currentQty--
                binding.edtQty.setText(currentQty.toString())

                onQtyUpdate?.invoke()
            }else{
                KeyboardUtil.closeKeyboardDialog(context, binding.edtQty)
                onQtyReachMinimum?.invoke()
            }
        }

        binding.edtQty.setOnEditorActionListener { _, actionId, _ ->
            var inputQty = binding.edtQty.text.toString().toInt()
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputQty > stok) {
                    inputQty = stok
                    onQtyReachMaximum?.invoke()
                }
                if (inputQty < 1) {
                    inputQty = 0
                    KeyboardUtil.closeKeyboardDialog(context, binding.edtQty)
                    onQtyReachMaximum?.invoke()
                }

                currentQty = inputQty
                onQtyUpdate?.invoke()
            }
            false
        }

    }
}