package com.shank.myinstagram.screens.register

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.screens.common.BaseActivity
import com.shank.myinstagram.screens.home.HomeActivity

class RegisterActivity : BaseActivity(), EmailFragment.Listener, NamePassFragment.Listener {

    private lateinit var mViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Log.d(TAG,"onCreate")

        mViewModel = initViewModel()

        mViewModel.goToNamePassScreen.observe(this, Observer {
            //если email не пустой, то вызываем(emailFragment заменяется следущим
            // фрагментом) фрагмент NamePassFragment по нажатию кнопки next
            supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, NamePassFragment())
                    //сохраняем предыдущий фрагмент в backStack,
                    // чтобы по кнопке назад можно было вернуться на предыдущий фрагмент
                    .addToBackStack(null)
                    .commit()// производим транзакцию
        })

        //переход на HomeActivity
        mViewModel.goToHomeScreen.observe(this, Observer {
            startHomeActivity()
        })

        //переход на emailFragment
        mViewModel.goBackToEmailScreen.observe(this, Observer {
            supportFragmentManager.popBackStack()
        })

        /**
         * управление фрагментом. при старте Активити будет вызван EmailFragment.
         * Который будет помещен в frame_layout
        данный код нужно выполнять только один раз, когда активити уже созданно.
        если активити перезапускается,
        то нам не надо создавать новый фрагмент
        для этого создадим проверку.
         Если savedInstanceState == null, значит активити только созданно.
        и тогда мы создаем фрагмент
         **/
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment()).commit()
    }


    //получаем с фрагмента 1 email
    override fun OnNext(email: String) {
        mViewModel.onEmailEntered(email)
    }

    //получаем с фрагмента 2 fullname и password
    override fun OnRegister(fullname: String, password: String) {
        mViewModel.onRegister(fullname, password)
    }

    //переход на homeActivity
    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()//убиваем данное активити. по нажатию на кнопку регистрации
    }



    companion object {
        const val TAG = "RegisterActivity"
    }

}


