package com.mobile.ta.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.mobile.ta.service.MessagingService

class MessagingServiceRestarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MessagingService", "Restarter" + MessagingService.isRunning.toString())
//        if (!MessagingService.isRunning) {
//            context.startService(Intent(context, MessagingService::class.java))
//            MessagingService.isRunning = true
//        }
    }
}