package com.pasukanlangit.cashplus.utils.print

import android.content.Context
import android.util.Log
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import kotlinx.coroutines.DelicateCoroutinesApi

class CoroutinesBluetoothEscPosPrint(context: Context):
    AsyncEscPosPresenter(context)  {

    @Suppress("KotlinConstantConditions")
    @DelicateCoroutinesApi
    override fun doInBackground(vararg params: NewAsyncEscPosPrinter?): Int {
        if (params.isEmpty()) return FINISH_NO_PRINTER
        var printerData = params.first()
        val deviceConnection = printerData?.printerConnection
        publishProgress(PROGRESS_CONNECTING)
        if (deviceConnection == null) {
            printerData = printerData?.let { printer ->
                BluetoothPrintersConnections.selectFirstPaired()?.let { connection ->
                    NewAsyncEscPosPrinter(
                        connection,
                        printer.printerDpi,
                        printer.printerWidthMM,
                        printer.printerNbrCharactersPerLine
                    )
                }
            }
            printerData?.textToPrint = printerData?.textToPrint.toString()
        } else {
            try {
                if (deviceConnection.isConnected) deviceConnection.connect()
                else deviceConnection.disconnect()
            }catch (e: EscPosConnectionException) {
                Log.d("Device Connected", e.message.toString())
                e.printStackTrace()
            }
        }
        return super.doInBackground(printerData!!)
    }
}