package com.pasukanlangit.id.ui_cashgold_address.addupdate

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.utils.getEnumExtra
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.ui_cashgold_address.R
import com.pasukanlangit.id.ui_cashgold_address.databinding.ActivityAddUpdateAddressBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class AddUpdateAddressType {
    TYPE_SAVE,
    TYPE_UPDATE
}

class AddUpdateAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddUpdateAddressBinding
    private val viewModel: AddUpdateAddressViewModel by viewModel()
    private var address: AddressParcelize? = null
    private var addUpdateAddressType: AddUpdateAddressType = AddUpdateAddressType.TYPE_SAVE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addUpdateAddressType = intent.getEnumExtra<AddUpdateAddressType>() ?: AddUpdateAddressType.TYPE_SAVE
        address = intent.parcelable(KEY_ADDRESS)

        if(address != null && addUpdateAddressType == AddUpdateAddressType.TYPE_UPDATE){
            bindInput(address!!)
        }
        with(binding){
            appbar.setIconBackPressed { finish() }
            btnAction.setUpToProgressButton(this@AddUpdateAddressActivity)
            binding.edtFullAddress.onDoneIME { processAction() }
            btnAction.setOnClickListener { processAction() }
        }

        collectState()
    }

    private fun bindInput(address: AddressParcelize) {
        with(binding){
            autocompleteProvinsi.setText(address.provinsi)
            autocompleteKabupaten.setText(address.kabupaten)
            autocompleteKecamatan.setText(address.kecamatan)
            autocompleteKelurahan.setText(address.kelurahan)
            edtKodepos.setValue(address.kodepos)
            edtFullAddress.setValue(address.alamat)
        }

        //it not necessary, because viewmodel will init get province
//        viewModel.onTriggerEvent(AddUpdateAddressEvent.GetProvinces)
        viewModel.onTriggerEvent(AddUpdateAddressEvent.GetCity(provinces = address.provinsi))
        viewModel.onTriggerEvent(AddUpdateAddressEvent.GetDistrict(city = address.kabupaten))
        viewModel.onTriggerEvent(AddUpdateAddressEvent.GetVillage(city = address.kabupaten, district = address.kecamatan))
    }

    private fun processAction() {
        try {
            with(binding){
                val province = autocompleteProvinsi.getSelectedValue()
                val city = autocompleteKabupaten.getSelectedValue()
                val district = autocompleteKecamatan.getSelectedValue()
                val village = autocompleteKelurahan.getSelectedValue()
                val postalCode = edtKodepos.getValue()
                val fullAddress = edtFullAddress.getValue()

                if(addUpdateAddressType == AddUpdateAddressType.TYPE_SAVE){
                    viewModel.onTriggerEvent(AddUpdateAddressEvent.AddAddress(
                        province = province,
                        city = city,
                        district = district,
                        village = village,
                        zipCode = postalCode,
                        address = fullAddress
                    ))
                }else if(addUpdateAddressType == AddUpdateAddressType.TYPE_UPDATE){
                    viewModel.onTriggerEvent(AddUpdateAddressEvent.UpdateAddress(
                        id = address?.id,
                        zipCode = postalCode,
                        address = fullAddress,
                        province = province,
                        city = city,
                        district = district,
                        village = village,
                        isMain = address?.isDefault
                    ))
                }
            }
        }catch (e: Exception){
            Toast.makeText(this, e.message ?: "Unknown Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun collectState() {
        collectProvinces()
        collectCity()
        collectDistrict()
        collectVillage()
        collectStateLoading()
        collectStateError()
        collectActionState()
    }

    private fun collectStateError() {
        lifecycleScope.launch {
            viewModel.stateError.collectLatest { message ->
                message.peek()?.let { info ->
                    val toast = Toast.makeText(this@AddUpdateAddressActivity, info, Toast.LENGTH_SHORT)
                    toast.show()
                    delay(toast.duration.toLong() + 500L)
                    viewModel.onTriggerEvent(AddUpdateAddressEvent.RemoveHeadQueue)
                }
            }
        }
    }

    private fun collectStateLoading() {
        lifecycleScope.launch {
            viewModel.stateLoadingButton.collectLatest { isLoading ->
                binding.btnAction.isEnabled = !isLoading
                if(isLoading){
                    binding.btnAction.showLoading()
                }else{
                    binding.btnAction.hideProgress(getString(R.string.simpan))
                }
            }
        }
    }

    private fun collectActionState() {
        lifecycleScope.launch {
            viewModel.stateActionSuccess.collectLatest {
                it?.let { isSuccess ->
                    if(isSuccess){
                        val toast = Toast.makeText(this@AddUpdateAddressActivity, "Berhasil menyimpan data", Toast.LENGTH_SHORT)
                        toast.show()
                        delay(toast.duration.toLong() + 500L)
                        finish()
                    }else{
                        Toast.makeText(this@AddUpdateAddressActivity, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun collectVillage() {
        lifecycleScope.launch {
            viewModel.stateVillage.collectLatest {
                with(binding){
                    autocompleteKelurahan.setAutocompleteItems(it.map { city -> city.name })
                    autocompleteKelurahan.setOnAutoCompleteClick { selectedVal ->
                        binding.edtKodepos.setValue(
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
                    autocompleteKecamatan.setAutocompleteItems(it.map { city -> city.name })
                    autocompleteKecamatan.setOnAutoCompleteClick {
                        viewModel.onTriggerEvent(AddUpdateAddressEvent.GetVillage(
                            city = autocompleteKabupaten.getSelectedValue(),
                            district = it
                        ))

                        autocompleteKelurahan.clearValue()
                    }
                }
            }
        }
    }

    private fun collectCity() {
        lifecycleScope.launch {
            viewModel.stateCity.collectLatest {
                with(binding){
                    autocompleteKabupaten.setAutocompleteItems(it.map { city -> city.name })
                    autocompleteKabupaten.setOnAutoCompleteClick {
                        viewModel.onTriggerEvent(AddUpdateAddressEvent.GetDistrict(it))

                        autocompleteKecamatan.clearValue()
                        autocompleteKelurahan.clearValue()
                    }
                }
            }
        }
    }

    private fun collectProvinces() {
        lifecycleScope.launch {
            viewModel.stateProvinces.collectLatest {
                with(binding){
                    autocompleteProvinsi.setAutocompleteItems(it.map { prov -> prov.name })
                    autocompleteProvinsi.setOnAutoCompleteClick {
                        viewModel.onTriggerEvent(AddUpdateAddressEvent.GetCity(it))

                        autocompleteKabupaten.clearValue()
                        autocompleteKecamatan.clearValue()
                        autocompleteKelurahan.clearValue()
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_ADDRESS = "KEY_ADDRESS"
    }
}