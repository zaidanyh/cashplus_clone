package com.pasukanlangit.cashplus.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.UpdateFirebaseTokenRequest
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.ui.splashscreen.SplashScreen
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.random.Random

class MyFirebase : FirebaseMessagingService() {

    private val sharedPrefDataSource: SharedPrefDataSource by inject()
    private val mainRepository: MainRepository by inject()

    companion object {
        private val TAG = MyFirebase::class.java.simpleName
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val accessToken = sharedPrefDataSource.getAccessToken()
        val uuid = sharedPrefDataSource.getUUID()
        if(accessToken != null && uuid != null){
            CoroutineScope(Dispatchers.IO).launch {
                mainRepository.updateFirebaseToken(
                    request = UpdateFirebaseTokenRequest(
                        uuid = uuid,
                        token = token
                    ),
                    accessToken
                )
            }
        }

        Log.d(TAG, "Refreshed token : $token")
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            sendNotification(it, remoteMessage.data)
        }
    }

    private fun sendNotification(
        remoteMessage: RemoteMessage.Notification,
        data: Map<String, String>
    ) {
        val channelId = getString(R.string.default_notification_channel_id)
        val channelName = getString(R.string.default_notification_channel_name)
        val direction: String? = data["direction"]
        val indexTab: String? = data["index_tab"]
        val inputPrefix: String? = data["input_prefix"]
        val providerCode: String? = data["provider_code"]

        val intent = Intent(this, SplashScreen::class.java)
        val extras = Bundle()
        if (!direction.isNullOrEmpty()) {
            when(direction) {
                "history" -> extras.putString("index_tab", indexTab)
                "provider" -> {
                    extras.putString("index_tab", indexTab)
                    extras.putString("input_prefix", inputPrefix)
                }
                "tagihan" -> extras.putString("index_tab", indexTab)
                "pln" -> extras.putString("index_tab", indexTab)
                "ewallet" -> extras.putString("provider_code", providerCode)
                "pdam" -> extras.putString("index_tab", "4")
                "game" -> extras.putString("provider_code", providerCode)
            }
            extras.putString("direction", direction)
            intent.putExtras(extras)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(remoteMessage.title)
            .setContentText(remoteMessage.body)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(remoteMessage.body)
            )
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        if (remoteMessage.imageUrl != null) {
            val imageBitmap =
                Glide.with(this).asBitmap().load(remoteMessage.imageUrl).submit().get()
            notificationBuilder.setLargeIcon(imageBitmap)
        }

        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationBuilder.setChannelId(channelId)
            mNotificationManager.createNotificationChannel(channel)
        }
        val notification = notificationBuilder.build()
        mNotificationManager.notify(createRandomCode(), notification)
    }

    private fun createRandomCode(): Int = Random.nextInt(0, 10000)
}