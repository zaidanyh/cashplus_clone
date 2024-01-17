package com.pasukanlangit.cashplus.ui.pages.history

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.BuildConfig.BASE_URL_CASHPLUS_WEB
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityHistoryDetailTransportBinding
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermissions
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import okhttp3.ResponseBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*

class HistoryDetailTransportActivity : AppCompatActivity() {
    private var dataWithoutSymbols: String = ""
    private lateinit var pathFile: File
    private lateinit var binding: ActivityHistoryDetailTransportBinding
    private val viewModel: HistoryDetailTransportViewModel by viewModel()

    private val permissionWriteExternal =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                downloadPdf()
            }else{
                Toast.makeText(
                    this,
                    "Berikan izin akses file untuk mendownload pdf",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailTransportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDownloadStruk.setUpToProgressButton(this)
        binding.webview.webChromeClient = object: WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                binding.loading.isVisible = newProgress < 100
            }
        }

        intent.getStringExtra(DATA_PARAMS)?.let { data ->
            dataWithoutSymbols = data.replace("(", " ").replace(")", " ")
            val url = "${BASE_URL_CASHPLUS_WEB}print/transportation?data=$dataWithoutSymbols"

            binding.webview.loadUrl(url)
            binding.btnDownloadStruk.isEnabled = true

            binding.btnDownloadStruk.setOnClickListener {
                if (hasPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                    downloadPdf()
                } else {
                    permissionWriteExternal.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }
        } ?: finish()

        observeDownloadFile()
    }

    private fun downloadPdf() {
        val fileName = intent.getStringExtra(TRX_ID)
        pathFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "cashplus-${fileName}.pdf"
        )
        if(pathFile.exists()){
            Toast.makeText(this, "Struk sudah ada, check di folder download", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.downloadFile(dataWithoutSymbols)
    }

    private fun observeDownloadFile() {
        viewModel.file.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                   it.data?.let { responseBody ->
                       downloadFile(responseBody)
                       Toast.makeText(this, "Download Success, Lokasi: Folder Download", Toast.LENGTH_SHORT).show()
                   }
                }
                Status.LOADING -> {
                    binding.btnDownloadStruk.showLoading()
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    hideLoadingButton()
                }
            }
        }
    }


    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody) {
        var count: Int
        val data = ByteArray(1024 * 4)
//        val fileSize = body.contentLength()
        val bis: InputStream = BufferedInputStream(body.byteStream(), 1024 * 8)
        val output: OutputStream = FileOutputStream(pathFile)
        var total: Long = 0
        while (bis.read(data).also { count = it } != -1) {
            total += count.toLong()
            output.write(data, 0, count)
        }
        hideLoadingButton()
        output.flush()
        output.close()
        bis.close()
    }

    private fun hideLoadingButton() {
        binding.btnDownloadStruk.hideProgress(getString(R.string.download_struk))
    }

    companion object {
        const val DATA_PARAMS = "DATA_PARAMS"
        const val TRX_ID = "TRX_ID"
    }
}