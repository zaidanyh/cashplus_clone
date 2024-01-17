package com.pasukanlangit.id.ui_cashgold_address.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.id.model.Address
import com.pasukanlangit.id.ui_cashgold_address.R
import com.pasukanlangit.id.ui_cashgold_address.databinding.ItemAddressBinding

class AddressAdapter(private val addressList: List<Address>,
                     private val eventListener: (eventType: AddressAdapterEvent) -> Unit,
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(private val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address){
            with(binding) {
                btnUpdateToMain.isVisible = !address.isDefault
                btnUpdateToMain.setOnClickListener { eventListener(AddressAdapterEvent.UpdateToMainAddress(address)) }
                btnUpdate.setOnClickListener { eventListener(AddressAdapterEvent.UpdateAddressFull(address)) }
                btnDelete.setOnClickListener { eventListener(AddressAdapterEvent.DeleteAddress(id = address.id)) }

                tvIsMain.isVisible = address.isDefault
                tvVillage.text = address.kelurahan
                tvAddress.text = address.alamat
                tvSubAddress.text = this.root.context.getString(
                    R.string.full_address_cashgold,
                    address.kabupaten,
                    address.kecamatan,
                    address.provinsi,
                    address.kodepos
                )
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = addressList.size

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(addressList[position])
    }
}