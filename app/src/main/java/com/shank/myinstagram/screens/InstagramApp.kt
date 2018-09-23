package com.shank.myinstagram.screens

import android.app.Application
import com.shank.myinstagram.common.firebase.FirebaseAuthManager
import com.shank.myinstagram.data.firebase.FireBaseUsersRepository
import com.shank.myinstagram.data.firebase.FirebaseFeedPostsRepository
import com.shank.myinstagram.data.firebase.FirebaseNotificationsRepository
import com.shank.myinstagram.screens.notification.NotificationsCreator

//наш синглтон
class InstagramApp: Application() {
    //т.к не во всех случаях  используются. поэтому объявляем в одном месте, а вызываем там ,
    //где понадобится
    val usersRepo by lazy { FireBaseUsersRepository() }
    val feedPostsRepo by lazy { FirebaseFeedPostsRepository() }
    val notificationRepo by lazy { FirebaseNotificationsRepository() }
    val authManager by lazy { FirebaseAuthManager() }

    override fun onCreate() {
        super.onCreate()
        //создаем NotificationCreator
        NotificationsCreator(notificationRepo, usersRepo, feedPostsRepo)
    }
}