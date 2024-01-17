package com.pasukanlangit.cashplus.ui.pages.others.settings.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View.OnFocusChangeListener
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityChangeProfilBinding
import com.pasukanlangit.cashplus.model.request_body.*
import com.pasukanlangit.cashplus.model.response.*
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.pages.others.settings.profile.close.CloseAccountActivity
import com.pasukanlangit.cashplus.utils.*
import com.pasukanlangit.cashplus.view_model.ChangeProfileViewModel
import com.pasukanlangit.id.core.CHANGE_PROFILE_FROM_EKYC
import com.pasukanlangit.id.core.PROFILE_KEY_DATA
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.core.presentation.components.*
import com.pasukanlangit.id.core.utils.*
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyWords
import com.pasukanlangit.id.core.utils.InputUtil.inputAllTypeWithSomeSpecialChars
import com.pasukanlangit.id.core.utils.InputUtil.inputAlphabetFilter
import com.pasukanlangit.id.core.utils.InputUtil.inputCharNumberFilter
import com.pasukanlangit.id.core.utils.InputUtil.inputNumericFilter
import com.pasukanlangit.id.library_core.domain.model.NotifType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class ChangeProfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeProfilBinding
    private val changeProfileViewModel: ChangeProfileViewModel by viewModel()
    private val sharedPrefDataSource : SharedPrefDataSource by inject()
    private val uuid get() = sharedPrefDataSource.getUUID()
    private val token get() = sharedPrefDataSource.getAccessToken()

    private lateinit var provinceResponse: ProvinceResponse
    private lateinit var districtsResponse: DistrictsResponse
    private lateinit var subDistrictsResponse: SubdistrictsResponse
    private lateinit var villageResponse: VillageResponse

    private var calendar = Calendar.getInstance()
    private lateinit var sdf: SimpleDateFormat
    private val sdfReq = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
