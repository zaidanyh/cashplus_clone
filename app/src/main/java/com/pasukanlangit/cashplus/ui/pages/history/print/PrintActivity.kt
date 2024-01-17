package com.pasukanlangit.cashplus.ui.pages.history.print

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.dantsu.escposprinter.EscPosPrinterCommands
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityPrintBinding
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.cashplus.utils.print.CoroutinesBluetoothEscPosPrint
import com.pasukanlangit.cashplus.utils.print.NewAsyncEscPosPrinter
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.library_core.domain.model.NotifType
import kotlinx.coroutines.DelicateCoroutinesApi

class PrintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrintBinding
    private lateinit var printersAdapter: PrintersAdapter

    private var stateBluetooth = false

    private val bluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        stateBluetooth = result.resultCode == Activity.RESULT_OK
        setPrinterList()
    }

    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false

        val receipt = intent.getStringExtra(PRINT_RECEIPT_KEY)

        setUpRecyclerView(receipt.toString())
        with(binding) {
            imgBack.setOnClickListener { finish() }
            setBluetoothState()
            switchBluetooth.setOnCheckedChangeListener { _, isChecked ->
                setBluetoothState(true)
                wrapperBluetoothNotActiveOrEmpty.isVisible = !isChecked
                txtPrinterList.isVisible = isChecked
                rvPrinter.isVisible = isChecked
            }
        }
    }

    @DelicateCoroutinesApi
    private fun setUpRecyclerView(receipt: String) {
        printersAdapter = PrintersAdapter()
        with(binding.rvPrinter) {
            layoutManager = LinearLayoutManager(this@PrintActivity)
            adapter = printersAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
        printersAdapter.setOnItemPrintClickListener(object: PrintersAdapter.OnItemPrintClickListener {
            override fun onItemPrintClicked(printer: BluetoothConnection) {
                CoroutinesBluetoothEscPosPrint(this@PrintActivity)
                    .execute(getAsyncEscPosPrinter(printer, receipt))
//                this@PrintActivity.printer = printer
//                PrintPreviewFragment.newInstance(
//                    receipt = formatReceipt(
//                        receipt = receipt, codeProvider = provider, price = priceForPrint
//                    ), eventClickPrint = this@PrintActivity
//                ).show(supportFragmentManager, "Preview Print")
//                EditStructFragment.newInstance(
//                    receipt = formatReceipt(
//                        receipt, provider
//                    ), eventPreviewClick = this@PrintActivity
//                ).show(supportFragmentManager, "Edit Struck")
            }
        })
    }

    private fun setPrinterList() {
        if (stateBluetooth) {
            val items = mutableListOf<BluetoothConnection>()
            BluetoothPrintersConnections().list?.forEach { device ->
                items.add(device)
            }
            with(binding) {
                wrapperBluetoothNotActiveOrEmpty.isVisible = items.isEmpty()
                txtPrinterList.isVisible = items.isNotEmpty()
                rvPrinter.isVisible = items.isNotEmpty()
                printersAdapter.setPrintersList(items)
                printersAdapter.notifyDataSetChanged()
                if (items.isEmpty()) {
                    txtStateActiveOrEmpty.text = getString(R.string.printers_is_empty)
                    txtStateDesc.text = getString(R.string.need_pairing_first)
                    return
                }
            }
        }
    }

    private fun setBluetoothState(onChange: Boolean = false) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            val customDialogFragment = ButtomSheetNotif(getString(R.string.bluetooth_not_supported), NotifType.NOTIF_ERROR)
            customDialogFragment.show(supportFragmentManager, customDialogFragment.tag)
            return
        }
        if (onChange) {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bluetoothLauncher.launch(enableBtIntent)
            }
            if (bluetoothAdapter.isEnabled) {
                stateBluetooth = false
                binding.txtStateActiveOrEmpty.text = getString(R.string.bluetooth_is_not_active)
                binding.txtStateDesc.text = getString(R.string.bluetooth_activation_first)
                bluetoothAdapter.disable()
            }
            return
        }
        stateBluetooth = bluetoothAdapter.isEnabled
        binding.switchBluetooth.isChecked = bluetoothAdapter.isEnabled
    }

    private fun getAsyncEscPosPrinter(printerConnection: DeviceConnection, receipt: String): NewAsyncEscPosPrinter {
        val printer = NewAsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        return printer.setTextToPrint(
            "[C]<img>${
                PrinterTextParserImg.bytesToHexadecimalString(
                    EscPosPrinterCommands.bitmapToBytes(
                        MyUtils.rescaleImagePrint(
                            printer,
                            BitmapFactory.decodeResource(resources, R.drawable.icon_cashplus_black)
                        )
                    )
                )
            }</img>\n\n$receipt"
        )
    }

    override fun onResume() {
        super.onResume()
        setBluetoothState()
        setPrinterList()
    }

    companion object {
        const val PRINT_RECEIPT_KEY = "print_receipt_key"
    }
}