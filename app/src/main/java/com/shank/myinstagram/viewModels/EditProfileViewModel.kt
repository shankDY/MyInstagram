package com.shank.myinstagram.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.shank.myinstagram.model.User
import com.shank.myinstagram.repositories.EditProfileRepository


//вьюМодел отдает нужные поля в репозиторий
//ViewModel хранит данные, связанные с UI, которые не уничтожаются при поворотах приложения.
class EditProfileViewModel(private val repository: EditProfileRepository) : ViewModel(){
    val user: LiveData<User> = repository.getUser()


    //получаем фотку юзера и заливаем ее в бд
    fun uploadAndSetUserPhoto(localImage: Uri): Task<Unit> =
            repository.uploadUserPhoto(localImage).onSuccessTask{ downloadUrl ->
                repository.updateUserPhoto(downloadUrl!!)
        }

    //изменяем email юзера
    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> =
        repository.updateEmail(
                currentEmail = currentEmail,
                newEmail = newEmail,
                password = password)

    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> =
        repository.updateUserProfile(currentUser = currentUser, newUser = newUser)
}