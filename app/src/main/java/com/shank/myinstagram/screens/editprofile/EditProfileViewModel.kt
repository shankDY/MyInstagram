package com.shank.myinstagram.screens.editprofile

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.shank.myinstagram.data.UsersRepository
import com.shank.myinstagram.model.User
import com.shank.myinstagram.screens.common.BaseViewModel


//вьюМодел отдает нужные поля в репозиторий
//ViewModel хранит данные, связанные с UI, которые не уничтожаются при поворотах приложения.
class EditProfileViewModel(onFailureListener: OnFailureListener,
                           private val usersRepo: UsersRepository) : BaseViewModel(onFailureListener){
    val user: LiveData<User> = usersRepo.getUser()


    //получаем фотку юзера и заливаем ее в бд
    fun uploadAndSetUserPhoto(localImage: Uri): Task<Unit> =
            usersRepo.uploadUserPhoto(localImage).onSuccessTask{ downloadUrl ->
                usersRepo.updateUserPhoto(downloadUrl!!)
        }.addOnFailureListener(onFailureListener)

    //изменяем email юзера
    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> =
        usersRepo.updateEmail(
                currentEmail = currentEmail,
                newEmail = newEmail,
                password = password).addOnFailureListener(onFailureListener)

    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> =
        usersRepo.updateUserProfile(currentUser = currentUser, newUser = newUser)
                .addOnFailureListener(onFailureListener)
}