//    private var dateRequest: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { onBackDialog() }
        })
        sdf = SimpleDateFormat("dd MMMM yyyy", Locale(getString(com.pasukanlangit.id.travelling.flight.R.string.locale_language), getString(
            com.pasukanlangit.id.travelling.flight.R.string.locale_country)))
        val currentProfile = intent.parcelable<ProfileResponse>(PROFILE_KEY_DATA)
        if (!currentProfile?.dateOfBirth.isNullOrEmpty())
            calendar.time = sdfReq.parse(currentProfile?.dateOfBirth.toString()) as Date

        with(binding) {
            btnUpdateProfile.setUpToProgressButton(this@ChangeProfilActivity)
            provinceList.setAdapter(
                ArrayAdapter(
                    this@ChangeProfilActivity,
                    R.layout.item_spinner_small_selected,
                    arrayOf(getString(R.string.choose_province))
                )
            )
            districtList.setAdapter(
                ArrayAdapter(
                    this@ChangeProfilActivity,
                    R.layout.item_spinner_small_selected,
                    arrayOf(getString(R.string.choose_province_first))
                )
            )
            subdistrictList.setAdapter(
                ArrayAdapter(
                    this@ChangeProfilActivity,
                    R.layout.item_spinner_small_selected,
                    arrayOf(getString(R.string.choose_city_first))
                )
            )
            villageList.setAdapter(
                ArrayAdapter(
                    this@ChangeProfilActivity,
                    R.layout.item_spinner_small_selected,
                    arrayOf(getString(R.string.choose_sub_district_first))
                )
            )

            if (currentProfile != null && uuid != null) {
                edtNikProfile.setText(currentProfile.nik)
                edtNikProfile.isEnabled = currentProfile.is_kyc_approved != "1"
                edtNikProfile.filters = arrayOf(InputFilter.LengthFilter(16), inputNumericFilter)
                edtNameChangeprofile.setText(currentProfile.full_name)
                edtNameChangeprofile.isEnabled = currentProfile.is_kyc_approved != "1"
                edtNameChangeprofile.filters = arrayOf(InputFilter.LengthFilter(30), inputAlphabetFilter)
                edtOwnerName.setText(currentProfile.owner_name)
                edtOwnerName.isEnabled = currentProfile.is_kyc_approved != "1"
                edtOwnerName.filters = arrayOf(InputFilter.LengthFilter(30), inputAlphabetFilter)
                edtAddressChangeprofile.setText(currentProfile.address)
                edtAddressChangeprofile.filters = arrayOf(InputFilter.LengthFilter(160), inputAllTypeWithSomeSpecialChars)
                edtPostalCode.setText(currentProfile.zipcode)
                edtPlaceOfBorn.setText(currentProfile.placeOfBorn)
                edtPlaceOfBorn.filters = arrayOf(InputFilter.LengthFilter(32), inputAlphabetFilter)
                edtDateOfBirth.filters = arrayOf(InputFilter.LengthFilter(16), inputCharNumberFilter)

                if (currentProfile.dateOfBirth.isNotEmpty()) {
                    val parseBirthDay = sdfReq.parse(currentProfile.dateOfBirth)
                    val formatBirthDay = sdf.format(parseBirthDay as Date)
                    edtDateOfBirth.setText(formatBirthDay)
                }
                edtDateOfBirth.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                     if (hasFocus) openDateDialog()
                }
                edtDateOfBirth.setOnClickListener { openDateDialog() }

                if (currentProfile.is_kyc_approved == "1") {
                    inputNikProfile.error = getString(R.string.cant_change_nik)
                    inputNameChangeprofile.error = getString(R.string.cant_change_name)
                    inputOwnerName.error = getString(R.string.cant_change_owner)
                }

                if (currentProfile.prov.isNotEmpty()) changeProfileViewModel.getProvinces(
                    ProfileRequest(uuid ?: "")
                )
                if (currentProfile.city.isNotEmpty()) changeProfileViewModel.getDistricts(
                    DistrictRequest(currentProfile.prov, uuid ?: "")
                )
                if (currentProfile.district.isNotEmpty()) changeProfileViewModel.getSubDistricts(
                    SubdistrictRequest(currentProfile.city, uuid ?: "")
                )
                if (currentProfile.village.isNotEmpty()) changeProfileViewModel.getVillage(
                    VillageRequest(currentProfile.district, uuid ?: "")
                )
            }

            if (!uuid.isNullOrEmpty() && !token.isNullOrEmpty()) {
                val request = ProfileRequest(uuid ?: "")

                changeProfileViewModel.getProvinces(request)

                var districtsItem: DistrictsItem? = null
                var subDistrictsItem: SubdistrictItem? = null
                var villageItem: VillageItem? = null
                var provinceItem: ProvinceItem? = null

                btnCloseAccount.setOnClickListener {
                    val intent = Intent(this@ChangeProfilActivity, CloseAccountActivity::class.java).apply {
                        putExtra(PROFILE_KEY_DATA, currentProfile)
                    }
                    startActivity(intent)
                }
                btnUpdateProfile.setOnClickListener {
                    val nik = edtNikProfile.text.toString().trim()
                    val name = edtNameChangeprofile.text.toString().trim()
                    val ownerName = edtOwnerName.text.toString().trim()
                    val placeOfBorn = edtPlaceOfBorn.text.toString().trim()
                    val dateOfBorn = edtDateOfBirth.text.toString().trim()
                    val address = edtAddressChangeprofile.text.toString().trim()
                    val postalCode = edtPostalCode.text.toString().trim()

                    if (nik.isNotEmpty()) {
                        if (nik.length < 16) {
                            inputNikProfile.error = getString(R.string.length_of_nik_required)
                            scrollLayout.smoothScrollTo(0, 0)
                            return@setOnClickListener
                        }
                    }

                    if(name.isEmpty()){
                        inputNameChangeprofile.error = getString(R.string.input_name_account_required)
                        scrollLayout.smoothScrollTo(0, 0)
                        return@setOnClickListener
                    }

                    if (ownerName.isEmpty()) {
                        inputOwnerName.error = getString(R.string.input_name_owner_required)
                        scrollLayout.smoothScrollTo(0, 0)
                        return@setOnClickListener
                    }

                    if(!name.checkIsOnlyWords()) {
                        inputNameChangeprofile.error = getString(R.string.input_contains_number_or_symbol)
                        scrollLayout.smoothScrollTo(0, 0)
                        return@setOnClickListener
                    }

                    if(!ownerName.checkIsOnlyWords()) {
                        inputOwnerName.error = getString(R.string.input_contains_number_or_symbol)
                        scrollLayout.smoothScrollTo(0, 0)
                        return@setOnClickListener
                    }

                    if (placeOfBorn.isEmpty()) {
                        inputPlaceOfBorn.error = getString(R.string.input_required, getString(R.string.place_of_birth))
                        scrollLayout.smoothScrollTo(0, 0)
                        return@setOnClickListener
                    }

                    if (dateOfBorn.isEmpty()) {
                        inputDateOfBirth.error = getString(R.string.input_required, getString(R.string.date_birth))
                        scrollLayout.smoothScrollTo(0, 0)
                        return@setOnClickListener
                    }
                    val parseDateOfBorn = sdf.parse(dateOfBorn)
                    val dateRequest = sdfReq.format(parseDateOfBorn as Date)
                    if (dateRequest.isNullOrEmpty()) {
                        inputDateOfBirth.error = getString(R.string.date_of_birth_not_valid)
                        scrollLayout.smoothScrollTo(0, 0)
                        return@setOnClickListener
                    }

                    if (::districtsResponse.isInitialized) {
                        districtsResponse.data.forEach lit@{ district ->
                            if (district.name.equals(districtList.text.toString(), ignoreCase = true)) {
                                districtsItem = district
                                return@lit
                            }
                        }
                    }

                    if (::subDistrictsResponse.isInitialized) {
                        subDistrictsResponse.data.forEach lit@{ subDistricts ->
                            if (subDistricts.name.equals(subdistrictList.text.toString(), ignoreCase = true)) {
                                subDistrictsItem = subDistricts
                                return@lit
                            }
                        }
                    }

                    if (::villageResponse.isInitialized) {
                        villageResponse.data.forEach lit@{ village ->
                            if (village.name.equals(villageList.text.toString(), ignoreCase = true)) {
                                villageItem = village
                                return@lit
                            }
                        }
                    }

                    if (::provinceResponse.isInitialized) {
                        provinceResponse.province.forEach lit@{ prov ->
                            if (prov.name.equals(provinceList.text.toString(), ignoreCase = true)) {
                                provinceItem = prov
                                return@lit
                            }
                        }
                    }

                    if (postalCode.isNotEmpty()) {
                        if (postalCode.length < 5) {
                            showModalError(getString(R.string.input_min_length_required, getString(R.string.postal_code), 5))
                            return@setOnClickListener
                        }
                    }

                    when {
                        provinceItem == null -> showModalError("Provinsi yang kamu masukkan tidak valid")
                        districtsItem == null -> showModalError("Kabupaten/Kota yang kamu masukkan tidak valid")
                        subDistrictsItem == null -> showModalError("Kecamatan yang kamu masukkan tidak valid")
                        villageItem == null -> showModalError("Kelurahan/Desa yang kamu masukkan tidak valid")
                        else -> {
                            inputNameChangeprofile.error = null
                            inputOwnerName.error = null

                            val updateProfileRequest = UpdateProfilRequest(
                                uuid = uuid ?: "", fullName = name, prov = provinceItem?.id.toString(),
                                city = districtsItem?.id.toString(), district = subDistrictsItem?.id.toString(),
                                village = villageItem?.id.toString(), address = address, zipcode = postalCode,
                                nik = nik, imgUrl = "", owner = ownerName, placeOfBorn = placeOfBorn,
                                dateOfBirth = dateRequest
                            )
                            changeProfileViewModel.updateProfile(updateProfileRequest, token ?: "")
                        }
                    }
                }

                provinceList.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (event.rawX >= (provinceList.right - provinceList.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                            provinceList.showDropDown()
                            return@setOnTouchListener true
                        }
                    }
                    false
                }

                districtList.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (event.rawX >= (districtList.right - districtList.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                            districtList.showDropDown()
                            return@setOnTouchListener true
                        }
                    }
                    false
                }

                subdistrictList.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (event.rawX >= (subdistrictList.right - subdistrictList.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                            subdistrictList.showDropDown()
                            return@setOnTouchListener true
                        }
                    }
                    false
                }

                villageList.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (event.rawX >= (villageList.right - villageList.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                            villageList.showDropDown()
                            return@setOnTouchListener true
                        }
                    }
                    false
                }

                provinceList.setOnItemClickListener { _, _, _, _ ->
                    val textSelected = provinceList.text.toString()
                    val districtRequest = if(::provinceResponse.isInitialized) DistrictRequest(
                        provinceResponse.province.single { prov -> prov.name == textSelected }.id,
                        uuid ?: ""
                    ) else return@setOnItemClickListener

                    districtList.text.clear()
                    subdistrictList.text.clear()
                    villageList.text.clear()

                    with(changeProfileViewModel) {
                        resetDistrict()
                        resetSubDistrict()
                        resetVillages()
                    }
                    changeProfileViewModel.getDistricts(districtRequest)
                }

                districtList.setOnItemClickListener { _, _, _, _ ->
                    val textSelected = districtList.text.toString()
                    val subdistrictRequest = if(::districtsResponse.isInitialized) SubdistrictRequest(
                        districtsResponse.data.single { district -> district.name == textSelected }.id,
                        uuid ?: ""
                    ) else return@setOnItemClickListener

                    subdistrictList.text.clear()
                    villageList.text.clear()

                    with(changeProfileViewModel){
                        resetSubDistrict()
                        resetVillages()
                    }
                    changeProfileViewModel.getSubDistricts(subdistrictRequest)
                }

                subdistrictList.setOnItemClickListener { _, _, _, _ ->
                    val textSelected = subdistrictList.text.toString()
                    val villageRequest =  if(::subDistrictsResponse.isInitialized) VillageRequest(
                        subDistrictsResponse.data.single { subDistrict -> subDistrict.name == textSelected }.id,
                        uuid ?: ""
                    ) else return@setOnItemClickListener

                    villageList.text.clear()
                    changeProfileViewModel.resetVillages()
                    changeProfileViewModel.getVillage(villageRequest)
                }
            }

            changeProfileViewModel.provinces.observe(this@ChangeProfilActivity) {
                if (it.status == Status.SUCCESS) {
                    provinceResponse = it.data!!
                    val listProvinces = arrayListOf<String>()

                    if (it.data.province.isNotEmpty()) {
                        it.data.province.forEach { provinces ->
                            if (currentProfile != null && currentProfile.prov.isNotEmpty() && currentProfile.prov == provinces.id) provinceList.setText(
                                provinces.name
                            )
                            listProvinces.add(provinces.name)
                        }
                    }
                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                        this@ChangeProfilActivity,
                        R.layout.item_spinner_small,
                        listProvinces
                    )
                    provinceList.setAdapter(adapter)
                }
            }

            changeProfileViewModel.districts.observe(this@ChangeProfilActivity) {
                if (it?.status == Status.SUCCESS) {
                    districtsResponse = it.data!!
                    val listDistricts = arrayListOf<String>()

                    if (it.data.data.isNotEmpty()) {
                        it.data.data.forEach { district ->
                            if (currentProfile != null && currentProfile.city.isNotEmpty() && currentProfile.city == district.id) districtList.setText(
                                district.name
                            )
                            listDistricts.add(district.name)
                        }
                    }
                    val districtAdapter: ArrayAdapter<String> = ArrayAdapter(
                        this@ChangeProfilActivity,
                        R.layout.item_spinner_small,
                        listDistricts
                    )
                    districtList.setAdapter(districtAdapter)
                }
            }

            changeProfileViewModel.subDistricts.observe(this@ChangeProfilActivity) {
                if (it?.status == Status.SUCCESS) {
                    subDistrictsResponse = it.data!!
                    val listSubDistricts = arrayListOf<String>()

                    if (it.data.data.isNotEmpty()) {
                        it.data.data.forEach { subDistrict ->
                            if (currentProfile != null && currentProfile.district.isNotEmpty() && currentProfile.district == subDistrict.id) subdistrictList.setText(
                                subDistrict.name
                            )
                            listSubDistricts.add(subDistrict.name)
                        }
                    }
                    val subDistrictsAdapter: ArrayAdapter<String> = ArrayAdapter(
                        this@ChangeProfilActivity,
                        R.layout.item_spinner_small,
                        listSubDistricts
                    )
                    subdistrictList.setAdapter(subDistrictsAdapter)
                }
            }

            changeProfileViewModel.villages.observe(this@ChangeProfilActivity) {
                if (it?.status == Status.SUCCESS) {
                    villageResponse = it.data!!
                    val listVillage = arrayListOf<String>()

                    if (it.data.data.isNotEmpty()) {
                        it.data.data.forEach { village ->
                            if (currentProfile != null && currentProfile.village.isNotEmpty() && currentProfile.village == village.id) villageList.setText(
                                village.name
                            )
                            listVillage.add(village.name)
                        }
                    }
                    val villagesAdapter: ArrayAdapter<String> = ArrayAdapter(
                        this@ChangeProfilActivity,
                        R.layout.item_spinner_small,
                        listVillage
                    )
                    villageList.setAdapter(villagesAdapter)
                }
            }

            edtNameChangeprofile.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if(input.isEmpty()){
                        inputNameChangeprofile.error = "Nama akun tidak boleh kosong"
                        return
                    }

                    if(!input.checkIsOnlyWords()) {
                        inputNameChangeprofile.error = "Nama tidak boleh mengandung angka atau simbol"
                        return
                    }
                    inputNameChangeprofile.error = null
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            edtOwnerName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if(input.isNotEmpty()){
                        if(!input.checkIsOnlyWords()) {
                            inputOwnerName.error = "Nama owner tidak boleh mengandung angka atau simbol"
                            return
                        }
                    }
                    inputOwnerName.error = null
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            appBar.setOnBackPressed { onBackDialog() }
        }
        observeUpdateProfile()
    }

    private fun openDateDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, R.style.DatePickerCustom,
            { _, _, _, _ -> }, year, month, day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(com.pasukanlangit.id.travelling.train.R.string.choose)) { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                val datePicker = datePickerDialog.datePicker

                val dayPicked = datePicker.dayOfMonth
                val monthPicked = datePicker.month
                val yearPicked = datePicker.year
                calendar.set(yearPicked, monthPicked, dayPicked)

                val formatDate = sdf.format(calendar.time)
                binding.edtDateOfBirth.setText(formatDate)
            }
        }
        datePickerDialog.show()
    }

    private fun observeUpdateProfile() {
        changeProfileViewModel.updateProfile.observe(this@ChangeProfilActivity) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.btnUpdateProfile.hideProgress(getString(R.string.save))
                    if (it.data != null) {

                        val menusAllFragment =
                            ButtomSheetNotif("Profil berhasil diubah", NotifType.NOTIF_SUCCESS)
                        menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)

                        lifecycleScope.launch {
                            delay(TIME_SHOW_NOTIF.div(2))
                            if (intent.getBooleanExtra(CHANGE_PROFILE_FROM_EKYC, false)) {
                                val intent = Intent()
                                setResult(RESULT_OK, intent)
                            }
                            finish()
                        }
                    }
                }
                Status.LOADING -> {
                    binding.btnUpdateProfile.showLoading()
                }
                Status.ERROR -> {
                    binding.btnUpdateProfile.hideProgress(getString(R.string.save))

                    val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                }
            }
        }
    }

    private fun showModalError(message: String) {
        GenericModalDialogCashplus.Builder()
            .title(getString(R.string.something_wrong))
            .image(R.drawable.illustration_error)
            .description(message)
            .buttonNegative(
                NegativeButtonAction(
                    btnLabel = getString(R.string.close),
                    backgroundButton = R.drawable.bg_primary_rounded_16,
                    buttonTextColor = Color.parseColor("#F8FAFC"),
                    setClickOnDismiss = true
                )
            ).show(supportFragmentManager, "Show Modal Error")
    }

    private fun onBackDialog() {
        GenericConfirmDialogFragment.Builder()
            .title(getString(R.string.warning))
            .description(getString(R.string.confirm_leave_edit_profile))
            .buttonPositive(
                PositiveButton(
                    backgroundButton = R.drawable.bg_red600_rounded_12,
                    buttonText = getString(R.string.yes),
                    buttonTextColor = Color.parseColor("#F8FAFC"),
                    onBtnAction = {
                        finish()
                    }
                )
            )
            .buttonNegative(
                NegativeButton(
                    backgroundButton = R.drawable.bg_red50_rounded_12,
                    buttonText = getString(R.string.cancel),
                    buttonTextColor = Color.parseColor("#FF6150"),
                    actionDismiss = true
                )
            ).show(supportFragmentManager, "Leave Edit Profile")
    }
}