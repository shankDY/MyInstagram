package com.shank.myinstagram.screens.notification

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.shank.myinstagram.data.NotificationsRepository
import com.shank.myinstagram.model.Notification
import com.shank.myinstagram.screens.common.BaseViewModel

class NotificationsViewModel(private val notificationsRepo: NotificationsRepository
        ,onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener) {

    lateinit var notifications: LiveData<List<Notification>>
    private lateinit var uid: String
    fun init(uid: String) {
        if (!this::uid.isInitialized) {
            this.uid = uid
            //иницилизируем наши нотификации, как только получили uid юзера
            notifications = notificationsRepo.getNotifications(uid)
        }
    }

    //функция, которая позводляет выставить все непрочитанные нотификации, как прочитанные
    fun setNotificationsRead(notifications: List<Notification>) {
        //фидьтруем нотификации так, что выбираем только те, что false
        val ids = notifications.filter {!it.read}.map {it.id}
        if (ids.isNotEmpty()){
            notificationsRepo.setNotificationsRead(uid, ids, true)
                    .addOnFailureListener(onFailureListener)
        }
    }
}