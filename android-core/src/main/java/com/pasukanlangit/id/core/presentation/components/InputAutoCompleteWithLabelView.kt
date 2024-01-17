package com.pasukanlangit.id.core.presentation.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import com.pasukanlangit.id.core.R
import com.pasukanlangit.id.core.databinding.InputAutocompleteWithLabelBinding
import com.pasukanlangit.id.core.utils.DrawablePosition

@SuppressLint("CustomViewStyleable")
class InputAutoCompleteWithLabelView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    private var isInputOptional: Boolean = false
    private val binding = InputAutocompleteWithLabelBinding.inflate(LayoutInflater.from(context), this)
    private var values = listOf<String>()
    private var label: String? = ""

    init {
        init()
    }

    fun setText(value: String?){
        binding.autocomplete.setText(value)
    }
    fun setOnAutoCompleteClick(func: (String) -> Unit){
        binding.autocomplete.setOnItemClickListener { _, _, _, _ ->
            try {
                func(getSelectedValue())
            }catch (e: Exception){
                Toast.makeText(context, e.message ?: "Unknown Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun clearValue(){
        binding.autocomplete.text.clear()
        val adapter = ArrayAdapter(
            context,
            R.layout.item_spinner_small, listOf<String>()
        )
        binding.autocomplete.setAdapter(adapter)
    }

    fun setAutocompleteItems(values: List<String>){
        this.values = values
        val adapter = ArrayAdapter(
            context,
            R.layout.item_spinner_small, values
        )
        binding.autocomplete.setAdapter(adapter)
    }

    fun getSelectedValue(): String {
        val inputValue = binding.autocomplete.text.trim().toString()
        if(inputValue.isEmpty()){
            if(!isInputOptional) {
                throw Exception("Input $label tidak boleh kosong")
            }else{
                return ""
            }
        }
        if(values.isEmpty()) return inputValue
        val selectedValue = values.singleOrNull { it == inputValue }

        if(selectedValue != null){
            return selectedValue
        }else{
            throw Exception("Input $label tidak valid, pilih sesuai list yang tersedia")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        orientation = VERTICAL

        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.InputAutoCompleteWithLabelView)
        try {
            label = typeArrayStyle.getString(R.styleable.InputAutoCompleteWithLabelView_label)
            isInputOptional = typeArrayStyle.getBoolean(R.styleable.InputAutoCompleteWithLabelView_isInputOptional, false)
            val placeholderText = typeArrayStyle.getString(R.styleable.InputAutoCompleteWithLabelView_placeholderText)

            with(binding){
                autocomplete.hint = placeholderText
                tvLabel.text = label

                autocomplete.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (event.rawX >= (autocomplete.right - autocomplete.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                            autocomplete.showDropDown()
                            return@setOnTouchListener true
                        }
                    }
                    false
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            typeArrayStyle.recycle()
        }
    }
}