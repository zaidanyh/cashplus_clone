package com.pasukanlangit.cashplus.utils.print

import com.dantsu.escposprinter.EscPosPrinterSize
import com.dantsu.escposprinter.connection.DeviceConnection

class NewAsyncEscPosPrinter(
    var printerConnection: DeviceConnection,
    printerDpi: Int,
    printerWidthMM: Float,
    printerNbrCharactersPerLine: Int
) : EscPosPrinterSize(printerDpi, printerWidthMM, printerNbrCharactersPerLine) {

    var textToPrint = ""
        internal set

    fun setTextToPrint(textToPrint: String): NewAsyncEscPosPrinter {
        this.textToPrint = textToPrint
        return this
    }
}