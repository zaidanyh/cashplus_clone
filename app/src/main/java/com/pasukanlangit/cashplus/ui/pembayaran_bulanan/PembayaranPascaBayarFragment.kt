package com.pasukanlangit.cashplus.ui.pembayaran_bulanan

import android.Manifest
import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.FragmentPembayaranBulananBinding
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.MyUtils.httpToHttps
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.PembayaranBulananViewModel
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermissions
import com.pasukanlangit.id.core.utils.DrawablePosition
import com.pasukanlangit.id.core.utils.InputUtil.inputNumericFilter
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PembayaranPascaBayarFragment : Fragment() {

    private var _binding: FragmentPembayaranBulananBinding? = null
    private val binding get() = _binding!!
    private val viewModel : PembayaranBulananViewModel by viewModel()
    private val sharedPrefDataSource : SharedPrefDataSource by inject()

    private var imgUrl: String? = null
    private var destination: String? = null

    private lateinit var productsModel: List<ProductModel>

    private val getContactLauncher = registerForActivityResult(ActivityResultContracts.PickContact()) {
        it?.let { contact ->
            val phone: Cursor? = requireActivity().contentResolver.query(contact, null, null, null, null)
            if (phone == null) {
                Toast.makeText(requireContext(), getString(R.string.fetch_contact_failed), Toast.LENGTH_SHORT).show()
            } else {
                try {
                    if (phone.moveToFirst()) {
                        val id = phone.getString(phone.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        if (phone.getString(phone.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt() > 0) {
                            val phoneData = requireActivity().contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                            if (phoneData != null) {
                                if (phoneData.moveToFirst()) {
                                    val phoneNumber = phoneData.getString(phoneData.getColumnIndexOrThrow(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    val phoneDigitOnly = phoneNumber.trim().filter { num -> num.isDigit() }
                                    val phoneDigit = if (phoneDigitOnly.substring(0, 2) == "62")
                                        phoneDigitOnly.replaceRange(0, 2, "0")
                                    else phoneDigitOnly
                                    viewModel.setPhoneNumber(phoneDigit)
                                }
                            }
                            phoneData?.close()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message ?: Constants.unknownError, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val permissionAccessContact = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) showContact()
        else Toast.makeText(requireContext(), getString(R.string.grant_permission_access_contact), Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPembayaranBulananBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uuid = sharedPrefDataSource.getUUID()
        val token = sharedPrefDataSource.getAccessToken()

        if(!uuid.isNullOrEmpty() && !token.isNullOrEmpty()) {
            val productRequest = ProductRequest(
                uuid = uuid,
                category = "#PASCABAYAR",
                kode_provider = "",
                is_faktur = "",
                is_active = "1"
            )
            viewModel.getProductPascaBayar(productRequest, token)
        }

        with(binding) {
            providerListSpinner.prompt = "Pilih Provider"
            edtNomerPascabayar.filters = arrayOf(InputFilter.LengthFilter(16), inputNumericFilter)
            btnCheckPascabayar.setUpToProgressButton(viewLifecycleOwner)
            edtNomerPascabayar.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edtNomerPascabayar.right - edtNomerPascabayar.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                        showContact()
                        return@setOnTouchListener true
                    }
                }
                false
            }
            btnCheckPascabayar.setOnClickListener {
                val operator = providerListSpinner.selectedItem.toString()
                val itemSelected = providerListSpinner.selectedItemPosition
                val dest = edtNomerPascabayar.text.toString().trim()

                if (dest.isEmpty()) {
                    GenericModalDialogCashplus.Builder()
                        .title(getString(R.string.something_wrong))
                        .image(R.drawable.illustration_empty)
                        .description(getString(R.string.input_customer_number_first))
                        .buttonNegative(
                            NegativeButtonAction(
                                btnLabel = getString(R.string.close),
                                backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                buttonTextColor = Color.parseColor("#0A57FF"),
                                setClickOnDismiss = true
                            )
                        )
                        .show(requireActivity().supportFragmentManager, "CustomerId_is_empty")
                    return@setOnClickListener
                }
                if (operator == Constants.NO_PROVIDER) {
                    GenericModalDialogCashplus.Builder()
                        .title(getString(R.string.something_wrong))
                        .image(R.drawable.illustration_empty)
                        .description(Constants.NO_PROVIDER)
                        .buttonNegative(
                            NegativeButtonAction(
                                btnLabel = getString(R.string.close),
                                backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                buttonTextColor = Color.parseColor("#0A57FF"),
                                setClickOnDismiss = true
                            )
                        )
                        .show(requireActivity().supportFragmentManager, "No_province_available")
                    return@setOnClickListener
                }

                destination = if (dest.length > 2 && dest.substring(0, 2) == "62")
                    dest.replaceRange(0, 2, "0")
                else dest
                val selectedOperator = productsModel[itemSelected]
                imgUrl = selectedOperator.img_url.ifEmpty { selectedOperator.img_url_2.httpToHttps() }.httpToHttps()
                viewModel.payBill(
                    TransactionRequest(
                        uuid = uuid ?: "", kode_produk = selectedOperator.kode_produk.replaceRange( 0, 3, "TAG"),
                        dest = destination ?: "", pin = ""
                    ), accessToken = token ?: ""
                )
            }
            lifecycleScope.launch {
                viewModel.phoneNumber.collectLatest {
                    if (!it.isNullOrEmpty()) edtNomerPascabayar.setText(it)
                }
            }
        }

        viewModel.tagihanPascaBayar.observe(viewLifecycleOwner) { response ->
            if (response.status == Status.SUCCESS) {
                val listProvider = arrayListOf(Constants.NO_PROVIDER)
                if (!response.data?.data.isNullOrEmpty()) {
                    productsModel = response.data?.data!!
                    listProvider.clear()
                    response.data.data.forEach { product ->
                        listProvider.add(product.kode_provider)
                    }
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner_small_selected, listProvider)
                adapter.setDropDownViewResource(R.layout.item_spinner_small)
                binding.providerListSpinner.adapter = adapter
            }
        }

        observeTAGPascaBayar()
    }

    private fun observeTAGPascaBayar() {
        viewModel.billTrx.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                when(response.status) {
                    Status.LOADING -> binding.btnCheckPascabayar.showLoading()
                    Status.SUCCESS -> {
                        binding.btnCheckPascabayar.hideProgress(getString(R.string.check))

                        BillDetailFragment.newInstance(
                            destination, response.data,
                            imgUrl, R.drawable.ic_tab_menu_pasca_bayar
                        ).show(childFragmentManager, "Bill Detail")
                    }
                    Status.ERROR -> {
                        binding.btnCheckPascabayar.hideProgress(getString(R.string.check))

                        GenericModalDialogCashplus.Builder()
                            .title(getString(R.string.something_wrong))
                            .image(R.drawable.illustration_login_error)
                            .description(
                                if (
                                    response.message?.contains("format salah", ignoreCase = true) == true ||
                                    response.message?.contains("tidak dikenal", ignoreCase = true) == true
                                ) getString(R.string.any_not_found, getString(R.string.number_dest))
                                else if (
                                    response.message?.contains("tagihan tidak", ignoreCase = true) == true
                                ) getString(R.string.unavailable_bill)
                                else if (
                                    response.message?.contains("diroute", ignoreCase = true) == true
                                ) getString(R.string.product_is_unavailable_contact_cs_please)
                                else if (
                                    response.message?.contains("terbayar", ignoreCase = true) == true
                                ) getString(R.string.bill_paid)
                                else if (
                                    response.message?.contains("antrian", ignoreCase = true) == true
                                ) getString(R.string.operator_is_in_queue)
                                else response.message.toString()
                            )
                            .buttonNegative(
                                NegativeButtonAction(
                                    btnLabel = getString(R.string.close),
                                    backgroundButton = R.drawable.bg_transparent_border_primary_rounded_16,
                                    buttonTextColor = Color.parseColor("#1570EF"),
                                    setClickOnDismiss = true
                                )
                            )
                            .show(childFragmentManager, "wrong format")
                    }
                }
            }
        }
    }

    private fun showContact() {
        if (hasPermissions(requireContext(), arrayOf(Manifest.permission.READ_CONTACTS))) {
            getContactLauncher.launch(null)
        } else permissionAccessContact.launch(arrayOf(Manifest.permission.READ_CONTACTS))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}