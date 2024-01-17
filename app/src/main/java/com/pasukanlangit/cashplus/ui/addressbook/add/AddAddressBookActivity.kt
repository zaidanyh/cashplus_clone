package com.pasukanlangit.cashplus.ui.addressbook.add

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.ArrayAdapter
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityAddAddressBookBinding
import com.pasukanlangit.cashplus.model.request_body.AddAddressBookRequest
import com.pasukanlangit.cashplus.model.request_body.CityShipmentRequest
import com.pasukanlangit.cashplus.model.request_body.SubdistrictShipmentRequest
import com.pasukanlangit.cashplus.model.response.*
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.*
import com.pasukanlangit.id.library_core.domain.model.NotifType

import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.StringBuilder

data class CityShipmentSelected(
    val type: String,
    val name: String
)

@SuppressLint("ClickableViewAccessibility")
class AddAddressBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAddressBookBinding
    private val viewModel: AddUpdateAddressBookViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var uuid: String ?= null
    private var token: String ?= null

    private var isUpdate = false
    private lateinit var provinceResponse: ProvinceShipmentResponse
    private lateinit var cityResponse: CityShipmentResponse
    private lateinit var subDistrictResponse: SubdistrictShipmentResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uuid = sharedPref.getUUID()
        token = sharedPref.getAccessToken()

        val currentAddressBook = intent.parcelable<AddressBookData>(KEY_ADDRESS_BOOK)

        if(!uuid.isNullOrEmpty() && !token.isNullOrEmpty()){
            viewModel.getProvinces(uuid!!, token!!)
        }

        if(currentAddressBook != null){
            isUpdate = true
            bindDataToInput(currentAddressBook)
        }

        binding.appbar.setOnBackPressed { finish() }
        binding.btnAddUpdate.setUpToProgressButton(this)
        viewModel.province.observe(this) {
            if (it.status == Status.SUCCESS) {
                it.data?.let { provinces ->
                    provinceResponse = provinces

                    val listProvinces = arrayListOf<String>()
                    provinces.data.forEach { province ->
                        if(currentAddressBook != null && currentAddressBook.provinceId.isNotEmpty() && currentAddressBook.provinceId == province.provinceId) binding.provinceList.setText(province.province)
                        listProvinces.add(province.province)
                    }
                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line, listProvinces
                    )
                    binding.provinceList.setAdapter(adapter)
                }
            }
        }

        viewModel.city.observe(this) {
            if(it == null){
                binding.cityList.setAdapter(null)
            }
            if (it?.status == Status.SUCCESS) {
                it.data?.let { cities ->
                    cityResponse = cities

                    val listCity = arrayListOf<String>()
                    cities.data.forEach { city ->
                        if(currentAddressBook != null && currentAddressBook.cityId.isNotEmpty() && currentAddressBook.cityId == city.cityId) binding.cityList.setText(city.cityName)
                        listCity.add("${city.type} ${city.cityName}")
                    }
                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line, listCity
                    )

                    binding.cityList.setAdapter(adapter)
                }
            }
        }
        with(binding) {
            viewModel.subdistrict.observe(this@AddAddressBookActivity){
                if(it == null){
                    binding.subdistrictList.setAdapter(null)
                }

                if (it?.status == Status.SUCCESS) {
                    it.data?.let { subDistricts ->
                        subDistrictResponse = subDistricts

                        val listSubDistrict = arrayListOf<String>()
                        subDistricts.data.forEach { subDistrict ->
                            if(currentAddressBook != null && currentAddressBook.subdistrictId.isNotEmpty() && currentAddressBook.subdistrictId == subDistrict.subdistrictId) subdistrictList.setText(subDistrict.subdistrictName)
                            listSubDistrict.add(subDistrict.subdistrictName)
                        }
                        val adapter: ArrayAdapter<String> = ArrayAdapter(
                            root.context,
                            android.R.layout.simple_dropdown_item_1line, listSubDistrict
                        )

                        subdistrictList.setAdapter(adapter)
                    }
                }
            }

            viewModel.addressAddedOrUpdated.observe(this@AddAddressBookActivity) {
                when (it.status) {
                    Status.SUCCESS -> {
                        btnAddUpdate.hideProgress(getString(R.string.add_label))
                        if (it.data != null) {
                            val menusAllFragment = ButtomSheetNotif(getString(R.string.successfully_add_message), NotifType.NOTIF_SUCCESS)
                            menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)

                            Handler(Looper.getMainLooper()).postDelayed({
                                finish()
                            }, TIME_SHOW_NOTIF)
                        }
                    }
                    Status.LOADING -> {
                        btnAddUpdate.showLoading()
                    }
                    Status.ERROR -> {
                        val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                        menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                        btnAddUpdate.hideProgress(getString(R.string.add_label))
                    }
                }
            }
        }

        binding.btnAddUpdate.setOnClickListener {
            if(!uuid.isNullOrEmpty() && !token.isNullOrEmpty()){
                with(binding) {
                    val name = edtName.text.toString().trim()
                    val phone = edtPhone.text.toString().trim()
                    val type = edtType.text.toString().trim()
                    val address = edtFullAddress.text.toString().trim()
                    val postalCode = edtPostalCode.text.toString().trim()
                    val provinceName = provinceList.text.toString()
                    val citySelected = getCitySelected(cityList.text.toString())
//                    val cityType = if(citySelected.size > 1) citySelected[0].trim() else ""
//                    val cityName = if(citySelected.size > 1) citySelected[1].trim() else ""
                    val subdistrictName = subdistrictList.text.toString()
                    val isMainAddress = if(cbMainAddress.isChecked) "1" else "0"


                    var provinceItem : ProfinceShipmentDataItem ?= null
                    var cityItem : CityShipmentDataItem ?= null
                    var subdistrictItem : SubdistrictShipmentDataItem ?= null

                    if(::provinceResponse.isInitialized) provinceResponse.data.forEach lit@{  province ->
                        if(province.province == provinceName){
                            provinceItem = province
                            return@lit
                        }
                    }

                    if(::cityResponse.isInitialized) cityResponse.data.forEach lit@{  city ->
                        if(city.cityName == citySelected.name && city.type == citySelected.type){
                            cityItem = city
                            return@lit
                        }
                    }

                    if(::subDistrictResponse.isInitialized) subDistrictResponse.data.forEach lit@{ subDistrict ->
                        if(subDistrict.subdistrictName == subdistrictName){
                            subdistrictItem = subDistrict
                            return@lit
                        }
                    }


                    if(name.isEmpty() || phone.isEmpty() || type.isEmpty() || address.isEmpty() || postalCode.isEmpty()){
                        val menusAllFragment = ButtomSheetNotif(getString(R.string.input_cannot_empty), NotifType.NOTIF_INPUT_UNCOMPLETED)
                        menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                    }else if(provinceItem == null || cityItem == null || subdistrictItem == null){
                        val menusAllFragment = ButtomSheetNotif(getString(R.string.address_selector_not_valid), NotifType.NOTIF_INPUT_UNCOMPLETED)
                        menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                    }else{
                        val addressBookData: AddAddressBookRequest = if(isUpdate && currentAddressBook != null){
                            AddAddressBookRequest(address,type,provinceItem!!.provinceId,postalCode,name,phone,isMainAddress,uuid!!,cityItem!!.cityId,subdistrictItem!!.subdistrictId, currentAddressBook.bookAddressId)
                        }else{
                            AddAddressBookRequest(address,type,provinceItem!!.provinceId,postalCode,name,phone,isMainAddress,uuid!!,cityItem!!.cityId, subdistrictItem!!.subdistrictId)
                        }

                        viewModel.addAddressBook(addressBookData, token!!, (isUpdate && currentAddressBook != null))
                    }
                }
            }
        }

        setOnTouchProvince()
        setOnTouchCity()
        setOnTouchSubDistrict()
