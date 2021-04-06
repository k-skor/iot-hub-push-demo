package com.capgemini.iothubfcmdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.capgemini.iothubfcmdemo.databinding.ActivityMainBinding
import com.capgemini.iothubfcmdemo.notification.PushNotificationListener
import com.capgemini.iothubfcmdemo.ui.RecyclerViewAdapter
import com.google.firebase.messaging.FirebaseMessaging
import com.microsoft.windowsazure.messaging.notificationhubs.NotificationHub

class MainActivity : AppCompatActivity() {
    lateinit var notifications: ArrayList<Map<String, String>>
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        NotificationHub.setListener(PushNotificationListener(object : PushNotificationListener.NotificationHandler {
            override fun onNotificationReceived(title: String, body: String, data: Map<String, String>) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Message Notification:\t$title: $body", Toast.LENGTH_LONG).show()
                    notifications.add(mapOf(
                            "title" to title,
                            "body" to body,
                            "mId" to (data["messageId"] ?: ""),
                            "temp" to (data["temperature"] ?: "")
                    ))
                    adapter.notifyDataSetChanged()
                }
            }
        }))
        NotificationHub.start(application, BuildConfig.hubName, BuildConfig.hubListenConnectionString)
        Log.d("MainActivity", "onCreate: ${NotificationHub.getPushChannel()}," +
                " ${NotificationHub.getTags()}," +
                " ${NotificationHub.getInstallationId()}")
        Log.d("MainActivity", "${FirebaseMessaging.getInstance().token}")

        binding.clear.setOnClickListener {
            reset()
        }
        reset()
    }

    private fun reset() {
        notifications = arrayListOf()
        adapter = RecyclerViewAdapter(notifications)
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = adapter
    }
}