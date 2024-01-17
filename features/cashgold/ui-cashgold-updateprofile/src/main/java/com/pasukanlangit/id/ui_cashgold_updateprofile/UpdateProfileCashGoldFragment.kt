package com.pasukanlangit.id.ui_cashgold_updateprofile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.extensions.onDone
import com.pasukanlangit.id.core.utils.InputUtil.inputAlphabetFilter
import com.pasukanlangit.id.core.utils.InputUtil.inputNumericFilter
import com.pasukanlangit.id.core.utils.setDropDownView
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.model.UserCashGold
import com.pasukanlangit.id.ui_cashgold_updateprofile.databinding.FragmentUpdateProfileCashGoldBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class UpdateProfileCashGoldFragment : Fragment() {

    private var _binding: FragmentUpdateProfileCashGoldBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateProfileCashGoldViewModel by viewModel()

    private var calender: Calendar = Calendar.getInstance()
    private var currentDay: Int = calender.get(Calendar.DAY_OF_MONTH)
    private var currentMonth: Int = calender.get(Calendar.MONTH)
    private var currentYear: Int = calender.get(Calendar.YEAR)

    private lateinit var datePickerDialog: DatePickerDialog

    private lateinit var genders: List<String>
    private lateinit var statuses: List<String>
    private lateinit var religions: List<String>
    private lateinit var lastEducations: List<String>
    private lateinit var professions: List<String>
    private lateinit var sourceIncome: List<String>
    private lateinit var annualYear: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateProfileCashGoldBinding.inflate(layoutInflater,container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpCalender()
        with(binding) {
            edtName.filters = arrayOf(inputAlphabetFilter)
            edtBornPlace.filters = arrayOf(inputAlphabetFilter)
            edtPhone.filters = arrayOf(inputNumericFilter)
            edtPostalCode.filters = arrayOf(inputNumericFilter)
            edtBornDate.isFocusable = false
            edtBornDate.setOnClickListener {
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

                datePickerDialog.setButton(
                    DialogInterface.BUTTON_POSITIVE,
                    getString(R.string.choose)
                ) { _, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        val datepicker = datePickerDialog.datePicker

                        currentDay = datepicker.dayOfMonth
                        currentMonth = datepicker.month
                        currentYear = datepicker.year

                        calender.set(currentYear, currentMonth, currentDay)
                        datePickerDialog.updateDate(currentYear,currentMonth, currentDay)

                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val formatDate = simpleDateFormat.format(calender.time)
                        edtBornDate.setText(formatDate)
                    }
                }
                datePickerDialog.show()
            }

            autoCompleteProvince.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (autoCompleteProvince.right - autoCompleteProvince.compoundDrawables[2].bounds.width())) {
                        autoCompleteProvince.showDropDown()
                        return@setOnTouchListener true
                    }
                }
                false
            }
            autoCompleteDistrict.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (autoCompleteDistrict.right - autoCompleteDistrict.compoundDrawables[2].bounds.width())) {
                        autoCompleteDistrict.showDropDown()
                        return@setOnTouchListener true
                    }
                }
                false
            }
            autoCompleteSubDistrict.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (autoCompleteSubDistrict.right - autoCompleteSubDistrict.compoundDrawables[2].bounds.width())) {
                        autoCompleteSubDistrict.showDropDown()
                        return@setOnTouchListener true
                    }
                }
                false
            }
            autoCompleteVillage.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (autoCompleteVillage.right - autoCompleteVillage.compoundDrawables[2].bounds.width())) {
                        autoCompleteVillage.showDropDown()
                        return@setOnTouchListener true
                    }
                }
                false
            }
            edtFullAddress.onDone { processSave() }
            btnSave.setUpToProgressButton(viewLifecycleOwner)
            btnSave.setOnClickListener {
                processSave()
            }
        }

        collectState()
        setUpSpinnerData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(UpdateProfileCashGoldEvent.GetProvinces)
    }

    private fun setUpSpinnerData() {
        genders = resources.getStringArray(R.array.item_gender).toList()
        val genderAdapter = setDropDownView(requireContext(), genders)
        genderAdapter.setDropDownViewResource(R.layout.item_spinner_small)

        statuses = resources.getStringArray(R.array.item_status).toList()
        val statusAdapter = setDropDownView(requireContext(), statuses)
        statusAdapter.setDropDownViewResource(R.layout.item_spinner_small)

        religions = resources.getStringArray(R.array.item_religion).toList()
        val religionAdapter = setDropDownView(requireContext(), religions)
        religionAdapter.setDropDownViewResource(R.layout.item_spinner_small)

        lastEducations = resources.getStringArray(R.array.item_last_education).toList()
        val lastEducationAdapter = setDropDownView(requireContext(), lastEducations)
        lastEducationAdapter.setDropDownViewResource(R.layout.item_spinner_small)

        professions = resources.getStringArray(R.array.item_profession).toList()
        val professionAdapter = setDropDownView(requireContext(), professions)
        professionAdapter.setDropDownViewResource(R.layout.item_spinner_small)

        sourceIncome = resources.getStringArray(R.array.item_source_of_income).toList()
        val sourceIncomeAdapter = setDropDownView(requireContext(), sourceIncome)
        sourceIncomeAdapter.setDropDownViewResource(R.layout.item_spinner_small)

        annualYear = resources.getStringArray(R.array.item_annual_income).toList()
        val annualIncomeAdapter = setDropDownView(requireContext(), annualYear)
        annualIncomeAdapter.setDropDownViewResource(R.layout.item_spinner_small)
        with(binding) {
            spinnerGender.adapter = genderAdapter
            spinnerStatus.adapter = statusAdapter
            spinnerReligion.adapter = religionAdapter
            spinnerLastEducation.adapter = lastEducationAdapter
            spinnerProfession.adapter = professionAdapter
            spinnerSourceOfIncome.adapter = sourceIncomeAdapter
            spinnerAnnualIncome.adapter = annualIncomeAdapter
        }
    }

    private fun collectState() {
        collectStateError()
        collectStateLoading()
        collectStateIsSaved()
        collectStateProfile()
        collectStateProfile()
        collectProvinces()
        collectCity()
        collectDistrict()
        collectVillage()
    }

    private fun collectStateProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profile.collectLatest {
                    it?.let { profileData ->
                        with(binding){
                            edtName.setText(profileData.user)
                            edtPhone.setText(profileData.phone)
                            edtBornDate.setText(profileData.dayOfBirth)
                            edtBornPlace.setText(profileData.birthPlace)
                            edtEmail.setText(profileData.email)
                            edtKtp.setText(profileData.identityNumber)
                            edtNpwp.setText(profileData.taxIdentityNumber)

                            genders.forEachIndexed { index, s ->
                                if (s == profileData.gender) spinnerGender.setSelection(index)
                            }
                            statuses.forEachIndexed { index, s ->
                                if (s == profileData.maritalStatus) spinnerStatus.setSelection(index)
                            }
                            religions.forEachIndexed { index, s ->
                                if (s == profileData.religion) spinnerReligion.setSelection(index)
                            }
                            lastEducations.forEachIndexed { index, s ->
                                if (s == profileData.lastEducation) spinnerLastEducation.setSelection(index)
                            }
                            professions.forEachIndexed { index, s ->
                                if (s == profileData.profession) spinnerProfession.setSelection(index)
                            }
                            sourceIncome.forEachIndexed { index, s ->
                                if (s == profileData.incomeSource) spinnerSourceOfIncome.setSelection(index)
                            }
                            annualYear.forEachIndexed { index, s ->
                                if (s == profileData.incomePerYear) spinnerAnnualIncome.setSelection(index)
                            }

                            bindInput(profileData)
                        }
                    }
                }
            }
        }
    }

    private fun bindInput(profileData: UserCashGold) {
        with(binding){
            autoCompleteProvince.setText(profileData.province)
            autoCompleteDistrict.setText(profileData.city)
            autoCompleteSubDistrict.setText(profileData.district)
            autoCompleteVillage.setText(profileData.village)
            edtPostalCode.setText(profileData.zipCode)
            edtFullAddress.setText(profileData.address)
        }

        //it not necessary, because viewmodel will init get province
        if(!profileData.province.isNullOrEmpty()) viewModel.onTriggerEvent(UpdateProfileCashGoldEvent.GetCity(provinces = profileData.province!!))
        if(!profileData.city.isNullOrEmpty()) viewModel.onTriggerEvent(UpdateProfileCashGoldEvent.GetDistrict(city = profileData.city!!))
        if(!profileData.city.isNullOrEmpty() && !profileData.district.isNullOrEmpty()) viewModel.onTriggerEvent(UpdateProfileCashGoldEvent.GetVillage(city = profileData.city!!, district = profileData.district!!))
    }


    private fun collectStateIsSaved() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateIsSaved.collectLatest {
                    it?.let { isSaved ->
                        if(isSaved){
                            Toast.makeText(requireContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun collectStateError() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateError.collectLatest { message ->
                    message.peek()?.let { info ->
                        val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                        toast.show()
                        delay(toast.duration.toLong() + 500L)
                        viewModel.onTriggerEvent(UpdateProfileCashGoldEvent.RemoveHeadQueue)
                    }
                }
            }
        }
    }

    private fun collectStateLoading() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateLoadingButton.collectLatest { isLoading ->
                    binding.btnSave.isEnabled = !isLoading

                    if(isLoading){
                        binding.btnSave.showLoading()
                    }else{
                        binding.btnSave.hideProgress(getString(R.string.simpan))
                    }
                }
            }
        }
    }

    private fun processSave() {
        try{
            with(binding){
                val username = edtName.text.toString().trim()
                val identityKtp = edtKtp.text.toString().trim()
                val gender = spinnerGender.selectedItem.toString()
                val materialStatus = spinnerStatus.selectedItem.toString()
                val placeBirth = edtBornPlace.text.toString().trim()
                val dateBirth = edtBornDate.text.toString().trim()
                val email = edtEmail.text.toString().trim()
                val noTelp = edtPhone.text.toString().trim()
                val religion = spinnerReligion.selectedItem.toString()
                val npwp = edtNpwp.text.toString().trim()
                val lastEducation = spinnerLastEducation.selectedItem.toString()
                val profession = spinnerProfession.selectedItem.toString()
                val incomeSource = spinnerSourceOfIncome.selectedItem.toString()
                val incomeYear = spinnerAnnualIncome.selectedItem.toString()
                val province = autoCompleteProvince.text.toString().trim()
                val city = autoCompleteDistrict.text.toString().trim()
                val district = autoCompleteSubDistrict.text.toString().trim()
                val village = autoCompleteVillage.text.toString().trim()
                val address = edtFullAddress.text.toString().trim()
                val zipCode = edtPostalCode.text.toString().trim()

                viewModel.onTriggerEvent(
                    UpdateProfileCashGoldEvent.SaveProfile(
                        user = username,
                        identityNumber = identityKtp,
                        gender = gender,
                        maritalStatus = materialStatus,
                        birthPlace = placeBirth,
                        dayOfBirth = dateBirth,
                        email = email,
                        phone = noTelp,
                        religion = religion,
                        taxIdentityNumber = npwp,
                        lastEducation = lastEducation,
                        profession = profession,
                        incomeSource = incomeSource,
                        incomePerYear = incomeYear,
                        province = province,
                        city = city,
                        district = district,
                        village = village,
                        address = address,
                        zipCode = zipCode
                    )
                )
            }
        }catch (e: Exception){
            Toast.makeText(requireContext(), e.message ?: "Unknown Message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun collectVillage() {
        lifecycleScope.launch {
            viewModel.stateVillage.collectLatest {
                with(binding){
                    autoCompleteVillage.setAutocompleteItems(it.map { city -> city.name })
                    autoCompleteVillage.setOnAutoCompleteClick { selectedVal ->
                        binding.edtPostalCode.setText(
                            it.singleOrNull { it.name == selectedVal }?.postalCode
                        )
                    }
                }
            }
        }
    }

    private fun collectDistrict() {
        lifecycleScope.launch {
            viewModel.stateDistrict.collectLatest {
                with(binding){
                    autoCompleteSubDistrict.setAutocompleteItems(it.map { city -> city.name })
                    autoCompleteSubDistrict.setOnAutoCompleteClick {
                        viewModel.onTriggerEvent(UpdateProfileCashGoldEvent.GetVillage(
                            city = autoCompleteDistrict.text.toString().trim(),
                            district = it
                        ))

                        autoCompleteVillage.text?.clear()
                    }
                }
            }
        }
    }

    private fun collectCity() {
        lifecycleScope.launch {
            viewModel.stateCity.collectLatest {
                with(binding){
                    autoCompleteDistrict.setAutocompleteItems(it.map { city -> city.name })
                    autoCompleteDistrict.setOnAutoCompleteClick {
                        viewModel.onTriggerEvent(UpdateProfileCashGoldEvent.GetDistrict(it))

                        autoCompleteSubDistrict.text?.clear()
                        autoCompleteVillage.text?.clear()
                    }
                }
            }
        }
    }

    private fun collectProvinces() {
        lifecycleScope.launch {
            viewModel.stateProvinces.collectLatest {
                with(binding){
                    autoCompleteProvince.setAutocompleteItems(it.map { prov -> prov.name })
                    autoCompleteProvince.setOnAutoCompleteClick {
                        viewModel.onTriggerEvent(UpdateProfileCashGoldEvent.GetCity(it))

                        autoCompleteDistrict.text?.clear()
                        autoCompleteSubDistrict.text?.clear()
                        autoCompleteVillage.text?.clear()
                    }
                }
            }
        }
    }

    private fun setUpCalender() {
        datePickerDialog = DatePickerDialog(
            requireContext(), R.style.DatePickerCustomGold,
            null, currentYear, currentMonth, currentDay
        )
    }

    private fun AppCompatAutoCompleteTextView.setAutocompleteItems(values: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(), R.layout.item_spinner_small, values
        )
        this.setAdapter(adapter)
    }

    private fun AppCompatAutoCompleteTextView.setOnAutoCompleteClick(func: (String) -> Unit){
        this.setOnItemClickListener { _, _, _, _ ->
            try {
                func(this.text.toString().trim())
            }catch (e: Exception){
                Toast.makeText(context, e.message ?: "Unknown Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}