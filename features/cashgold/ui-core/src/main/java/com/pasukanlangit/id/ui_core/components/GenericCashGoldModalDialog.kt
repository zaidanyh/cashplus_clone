package com.pasukanlangit.id.ui_core.components

import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.ui_core.R
import com.pasukanlangit.id.ui_core.databinding.FragmentGenericModalDialogBinding


class GenericCashGoldModalDialog
    private constructor(builder: Builder): DialogFragment() {

    private var _binding: FragmentGenericModalDialogBinding? = null
    private val binding get() = _binding!!

    //required
    val title: String
    val description: String
    val image: Int

    val positiveAction: PositiveButtonAction?
    val negativeAction: NegativeButtonAction?

    init {
        if(builder.title == null){
            throw Exception("GenericDialog title cannot be null")
        }

        if(builder.description == null){
            throw Exception("GenericDialog description cannot be null")
        }

        if(builder.image == null){
            throw Exception("GenericDialog image cannot be null")
        }

        if(builder.negativeAction != null && builder.negativeAction?.setClickOnDismiss == null && builder.negativeAction?.onBtnClicked == null){
            throw Exception("GenericDialog button negative must have setClickOnDismiss either onBtnClicked")
        }


        this.title = builder.title!!
        this.description = builder.description!!
        this.image = builder.image!!

        this.positiveAction = builder.positiveAction
        this.negativeAction = builder.negativeAction
    }

    class Builder {
        var title: String? = null
            private set

        var description: String? = null
            private set

        var image: Int? = null
            private set


        var positiveAction: PositiveButtonAction? = null
            private set

        var negativeAction: NegativeButtonAction? = null
            private set

        fun title(title: String): Builder{
            this.title = title
            return this
        }

        fun description(description: String): Builder{
            this.description = description
            return this
        }

        fun image(image: Int): Builder{
            this.image = image
            return this
        }

        fun buttonPositive(positiveAction: PositiveButtonAction): Builder{
            this.positiveAction = positiveAction
            return this
        }

        fun buttonNegative(negativeAction: NegativeButtonAction): Builder{
            this.negativeAction = negativeAction
            return this
        }

        fun show(fragmentManager: FragmentManager, tag: String? = null){
            GenericCashGoldModalDialog(this).show(fragmentManager, tag)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //make background transparent and full width
        setStyle(STYLE_NORMAL, R.style.Cashgold_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenericModalDialogBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindLayout()
    }

    override fun onResume() {
        super.onResume()

        setCustomSizeDialog()
    }

    private fun setCustomSizeDialog() {
        val window: Window? = dialog?.window
        val size = Point()

        val display: Display? = window?.windowManager?.defaultDisplay
        display?.getSize(size)

        val width: Int = size.x

        window?.setLayout((width * 0.90).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
    }

    private fun bindLayout() {
        with(binding){
            tvTitle.text = title
            tvDesc.text = description
            ivLogo.setImageResource(image)

            btnPositive.isVisible = positiveAction != null
            btnNegative.isVisible = negativeAction != null

            negativeAction?.let { negative ->
                btnNegative.text = negative.btnLabel
                btnNegative.setOnClickListener {
                    if(negative.setClickOnDismiss != null){
                        dismiss()
                        return@setOnClickListener
                    }

                    if(negative.onBtnClicked != null){
                        dismiss()
                        negative.onBtnClicked!!.invoke()
                        return@setOnClickListener
                    }
                }
            }

            positiveAction?.let { positive ->
                btnPositive.text = positive.btnLabel
                btnPositive.setOnClickListener {
                    dismiss()
                    positive.onBtnClicked()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

