package com.pasukanlangit.cashplus.ui.addressbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.ActivityAdressBookBinding
import com.pasukanlangit.cashplus.model.request_body.AddAddressBookRequest
import com.pasukanlangit.cashplus.ui.addressbook.add.AddAddressBookActivity
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddressBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdressBookBinding
    private val viewModel: AddressBookViewModel by viewModel()
    private val sharedPref:SharedPrefDataSource by inject()

    private var savedBookAddressId : String = ""
    private lateinit var adapter : ListAddressBookAdapter

    private var uuid: String ?= null
    private var accessToken: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdressBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()
        adapter = ListAddressBookAdapter{
            if(!uuid.isNullOrEmpty() && !accessToken.isNullOrEmpty()){
                val addressBookRequest = AddAddressBookRequest("","","","", "","","",uuid!!,"","",it)
                viewModel.deleteAddressBook(addressBookRequest, accessToken!!)
            }
        }

        binding.appBar.setOnBackPressed { finish() }
        binding.btnAddAddress.setOnClickListener { startActivity(Intent(this, AddAddressBookActivity::class.java)) }
        viewModel.getSavedAddressBook()

        setUpRecyclerview()
        observeSavedAddressBook()
        observeAllAddressBook()
        observeDeletedAddress()
        observeUpdateAddress()

        with(binding.btnChoseAddress) {
            setUpToProgressButton(this@AddressBookActivity)

            setOnClickListener {
                val selectedAddressBook = adapter.getSelectedProductAddressBook()
                if (selectedAddressBook == null) {
                    Toast.makeText(
                        this@AddressBookActivity,
                        getString(R.string.choose_address_first),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (uuid != null && accessToken != null) {
                        viewModel.updateAddressBook(
                            AddAddressBookRequest(
                                address = selectedAddressBook.address,
                                addressType = selectedAddressBook.addressType,
                                provinceId = selectedAddressBook.provinceId,
                                posCode = selectedAddressBook.posCode,
                                receiverName = selectedAddressBook.receiverName,
                                phoneNumber = selectedAddressBook.phoneNumber,
                                isMainAddress = "1",
                                uuid = uuid!!,
                                cityId = selectedAddressBook.cityId,
                                subdistrictId = selectedAddressBook.subdistrictId,
                                book_address_id = selectedAddressBook.bookAddressId
                            ),
                            accessToken!!
                        )
                    }
                }
            }
        }
    }

    private fun observeUpdateAddress() {
        viewModel.updateAddressBook.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.btnChoseAddress.hideProgress(getString(R.string.choose_address_btn))
                    finish()
                }
                Status.ERROR -> {
                    binding.btnChoseAddress.hideProgress(getString(R.string.choose_address_btn))

                    val menusAllFragment = ButtomSheetNotif(
                        it.message,
                        NotifType.NOTIF_ERROR
                    )
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                }
                Status.LOADING -> {
                    binding.btnChoseAddress.showLoading()
                }

            }
        }
    }

    private fun observeDeletedAddress() {
       viewModel.deletedAddressBook.observe(this) {
           binding.loading.isVisible = it.status == Status.LOADING
           when (it.status) {
               Status.SUCCESS -> {
                   it.data?.getContentIfNotHandled()?.let {
                       Toast.makeText(
                           this,
                           getString(R.string.address_successfully_deleted),
                           Toast.LENGTH_SHORT
                       ).show()
                   }
               }
               Status.ERROR -> {
                   val menusAllFragment = ButtomSheetNotif(
                       it.message,
                       NotifType.NOTIF_ERROR
                   )
                   menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
               }
               else -> {}
           }
       }
    }


    private fun setUpRecyclerview() {
        with(binding.rvAddress) {
            layoutManager = LinearLayoutManager(this@AddressBookActivity)
            adapter = this@AddressBookActivity.adapter
        }
    }

    override fun onResume() {
        super.onResume()
        if(!uuid.isNullOrEmpty() && !accessToken.isNullOrEmpty()){
            viewModel.getAllAddressBook(uuid!!, accessToken!!)
        }
    }

    private fun observeAllAddressBook() {
        viewModel.allAddressBook.observe(this) {
            binding.loading.isVisible = it.status == Status.LOADING
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.data?.let { addressbook ->
                        if (addressbook.isNotEmpty()) {
                            binding.btnChoseAddress.isEnabled = true

                            if (savedBookAddressId.isNotEmpty()) {
                                var selectedAddressBook =
                                    addressbook.find { addrs -> addrs.bookAddressId == savedBookAddressId }
                                if (selectedAddressBook == null) selectedAddressBook =
                                    addressbook.find { addrs -> addrs.isMainAddress == "1" }

                                selectedAddressBook?.isSelected = true
                            } else {
                                addressbook.find { addrs -> addrs.isMainAddress == "1" }?.isSelected =
                                    true
                            }
                            adapter.submitList(addressbook)
                        } else {
                            adapter.submitList(null)
                        }
                    } ?: adapter.submitList(null)
                }
                Status.ERROR -> {
                    val menusAllFragment = ButtomSheetNotif(
                        it.message,
                        NotifType.NOTIF_ERROR
                    )
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                }
                else -> {}
            }
        }
    }

    private fun observeSavedAddressBook() {
        viewModel.savedAddressBook.observe(this) {
            it?.let { address ->
               savedBookAddressId = address.bookAddressId
            }
        }
    }
}