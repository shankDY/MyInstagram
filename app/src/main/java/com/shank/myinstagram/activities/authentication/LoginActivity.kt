package com.shank.myinstagram.activities.authentication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.shank.myinstagram.R
import com.shank.myinstagram.activities.bottomActivities.HomeActivity
import com.shank.myinstagram.utils.auth
import com.shank.myinstagram.utils.coordinateBtnAndInputs
import com.shank.myinstagram.utils.showToast
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener, View.OnClickListener {

    private val TAG = "LoginActivity"

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
            R.id.login_btn -> {

                val email = email_input.text.toString()
                val password = password_input.text.toString()
                if (validate(email, password)) {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    showToast(getString(R.string.enter_email_and_password))
                }
            }
            R.id.create_account_text -> {
                startActivity(Intent(this,RegisterActivity::class.java))
                //finish() делать не будем. Т.к в данном случае кнопка назад должна работать
            }
        }
    }

    //проверка пустой пороль и емаил или нет
    private fun validate(email:String,pass:String):Boolean = email.isNotEmpty() and pass.isNotEmpty()
}
