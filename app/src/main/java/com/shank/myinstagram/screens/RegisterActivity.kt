package com.shank.myinstagram.screens

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shank.myinstagram.R
import com.shank.myinstagram.screens.common.BaseActivity
import com.shank.myinstagram.screens.common.coordinateBtnAndInputs
import com.shank.myinstagram.screens.home.HomeActivity
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_namepass.*

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


//1 email - nextButton
class EmailFragment : Fragment(){
    private lateinit var mListener: Listener

    interface Listener {
        fun OnNext(email:String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_email,container,false)
    }

    //данный метод будет вызыван, когда будет создан OnCreateView (тут можно вызывать view )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // вызываем метод , который позволит делать кнопку аутивной , если нужные поля заполнены
        coordinateBtnAndInputs(nextbutton, email_input_reg)

        nextbutton.setOnClickListener {
            val email = email_input_reg.text.toString()
            mListener.OnNext(email)// передаем наш email в активити
        }

    }

    //для получения ссылки на активити. для того чтобы передать в активити нашь email
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as Listener
    }
}

//2 - full name,password , register button
class NamePassFragment : Fragment(){

    private lateinit var mListener: Listener

    interface Listener {
        fun OnRegister(fullname:String, pass:String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_namepass,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnAndInputs(register_btn, full_name_input, password_input)
        register_btn.setOnClickListener{
            val fullName = full_name_input.text.toString()
            val password = password_input.text.toString()
            mListener.OnRegister(fullName, password)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as Listener
    }
}
