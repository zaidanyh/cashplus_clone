package com.pasukanlangit.cashplus.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TrxPendingReceiver : BroadcastReceiver() {

    private val trxPendingHelper : TrxPendingHelper by lazy { TrxPendingHelper() }

    override fun onReceive(context: Context, intent: Intent) {
//        val serviceIntent = Intent(context, TrxPendingService::class.java)
//        context.startService(serviceIntent)

        trxPendingHelper.onReceive(context,intent)
    }

}