package com.pasukanlangit.cashplus.ui.pages.history.print

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.pasukanlangit.cashplus.databinding.ItemDataPrintBinding

class PrintersAdapter: RecyclerView.Adapter<PrintersAdapter.PrinterViewHolder>() {

    private val printers = ArrayList<BluetoothConnection>()
    private lateinit var onItemPrintClickListener: OnItemPrintClickListener

    fun setPrintersList(printers: List<BluetoothConnection>?) {
        if (printers.isNullOrEmpty()) return
        this.printers.clear()
        this.printers.addAll(printers)
    }

    fun setOnItemPrintClickListener(onItemPrintClickListener: OnItemPrintClickListener) {
        this.onItemPrintClickListener = onItemPrintClickListener
    }

    inner class PrinterViewHolder(val binding: ItemDataPrintBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(printer: BluetoothConnection) {
            with(binding) {
                tvNamePrint.text = printer.device.name
                tvAddressPrint.text = printer.device.address
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrinterViewHolder {
        return PrinterViewHolder(ItemDataPrintBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = printers.size

    override fun getItemViewType(position: Int): Int = position

    override fun onBindViewHolder(holder: PrinterViewHolder, position: Int) {
        holder.bind(printers[position])
        holder.binding.root.setOnClickListener {
            onItemPrintClickListener.onItemPrintClicked(printers[holder.bindingAdapterPosition])
        }
    }

    interface OnItemPrintClickListener {
        fun onItemPrintClicked(printer: BluetoothConnection)
    }
}