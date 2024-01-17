package com.pasukanlangit.id.nobu.presentation.scan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.pasukanlangit.id.core.utils.CustomViewQR
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.ActivityQrScanBinding
import com.pasukanlangit.id.nobu.presentation.WebviewNobuActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class QrScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var binding: ActivityQrScanBinding
    private val viewModel: ScanViewModel by viewModel()

    private lateinit var mScannerView: ZXingScannerView
    private lateinit var camManager: CameraManager
    private var isFlashActive : Boolean = false
    private var stateCanBack = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { onFinish() }
        })

        binding.btnExit.setOnClickListener { onFinish() }
        collectResultScan()
    }

    private fun initScannerView() {
        mScannerView = object : ZXingScannerView(this) {
            override fun createViewFinderView(context: Context?): IViewFinder {
                return CustomViewQR(context!!)
            }
        }
        mScannerView.setFormats(listOf(BarcodeFormat.QR_CODE))
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
                            getString(R.string.request_permission_camera),
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

    override fun handleResult(rawResult: Result?) {
        rawResult?.text?.let { result ->
            mScannerView.setLaserEnabled(false)
            viewModel.sendResultQris(result)
        } ?: run {
            Toast.makeText(this, getString(R.string.result_scan_not_found), Toast.LENGTH_SHORT).show()
        }
    }

    private fun collectResultScan() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE ERROR
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@QrScanActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeHeadQueue()
                            onResume()
                        }
                    }
                }
                // STATE LOADING
                launch {
                    viewModel.stateLoading.collectLatest { binding.resultLoading.isVisible = it }
                }
                // STATE RESULT SCAN
                launch {
                    viewModel.sendResultScan.collectLatest { response ->
                        if (response != null) {
                            val intent = Intent(this@QrScanActivity, WebviewNobuActivity::class.java).apply {
                                putExtra(WebviewNobuActivity.URL_WEBVIEW_KEY, response.url)
                            }
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initScannerView()

        mScannerView.startCamera()
        doRequestPermission()
    }

    override fun onPause() {
        mScannerView.stopCamera()
        super.onPause()
    }

    private fun onFinish() {
        if (!stateCanBack) return
        else finish()
    }
}