package com.shank.myinstagram.data.firebase

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.shank.myinstagram.common.toUnit
import com.shank.myinstagram.data.NotificationsRepository
import com.shank.myinstagram.data.common.map
import com.shank.myinstagram.data.firebase.common.FirebaseLiveData
import com.shank.myinstagram.data.firebase.common.database
import com.shank.myinstagram.model.Notification

class FirebaseNotificationsRepository : NotificationsRepository{

    //создаем уведомление
    override fun createNotification(uid: String, notification: Notification): Task<Unit> =
            notificationsRef(uid).push().setValue(notification).toUnit()

    //получаем уведомление
    override fun getNotifications(uid: String): LiveData<List<Notification>> =
            FirebaseLiveData(notificationsRef(uid)).map {
                it.children.map { it.asNotification()!! }
            }

    //отметить уведомления, как прочитанные
    override fun setNotificationsRead(uid: String, ids: List<String>, read: Boolean): Task<Unit> {

        //для каждой нотификации, по ее индификатору, в поле read,
        // будет передаваться значение true or false
        val updatesMap = ids.map { "$it/read" to read }.toMap()
        return notificationsRef(uid).updateChildren(updatesMap).toUnit()
    }

    //ссылка на хранилище нотификейшенов в бд
    private fun notificationsRef(uid: String) =
            database.child("notifications").child(uid)

   // функция расширения, с помощью которой получаем замапенный список notification,
   // где ключ id notification
   private fun DataSnapshot.asNotification() =
           getValue(Notification::class.java)?.copy(id = key!!)
}