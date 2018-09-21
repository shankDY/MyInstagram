package com.shank.myinstagram.screens.register

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.common.SingleLiveEvent
import com.shank.myinstagram.data.UsersRepository
import com.shank.myinstagram.model.User
import com.shank.myinstagram.screens.common.CommonViewModel

class RegisterViewModel(private val commonViewModel: CommonViewModel,
                        private val app: Application,
                        private val usersRepo: UsersRepository) : ViewModel(){

    private var email: String? = null
    private val _goToNamePassScreen = SingleLiveEvent<Unit>()
    private val _goToHomeScreen = SingleLiveEvent<Unit>()
    private val _goBackToEmailScreen = SingleLiveEvent<Unit>()
    val goToHomeScreen = _goToHomeScreen
    val goBackToEmailScreen = _goBackToEmailScreen
    val goToNamePassScreen = _goToNamePassScreen

    //обрабатываем полученный с фрагмента email, если не пустой, то проверяем на уникальность
    fun onEmailEntered(email: String) {
        if (email.isNotEmpty()) {
            this.email = email
            //проверка уникальности email
            usersRepo.isUserExistsForEmail(email).addOnSuccessListener { exist ->
                if (!exist){
                    _goToNamePassScreen.call()
                }else{
                    commonViewModel.setErrorMessage(app.getString(R.string.email_exist))
                }
            }
        }else{
            commonViewModel.setErrorMessage(app.getString(R.string.please_enter_email))
        }

    }

    fun onRegister(fullname: String, password: String) {
        //если мы получили все необходимые данные с нашего фрагмента 2
        if (fullname.isNotEmpty() and  password.isNotEmpty()){
            // тут на всякий случай проверим получен ли email юзера
            val localEmail = email
            //если все ок, то
            if (localEmail != null){
                usersRepo.createUser(mkUser(fullname,localEmail),password).addOnSuccessListener {
                    //переход на HomeActivity
                    _goToHomeScreen.call()
                }

            }else{
                Log.e(RegisterActivity.TAG,"OnRegister: email is null")
                commonViewModel.setErrorMessage(app.getString(R.string.please_enter_email))
                //в случае чего возротит нас на предыдущий фрагмент
                _goBackToEmailScreen.call()
            }
        }else{
            commonViewModel.setErrorMessage(app.getString(R.string.enter_fullname_and_password))
        }
    }

    private fun mkUser(fullname: String, email: String): User {
        val username = mkUsername(fullname)
        return User(name = fullname,username = username, email = email)
    }

    //пока сделаем такую убогую логику)
    private fun mkUsername(fullname: String): String  = fullname.toLowerCase()
            .replace(" ", ".")
}

