package com.shank.myinstagram.repositories

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.shank.myinstagram.model.User
import com.shank.myinstagram.utils.*


// вся логика реализуется в репозиториях

interface EditProfileRepository {
    fun getUser(): LiveData<User>
    fun uploadUserPhoto(localImage: Uri): Task<Uri>
    fun updateUserPhoto(downloadUrl: Uri): Task<Unit>
    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit>
    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit>
}

//данный класс имплементит нужные нам функции
class FireBaseEditProfileRepository: EditProfileRepository {


    /**
    обновление полей юзера , которые изменились
    если поле изменилось мы добавляем элемент в карту и  сохраняем изменение в базу
     **/
    override fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> {
        //карта , с измененными полями юзера
        val updatesMap = mutableMapOf<String,Any?>()

        if (newUser.name != currentUser.name) updatesMap["name"] = newUser.name
        if (newUser.username != currentUser.username) updatesMap["username"] = newUser.username
        if (newUser.website != currentUser.website) updatesMap["website"] = newUser.website
        if (newUser.bio != currentUser.bio) updatesMap["bio"] = newUser.bio
        if (newUser.email != currentUser.email) updatesMap["email"] = newUser.email
        if (newUser.phone != currentUser.phone) updatesMap["phone"] = newUser.phone

        return database.child("users").child(currentUid()!!).updateChildren(updatesMap).toUnit()
    }

    //изменение email юзера
    override fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> {
        val currentUser = auth.currentUser

        return if (currentUser != null) {
            val credential = EmailAuthProvider.getCredential(currentEmail, password)
            //реаутентикейт пользователя, для смены email
            currentUser.reauthenticate(credential).onSuccessTask {
                //update email in auth
                currentUser.updateEmail(newEmail)
            }.toUnit()
       } else{
           Tasks.forException(IllegalStateException("User is no authenticated"))
       }
    }

    override fun uploadUserPhoto(localImage: Uri): Task<Uri> {
        //ссылка на storage
        val storageReference = storage.child("users/${currentUid()!!}/photo")
        //полученный uri фотографии, кидаем в наш storage
        return storageReference.putFile(localImage).onSuccessTask { it ->
            //получаем public url для нашей фотки в storage
            storageReference.downloadUrl.addOnSuccessListener {
                Tasks.forResult(it)
            }
        }
    }

    override fun updateUserPhoto(downloadUrl: Uri): Task<Unit> =
            //не умеет firebase разбирать uri в данном методе. он принимает Any. поэтому надо преобразовать в строку
        database.child("users/${currentUid()!!}/photo").setValue(downloadUrl.toString()).toUnit()

    override fun getUser(): LiveData<User> =
            database.child("users").child(currentUid()!!).liveData().map { it.asUser()!! }
}
