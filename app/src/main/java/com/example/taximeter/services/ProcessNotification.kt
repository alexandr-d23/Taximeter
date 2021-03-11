package com.example.taximeter.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.taximeter.R
import com.example.taximeter.ui.MainActivity

class ProcessNotification(
    private val context: Context
) {
    private val CHANNEL_ID = "DANIL_JOB_CHANNEL_ID"
    private val CHANNEL_NAME = "DANIL_JOB_CHANNEL_NAME"
    private val NOTIFICATION_ID = 0
    private lateinit var intent : Intent
    private lateinit var pendingIntent : PendingIntent
    private lateinit var notification : Notification

    init {
        createNotificationChannel()
        intent = Intent(context, MainActivity::class.java)
        pendingIntent = PendingIntent.getActivity(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notification = NotificationCompat.Builder(context, CHANNEL_ID).run {
            setContentTitle("Данил рубит бабосы")
            setContentText("Мигои бери трубку пёс, когда набирает босс")
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentIntent(pendingIntent)
            build()
        }
    }

    fun getNotification() : Notification = notification

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}