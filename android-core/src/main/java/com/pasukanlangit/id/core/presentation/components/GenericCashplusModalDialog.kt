package com.pasukanlangit.id.core.presentation.components

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.pasukanlangit.id.core.R
import com.pasukanlangit.id.core.databinding.FragmentGenericModalDialogCashplusBinding

data class PositiveButtonAction(
    val btnLabel: String,
    val backgroundButton : Int? = null,
    val buttonTextColor: Int? = null,
    inline val onBtnClicked: () -> Unit
)

data class NegativeButtonAction(
    val btnLabel: String,
    val backgroundButton: Int? = null,
    val buttonTextColor: Int? = null,
    val setClickOnDismiss: Boolean? = null,
    inline val onBtnClicked: (() -> Unit)? = null
)
class GenericModalDialogCashplus private constructor(
    builder: Builder
): DialogFragment() {

    private var _binding: FragmentGenericModalDialogCashplusBinding? = null
    private val binding get() = _binding!!

    //required
    val title: String
    val description: String
    val image: Int

    private val isCanClose: Boolean
    private val isRaw: Boolean
    private val positiveAction: PositiveButtonAction?
    private val negativeAction: NegativeButtonAction?

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
        this.isRaw = builder.isRaw
        this.isCanClose = builder.isCanClose

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

        var isRaw: Boolean = false
            private set

        var isCanClose = true
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

        fun image(image: Int, isRaw: Boolean = false): Builder{
            this.image = image
            this.isRaw = isRaw
            return this
        }

        fun setIsCanClose(isCanClose: Boolean = true): Builder {
            this.isCanClose = isCanClose
            return this
        }

        fun buttonPositive(positiveAction: PositiveButtonAction?): Builder{
            this.positiveAction = positiveAction
            return this
        }

        fun buttonNegative(negativeAction: NegativeButtonAction): Builder{
            this.negativeAction = negativeAction
            return this
        }

        fun show(fragmentManager: FragmentManager, tag: String? = null) {
            GenericModalDialogCashplus(this).show(fragmentManager, tag)
            isShown = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogCashplus)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenericModalDialogCashplusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindLayout()
    }

    private fun bindLayout() {
        with(binding) {
            this@GenericModalDialogCashplus.isCancelable = isCanClose
            tvTitle.text = title
            tvDesc.text = description
            if (isRaw) Glide.with(requireContext()).asGif().load(image).into(ivLogo)
            else ivLogo.setImageResource(image)

            btnPositive.isVisible = positiveAction != null
            btnNegative.isVisible = negativeAction != null

            negativeAction?.let { negative ->
                btnNegative.setBackgroundResource(
                    negative.backgroundButton ?: R.drawable.bg_blue50_rounded_12
                )
                btnNegative.setTextColor(
                    negative.buttonTextColor ?: Color.parseColor("#0A57FF")
                )
                btnNegative.text = negative.btnLabel
                btnNegative.setOnClickListener {
                    if (negative.setClickOnDismiss != null) {
                        dismiss()
                        return@setOnClickListener
                    }

                    if (negative.onBtnClicked != null){
                        dismiss()
                        negative.onBtnClicked.invoke()
                        return@setOnClickListener
                    }
                }
            }

            positiveAction?.let { positive ->
                btnPositive.setBackgroundResource(
                    positive.backgroundButton ?: R.drawable.bg_primary_rounded_12
                )
                btnPositive.setTextColor(
                    positive.buttonTextColor ?: Color.parseColor("#FFFFFF")
                )
                btnPositive.text = positive.btnLabel
                btnPositive.setOnClickListener {
                    dismiss()
                    positive.onBtnClicked.invoke()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (isShown) return
        else dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        isShown = false
        super.onDismiss(dialog)
    }

    companion object {
        var isShown: Boolean = false
    }
}

