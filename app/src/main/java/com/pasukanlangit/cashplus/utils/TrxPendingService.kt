package com.pasukanlangit.cashplus.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.pasukanlangit.cashplus.repository.MainRepository
import org.koin.android.ext.android.inject

class TrxPendingService : Service() {

    private val mainRepository by inject<MainRepository>()
    private val sharedPrefDataSource: com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource by inject()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent, flags: Int, startid: Int): Int {
//        runBlocking(Dispatchers.IO) {
//            val trxPendingSaved = sharedPrefDataSource.getTransactionPending()
//            val token = sharedPrefDataSource.getAccessToken()
//
//            if(trxPendingSaved.isEmpty()){
//                //stop this service
//                stopSelf()
//                cancelAlarm()
//            }else{
//                if(token != null){
//                    trxPendingSaved.forEach { trxpending ->
//                        val response = mainRepository.checkTransaction(trxpending, token)
//
//                        if(response.isSuccessful){
//                            val responseBody = response.body()?.data
//
//                            responseBody?.forEach { trx ->
//                                if(trx?.productCode == trxpending.kode_provider){
//                                    if(MyUtils.isResponseSuccess(trx.transStat)){
//                                        showAlarmNotification(
//                                            this@TrxPendingService,
//                                            "Transaksi ${trx.trxId} sukses",
//                                            "${trxpending.dsc} berhasil",
//                                            trx.trxId!!.toInt()
//                                        )
//
//                                        val filteredTrxPending = trxPendingSaved.filter { trxfilter -> trxfilter.kode_provider != trx.productCode }
//                                        sharedPrefDataSource.setTransactionPending(filteredTrxPending)
//
//                                    }else if(trx.transStat!!.toInt() in 201..299){
//                                        showAlarmNotification(
//                                            this@TrxPendingService,
//                                            "Transaksi ${trx.trxId} gagal",
//                                            "${trxpending.dsc} gagal",
//                                            trx.trxId!!.toInt()
//                                        )
//
//                                        val filteredTrxPending = trxPendingSaved.filter { trxfilter -> trxfilter.kode_provider != trx!!.productCode }
//                                        sharedPrefDataSource.setTransactionPending(filteredTrxPending)
//                                    }
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
        return START_STICKY
    }

    private fun cancelAlarm() {
        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, TrxPendingReceiver::class.java)
        val requestCode = 0
        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0)
        pendingIntent.cancel()

        alarmManager.cancel(pendingIntent)
    }

//    private fun showAlarmNotification(context: Context, title: String, message: String, notifId: Int) {
//
//        val channelId = "Channel_1"
//        val channelName = "Transaction channel"
//
//        val notificationManagerCompat = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//        val builder = NotificationCompat.Builder(context, channelId)
//            .setSmallIcon(R.mipmap.ic_stat_btmnavbar_home)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setColor(ContextCompat.getColor(context, android.R.color.white))
//            .setSound(alarmSound)
//            .setDefaults(Notification.DEFAULT_ALL)
//
//        /*
//        Untuk android Oreo ke atas perlu menambahkan notification channel
//        Materi ini akan dibahas lebih lanjut di modul extended
//         */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            /* Create or update. */
//            val channel = NotificationChannel(channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_DEFAULT)
//            builder.setChannelId(channelId).setDefaults(NotificationCompat.DEFAULT_ALL).priority = NotificationCompat.PRIORITY_HIGH
//            notificationManagerCompat.createNotificationChannel(channel)
//        }
//
//        val notification = builder.build()
//        notificationManagerCompat.notify(notifId, notification)
//
//    }

}