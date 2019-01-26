package io.github.artenes.ostatic.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class Notification {

    companion object {

        fun createChannel(context: Context, id: String, name: String, description: String, importance: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(id, name, importance)
                channel.description = description
                context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
            }
        }

    }

}