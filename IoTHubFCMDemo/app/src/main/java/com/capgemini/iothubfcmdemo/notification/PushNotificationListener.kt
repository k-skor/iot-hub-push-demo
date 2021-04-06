package com.capgemini.iothubfcmdemo.notification

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.RemoteMessage
import com.microsoft.windowsazure.messaging.notificationhubs.NotificationListener

class PushNotificationListener(val handler: NotificationHandler) : NotificationListener {
    companion object {
        const val TAG = "PushNotificationListener"
    }
    interface NotificationHandler {
        fun onNotificationReceived(title: String, body: String, data: Map<String, String>)
    }
    override fun onPushNotificationReceived(p0: Context?, p1: RemoteMessage?) {

        p1?.notification?.let {

            val title = it.title
            val body = it.body
            val data = p1.data

            Log.d(TAG, "Message Notification Title: $title")
            Log.d(TAG, "Message Notification Body: $body")

            for (entry in data.entries) {
                Log.d(TAG, "key, " + entry.key + " value " + entry.value)
            }

            handler.onNotificationReceived(title ?: "", body ?: "", data)
        }
    }

}