package com.pasukanlangit.id.ui_downline_home.mintasaldoqr

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.pasukanlangit.id.core.utils.CustomViewQR
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.ActivityScanQragenBinding
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.ext.android.inject

class ScanQRAgentActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private lateinit var binding: ActivityScanQragenBinding

    private lateinit var mScannerView: ZXingScannerView
    private lateinit var camManager: CameraManager
    private var isFlashActive : Boolean = false

    private val gson: Gson by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQragenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExit.setOnClickListener { finish() }
    }

    private fun initScannerView() {
        mScannerView = object : ZXingScannerView(this) {
            override fun createViewFinderView(context: Context?): IViewFinder {
                return CustomViewQR(context!!)
            }
        }
        mScannerView.setFormats(listOf(BarcodeFormat.QR_CODE, BarcodeFormat.AZTEC))
        mScannerView.setAutoFocus(true)
        mScannerView.setResultHandler(this)
        camManager = getSystemService(CAMERA_SERVICE) as CameraManager

        binding.frameScanner.addView(mScannerView)
        binding.btnFlash.setOnClickListener {
            isFlashActive = !isFlashActive
            mScannerView.flash = isFlashActive
            if (isFlashActive) binding.btnFlash.setImageResource(R.drawable.flash_on)
            else binding.btnFlash.setImageResource(R.drawable.flash_off)
        }
    }

    override fun onResume() {
        super.onResume()
        initScannerView()

        mScannerView.startCamera()
        doRequestPermission()
    }


    private fun doRequestPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                if(grantResults.isNotEmpty()){
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            this,
                            "Terima izin kamera untuk menggunakan QR code",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        initScannerView()
                        mScannerView.startCamera()
                    }
                }
            }
            else -> {
                /* nothing to do in here */
            }
        }
    }

    override fun onPause() {
        mScannerView.stopCamera()
        super.onPause()
    }

    override fun handleResult(rawResult: Result?) {
        rawResult?.text?.let { resultString ->
            try {
                val respObj = gson.fromJson(resultString, DataScanResult::class.java)
                startActivity(
                    Intent(this@ScanQRAgentActivity, ScanQRConfirmationActivity::class.java).apply {
                        putExtra(ScanQRConfirmationActivity.KEY_DATA_QR, respObj)
                    }
                )
            }catch (e: Exception){
                Toast.makeText(this@ScanQRAgentActivity, "Error, terjadi perubahan format data", Toast.LENGTH_SHORT).show()
            }

        } ?: run {
            Toast.makeText(this@ScanQRAgentActivity, "Cannot scan QR Code", Toast.LENGTH_SHORT).show()
        }
    }
}