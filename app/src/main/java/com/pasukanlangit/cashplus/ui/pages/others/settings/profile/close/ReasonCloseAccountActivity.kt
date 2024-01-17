package com.pasukanlangit.cashplus.ui.pages.others.settings.profile.close

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityReasonCloseAccountBinding
import com.pasukanlangit.id.core.utils.InputUtil.inputAlphabetFilter

class ReasonCloseAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReasonCloseAccountBinding

    private var isOther = false
    private var stateButton = false
    private var result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReasonCloseAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reason = intent.getStringExtra(REASON_RESULT_KEY)
        if (!reason.isNullOrEmpty()) {
            when(reason) {
                getString(R.string.answer_1) -> setStateRadioButton(first = true)
                getString(R.string.answer_2) -> setStateRadioButton(second = true)
                getString(R.string.answer_3) -> setStateRadioButton(third = true)
                getString(R.string.answer_4) -> setStateRadioButton(fourth = true)
                getString(R.string.answer_5) -> setStateRadioButton(fifth = true)
                getString(R.string.answer_6) -> setStateRadioButton(sixth = true)
                getString(R.string.answer_7) -> setStateRadioButton(seventh = true)
                getString(R.string.answer_8) -> setStateRadioButton(eighth = true)
                else -> {
                    isOther = true
                    binding.edtReasonCloseAccount.isVisible = isOther
                    binding.rbOther.isChecked = true
                    animationLayout()
                    binding.edtReasonCloseAccount.setText(reason)
                }
            }
        }
        binding.apply {
            appBar.setOnBackPressed { finish() }
            wrapperRbFirst.setOnClickListener {
                setStateRadioButton(first = true)
                result = getString(R.string.answer_1)
            }
            wrapperRbSecond.setOnClickListener {
                setStateRadioButton(second = true)
                result = getString(R.string.answer_2)
            }
            wrapperRbThird.setOnClickListener {
                setStateRadioButton(third = true)
                result = getString(R.string.answer_3)
            }
            wrapperRbFourth.setOnClickListener {
                setStateRadioButton(fourth = true)
                result = getString(R.string.answer_4)
            }
            wrapperRbFifth.setOnClickListener {
                setStateRadioButton(fifth = true)
                result = getString(R.string.answer_5)
            }
            wrapperRbSixth.setOnClickListener {
                setStateRadioButton(sixth = true)
                result = getString(R.string.answer_6)
            }
            wrapperRbSeventh.setOnClickListener {
                setStateRadioButton(seventh = true)
                result = getString(R.string.answer_7)
            }
            wrapperRbEighth.setOnClickListener {
                setStateRadioButton(eighth = true)
                result = getString(R.string.answer_8)
            }
            wrapperRbOther.setOnClickListener {
                setStateRadioButton(other = true)
            }
            edtReasonCloseAccount.filters = arrayOf(InputFilter.LengthFilter(64), inputAlphabetFilter)
            edtReasonCloseAccount.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString()
                    if (value.isEmpty()) {
                        edtReasonCloseAccount.error = getString(R.string.input_required, getString(R.string.reason_close_account))
                        stateButton = false
                        return
                    }
                    if (value.length < 5) {
                        edtReasonCloseAccount.error = getString(R.string.input_min_length_required, getString(R.string.reason_close_account), 5)
                        stateButton = false
                        return
                    }
                    stateButton = true
                    edtReasonCloseAccount.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnNext.isEnabled = stateButton
                }
            })
            btnNext.setOnClickListener {
                if (isOther) {
                    result = edtReasonCloseAccount.text.toString().trim()
                }
                val intent = Intent()
                intent.putExtra(REASON_RESULT_KEY, result)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun setStateRadioButton(
        first: Boolean = false, second: Boolean = false, third: Boolean = false, fourth: Boolean = false,
        fifth: Boolean = false, sixth: Boolean = false, seventh: Boolean = false, eighth: Boolean = false,
        other: Boolean = false
    ) {
        binding.apply {
            rbFirst.isChecked = first
            rbSecond.isChecked = second
            rbThird.isChecked = third
            rbFourth.isChecked = fourth
            rbFifth.isChecked = fifth
            rbSixth.isChecked = sixth
            rbSeventh.isChecked = seventh
            rbEighth.isChecked = eighth
            rbOther.isChecked = other
            edtReasonCloseAccount.isVisible = other
            if (isOther) {
                isOther = false
                edtReasonCloseAccount.text?.clear()
            } else {
                isOther = other
                btnNext.isEnabled = false
            }
            btnNext.isEnabled = first || second || third || fourth || fifth || sixth || seventh || eighth
            animationLayout()
        }
    }

    private fun animationLayout() {
        val animation = AnimationUtils.loadAnimation(
            this, if (isOther) R.anim.translate_fade_in_anim
            else R.anim.translate_fade_out_anim
        )
        animation.startOffset = 40
        binding.edtReasonCloseAccount.startAnimation(animation)
    }

    companion object {
        const val REASON_RESULT_KEY = "reason_result_key"
    }
}