//        setUpBtnAdd()
    }

    private fun setOnTouchSubDistrict() {
        with(binding.subdistrictList) {
            setOnTouchListener { _, event ->
                
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (right - compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                        showDropDown()
                    }
                }
                false
            }
        }
    }

    private fun bindDataToInput(currentAddressBook: AddressBookData) {
        with(binding) {
            edtName.setText(currentAddressBook.receiverName)
            edtPhone.setText(currentAddressBook.phoneNumber)
            edtType.setText(currentAddressBook.addressType)
            edtFullAddress.setText(currentAddressBook.address)
            edtPostalCode.setText(currentAddressBook.posCode)
            cbMainAddress.isChecked = currentAddressBook.isMainAddress == "1"
            btnAddUpdate.text = getString(R.string.change_label)
        }

        if(!uuid.isNullOrEmpty() && !token.isNullOrEmpty()) {
            binding.cityList.text.clear()
            viewModel.getCity(CityShipmentRequest(currentAddressBook.provinceId, uuid!!), token!!)
            viewModel.getSubDistrict(SubdistrictShipmentRequest(uuid!!, currentAddressBook.cityId ), token!!)
        }
    }


    private fun setOnTouchCity() {
        with(binding.cityList) {
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (right - compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                        showDropDown()
                    }
                }
                false
            }
            setOnItemClickListener { _, _, _, _ ->
                if(!uuid.isNullOrEmpty() && !token.isNullOrEmpty()) {
//                    val citySelected = text.toString().split(" ")
//                    val cityType = citySelected[0].trim()
//                    val cityName = StringBuilder()
//                    for(i in 1 until citySelected.size){
//                        cityName.append(citySelected[i])
//                        cityName.append(" ")
//                    }
                    val citySelected = getCitySelected(text.toString())
                    val subDistrictRequest = SubdistrictShipmentRequest(
                        uuid!!,
                        cityResponse.data.singleOrNull { city -> city.cityName == citySelected.name && city.type == citySelected.type }?.cityId,
                    )

                    binding.subdistrictList.text.clear()

                    viewModel.resetSubdistrict()
                    viewModel.getSubDistrict(subDistrictRequest, token!!)
                }
            }
        }

    }


    private fun setOnTouchProvince() {
        with(binding.provinceList) {
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (right - compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                        showDropDown()
                    }
                }
                false
            }

            setOnItemClickListener { _, _, _, _ ->
                if(!uuid.isNullOrEmpty() && !token.isNullOrEmpty()) {
                    val textSelected = text.toString()
                    val cityShipmentRequest = CityShipmentRequest(
                        provinceResponse.data.single { prov -> prov.province == textSelected }.provinceId,
                        uuid!!
                    )

                    binding.cityList.text.clear()
                    binding.subdistrictList.text.clear()

                    viewModel.resetCity()
                    viewModel.resetSubdistrict()

                    viewModel.getCity(cityShipmentRequest, token!!)
                }
            }
        }
    }

    /**
     * this function extract city type and city name from dropdown selected
     * */
    private fun getCitySelected(city: String): CityShipmentSelected {
        val citySelected = city.split(" ")
        val cityType = citySelected[0].trim()
        val cityName = StringBuilder()
        for(i in 1 until citySelected.size){
            cityName.append(citySelected[i])
            cityName.append(" ")
        }

        return CityShipmentSelected(
            type = cityType,
            name = cityName.toString().trim()
        )
    }

    companion object {
        const val KEY_ADDRESS_BOOK ="KEY_ADDRESS_BOOK"
    }
}
