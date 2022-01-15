package com.manuel.delivery.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.manuel.delivery.channel.NotificationHelper
import com.manuel.delivery.utils.Constants

class MyFirebaseMessagingClient : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        val notificationId = data[Constants.PROP_NOTIFICATION_ID]
        val title = data[Constants.PROP_TITLE]
        val body = data[Constants.PROP_BODY]
        if (!title.isNullOrBlank() && !body.isNullOrBlank() && !notificationId.isNullOrBlank()) {
            showNotification(title, body, notificationId)
        }
    }

    private fun showNotification(title: String, body: String, notificationId: String) {
        val helper = NotificationHelper(baseContext)
        val builder = helper.getNotification(title, body)
        val id = notificationId.toInt()
        helper.getManager().notify(id, builder.build())
    }
}