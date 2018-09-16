package com.shank.myinstagram.common.firebase

<<<<<<< HEAD
import com.google.android.gms.tasks.Task
import com.shank.myinstagram.common.AuthManager
import com.shank.myinstagram.common.toUnit
=======
import com.shank.myinstagram.common.AuthManager
>>>>>>> f9f35d23b66606e41c731143864e04ee19934201
import com.shank.myinstagram.data.firebase.common.auth


//реализация authManager(выход из аккаунта)
class FirebaseAuthManager: AuthManager {
<<<<<<< HEAD

    //регистрация профиля
    override fun signIn(email: String, password: String): Task<Unit> =
        auth.signInWithEmailAndPassword(email, password).toUnit()

    //выход из профиля
    override fun signOut() { auth.signOut() }
=======
    override fun signOut() {
        //выход из профиля
        auth.signOut()
    }
>>>>>>> f9f35d23b66606e41c731143864e04ee19934201
}