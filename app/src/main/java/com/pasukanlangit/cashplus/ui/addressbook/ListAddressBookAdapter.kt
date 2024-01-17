package com.pasukanlangit.cashplus.ui.addressbook

import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ItemAddressbookBinding
import com.pasukanlangit.cashplus.model.response.AddressBookData
import com.pasukanlangit.cashplus.ui.addressbook.add.AddAddressBookActivity


class ListAddressBookAdapter(private val deleteListener: (String) -> Unit) :
    ListAdapter<AddressBookData, ListAddressBookAdapter.CartViewHolder>(
        DIFF_UTIL
    ) {

    inner class CartViewHolder(val binding: ItemAddressbookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(ItemAddressbookBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val addressBook = getItem(position)
        with(holder.binding) {
            tvTypeAddress.text = addressBook.addressType
            labelMainAddress.isVisible = addressBook.isMainAddress == "1"
            tvName.text = addressBook.receiverName
            tvDescAddress.text = root.context.getString(R.string.location_hotel,addressBook.address,addressBook.posCode)

            iconChecklist.isVisible = addressBook.isSelected
            if (addressBook.isSelected) root.setBackgroundResource(R.drawable.bg_transparent_border_primary_rounded_12)
            else root.setBackgroundResource(R.drawable.bg_transparent_border_slate200_rounded_12)

            root.setOnClickListener {
                setOnClickRootAddress(addressBook)
            }

            btnChangeAddress.setOnClickListener {
                val intent = Intent(root.context, AddAddressBookActivity::class.java).apply {
                    putExtra(AddAddressBookActivity.KEY_ADDRESS_BOOK, addressBook)
                }
                root.context.startActivity(intent)
            }

            btnDelete.setOnClickListener {
                val dialogClickListener =
                    DialogInterface.OnClickListener { _, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                deleteListener(addressBook.bookAddressId)
                            }
                        }
                    }

                val builder: AlertDialog.Builder = AlertDialog.Builder(root.context)
                builder.setMessage(root.context.getString(R.string.sure_to_delete_address)).setPositiveButton(
                    root.context.getString(R.string.yes),
                    dialogClickListener
                )
                    .setNegativeButton(root.context.getString(R.string.no), dialogClickListener).show()
            }
        }

    }

    private fun setOnClickRootAddress(addressBookData: AddressBookData) {
        currentList.map {
            it.isSelected = addressBookData.bookAddressId == it.bookAddressId
        }
        notifyDataSetChanged()
    }

    fun getSelectedProductAddressBook(): AddressBookData? = currentList.singleOrNull { it.isSelected }


    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<AddressBookData>() {
            override fun areItemsTheSame(
                oldItem: AddressBookData,
                newItem: AddressBookData
            ): Boolean = oldItem.bookAddressId == newItem.bookAddressId

            override fun areContentsTheSame(
                oldItem: AddressBookData,
                newItem: AddressBookData
            ): Boolean = oldItem == newItem
        }
    }


}