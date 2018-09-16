package com.shank.myinstagram.common.firebase

import com.google.android.gms.tasks.Task
import com.shank.myinstagram.common.AuthManager
import com.shank.myinstagram.common.toUnit
import com.shank.myinstagram.data.firebase.common.auth


//реализация authManager(выход из аккаунта)
class FirebaseAuthManager: AuthManager {

    //регистрация профиля
    override fun signIn(email: String, password: String): Task<Unit> =
        auth.signInWithEmailAndPassword(email, password).toUnit()

    //выход из профиля
    override fun signOut() { auth.signOut() }
}