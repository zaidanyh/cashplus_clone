package com.pasukanlangit.cashplus.ui.omni

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityEnterNumOmniBinding
import com.pasukanlangit.cashplus.ui.omni.packageomni.PackageOmniActivity
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermission
import com.pasukanlangit.id.core.utils.InputUtil
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyNumber
import com.pasukanlangit.id.core.utils.InputUtil.inputNumericFilter
import com.pasukanlangit.id.library_core.utils.Constants

class EnterNumOmniActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterNumOmniBinding

    private val getNumberFromContact = registerForActivityResult(ActivityResultContracts.PickContact()) {
        it?.let { result ->
            val cursor: Cursor? = contentResolver.query(result, null, null, null, null)
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val isHasPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt() > 0
                        if (isHasPhoneNumber) {
                            val phoneData = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                            if (phoneData != null) {
                                if (phoneData.moveToFirst()) {
                                    val phoneNumber = phoneData.getString(phoneData.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    val phoneDigitOnly = phoneNumber.trim().filter { num -> num.isDigit() }
                                    val phoneNumberFormatted = if (phoneDigitOnly.substring(0, 2) == "62")
                                        phoneDigitOnly.replaceRange(0, 2, "0")
                                    else phoneDigitOnly
                                    binding.edtPhoneDest.setText(phoneNumberFormatted)
                                }
                            }
                            phoneData?.close()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, e.message ?: Constants.unknownError, Toast.LENGTH_SHORT).show()
                }
                return@registerForActivityResult
            }
            Toast.makeText(this, getString(R.string.fetch_contact_failed), Toast.LENGTH_SHORT).show()
        }
    }
    private val accessContactPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            showContact()
            return@registerForActivityResult
        }
        Toast.makeText(this, getString(R.string.grant_permission_access_contact), Toast.LENGTH_SHORT).show()
    }

    private var stateDest = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterNumOmniBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        with(binding) {
            imgBack.setOnClickListener { finish() }
            inputPhoneDest.setEndIconOnClickListener {
                showContact()
            }
            edtPhoneDest.filters = arrayOf(InputFilter.LengthFilter(14), inputNumericFilter)
            edtPhoneDest.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputPhoneDest.error = getString(R.string.input_phone_number_first)
                        stateDest = false
                        return
                    }
                    if (input.length < 9) {
                        inputPhoneDest.error = getString(R.string.input_min_length_required, getString(R.string.phone_number), 9)
                        stateDest = false
                        return
                    }
                    if (!input.checkIsOnlyNumber()) {
                        inputPhoneDest.error = getString(R.string.number_or_code_not_valid)
                        stateDest = false
                        return
                    }
                    stateDest = true
                    inputPhoneDest.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnCheckProductSelection.isEnabled = stateDest
                }
            })
            btnCheckProductSelection.setOnClickListener {
                checkValidationInput()
            }
        }
    }

    private fun showContact() {
        if (hasPermission(this, Manifest.permission.READ_CONTACTS)) {
            getNumberFromContact.launch(null)
            return
        }
        accessContactPermission.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun checkValidationInput() {
        with(binding) {
            val dest = edtPhoneDest.text.toString().trim()

            val numberPhonePrefix = dest.replace("^(62|0|\\+62)".toRegex(), "")
            if (!InputUtil.prefixTelkomsel(numberPhonePrefix.substring(0, 3))) {
                inputPhoneDest.error = getString(R.string.only_telkomsel_number)
                btnCheckProductSelection.isEnabled = false
                return
            }
            startActivity(
                Intent(this@EnterNumOmniActivity, PackageOmniActivity::class.java).apply {
                    putExtra(PackageOmniActivity.PHONE_DEST_KEY, dest)
                }
            )
        }
    }
}