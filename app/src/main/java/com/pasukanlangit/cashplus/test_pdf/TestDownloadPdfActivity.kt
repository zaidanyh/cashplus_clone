package com.pasukanlangit.cashplus.test_pdf

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.pasukanlangit.cashplus.databinding.ActivityTestDownloadPdfBinding
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.BroadcastReceiver
import android.content.Context
import android.net.Uri
import android.app.DownloadManager
import android.os.Environment
import androidx.core.content.PackageManagerCompat
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.lifecycle.lifecycleScope
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermissions
import com.pasukanlangit.id.core.utils.parcelable
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL


class TestDownloadPdfActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestDownloadPdfBinding

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

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == MESSAGE_PROGRESS) {
                val download: Download? = intent.parcelable("download")
                download?.progress?.let { progress ->
                    binding.loading.progress = progress
                }
                if (download?.progress == 100) {
                   binding.tvLoading.text = "File Download Complete"
                } else {
                    binding.tvLoading.text = String.format(
                        "Downloaded (%d/%d) MB",
                        download?.currentFileSize,
                        download?.totalFileSize
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestDownloadPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDownload.setOnClickListener {
            val uri = Uri.parse("https://cashplus.id/print/transportation?ex=true&data={date:,departure_date:,departure_time:,booking_code:,transport_name:,transport_number:flightcode|trainnumber,transport_class:,transport_rute:flight_info_transit|train_info_route,print_category:KERETA|PESAWAT,passengers:[{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:}]}")
//            lifecycleScope.launchWhenCreated {
//                downloadPdfContent("http://10.9.81.62:3000/print/transportation?ex=true&data={date:,departure_date:,departure_time:,booking_code:,transport_name:,transport_number:flightcode|trainnumber,transport_class:,transport_rute:flight_info_transit|train_info_route,print_category:KERETA|PESAWAT,passengers:[{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:},{name:dsfsadf,id:12412312,title:Mr,type:Adult|young,seat:,gerbong:}]}")
//            }
//            val request: DownloadManager.Request = DownloadManager.Request(uri)
//            request.setTitle("PrintTrx")
//            request.setMimeType("Application/pdf")
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "PrintTrx.pdf")
//            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//            downloadManager.enqueue(request)
//            startActivity(Intent(Intent.ACTION_VIEW, uri))

//            startActivity(Intent(this, WebviewActivity::class.java))
            if (hasPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                downloadPdf()
            }else{
                permissionWriteExternal.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
        }

        registerReceiver()
    }


    private fun downloadPdf() {
        val intent = Intent(this, DownloadService::class.java)
        startService(intent)
    }

    private fun registerReceiver() {
        val bManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(MESSAGE_PROGRESS)
        bManager.registerReceiver(broadcastReceiver, intentFilter)
    }

    suspend fun downloadPdfContent(urlToDownload: String) { try {
            val fileName = "xyz"
            val fileExtension = ".pdf"

//           download pdf file.
            val url = URL(urlToDownload)
            val c: HttpURLConnection = url.openConnection() as HttpURLConnection
            c.requestMethod = "GET"
            c.doOutput = true
            c.connect()
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "xyz.pdf")
//            val file = File(PATH)
            file.mkdirs()
            val outputFile = File(file, fileName + fileExtension)
            val fos = FileOutputStream(outputFile)
            val `is`: InputStream = c.inputStream
            val buffer = ByteArray(1024)
            var len1: Int
            while (`is`.read(buffer).also { len1 = it } != -1) {
                fos.write(buffer, 0, len1)
            }
            fos.close()
            `is`.close()
            println("--pdf downloaded--ok--$urlToDownload")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {
        const val MESSAGE_PROGRESS = "MESSAGE_PROGRESS"
    }
}