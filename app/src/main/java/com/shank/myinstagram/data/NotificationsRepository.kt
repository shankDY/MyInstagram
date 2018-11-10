package com.shank.myinstagram.data

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.shank.myinstagram.model.Notification

interface NotificationsRepository {
    fun createNotification(uid: String, notification: Notification): Task<Unit>
    fun getNotifications(uid: String): LiveData<List<Notification>>
    fun setNotificationsRead(uid: String, ids: List<String>, read: Boolean): Task<Unit>
}