package com.pasukanlangit.id.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class NetworkConnectivity(
    private val appExecutors: AppExecutors,
    private val context: Context
) {
    @Synchronized
    fun checkInternetConnection(callback: ConnectivityCallback) {
        appExecutors.getNetworkIO().execute {
            if (isNetworkAvailable) {
                var connection: HttpsURLConnection? = null
                try {
                    connection = (URL("https://clients3.google.com/generate_204").openConnection() as HttpsURLConnection).apply {
                        setRequestProperty("User-Agent", "Android")
                        setRequestProperty("Connection", "close")
                        connectTimeout = 1000
                        connect()
                    }
                    val isConnected =
                        connection.responseCode == 204 && connection.contentLength == 0
                    postCallback(callback, isConnected)
                    connection.disconnect()
                } catch (e: Exception) {
                    postCallback(callback, false)
                    connection?.disconnect()
                }
            } else {
                postCallback(callback, false)
            }
        }
    }

    private val isNetworkAvailable: Boolean
        get() {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager? ?: return false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val cap = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
                return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val networks = cm.allNetworks
                networks.forEach {
                    val nInfo = cm.getNetworkInfo(it)
                    if (nInfo != null && nInfo.isConnected) return true
                }
            }
            return false
        }

    private fun postCallback(callBack: ConnectivityCallback, isConnected: Boolean) {
        appExecutors.mainThread().execute { callBack.onDetected(isConnected) }
    }

    interface ConnectivityCallback {
        fun onDetected(isConnected: Boolean)
    }
}