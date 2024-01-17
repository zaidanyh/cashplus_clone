package com.pasukanlangit.cashplus.ui.ewallet

import android.Manifest
import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityInputManualBinding
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.MyUtils.goToMainAndSendMessage
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.extensions.setRupiahListener
import com.pasukanlangit.id.core.presentation.EnterPinDialogFragment
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermission
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyNumber
import com.pasukanlangit.id.core.utils.InputUtil.validPrefixNumber
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiahWithoutRp
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class InputManualActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputManualBinding
    private val viewModel: DirectProductPurchasesViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var phoneNumberState = false
    private var productPurchase: ProductModel? = null

    private var uuid: String? = null
    private var accessToken: String? = null
    private var phoneNumber: String? = null
    private var nominalQty: String? = null

    private val getContactLauncher = registerForActivityResult(ActivityResultContracts.PickContact()) {
        it?.let { contact ->
            val phone: Cursor? = contentResolver.query(contact, null, null, null, null)
            if (phone == null) {
                Toast.makeText(this, "Gagal Mendapat Kontak", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    if (phone.moveToFirst()) {
                        val id = phone.getString(phone.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        if (phone.getString(phone.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt() > 0) {
                            val phoneData = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                            if (phoneData != null) {
                                if (phoneData.moveToFirst()) {
                                    val phoneNumber = phoneData.getString(phoneData.getColumnIndexOrThrow(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    val phoneDigitOnly = phoneNumber.trim().filter { num -> num.isDigit() }
                                    val phoneDigit = if (phoneDigitOnly.substring(0, 2) == "62")
                                        phoneDigitOnly.replaceRange(0, 2, "0")
                                    else phoneDigitOnly
                                    binding.edtInputNumber.setText(phoneDigit)
                                }
                            }
                            phoneData?.close()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, e.message ?: Constants.unknownError, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val permissionAccessContact = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) showContact()
        else Toast.makeText(this, "Berikan izin untuk mengakses kontak anda", Toast.LENGTH_SHORT).show()
    }

    val enterPIN = EnterPinDialogFragment(
        onEnterPinCompleted = {
            viewModel.transactionsRequest(
                uuid = uuid, productCode = productPurchase?.kode_produk.toString(),
                dest = phoneNumber.toString(), pin = it, qty = nominalQty.toString(),
                accessToken = accessToken, reqId = "${(1..100).random()}"
            )
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputManualBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()
        productPurchase = intent.parcelable(PRODUCT_PURCHASE_MANUAL_KEY)

        with(binding) {
            appBar.setOnBackPressed { finish() }
            inputPhoneNumber.setEndIconOnClickListener {
                showContact()
            }
            txtWarning.text = getString(R.string.buy_minimum_unit_product_format, productPurchase?.kode_provider?.replace("_", " "))
            edtInputNumber.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString()
                    if (value.isEmpty()) {
                        inputPhoneNumber.error = getString(R.string.input_phone_number_first)
                        phoneNumberState = false
                        return
                    }
                    if (value.length < 9) {
                        inputPhoneNumber.error = getString(R.string.input_min_length_required, getString(R.string.customer_number), 9)
                        phoneNumberState = false
                        return
                    }
                    if (!value.checkIsOnlyNumber()) {
                        inputPhoneNumber.error = getString(R.string.number_or_code_not_valid)
                        phoneNumberState = false
                        return
                    }
                    phoneNumberState = true
                    inputPhoneNumber.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnNext.isEnabled = phoneNumberState
                }
            })
            edtNominalTopup.filters = arrayOf(InputFilter.LengthFilter(10))
            edtNominalTopup.setRupiahListener()
            btnNext.setUpToProgressButton(this@InputManualActivity)
            btnNext.setOnClickListener {
                validationInput()
            }
        }
        collectData()
    }

    private fun showContact() {
        if (hasPermission(this, Manifest.permission.READ_CONTACTS)) {
            getContactLauncher.launch(null)
        } else permissionAccessContact.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun validationInput() {
        with(binding) {
            val inputPhone = edtInputNumber.text.toString().trim()
            val nominal = edtNominalTopup.text.toString().trim().replace(",", "")

            KeyboardUtil.hideSoftKeyboard(this@InputManualActivity)
            if (!inputPhone.validPrefixNumber()) {
                inputPhoneNumber.error = getString(R.string.any_input_is_not_valid, getString(R.string.phone_number))
                phoneNumberState = false
                return
            }
            if (nominal.length > 10) {
                inputNominalTopup.isErrorEnabled = true
                inputNominalTopup.error = getString(R.string.top_up_balance_error_out_of_capacity)
                return
            }
            if (nominal.isEmpty()) {
                inputNominalTopup.isErrorEnabled = true
                inputNominalTopup.error = getString(R.string.top_up_balance_error_required)
                return
            }
            if (nominal.toInt() < 1000) {
                inputNominalTopup.isErrorEnabled = true
                inputNominalTopup.error = getString(R.string.buy_minimum_required)
                return
            }
            if (nominal.toInt() > 20000000) {
                inputNominalTopup.isErrorEnabled = true
                inputNominalTopup.error = getString(R.string.buy_maximum_required)
                return
            }

            inputNominalTopup.isErrorEnabled = false
            inputNominalTopup.error = null
            phoneNumber = inputPhone
            nominalQty = nominal
            viewModel.calculateUnitPrice(
                uuid = uuid, productCode = productPurchase?.kode_produk.toString(),
                qty = nominalQty.toString(), dest = inputPhone, accessToken = accessToken
            )
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.calculateLoading.collectLatest {
                        if (it) {
                            binding.btnNext.isEnabled = false
                            binding.btnNext.showLoading()
                            return@collectLatest
                        }
                        binding.btnNext.isEnabled = true
                        binding.btnNext.hideProgress(getString(R.string.next))
                    }
                }
                launch {
                    viewModel.calculate.collectLatest { response ->
                        if (response != null) {
                            DirectProductPurchasesFragment.newInstance(
                                phoneNumber = phoneNumber,
                                imgUrl = productPurchase?.img_url?.ifEmpty { productPurchase?.img_url_2 }.toString(),
                                result = response
                            ).show(supportFragmentManager, "Detail product purchase")
                        }
                    }
                }
                launch {
                    viewModel.calculateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.contains("tidak dikenal", ignoreCase = true)) {
                                GenericModalDialogCashplus.Builder()
                                    .title(getString(R.string.something_wrong))
                                    .image(R.drawable.illustration_error)
                                    .description(getString(R.string.phone_number_not_found))
                                    .buttonNegative(
                                        NegativeButtonAction(
                                            btnLabel = getString(R.string.close),
                                            backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                            buttonTextColor = Color.parseColor("#0A57FF"),
                                            onBtnClicked = {
                                                viewModel.removeCalculateError()
                                            }
                                        )
                                    )
                                    .show(supportFragmentManager, "Customer Id Is Empty")
                                return@collectLatest
                            }
                            val toast = Toast.makeText(this@InputManualActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeCalculateError()
                        }
                    }
                }

                launch {
                    viewModel.transactionsLoading.collectLatest { enterPIN.setLoading(it) }
                }
                launch {
                    viewModel.transactions.collectLatest { response ->
                        if (response != null) {
                            goToMainAndSendMessage(
                                this@InputManualActivity,
                                if (response.rc == "00") getString(
                                    R.string.transaction_dana_successfully,
                                    getNumberRupiahWithoutRp(nominalQty?.toInt()), phoneNumber.toString(),
                                    getString(R.string.check_history_message)
                                )
                                else getString(R.string.transaction_pending_description),
                                if (response.rc == "00") NotifType.NOTIF_TRX_SUCCESS
                                else NotifType.NOTIF_PENDING_OR_PROCESS
                            )
                        }
                    }
                }
                launch {
                    viewModel.transactionsError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.contains("pending", ignoreCase = true)) {
                                goToMainAndSendMessage(
                                    this@InputManualActivity,
                                    getString(R.string.transaction_pending_description),
                                    NotifType.NOTIF_PENDING_OR_PROCESS
                                )
                                viewModel.removeHeadTransactionsError()
                                return@collectLatest
                            }
                            val toast = Toast.makeText(this@InputManualActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeHeadTransactionsError()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.btnNext.isEnabled = phoneNumberState
    }

    companion object {
        const val PRODUCT_PURCHASE_MANUAL_KEY = "product_purchase_manual_key"
    }
}