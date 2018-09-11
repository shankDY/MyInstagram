package com.shank.myinstagram.data.firebase

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.shank.myinstagram.data.UsersRepository
import com.shank.myinstagram.model.User
import com.shank.myinstagram.utils.*


// вся логика реализуется в репозиториях

//данный класс имплементит нужные нам функции
class FireBaseUsersRepository: UsersRepository {

    override fun getUsers(): LiveData<List<User>> = database.child("users").liveData().map{
        it.children.map { it.asUser()!! }
    }

    //добавить подписку(подписаться на юзера)
    //fromUid -наш Юзер, toUid - другие пользователи
    override fun addFollow(fromUid: String, toUid: String): Task<Unit> =
            getFollowsRef(fromUid, toUid).setValue(true).toUnit()

    //удалить подписку(отписаться от юзверя)
    override fun deleteFollow(fromUid: String, toUid: String): Task<Unit> =
            getFollowsRef(fromUid, toUid).removeValue().toUnit()

    //добавить подписчика(подписанный на юзера юзверь)
    override fun addFollower(fromUid: String, toUid: String): Task<Unit> =
            getFollowersRef(fromUid, toUid).setValue(true).toUnit()

    //удалить подписчика(юзверь отписался от юзера)
    override fun deleteFollower(fromUid: String, toUid: String): Task<Unit> =
            getFollowersRef(fromUid, toUid).removeValue().toUnit()


    //получаем ссылку на бд с подписками
    private fun getFollowsRef(fromUid: String, toUid: String)=
            database.child("users").child(fromUid).child("follows").child(toUid)

    //получаем ссылку на бд с подписчиками
    private fun getFollowersRef(fromUid: String, toUid: String)=
            database.child("users").child(toUid).child("followers").child(fromUid)

    //ссылка на авторизованного юзера
    override fun currentUid() = FirebaseAuth.getInstance().currentUser?.uid


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
