package com.shank.myinstagram.screens

import android.app.Application
import com.shank.myinstagram.common.firebase.FirebaseAuthManager
import com.shank.myinstagram.data.firebase.FireBaseUsersRepository
import com.shank.myinstagram.data.firebase.FirebaseFeedPostsRepository

//наш синглтон
class InstagramApp: Application() {
    //т.к не во всех случаях он используется. поэтому объявляем в одном месте, а вызываем там ,
    //где понадобится
    val usersRepo by lazy { FireBaseUsersRepository() }
    val feedPostsRepo by lazy { FirebaseFeedPostsRepository() }
    val authManager by lazy { FirebaseAuthManager() }

}