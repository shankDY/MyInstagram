package com.shank.myinstagram.activities.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.shank.myinstagram.R
import com.shank.myinstagram.activities.bottomActivities.HomeActivity
import com.shank.myinstagram.model.Users
import com.shank.myinstagram.utils.coordinateBtnAndInputs
import com.shank.myinstagram.utils.showToast
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_namepass.*

class RegisterActivity : AppCompatActivity(),EmailFragment.Listener,NamePassFragment.Listener {


    private val TAG = "RegisterActivity"
    private var mEmail:String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Log.d(TAG,"onCreate")

        mAuth = FirebaseAuth.getInstance() //сыылка на регистрацию в firebase
        mDatabase = FirebaseDatabase.getInstance().reference // получем ссылку на объект в бд

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
            supportFragmentManager.beginTransaction().add(R.id.frame_layout,EmailFragment()).commit()
    }


    //получаем с фрагмента 1 email
    override fun OnNext(email: String) {
        if (email.isNotEmpty()) {
            mEmail = email
            //проверка уникальности email
            mAuth.fetchSignInMethodsForEmail(email) {signInMethods ->
                    //если signInMethods либо isEmpty = true
                if (signInMethods.isEmpty()) {

                        //если email не пустой, то вызываем(emailFragment заменяется следущим
                    // фрагментом) фрагмент NamePassFragment по нажатию кнопки next
                        supportFragmentManager.beginTransaction()
                                .replace(R.id.frame_layout, NamePassFragment())
                                //сохраняем предыдущий фрагмент в backStack,
                                // чтобы по кнопке назад можно было вернуться на предыдущий фрагмент
                                .addToBackStack(null)
                                .commit()// производим транзакцию
                    } else{
                        showToast(getString(R.string.email_exist))
                    }
            }

        }else{
            showToast(getString(R.string.please_enter_email))
        }
    }

    //получаем с фрагмента 2 fullname и password
    override fun OnRegister(fullname: String, pass: String) {
        //если мы получили все необходимые данные с нашего фрагмента 2
        if (fullname.isNotEmpty() and  pass.isNotEmpty()){
            // тут на всякий случай проверим получен ли email юзера
            val email = mEmail
            //если все ок, то
            if (email != null){
                //создаем аутентикейшен
                mAuth.createUserWithEmailAndPassword(email,pass){
                    val user = mkUser(fullname,email)
                    //создаем профиль пользователя
                    mDatabase.createUser(it.user.uid, user){
                        //если данные успешно добавленны в бд, то переходим на HomeActivity
                        startActivity()
                    }
                }
            }else{
                Log.e(TAG,"OnRegister: email is null")
                showToast(getString(R.string.please_enter_email))
                //в случае чего возротит нас на предыдущий фрагмент
                supportFragmentManager.popBackStack()
            }
        }else{
            showToast(getString(R.string.enter_fullname_and_password))
        }
    }



    //выводим в логи и в тост ошибку. out Any либо * - означает что мы принимаем любой тип объекта
    private fun unknowRegisterError(it: Task<out Any>) {
        Log.e(TAG, "Failed profile register", it.exception)
        showToast(getString(R.string.unknown_error))
    }


    //переход на homeActivity
    private fun startActivity() {
        startActivity(Intent(this,HomeActivity::class.java))
        finish()//убиваем данное активити. по нажатию на кнопку регистрации
    }



    private fun mkUser(fullname: String, email: String): Users{
        val username = mkUsername(fullname)
        return Users(name = fullname,username = username, email = email)
    }

    //пока сделаем такую убогую логику)
    private fun mkUsername(fullname: String): String  = fullname.toLowerCase()
            .replace(" ", ".")


    //Функции расширения createUser, createUserWithEmailAndPassword, которые обрабатывает
    // наши колбеки  и говорят, что все прошло успешно(isSuccessfull) или нет
    private fun  DatabaseReference.createUser(uid: String, user: Users, onSuccess: () -> Unit){
        //создаем профиль пользователя
        val reference = child("users").child(uid)
        reference.setValue(user).addOnCompleteListener{
            //если данные успешно добавленны в бд, то вызываем onSucces(),
            // котоорый говорит нам, что можно переходить на след активити
            if (it.isSuccessful){
                onSuccess()
            }else{
                unknowRegisterError(it)
            }
        }
    }

    private fun FirebaseAuth.createUserWithEmailAndPassword(email: String, password: String,
                                                            onSuccess: (AuthResult) -> Unit){
        createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful){
                onSuccess(it.result)
            }else{
                unknowRegisterError(it)
            }
        }
    }
    private fun FirebaseAuth.fetchSignInMethodsForEmail(email: String,
                                                        onSuccess: (List<String>) -> Unit){
        //проверка уникальности email
        fetchSignInMethodsForEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                //если signInMethods пустой, то создаем пустой лист
                onSuccess(it.result.signInMethods?: emptyList<String>() )
            }else{
                showToast(it.exception!!.message!!)
            }
        }
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
