package com.pasukanlangit.id.core.utils

import android.content.Context
import android.text.InputFilter
import android.text.TextUtils
import android.util.Patterns
import com.pasukanlangit.id.core.R
import java.util.regex.Pattern

object InputUtil {

    private fun CharSequence.digitsOnly(): String = Regex("\\D").replace(this, "")
    private fun CharSequence.withoutSpecialChars() = Regex("[^A-Za-z \\d]").replace(this, "")
    private fun CharSequence.alphabetOnly(): String = Regex("[^A-Za-z ]").replace(this, "")
    private fun CharSequence.allTypeWithSomeSpecialChars() = Regex("[^A-Za-z \\d,./()&-]").replace(this, "")

    val inputNumericFilter = InputFilter { source, _, _, _, _, _ ->
        source.digitsOnly()
    }
    val inputCharNumberFilter = InputFilter { source, _, _, _, _, _ ->
        source.withoutSpecialChars()
    }
    val inputAlphabetFilter = InputFilter { source, _, _, _, _, _ ->
        source.alphabetOnly()
    }
    val inputAllTypeWithSomeSpecialChars = InputFilter { source, _, _, _, _, _ ->
        source.allTypeWithSomeSpecialChars()
    }

    data class ErrorInputRegister(
        val isError: Boolean,
        val message: String
    )

    //check more https://stackoverflow.com/questions/1795402/check-if-a-string-contains-a-special-character
    fun String.checkIsOnlyWords() = Pattern.matches("[a-zA-Z .]+", this)

    fun String.checkIsOnlyNumber() = Pattern.matches("\\d+", this)

    fun String.isValidEmail() = !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun String.validPrefixNumber(): Boolean {
        if (this.validPhoneNumber()) {
            val numberWithPrefix = this.replace(phoneNumberRegex, "")
            val prefix = arrayOf(
                811, 812, 813, 821, 822, 823, 851, 852, 853, // Prefix Telkomsel
                814, 815, 816, 855, 856, 857, 858, // Prefix Indosat
                817, 818, 819, 859, 877, 878, // Prefix XL
                895, 896, 897, 898, 899, // Prefix Three
                831, 832, 833, 838, // Prefix Axis
                881, 882, 883, 884, 885, 886, 887, 888,889, // Prefix Smartfren

                601, 603, 604, 605, 606, 607, 608, 609 // Prefix Nomor Malaysia
            )
            val phoneNumber = numberWithPrefix.trim().substring(0, 3).toIntOrNull() ?: -1
            return prefix.contains(phoneNumber)
        }
        return false
    }
    private val phoneNumberRegex = "^(62|0|\\+62)".toRegex()
    private val phoneNumberPrefix = "^(62|0|60|\\+62)".toRegex()
    private fun String.validPhoneNumber(): Boolean = phoneNumberPrefix.containsMatchIn(this)

    fun passwordValidate(context: Context, password: String): ErrorInputRegister =
        when {
            password.length < 6 -> {
                ErrorInputRegister(
                    isError = true,
                    message = context.getString(R.string.input_password_min_length)
                )
            }
//            !password.matches(".*[A-Z].*".toRegex()) -> {
//                ErrorInputRegister(
//                    isError = true,
//                    message = "Password harus memiliki minimal 1 huruf besar"
//                )
//            }
            !password.matches(".*[a-zA-Z].*".toRegex()) -> {
                ErrorInputRegister(
                    isError = true,
                    message = context.getString(R.string.input_password_letter_required)
                )
            }
//            !password.matches(".*[!@#$%^&*+=/?].*".toRegex()) -> {
//                ErrorInputRegister(
//                    isError = true,
//                    message = "Password harus memiliki minimal 1 symbol"
//                )
//            }
            !password.matches(".*\\d.*".toRegex()) -> {
                ErrorInputRegister(
                    isError = true,
                    message = context.getString(R.string.input_password_number_required)
                )
            }
            else -> ErrorInputRegister(isError = false, message = "success")
        }

    fun String.toCapitalize(): String {
        val newNameCapitalize = StringBuilder()
        this.trim().split(" ").forEach { word ->
            val cap = StringBuilder()
            word.toCharArray().forEachIndexed { index, c ->
                cap.append(if (index == 0) c.uppercaseChar() else c.lowercaseChar())
            }
            cap.append(" ")
            newNameCapitalize.append(cap)
        }
        return newNameCapitalize.toString()
    }

    fun String.toDoubleNumber(): Double {
        val valueFiltered = this.filter { it.isDigit() }
        return valueFiltered.toDouble()
    }

    fun String.toIntegerNumber(): Int {
        val valueFiltered = this.filter { it.isDigit() }
        return valueFiltered.toInt()
    }

    fun prefixTelkomsel(phone: String): Boolean {
        val prefix = arrayOf(811, 812, 813, 821, 822, 823, 851, 852, 853)
        val isFound = phone.toIntOrNull() ?: -1
        return prefix.contains(isFound)
    }
}