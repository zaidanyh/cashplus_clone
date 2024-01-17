package com.pasukanlangit.cashplus.test_pdf;

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.repository.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.koin.android.ext.android.inject
import java.io.*
import kotlin.math.pow
import kotlin.math.roundToInt
import java.nio.Buffer
import kotlin.math.roundToLong


class DownloadService : IntentService("Download Service") {
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private var totalFileSize = 0
    private val mainRepository: MainRepository by inject()

    override fun onHandleIntent(intent: Intent?) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Download")
            .setContentText("Download File")
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_icon_building)

        notificationManager?.notify(0, notificationBuilder?.build())

        initDownload()
    }

    private fun initDownload() {
        CoroutineScope(Dispatchers.IO).launch {
//            http://10.9.81.62:3000/assets/ComprofST24NoLoop.mp4

//            val response = mainRepository.downloadFile("http://10.9.81.62:3000/api/print/transportation",  "{\"date\":\"2423123213\",\"departure\":\"\",\"booking_code\":\"\",\"transport_name\":\"\",\"transport_number\":\"flightcode|trainnumber\",\"transport_class\":\"\",\"transport_rute\":\"Jakarta(CGK)06:00-YogyakartaKulonprogo(YIA)07:15\",\"print_category\":\"PESAWAT\",\"passengers\":[{\"name\":\"dsfsadf\",\"id\":\"12412312\",\"title\":\"Mr\",\"type\":\"Adult|young\",\"seat\":\"\",\"gerbong\":\"\"}]}")
            val response = mainRepository.downloadFileExport("https://cashplus.id/api/print/transportation", true, "{\"date\":\"2423123213\",\"departure\":\"\",\"booking_code\":\"\",\"transport_name\":\"\",\"transport_number\":\"flightcode|trainnumber\",\"transport_class\":\"\",\"transport_rute\":\"Jakarta(CGK)06:00-YogyakartaKulonprogo(YIA)07:15\",\"print_category\":\"PESAWAT\",\"passengers\":[{\"name\":\"dsfsadf\",\"id\":\"12412312\",\"title\":\"Mr\",\"type\":\"Adult|young\",\"seat\":\"\",\"gerbong\":\"\"}]}")
            if (response?.isSuccessful == true) {
                response.body()?.let { body ->
                    downloadFile(body)
                }
//                response.body()?.byteStream()?.saveToFile("${System.currentTimeMillis()}.pdf")
            } else {
                println(response?.errorBody()?.string())
            }
        }
    }

    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody) {
        var count: Int
        val data = ByteArray(1024 * 4)
        val fileSize = body.contentLength()
        val bis: InputStream = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "ekkdskdskd.pdf"
        )
        val output: OutputStream = FileOutputStream(outputFile)
        var total: Long = 0
        val startTime = System.currentTimeMillis()
        var timeCount = 1
        while (bis.read(data).also { count = it } != -1) {
            total += count.toLong()
            totalFileSize = (fileSize / 1024.0.pow(2.0)).toInt()
            val current = (total / 1024.0.pow(2.0)).roundToLong().toDouble()
            val progress = (total * 100 / fileSize).toInt()
            val currentTime = System.currentTimeMillis() - startTime
            val download = Download()
            download.totalFileSize = totalFileSize
            if (currentTime > 1000 * timeCount) {
                download.currentFileSize = current.toInt()
                download.progress = progress
                sendNotification(download)
                timeCount++
            }
            output.write(data, 0, count)
        }
        onDownloadComplete()
        output.flush()
        output.close()
        bis.close()
    }

    private fun sendNotification(download: Download) {
        sendIntent(download)
        notificationBuilder!!.setProgress(100, download.progress, false)
        notificationBuilder!!.setContentText(
            String.format(
                "Downloaded (%d/%d) MB",
                download.currentFileSize,
                download.totalFileSize
            )
        )
        notificationManager!!.notify(0, notificationBuilder!!.build())
    }


    private fun sendIntent(download: Download) {
        val intent = Intent(TestDownloadPdfActivity.MESSAGE_PROGRESS)
        intent.putExtra("download", download)
        LocalBroadcastManager.getInstance(this@DownloadService).sendBroadcast(intent)
    }

    private fun onDownloadComplete() {
        val download = Download()
        download.progress = 100
        sendIntent(download)
        notificationManager?.cancel(0)
        notificationBuilder?.setProgress(0, 0, false)
        notificationBuilder?.setContentText("File Downloaded")
        notificationManager?.notify(0, notificationBuilder!!.build())
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        notificationManager?.cancel(0)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "202"
    }
}