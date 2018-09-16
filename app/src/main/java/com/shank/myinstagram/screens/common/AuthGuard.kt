package com.shank.myinstagram.screens.common

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.google.firebase.auth.FirebaseAuth
import com.shank.myinstagram.data.firebase.common.auth

//сущность, которая отвечает за авторизацию в firebase

class AuthGuard(private val activity: BaseActivity, f: (String) -> Unit) : LifecycleObserver {

    init {

        val user = auth.currentUser
        if (user == null){
            activity.goToLogin()
        }else{
            f(user.uid)
            //добавляем обзервера в случае,если мы уже зареганы, тогда сможем слушать evants
            activity.lifecycle.addObserver(this)
        }
    }

    private  val listener=  FirebaseAuth.AuthStateListener {
        if ( it.currentUser == null){
            //переходим в окно регистрации
            activity.goToLogin()
        }
    }

<<<<<<< HEAD
    //когда активити или фрагмент живы, то мы слушаем колбеки
=======
>>>>>>> f9f35d23b66606e41c731143864e04ee19934201
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){
        auth.addAuthStateListener(listener)
    }

<<<<<<< HEAD
    //когда активити неактивна, то наши слушатели уничтожаются
=======
>>>>>>> f9f35d23b66606e41c731143864e04ee19934201
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(){
        auth.removeAuthStateListener(listener)
    }
}

<<<<<<< HEAD
//функция , которая позволит нам устанавливать authGuard в каждое активити
=======
>>>>>>> f9f35d23b66606e41c731143864e04ee19934201
fun BaseActivity.setupAuthGuard(f: (String) -> Unit){
    AuthGuard(this, f)
}