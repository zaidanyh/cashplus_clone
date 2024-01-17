package com.pasukanlangit.cashplus.ui.injectvoucher

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.pasukanlangit.cashplus.databinding.ActivityScanBarcodeBinding
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermission
import com.pasukanlangit.id.core.utils.CustomViewQR
import com.pasukanlangit.id.nobu.R
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanBarcodeActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var binding: ActivityScanBarcodeBinding

    private lateinit var mScannerView: ZXingScannerView
    private var isFlashActive : Boolean = false

    private var destScan: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        destScan = intent.getStringExtra(InjectVoucherActivity.DEST_SCAN)
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { finish() }
        })

        binding.btnExit.setOnClickListener { finish() }
    }

    private fun initCameraScan(){
        mScannerView = object: ZXingScannerView(this) {
            override fun createViewFinderView(context: Context?): IViewFinder {
                return CustomViewQR(context!!)
            }
        }
        mScannerView.setAspectTolerance(0.5f)
        mScannerView.setFormats(
            listOf(
                BarcodeFormat.CODE_39, BarcodeFormat.CODE_128, BarcodeFormat.UPC_A,
                BarcodeFormat.UPC_E, BarcodeFormat.EAN_8, BarcodeFormat.EAN_13,
                BarcodeFormat.ITF
            )
        )
        mScannerView.setAutoFocus(true)
        mScannerView.setResultHandler(this)
        with(binding) {
            frameScanner.addView(mScannerView)
            btnFlash.setOnClickListener {
                isFlashActive = !isFlashActive
                mScannerView.flash = isFlashActive
                if (isFlashActive) binding.btnFlash.setImageResource(R.drawable.flash_on)
                else binding.btnFlash.setImageResource(R.drawable.flash_off)
            }
        }
    }

    private fun requestPermission() {
        if (!hasPermission(this, Manifest.permission.CAMERA)) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            100 -> {
                if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    initCameraScan()
                    mScannerView.startCamera()
                    return
                }
                Toast.makeText(this, getString(R.string.request_permission_camera), Toast.LENGTH_SHORT).show()
                finish()
            }
            else -> {}
        }
    }

    override fun handleResult(rawResult: Result?) {
        val intent = Intent()
        rawResult?.text?.let { result ->
            mScannerView.setLaserEnabled(false)
            intent.putExtra(InjectVoucherActivity.RESULT_SCAN_BARCODE, result)
            intent.putExtra(InjectVoucherActivity.DEST_SCAN, destScan)
        } ?: run {
            intent.putExtra(InjectVoucherActivity.RESULT_SCAN_BARCODE, getString(R.string.result_scan_not_found))
            intent.putExtra(InjectVoucherActivity.DEST_SCAN, "404")
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        initCameraScan()
        mScannerView.startCamera()
        requestPermission()
    }

    override fun onPause() {
        mScannerView.stopCamera()
        super.onPause()
    }
}