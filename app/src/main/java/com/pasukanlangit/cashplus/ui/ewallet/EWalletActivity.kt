package com.pasukanlangit.cashplus.ui.ewallet

import android.Manifest
import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.InputFilter
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.adapter.DataProductAdapter
import com.pasukanlangit.cashplus.databinding.ActivityEwalletBinding
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.pages.home.KEY_GLOBAL_DETAIL_MENU_PROVIDER_CODE
import com.pasukanlangit.cashplus.ui.pembayaran_provider.DetailTopUpFragment
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.TopUpGameViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.utils.CategoryProduct
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermission
import com.pasukanlangit.id.core.utils.DrawablePosition
import com.pasukanlangit.id.core.utils.InputUtil.validPrefixNumber
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.id.library_core.utils.Constants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EWalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEwalletBinding
    private val viewModel: TopUpGameViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

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
                                    val phoneNumber = phoneData.getString(phoneData.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEwalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri: Uri? = intent.data
        val providerCodeFromUri = uri?.getQueryParameter(KEY_GLOBAL_DETAIL_MENU_PROVIDER_CODE)
        val providerCode = intent.getStringExtra(KEY_PROVIDER_CODE) ?: providerCodeFromUri

        val uuid = sharedPref.getUUID()
        val accessToken = sharedPref.getAccessToken()

        viewModel.getProduct(
            ProductRequest(
                uuid = uuid ?: "",
                category = CategoryProduct.E_WALLET.value,
                kode_provider = providerCode ?: "",
                is_faktur = "",
                is_active = "1"
            ),
            accessToken = accessToken ?: ""
        )

        with(binding) {
            appBar.setOnBackPressed { finish() }
            appBar.setTitle(CategoryProduct.E_WALLET.title)
            edtInputNumber.filters = arrayOf(InputFilter.LengthFilter(16))
            edtInputNumber.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edtInputNumber.right - edtInputNumber.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                        showContact()
                        return@setOnTouchListener true
                    }
                }
                false
            }
        }

        observeProductEWallet()
        setupRecyclerView()
    }

    private fun showContact() {
        if (hasPermission(this, Manifest.permission.READ_CONTACTS)) {
            getContactLauncher.launch(null)
        } else permissionAccessContact.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun setupRecyclerView() {
        with(binding.rvEWallet) {
            layoutManager = LinearLayoutManager(this@EWalletActivity)
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    private fun observeProductEWallet() {
        viewModel.productList.observe(this) { response ->
            when(response.status) {
                Status.LOADING -> {
                    with(binding) {
                        rvEWalletShimmer.isVisible = true
                        rvEWalletShimmer.startShimmer()
                        rvEWallet.isVisible = false
                    }
                }
                Status.SUCCESS -> {
                    with(binding) {
                        rvEWalletShimmer.isVisible = false
                        rvEWalletShimmer.stopShimmer()
                        rvEWallet.isVisible = true

                        if (response.data != null) {
                            val productEWallet = response.data.data?.filter { product -> product.product_type.isEmpty() }
                            rvEWallet.adapter = productEWallet?.let { data ->
                                DataProductAdapter(data) {
                                    val inputNumber = edtInputNumber.text.toString().trim()
                                    if (inputNumber.isEmpty()) {
                                        GenericModalDialogCashplus.Builder()
                                            .title(getString(R.string.something_wrong))
                                            .image(R.drawable.illustration_verify)
                                            .description(getString(R.string.input_required, getString(R.string.customer_number)))
                                            .buttonNegative(
                                                NegativeButtonAction(
                                                    btnLabel = getString(R.string.close),
                                                    backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                                    buttonTextColor = Color.parseColor("#0A57FF"),
                                                    setClickOnDismiss = true
                                                )
                                            )
                                            .show(supportFragmentManager, "Customer Id Is Empty")
                                        return@DataProductAdapter
                                    }
                                    if (inputNumber.length < 4) {
                                        GenericModalDialogCashplus.Builder()
                                            .title(getString(R.string.something_wrong))
                                            .image(R.drawable.illustration_verify)
                                            .description(getString(R.string.input_min_length_required, getString(R.string.customer_number), 4))
                                            .buttonNegative(
                                                NegativeButtonAction(
                                                    btnLabel = getString(R.string.close),
                                                    backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                                    buttonTextColor = Color.parseColor("#0A57FF"),
                                                    setClickOnDismiss = true
                                                )
                                            )
                                            .show(supportFragmentManager, "Customer Id Is Empty")
                                        return@DataProductAdapter
                                    }
                                    if (!it.kode_provider.contains("maxim", ignoreCase = true) && !inputNumber.validPrefixNumber()) {
                                        GenericModalDialogCashplus.Builder()
                                            .title(getString(R.string.something_wrong))
                                            .image(R.drawable.illustration_verify)
                                            .description(getString(R.string.any_input_is_not_valid, getString(R.string.phone_number)))
                                            .buttonNegative(
                                                NegativeButtonAction(
                                                    btnLabel = getString(R.string.close),
                                                    backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                                    buttonTextColor = Color.parseColor("#0A57FF"),
                                                    setClickOnDismiss = true
                                                )
                                            )
                                            .show(supportFragmentManager, "Customer Id Is Empty")
                                        return@DataProductAdapter
                                    }
                                    DetailTopUpFragment.newInstance(
                                        productModel = it,
                                        phoneNumber = if (inputNumber.substring(0, 2) == "62")
                                            inputNumber.replaceRange(0, 2, "0")
                                        else inputNumber
                                    ).show(supportFragmentManager, "Detail Top Up Telepon")
                                }
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    with(binding) {
                        rvEWalletShimmer.isVisible = false
                        rvEWalletShimmer.stopShimmer()
                        rvEWallet.isVisible = true
                    }

                    val buttomSheetNotif = ButtomSheetNotif(response.message, NotifType.NOTIF_ERROR)
                    buttomSheetNotif.show(supportFragmentManager, buttomSheetNotif.tag)
                }
            }
        }
    }

    companion object {
        const val KEY_PROVIDER_CODE = "KEY_PROVIDER_CODE"
    }
}