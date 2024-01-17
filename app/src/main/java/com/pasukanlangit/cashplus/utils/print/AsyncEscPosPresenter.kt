package com.pasukanlangit.cashplus.utils.print

import android.content.Context
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import com.dantsu.escposprinter.EscPosCharsetEncoding
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import kotlinx.coroutines.DelicateCoroutinesApi
import java.lang.ref.WeakReference

abstract class AsyncEscPosPresenter(
    context: Context,
): CoroutinesAsyncTask<NewAsyncEscPosPrinter, Int, Int>("AsyncPrint") {
    private var dialog: AlertDialog.Builder? = null
    private var progress: AlertDialog? = null
    protected var layout: RelativeLayout? = null
    private var weakContext: WeakReference<Context>

    @DelicateCoroutinesApi
    protected open fun doInBackground(vararg printersData: NewAsyncEscPosPrinter): Int {
        if (printersData.isEmpty()) {
            return FINISH_NO_PRINTER
        }
        publishProgress(PROGRESS_CONNECTING)
        val printerData = printersData.first()
        try {
            val deviceConnection = printerData.printerConnection
            val printer = EscPosPrinter(
                deviceConnection,
                printerData.printerDpi,
                printerData.printerWidthMM,
                printerData.printerNbrCharactersPerLine,
                EscPosCharsetEncoding("windows-1252", 16)
            )
            publishProgress(PROGRESS_PRINTING)
            printer.printFormattedTextAndCut(printerData.textToPrint)
            publishProgress(PROGRESS_PRINTED)
        } catch (e: EscPosConnectionException) {
            e.printStackTrace()
            return FINISH_PRINTER_DISCONNECTED
        } catch (e: EscPosParserException) {
            e.printStackTrace()
            return FINISH_PARSER_ERROR
        } catch (e: EscPosEncodingException) {
            e.printStackTrace()
            return FINISH_ENCODING_ERROR
        } catch (e: EscPosBarcodeException) {
            e.printStackTrace()
            return FINISH_BARCODE_ERROR
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return FINISH_SUCCESS
    }

    override fun onPreExecute() {
        if (this.layout == null) {
            val context = weakContext.get() ?: return
            dialog = AlertDialog.Builder(context)
            dialog?.setTitle("Printing in progress...")
            dialog?.setMessage("...")
            progress = dialog?.create()
            progress?.setCancelable(false)
            progress?.show()
        }
    }

    override fun onProgressUpdate(vararg values: Int?) {
        when (values.first()) {
            PROGRESS_CONNECTING -> progress?.setMessage("Connecting printer...")
            PROGRESS_CONNECTED -> progress?.setMessage("Printer is connected...")
            PROGRESS_PRINTING -> progress?.setMessage("Printer is printing...")
            PROGRESS_PRINTED -> progress?.setMessage("Printer has finished...")
        }
    }

    override fun onPostExecute(result: Int?) {
        progress?.dismiss()
        progress = null
        when (result) {
            FINISH_SUCCESS -> dialog?.setTitle("Success")
                ?.setMessage("Congratulation! Receipt is printed!")
                ?.show()
            FINISH_NO_PRINTER -> dialog?.setTitle("No Printer")
                ?.setMessage("Cashplus can't find any printer connected.")
                ?.show()
            FINISH_PRINTER_DISCONNECTED -> dialog?.setTitle("Printer Disconnected")
                ?.setMessage("Unable to connect the printer.")
                ?.show()
            FINISH_PARSER_ERROR -> dialog?.setTitle("Invalid formatted text")
                ?.setMessage("It seems to ve an invalid syntax problem.")
                ?.show()
            FINISH_ENCODING_ERROR -> dialog?.setTitle("Bad selected encoding")
                ?.setMessage("The selected encoding character returning an error.")
                ?.show()
            FINISH_BARCODE_ERROR -> dialog?.setTitle("Invalid barcode")
                ?.setMessage("Data send to be converted to barcode or QR code seems to be invalid.")
                ?.show()
        }
    }

    companion object {
        protected const val FINISH_SUCCESS = 1
        const val FINISH_NO_PRINTER = 2
        protected const val FINISH_PRINTER_DISCONNECTED = 3
        protected const val FINISH_PARSER_ERROR = 4
        protected const val FINISH_ENCODING_ERROR = 5
        protected const val FINISH_BARCODE_ERROR = 6

        const val PROGRESS_CONNECTING = 11
        protected const val PROGRESS_CONNECTED = 12
        protected const val PROGRESS_PRINTING = 13
        protected const val PROGRESS_PRINTED = 14
    }

    init {
        weakContext = WeakReference(context)
    }
}