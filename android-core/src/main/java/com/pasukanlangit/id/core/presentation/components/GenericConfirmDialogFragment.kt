package com.pasukanlangit.id.core.presentation.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.pasukanlangit.id.core.R
import com.pasukanlangit.id.core.databinding.FragmentGenericConfirmDialogBinding

data class PositiveButton(
    val backgroundButton: Int,
    val buttonText: String,
    val buttonTextColor: Int,
    inline val onBtnAction: () -> Unit
)

data class NegativeButton(
    val backgroundButton: Int,
    val buttonText: String,
    val buttonTextColor: Int,
    val actionDismiss: Boolean? = null,
    inline val onBtnAction: (() -> Unit)? = null
)

class GenericConfirmDialogFragment private constructor(
    builder: Builder
): DialogFragment() {

    private var _binding: FragmentGenericConfirmDialogBinding? = null
    private val binding get() = _binding!!

    // Required
    val title: String
    val description: String

    private val positiveAction: PositiveButton?
    private val negativeAction: NegativeButton?

    init {
        if(builder.title == null){
            throw Exception("Generic Confirm Dialog title cannot be null")
        }

        if(builder.description == null){
            throw Exception("Generic Confirm Dialog description cannot be null")
        }

        this.title = builder.title!!
        this.description = builder.description!!

        this.positiveAction = builder.positiveAction
        this.negativeAction = builder.negativeAction
    }

    class Builder {
        var title: String? = null
            private set

        var description: String? = null
            private set

        var positiveAction: PositiveButton? = null
            private set

        var negativeAction: NegativeButton? = null
            private set

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun buttonPositive(positiveAction: PositiveButton): Builder {
            this.positiveAction = positiveAction
            return this
        }

        fun buttonNegative(negativeAction: NegativeButton): Builder {
            this.negativeAction = negativeAction
            return this
        }

        fun show(fragmentManager: FragmentManager, tag: String? = null) {
            GenericConfirmDialogFragment(this).show(fragmentManager, tag)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenEnterPinDialogNoClose)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenericConfirmDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            tvTitle.text = title
            tvDescription.text = description

            btnPositive.isVisible = positiveAction != null
            btnNegative.isVisible = negativeAction != null

            with(btnPositive) {
                positiveAction?.let { positive ->
                    setBackgroundResource(positive.backgroundButton)
                    text = positive.buttonText
                    setTextColor(positive.buttonTextColor)
                    setOnClickListener {
                        dismiss()
                        positive.onBtnAction.invoke()
                    }
                }
            }
            with(btnNegative) {
                negativeAction?.let { negative ->
                    setBackgroundResource(negative.backgroundButton)
                    text = negative.buttonText
                    setTextColor(negative.buttonTextColor)
                    setOnClickListener {
                        if(negative.actionDismiss != null){
                            dismiss()
                            return@setOnClickListener
                        }

                        if(negative.onBtnAction != null){
                            dismiss()
                            negative.onBtnAction.invoke()
                            return@setOnClickListener
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}