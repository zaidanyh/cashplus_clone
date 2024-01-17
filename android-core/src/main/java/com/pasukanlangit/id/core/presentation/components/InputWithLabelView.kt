package com.pasukanlangit.id.core.presentation.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.pasukanlangit.id.core.R
import com.pasukanlangit.id.core.databinding.InputWithLabelBinding
import com.pasukanlangit.id.core.extensions.onDone
import com.pasukanlangit.id.core.utils.DrawablePosition

@SuppressLint("CustomViewStyleable")
class InputWithLabelView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    private var drawableEnd: Drawable? = null
    private var isInputOptional: Boolean = false
    private val binding = InputWithLabelBinding.inflate(LayoutInflater.from(context), this)
    val information = binding.tvNote
    private var label: String? = ""

    init {
        init()
    }

    fun getValue(): String {
        val value =
            if(binding.edtInput.isVisible){
                binding.edtInput.text.toString().trim()
            }else{
                binding.edtInputArea.text.toString().trim()
            }

        if(value.isEmpty() && !isInputOptional){
            throw Exception("input $label tidak boleh kosong")
        }else{
            return value
        }
    }

    fun observeInputOnChange(func: (String) -> Unit){
        binding.edtInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                func(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    fun setInputType(inputType: Int){
        binding.edtInput.inputType = inputType
    }

    fun setLabel(value: String){
        label = value
        binding.tvLabel.text = value
    }

    fun setPlaceHolder(value: String?){
        binding.edtInputArea.hint = value
        binding.edtInput.hint = value
    }

    fun setValue(value: String?){
        if(binding.edtInput.isVisible){
            binding.edtInput.setText(value)
        }else{
            binding.edtInputArea.setText(value)
        }
    }

    fun onDoneIME(action: () -> Unit){
        binding.edtInput.onDone { action() }
        binding.edtInputArea.onDone { action() }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setOnDrawableEndClicked(func: () -> Unit){
        if(drawableEnd == null) return
        with(binding.edtInput){
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (right - compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                        func()
                    }
                }
                false
            }
        }
    }

    private fun init() {
        orientation = VERTICAL

        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.InputWithLabelView)

        try {
            label = typeArrayStyle.getString(R.styleable.InputWithLabelView_label)
            val information = typeArrayStyle.getString(R.styleable.InputWithLabelView_information)
            val placeholderText = typeArrayStyle.getString(R.styleable.InputWithLabelView_placeholderText)
            val isTextAreaMode = typeArrayStyle.getBoolean(R.styleable.InputWithLabelView_textAreaMode, false)
            drawableEnd = typeArrayStyle.getDrawable(R.styleable.InputWithLabelView_android_drawableEnd)
            isInputOptional = typeArrayStyle.getBoolean(R.styleable.InputWithLabelView_isInputOptional, false)
            val isLabelOptionalShow = typeArrayStyle.getBoolean(R.styleable.InputWithLabelView_showOptionalLabel, true) && isInputOptional
            val isInputFocusable = typeArrayStyle.getBoolean(R.styleable.InputWithLabelView_isInputFocusable, true)
            val inputType: Int = typeArrayStyle.getInt(R.styleable.InputWithLabelView_android_inputType, EditorInfo.TYPE_NULL)
            val imeOptions: Int = typeArrayStyle.getInt(R.styleable.InputWithLabelView_android_imeOptions, EditorInfo.IME_ACTION_NEXT)

            with(binding){
                tvNote.isVisible = information != null
                labelOptional.isVisible = isLabelOptionalShow
                tvLabel.text = label
                tvNote.text = information
                setPlaceHolder(placeholderText)
                edtInput.isVisible = !isTextAreaMode
                edtInputArea.isVisible = isTextAreaMode
                edtInput.imeOptions = imeOptions
                edtInputArea.imeOptions = imeOptions
                if(inputType != EditorInfo.TYPE_NULL) {
                    edtInput.inputType = inputType
                    edtInputArea.inputType = inputType
//                    edtInputArea.hint = placeholderText
                }

                edtInput.isFocusable = isInputFocusable
                edtInput.isFocusableInTouchMode = isInputFocusable
                edtInput.setCompoundDrawablesWithIntrinsicBounds(null,null,drawableEnd, null)
            }

        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            typeArrayStyle.recycle()
        }
    }
}