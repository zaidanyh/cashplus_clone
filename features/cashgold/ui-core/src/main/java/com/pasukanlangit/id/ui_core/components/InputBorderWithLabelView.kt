package com.pasukanlangit.id.ui_core.components

//import android.annotation.SuppressLint
//import android.content.Context
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.view.inputmethod.EditorInfo
//import android.widget.LinearLayout
//import com.pasukanlangit.id.ui_core.R
//import com.pasukanlangit.id.ui_core.databinding.InputBorderWithLabelBinding
//
//
//@SuppressLint("CustomViewStyleable")
//class InputBorderWithLabelView @JvmOverloads constructor(
//    context: Context,
//    private val attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//): LinearLayout(context, attrs, defStyleAttr) {
//
//    private val binding = InputBorderWithLabelBinding.inflate(LayoutInflater.from(context), this)
//    private var label: String? = ""
//
//    init {
//        init()
//    }
//
//    /**
//     * return trimmed string value from input (remove white space). throw exception if input empty
//     * */
//    fun getInputText(): String{
//        val inputText = binding.edtInput.text.toString().trim()
//        if(inputText.isEmpty()){
//            throw Exception("Input $label wajib diisi")
//        }
//        return inputText
//    }
//
//    private fun init() {
//        orientation = VERTICAL
//
//        val typeArrayStyle = context.obtainStyledAttributes(attrs, R.styleable.InputWithLabelView)
//
//        try {
//            label = typeArrayStyle.getString(R.styleable.InputWithLabelView_label)
//            val placeholderText = typeArrayStyle.getString(R.styleable.InputWithLabelView_placeholderText)
//            val inputType: Int = typeArrayStyle.getInt(R.styleable.InputWithLabelView_android_inputType, EditorInfo.TYPE_NULL)
//
//            with(binding){
//                tvLabel.text = label
//                edtInput.hint = placeholderText
//                if(inputType != EditorInfo.TYPE_NULL) edtInput.inputType = inputType
//            }
//
//        }catch (e: Exception){
//            e.printStackTrace()
//        }finally {
//            typeArrayStyle.recycle()
//        }
//    }
//}