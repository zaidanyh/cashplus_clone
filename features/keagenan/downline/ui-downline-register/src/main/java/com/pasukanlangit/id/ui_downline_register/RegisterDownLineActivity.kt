package com.pasukanlangit.id.ui_downline_register

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.DrawablePosition.DRAWABLE_RIGHT
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyNumber
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyWords
import com.pasukanlangit.id.core.utils.InputUtil.inputAlphabetFilter
import com.pasukanlangit.id.core.utils.InputUtil.inputNumericFilter
import com.pasukanlangit.id.core.utils.InputUtil.isValidEmail
import com.pasukanlangit.id.core.utils.InputUtil.passwordValidate
import com.pasukanlangit.id.core.utils.InputUtil.validPrefixNumber
import com.pasukanlangit.id.core.utils.setDropDownView
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.domain_downline.model.LocationNameResponse
import com.pasukanlangit.id.ui_downline_register.databinding.ActivityRegisterDownlineBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterDownLineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterDownlineBinding
    private val viewModel: RegisterDownLineViewModel by viewModel()

    private lateinit var provinces: List<LocationNameResponse>
    private lateinit var cities: List<LocationNameResponse>
    private lateinit var subDistricts: List<LocationNameResponse>
    private lateinit var villages: List<LocationNameResponse>

    private var provinceId: String? = null
    private var cityId: String? = null
    private var subDistrictsId: String? = null
    private var villageId: String? = null
    private var stateFullName = false
    private var statePhone = false
    private var stateOwner = false
    private var stateEmail = false
    private var statePassword = false
    private var stateRepeatPassword = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDownlineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            appbar.setOnBackPressed { finish() }
            provinceList.setAdapter(
                setDropDownView(
                    this@RegisterDownLineActivity,
                    listOf(getString(R.string.choose_province))
                )
            )
            districtList.setAdapter(
                setDropDownView(
                    this@RegisterDownLineActivity,
                    listOf(getString(R.string.choose_province_first))
                )
            )
            subDistrictList.setAdapter(
                setDropDownView(
                    this@RegisterDownLineActivity,
                    listOf(getString(R.string.choose_city_first))
                )
            )
            villageList.setAdapter(
                setDropDownView(
                    this@RegisterDownLineActivity,
                    listOf(getString(R.string.choose_sub_district_first))
                )
            )
            edtAccountName.filters = arrayOf(InputFilter.LengthFilter(30), inputAlphabetFilter)
            edtAccountName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputAccountName.error = getString(R.string.input_required, getString(R.string.account_name))
                        stateFullName = false
                        return
                    }
                    if (!input.checkIsOnlyWords()) {
                        inputAccountName.error = getString(R.string.input_check_only_word_error, getString(R.string.account_name))
                        stateFullName = false
                        return
                    }
                    inputAccountName.error = null
                    stateFullName = true
                }
                override fun afterTextChanged(s: Editable?) {
                    btnRegister.isEnabled = stateFullName && statePhone && stateOwner && stateEmail && statePassword && stateRepeatPassword
                }
            })
            edtPhoneNumber.filters = arrayOf(InputFilter.LengthFilter(16), inputNumericFilter)
            edtPhoneNumber.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val inputNumber = s.toString()
                    if (inputNumber.isEmpty()) {
                        inputPhoneNumber.error = getString(R.string.input_required, getString(R.string.phone_number))
                        statePhone = false
                        return
                    }
                    if (inputNumber.length < 9) {
                        inputPhoneNumber.error = getString(R.string.input_min_length_required, getString(R.string.phone_number), 9)
                        statePhone = false
                        return
                    }
                    if (!inputNumber.checkIsOnlyNumber()) {
                        inputPhoneNumber.error = getString(R.string.number_or_code_not_valid)
                        statePhone = false
                        return
                    }
                    inputPhoneNumber.error = null
                    statePhone = true
                }
                override fun afterTextChanged(s: Editable?) {
                    btnRegister.isEnabled = stateFullName && statePhone && stateOwner && stateEmail && statePassword && stateRepeatPassword
                }
            })
            edtNameOwner.filters = arrayOf(InputFilter.LengthFilter(16), inputAlphabetFilter)
            edtNameOwner.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputNameOwner.error = getString(R.string.input_required, getString(R.string.name_owner))
                        stateOwner = false
                        return
                    }
                    if (!input.checkIsOnlyWords()) {
                        inputNameOwner.error = getString(R.string.input_check_only_word_error, getString(R.string.name_owner))
                        stateOwner = false
                        return
                    }
                    inputNameOwner.error = null
                    stateOwner = true
                }
                override fun afterTextChanged(s: Editable?) {
                    btnRegister.isEnabled = stateFullName && statePhone && stateOwner && stateEmail && statePassword && stateRepeatPassword
                }
            })
            edtEmail.filters = arrayOf(InputFilter.LengthFilter(32))
            edtEmail.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputEmail.error = getString(R.string.input_email_is_empty)
                        stateEmail = false
                        return
                    }
                    if (!input.isValidEmail()) {
                        inputEmail.error = getString(R.string.input_email_not_valid)
                        stateEmail = false
                        return
                    }
                    inputEmail.error = null
                    stateEmail = true
                }
                override fun afterTextChanged(s: Editable?) {
                    btnRegister.isEnabled = stateFullName && statePhone && stateOwner && stateEmail && statePassword && stateRepeatPassword
                }
            })
            edtPassword.filters = arrayOf(InputFilter.LengthFilter(16))
            edtPassword.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    val result = passwordValidate(this@RegisterDownLineActivity, input)
                    if (input.isEmpty()) {
                        inputPassword.error = getString(R.string.input_required, getString(R.string.password))
                        statePassword = false
                        return
                    }
                    if (result.isError) {
                        inputPassword.error = result.message
                        statePassword = false
                        return
                    }
                    inputPassword.error = null
                    statePassword = true
                }
                override fun afterTextChanged(s: Editable?) {
                    btnRegister.isEnabled = stateFullName && statePhone && stateOwner && stateEmail && statePassword && stateRepeatPassword
                }
            })
            edtRepeatPassword.filters = arrayOf(InputFilter.LengthFilter(16))
            edtRepeatPassword.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    val password = edtPassword.text.toString()
                    if (input.isEmpty()) {
                        inputRepeatPassword.error = getString(R.string.input_confirm_password_required)
                        stateRepeatPassword = false
                        return
                    }
                    if (password != input) {
                        inputRepeatPassword.error = getString(R.string.input_confirm_password_not_same)
                        stateRepeatPassword = false
                        return
                    }
                    inputRepeatPassword.error = null
                    stateRepeatPassword = true
                }
                override fun afterTextChanged(s: Editable?) {
                    btnRegister.isEnabled = stateFullName && statePhone && stateOwner && stateEmail && statePassword && stateRepeatPassword
                }
            })

            provinceList.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (provinceList.right - provinceList.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        provinceList.showDropDown()
                        return@setOnTouchListener true
                    }
                }
                false
            }
            provinceList.setOnItemClickListener { _, _, position, _ ->
                if (::provinces.isInitialized) {
                    provinceId = provinces[position].id
                    districtList.text?.clear()
                    subDistrictList.text?.clear()
                    villageList.text?.clear()
                    viewModel.onTriggerEvent(RegisterDownLineEvent.GetCity(provinceId = provinceId.toString()))
                }
            }
            districtList.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (districtList.right - districtList.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        districtList.showDropDown()
                        return@setOnTouchListener true
                    }
                }
                false
            }
            districtList.setOnItemClickListener { _, _, position, _ ->
                if (::cities.isInitialized) {
                    cityId = cities[position].id
                    subDistrictList.text?.clear()
                    villageList.text?.clear()
                    viewModel.onTriggerEvent(RegisterDownLineEvent.GetDistrict(cityId = cityId.toString()))
                }
            }
            subDistrictList.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (subDistrictList.right - subDistrictList.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        subDistrictList.showDropDown()
                        return@setOnTouchListener true
                    }
                }
                false
            }
            subDistrictList.setOnItemClickListener { _, _, position, _ ->
                if (::subDistricts.isInitialized) {
                    subDistrictsId = subDistricts[position].id
                    villageList.text?.clear()
                    viewModel.onTriggerEvent(RegisterDownLineEvent.GetVillage(districtId = subDistrictsId.toString()))
                }
            }
            villageList.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (villageList.right - villageList.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        villageList.showDropDown()
                        return@setOnTouchListener true
                    }
                }
                false
            }
            villageList.setOnItemClickListener { _, _, position, _ ->
                if (::villages.isInitialized) {
                    villageId = villages[position].id
                }
            }
            btnRegister.setUpToProgressButton(this@RegisterDownLineActivity)
            btnRegister.setOnClickListener {
                submitRegister()
            }
        }
        collectState()
    }

    private fun submitRegister() {
        with(binding) {
            val accountName = edtAccountName.text.toString().trim()
            val phoneNumber = edtPhoneNumber.text.toString().trim()
            val ownerName = edtNameOwner.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()
            val confirmPassword = edtRepeatPassword.text.toString()
            val address = edtAddress.text.toString().trim()
            val postalCode = edtPostalCode.text.toString().trim()

            if (!phoneNumber.validPrefixNumber()) {
                inputPhoneNumber.error = getString(R.string.any_input_is_not_valid, getString(R.string.phone_number))
                scrolllayout.smoothScrollTo(0,0)
                return
            }
            if (password != confirmPassword) {
                inputRepeatPassword.error = getString(R.string.input_confirm_password_not_same)
                return
            }
            if (postalCode.isNotEmpty()) {
                if (postalCode.length < 5) {
                    inputPostalCode.error = getString(R.string.input_min_length_required, getString(R.string.postal_code), 5)
                    scrolllayout.smoothScrollTo(0,0)
                    return
                }
            }
            viewModel.onTriggerEvent(
                RegisterDownLineEvent.RegisterDownLine(
                    phone = phoneNumber, fullName = accountName, ownerName = ownerName, email = email,
                    password = password, address = address, prov = if (provinceId.isNullOrEmpty()) "" else provinceId.toString(),
                    city = if (cityId.isNullOrEmpty()) "" else cityId.toString(),
                    district = if (subDistrictsId.isNullOrEmpty()) "" else subDistrictsId.toString(),
                    village = if (villageId.isNullOrEmpty()) "" else villageId.toString(),
                    zipcode = postalCode
                )
            )
        }
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoadingButton.collectLatest { isLoading ->
                        binding.btnRegister.isEnabled = !isLoading
                        if (isLoading) {
                            binding.btnRegister.showLoading()
                            return@collectLatest
                        }
                        binding.btnRegister.hideProgress(getString(R.string.downline_register))
                    }
                }
                launch {
                    viewModel.downlineRegister.collectLatest {
                        it?.let { response ->
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.success))
                                .description(response.message)
                                .image(R.drawable.ilustration_warning)
                                .buttonNegative(
                                    NegativeButtonAction(
                                        btnLabel = getString(R.string.close),
                                        onBtnClicked =  {
                                            this@RegisterDownLineActivity.finish()
                                        }
                                    )
                                ).show(supportFragmentManager)
                        }
                    }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@RegisterDownLineActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(RegisterDownLineEvent.RemoveHeadMessage)
                        }
                    }
                }

                launch {
                    viewModel.provinces.collectLatest { response ->
                        val provinces = mutableListOf<String>()
                        if (!response.isNullOrEmpty()) {
                            this@RegisterDownLineActivity.provinces = response
                            response.forEach { data ->
                                provinces.add(data.name)
                            }
                            val adapter: ArrayAdapter<String> = ArrayAdapter(
                                this@RegisterDownLineActivity,
                                R.layout.item_spinner_small_selected,
                                provinces
                            )
                            adapter.setDropDownViewResource(R.layout.item_spinner_small)
                            binding.provinceList.setAdapter(adapter)
                        }
                    }
                }
                launch {
                    viewModel.cities.collectLatest { response ->
                        val cities = mutableListOf<String>()
                        if (!response.isNullOrEmpty()) {
                            this@RegisterDownLineActivity.cities = response
                            response.forEach { data ->
                                cities.add(data.name)
                            }
                            val adapter: ArrayAdapter<String> = ArrayAdapter(
                                this@RegisterDownLineActivity, R.layout.item_spinner_small_selected,
                                cities
                            )
                            adapter.setDropDownViewResource(R.layout.item_spinner_small)
                            binding.districtList.setAdapter(adapter)
                        }
                    }
                }
                launch {
                    viewModel.districts.collectLatest { response ->
                        val subDistricts = mutableListOf<String>()
                        if (!response.isNullOrEmpty()) {
                            this@RegisterDownLineActivity.subDistricts = response
                            response.forEach { data ->
                                subDistricts.add(data.name)
                            }
                            val adapter: ArrayAdapter<String> = ArrayAdapter(
                                this@RegisterDownLineActivity, R.layout.item_spinner_small_selected,
                                subDistricts
                            )
                            adapter.setDropDownViewResource(R.layout.item_spinner_small)
                            binding.subDistrictList.setAdapter(adapter)
                        }
                    }
                }
                launch {
                    viewModel.villages.collectLatest { response ->
                        val villages = mutableListOf<String>()
                        if (!response.isNullOrEmpty()) {
                            this@RegisterDownLineActivity.villages = response
                            response.forEach { data ->
                                villages.add(data.name)
                            }
                            val adapter: ArrayAdapter<String> = ArrayAdapter(
                                this@RegisterDownLineActivity, R.layout.item_spinner_small_selected,
                                villages
                            )
                            adapter.setDropDownViewResource(R.layout.item_spinner_small)
                            binding.villageList.setAdapter(adapter)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(RegisterDownLineEvent.GetProvinces)
        binding.btnRegister.isEnabled = stateFullName && statePhone && stateOwner && stateEmail && statePassword && stateRepeatPassword
    }
}