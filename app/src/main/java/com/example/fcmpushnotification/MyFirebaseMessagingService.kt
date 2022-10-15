package com.example.fcmpushnotification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelId = "notification_channel"
const val channelName = "com.example.fcmpushnotification"

// Do everything related to Firebase here only
class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Generate the notification when the receive the Firebase message
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // If the remote message notification is not null, then generate the notification
        if (remoteMessage.notification != null){
            generateNotification(remoteMessage.notification!!.title!!,remoteMessage.notification!!.body!!)
        }
    }

    // Attach the notification created with the custom layout
    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews("com.example.fcmpushnotification", R.layout.notification)

        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.buythediplogo)

        return remoteView;
    }

    // Generate the notification
    private fun generateNotification(title: String, message: String){
        val intent = Intent(this,MainActivity::class.java)

        // Clear all activities and put this activity in the top
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // A Pending intent means we use the intent in the future
        // Flag One Shot means using the intent activity just one, when user click then destroy
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        // Use Channel id, channel name make the builder and setup some details of the notification
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.buythediplogo)
            .setAutoCancel(false)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        // Set the content to the builder
        builder = builder.setContent(getRemoteView(title, message))

        // Initialize the notification manager
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Check the client android version is grater than the current version to make sure the features is working in latest version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0,builder.build())
    }
}