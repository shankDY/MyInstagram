package com.shank.myinstagram.screens.login

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.shank.myinstagram.R
import com.shank.myinstagram.common.AuthManager
import com.shank.myinstagram.common.SingleLiveEvent
import com.shank.myinstagram.screens.common.CommonViewModel

class LoginViewModel(private  val authManager: AuthManager,
                     private val app: Application,
                     private val commmonViewModel: CommonViewModel,
                     private val onFailureListener: OnFailureListener): ViewModel() {

    private val _goToHomeScreen = SingleLiveEvent<Unit>()
    //ссылка на переход на homeActivity
    val goToHomeScreen: LiveData<Unit> = _goToHomeScreen

    private val _goToRegisterScreen = SingleLiveEvent<Unit>()
    //ссылка на переход на homeActivity
    val goToRegisterScreen: LiveData<Unit> = _goToRegisterScreen

    fun onLoginClick(email: String, password: String) {
        if (validate(email, password)) {
            authManager.signIn(email, password).addOnSuccessListener {
                _goToHomeScreen.call()
            }.addOnFailureListener(onFailureListener)
        } else {
            //application - context, делаем так, т.к ViewModel не должна иметь ссылку на активити
            commmonViewModel.setErrorMessage(app.getString(R.string.enter_email_and_password))
        }
    }

    //проверка пустой пороль и емаил или нет
    private fun validate(email:String,pass:String):Boolean = email.isNotEmpty() and pass.isNotEmpty()

    fun onRegisterClick() {
        _goToRegisterScreen.call()
    }

}