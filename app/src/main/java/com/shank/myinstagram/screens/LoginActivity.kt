package com.shank.myinstagram.screens

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.shank.myinstagram.R
import com.shank.myinstagram.data.firebase.common.auth
import com.shank.myinstagram.screens.common.BaseActivity
import com.shank.myinstagram.screens.common.coordinateBtnAndInputs
import com.shank.myinstagram.screens.common.setupAuthGuard
import com.shank.myinstagram.screens.common.showToast
import com.shank.myinstagram.screens.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : BaseActivity(), KeyboardVisibilityEventListener, View.OnClickListener {

    private lateinit var mViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")


        //подключаем слушатель , который следит за положением клавиатуры
        KeyboardVisibilityEvent.setEventListener(this,this)

        // вызываем метод , который позволит делать кнопку аутивной , если нужные поля заполнены
        coordinateBtnAndInputs(login_btn, email_input, password_input)

        //вешаем слушатели на кнопку и текс создать аккаунт
        login_btn.setOnClickListener(this)
        create_account_text.setOnClickListener(this)

            mViewModel = initViewModel()
            mViewModel.goToHomeScreen.observe(this, Observer {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            })

            mViewModel.goToRegisterScreen.observe(this, Observer {
                startActivity(Intent(this, RegisterActivity::class.java))
            })

    }



    //переопределяем метод onVisibilityChanged - который позволяет отсеживать положение клавиатуры
    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        //если клавиатура открыта, то проскролить вниз. иначе вверх
        if (isKeyboardOpen){
            create_account_text.visibility = View.GONE // скрываем текс внижнем тулбаре
        }
        else {
            create_account_text.visibility = View.VISIBLE
        }
    }

    //безопасный вызов можно убрать. т.к тут горантированно будет не null.
    // т.к если мы нажимаем на кнопку, то передается view
    override fun onClick(view: View) {
        when(view.id){
            R.id.login_btn ->
                mViewModel.onLoginClick(
                        email = email_input.text.toString(),
                        password = password_input.text.toString()
                )
            R.id.create_account_text -> mViewModel.onRegisterClick()
        }
    }

      companion object {
        const val TAG = "LoginActivity"
    }
}
