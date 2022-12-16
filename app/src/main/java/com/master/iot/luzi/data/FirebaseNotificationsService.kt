package com.master.iot.luzi.data

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.master.iot.luzi.ui.HomeActivity


class FirebaseNotificationsService : FirebaseMessagingService()  {

    /* Add notifications when app is running */
/*    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationData = remoteMessage.data
        val notificationIntent = Intent(this, HomeActivity::class.java)
//        notificationIntent.putExtra(NOTIFICATION_MESSAGE, notificationData[NOTIFICATION_DATA])
//        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this)
                .setContentTitle("hola")
                .setContentText("descripcion")
                //.setSmallIcon(R.drawable.ic_alert)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                //.setColor(resources.getColor(R.color.snackbarAlertColor))
                .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, builder.build())
    }*/
}