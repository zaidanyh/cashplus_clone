package com.pasukanlangit.id.ui_cashgold_address.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.pasukanlangit.id.ui_cashgold_address.databinding.ActivityAddressGoldHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.pasukanlangit.id.core.utils.putExtra
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.id.ui_cashgold_address.addupdate.AddUpdateAddressType
import com.pasukanlangit.id.ui_cashgold_address.addupdate.AddUpdateAddressActivity
import com.pasukanlangit.id.ui_cashgold_address.addupdate.AddressParcelize
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddressGoldHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressGoldHomeBinding
    private val viewModel: AddressGoldHoveViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressGoldHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appbar.setIconBackPressed { finish() }
        binding.btnAddAddress.setOnClickListener {
            startActivity(Intent(this, AddUpdateAddressActivity::class.java))
        }

        setUpRvAddress()
        collectState()
    }

    private fun setUpRvAddress() {
        binding.rvAddress.layoutManager = LinearLayoutManager(this)
    }

    private fun collectState() {
        collectStateLoading()
        collectStateError()
        collectAddressList()
    }


    private fun collectAddressList() {
        lifecycleScope.launch {
            viewModel.stateAddressList.collectLatest {
                it?.let { address ->
                    binding.note.isVisible = address.isEmpty()
                    binding.rvAddress.adapter = AddressAdapter(address){
                        when(it){
                            is AddressAdapterEvent.DeleteAddress -> {
                                viewModel.onTriggerEvent(AddressGoldHomeEvent.DeleteAddress(it.id))
                            }
                            is AddressAdapterEvent.UpdateAddressFull -> {
                                startActivity(
                                    Intent(this@AddressGoldHomeActivity, AddUpdateAddressActivity::class.java)
                                        .putExtra(AddUpdateAddressType.TYPE_UPDATE)
                                        .putExtra(AddUpdateAddressActivity.KEY_ADDRESS, AddressParcelize(
                                            id = it.address.id,
                                            kodepos = it.address.kodepos,
                                            alamat = it.address.alamat,
                                            provinsi = it.address.provinsi,
                                            kabupaten = it.address.kabupaten,
                                            kecamatan = it.address.kecamatan,
                                            kelurahan = it.address.kelurahan,
                                            isDefault = it.address.isDefault
                                        ))
                                )
                            }
                            is AddressAdapterEvent.UpdateToMainAddress -> {
                                viewModel.onTriggerEvent(AddressGoldHomeEvent.UpdateAddress(
                                    id = it.address.id,
                                    zipCode = it.address.kodepos,
                                    address = it.address.alamat,
                                    province = it.address.provinsi,
                                    city = it.address.kabupaten,
                                    district = it.address.kecamatan,
                                    village = it.address.kelurahan,
                                    isMain = true
                                ))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun collectStateError() {
        lifecycleScope.launch {
            viewModel.stateError.collectLatest { message ->
                message.peek()?.let { info ->
                    val toast = Toast.makeText(this@AddressGoldHomeActivity, info, Toast.LENGTH_SHORT)
                    toast.show()
                    delay(toast.duration.toLong() + 500L)
                    viewModel.onTriggerEvent(AddressGoldHomeEvent.RemoveHeadQueue)
                }
            }
        }
    }

    private fun collectStateLoading() {
        lifecycleScope.launch {
            viewModel.stateLoading.collectLatest { isLoading ->
                binding.loading.isVisible = isLoading
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(AddressGoldHomeEvent.GetAddressList)
    }
}