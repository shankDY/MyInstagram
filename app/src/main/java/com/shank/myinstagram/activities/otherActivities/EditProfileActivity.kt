package com.shank.myinstagram.activities.otherActivities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.shank.myinstagram.R
import com.shank.myinstagram.model.User
import com.shank.myinstagram.utils.*
import com.shank.myinstagram.views.PasswordDialog
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"
    private lateinit var mPendingUser: User // юзер ожидающий изменения
    private lateinit var mUser: User
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mCamera: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        //вспомогательный класс, который поможет нам работать с firebase
        mFirebase = FirebaseHelper(this)
        // вспомогательный класс, который позволяет нам получить сделать фото с камеры
        mCamera = CameraHelper(this)

        //зыкрываем активити(завершаем ее )
        back_image.setOnClickListener{ finish() }

        //сохраняем изменения профиля юзера  в базу данных
        save_image.setOnClickListener {updateProfile()}

        //по клику на текст предоставить юзеру возможность изменить фото
        change_photo_text.setOnClickListener { mCamera.takeCameraPicture() }

        // в firebase берем users и в ней берем по id юзеров

        mFirebase.currentUserReference().addListenerForSingleValueEvent(ValueEventListenerAdapter {
            // когда мы получим ответ с firebase, то переконвертируем в удобный нам объект(class User)
            mUser = it.asUser()!!
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
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //если реквест код соотвествует нашей константе(то это тот ответ, который нам нужен)
        // и действие выполнено успешно, то загружаем фотку в бд
       if (requestCode == mCamera.REQUEST_CODE && resultCode == RESULT_OK ){

           // upload photo to firebase storage
           mFirebase.downLoadUserPhotoInStorage(mCamera.imageUri!!){
               // получаем ссылку из хранилища на загруженную фотографию
               mFirebase.getUserPhotoOfStorage {
                   val photoUrl = it.toString()
                   //save image to database user.photo
                   mFirebase.updateUserPhoto(photoUrl){
                       //обновляем наш USERS
                       mUser = mUser.copy(photo = photoUrl)
                       profile_image.loadUserPhoto(mUser.photo)
                   }
               }
           }
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
       /**
        есть правило в firebase, если вы хотите изменить имеил, то надо
        аутентифицировать юзера повторно с помощью метода reauthenticate
       чтобы реализовать reauthenticate, нам надо получить пароль юзера, затем:
            1)update email in auth
            2)update user**/

        //добавим проверку пустой пароль или нет
        if (password.isNotEmpty()) {

        val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mFirebase.reauthenticate(credential){
                //update email in auth
                mFirebase.updateEmail(mPendingUser.email) {
                        //update user
                        updateUser(mPendingUser)
                }
            }
        }else{
            showToast(getString(R.string.you_should_enter_your_password))
        }
    }

    /**
        обновление полей юзера , которые изменились
        если поле изменилось мы добавляем элемент в карту и  сохраняем изменение в базу
     **/
    private fun updateUser(user: User) {

        val updatesMap = mutableMapOf<String,Any?>()

        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.website != mUser.website) updatesMap["website"] = user.website
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone

        mFirebase.updateUser(updatesMap) {
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
}

