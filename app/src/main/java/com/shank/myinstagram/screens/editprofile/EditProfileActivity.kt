package com.shank.myinstagram.screens.editprofile

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.data.firebase.common.FirebaseHelper
import com.shank.myinstagram.model.User
import com.shank.myinstagram.screens.common.*
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : BaseActivity(), PasswordDialog.Listener {
    private lateinit var mPendingUser: User // юзер ожидающий изменения
    private lateinit var mUser: User
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mCamera: CameraHelper
    private lateinit var mViewModel: EditProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        //вспомогательный класс, который поможет нам работать с firebase
        mFirebase = FirebaseHelper(this)


        //подключаем вьюмодел для данного активити
        mViewModel = initViewModel()

        //получаем текущего юзера
        mViewModel.user.observe(this, Observer{it.let{

            // когда мы получим ответ с firebase, то переконвертируем в удобный нам объект(class User)
            mUser = it!!
            //мы получаем данные с бд и проставляем их в наши editText
            //.EDITABLE можно не использовать, т.к и т.к в editText Buffer type
            // не используется и юзается EDITABLE
            name_input.setText(mUser.name)
            username_input.setText(mUser.username)
            website_input.setText(mUser.website)
            bio_input.setText(mUser.bio)
            email_input.setText(mUser.email)
            phone_input.setText(mUser.phone?.toString())
            profile_image.loadUserPhoto(mUser.photo)

        }})

        // вспомогательный класс, который позволяет нам получить сделать фото с камеры
        mCamera = CameraHelper(this)

        //зыкрываем активити(завершаем ее )
        back_image.setOnClickListener{ finish() }

        //сохраняем изменения профиля юзера  в базу данных
        save_image.setOnClickListener {updateProfile()}

        //по клику на текст предоставить юзеру возможность изменить фото
        change_photo_text.setOnClickListener { mCamera.takeCameraPicture() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //если реквест код соотвествует нашей константе(то это тот ответ, который нам нужен)
        // и действие выполнено успешно, то загружаем фотку в бд
       if (requestCode == mCamera.REQUEST_CODE && resultCode == RESULT_OK ){

           // upload photo to firebase storage
           mViewModel.uploadAndSetUserPhoto(mCamera.imageUri!!)
       }
    }



    // метод записи измененных данных в бд
    private fun updateProfile() {
        //для этого нам надо:
        // get user from inputs
        mPendingUser = readInputs()
        //validate(проверка, что поля заполнены)
        val error = validate(mPendingUser)
        if (error == null){
            if (mPendingUser.email == mUser.email){
                //update user
                updateUser(mPendingUser)
            } else{

                //первый параметр fragment manager, второй - тег, который нужен андроиду,
                // чтобы сохранять состояние диалога, когда например приложение  свернуто
                PasswordDialog().show(supportFragmentManager,"password_dialog")


            }
        }
        else{
            showToast(error)
        }
    }

    //читаем с editText данные введенные пользователем и помещаем в наш DAO
private fun readInputs(): User {
        return User(
                name = name_input.text.toString(),
                username = username_input.text.toString(),
                email = email_input.text.toString(),
                website = website_input.text.toStringOrNull(),
                bio = bio_input.text.toStringOrNull(),
                //если мы укажим пустую строку в телефоне, то у нас возратиться null иначе кастим полученные данные в Long
                phone =  phone_input.text.toString().toLongOrNull()
        )
}


    //получем пороль пользователя с нашей диаложки
    override fun onPasswordConfirm(password: String) {



        //добавим проверку пустой пароль или нет
        if (password.isNotEmpty()) {
            mViewModel.updateEmail(
                    currentEmail = mUser.email,
                    newEmail = mPendingUser.email,
                    password = password)
                    .addOnSuccessListener { updateUser(mPendingUser) }
        }else{
            showToast(getString(R.string.you_should_enter_your_password))
        }
    }


    private fun updateUser(user: User) {

        mViewModel.updateUserProfile(currentUser = mUser, newUser = user)
                .addOnSuccessListener {
                    showToast(getString(R.string.profile_saved))
                    finish()
                }
    }

    //если все поля заполнены возращаем null, иначе ошибку
    private fun validate(user: User): String? = when{
                user.name.isEmpty() -> getString(R.string.please_enter_name)
                user.username.isEmpty() -> getString(R.string.please_enter_username)
                user.email.isEmpty() -> getString(R.string.please_enter_email)
                else -> null
    }


    companion object {
        const val TAG = "EditProfileActivity"
    }
}

