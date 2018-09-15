package com.shank.myinstagram.common.firebase

import com.shank.myinstagram.common.AuthManager
import com.shank.myinstagram.data.firebase.common.auth


//реализация authManager(выход из аккаунта)
class FirebaseAuthManager: AuthManager {
    override fun signOut() {
        //выход из профиля
        auth.signOut()
    }
}