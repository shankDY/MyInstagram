package com.shank.myinstagram.data.firebase

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
<<<<<<< HEAD:app/src/main/java/com/shank/myinstagram/data/firebase/FireBaseUsersRepository.kt
=======
import com.google.firebase.auth.FirebaseAuth
>>>>>>> f9f35d23b66606e41c731143864e04ee19934201:app/src/main/java/com/shank/myinstagram/data/firebase/FireBaseUsersRepository.kt
import com.shank.myinstagram.common.toUnit
import com.shank.myinstagram.data.UsersRepository
import com.shank.myinstagram.model.User
import com.shank.myinstagram.data.common.map
import com.shank.myinstagram.data.firebase.common.*
<<<<<<< HEAD:app/src/main/java/com/shank/myinstagram/data/firebase/FireBaseUsersRepository.kt
import com.shank.myinstagram.model.FeedPost
=======
>>>>>>> f9f35d23b66606e41c731143864e04ee19934201:app/src/main/java/com/shank/myinstagram/data/firebase/FireBaseUsersRepository.kt


// вся логика реализуется в репозиториях

//данный класс имплементит нужные нам функции UsersRepository
class FireBaseUsersRepository: UsersRepository {


    //создаем feedPosts
    override fun createFeedpost(uid: String, feedpost: FeedPost): Task<Unit> =
        database.child("feed-posts").child(uid).push().setValue(feedpost).toUnit()


    //проставляем фотки в профиль юзера
    override fun setUserImage(uid: String, downloadUri: Uri): Task<Unit> =
        database.child("images").child(uid).push()
                .setValue(downloadUri.toString()).toUnit()



    //загружаем фотки юзеров в сторедж(посты)
    override fun uploadUserImage(uid: String, imageUri: Uri): Task<Uri> {

        //upload image to user folder <- storage
        //lastPathSegment - имя файла
        val usersStorageReference = storage.child("users")
                .child(uid).child("images").child(imageUri.lastPathSegment)

        //загржуаем фотку по нужному адрессу и берем публичный url для нашей фотки,
        // чтобы запостить ее
       return usersStorageReference.putFile(imageUri).onSuccessTask { it ->
                    usersStorageReference.downloadUrl.addOnSuccessListener {
                        Tasks.forResult(it)
                    }
               }
    }


    //создаем юзверя
    override fun createUser(user: User, password: String): Task<Unit> =
        auth.createUserWithEmailAndPassword(user.email,password).onSuccessTask {
            database.child("users").child(it!!.user.uid).setValue(user)
        }.toUnit()


    //проверка уникальности email
    override fun isUserExistsForEmail(email: String): Task<Boolean> =
        auth.fetchSignInMethodsForEmail(email).onSuccessTask {
            val signInMethods = it?.signInMethods?: emptyList<String>()
            Tasks.forResult(signInMethods.isNotEmpty())
        }


    //получаем картинку
    override fun getImages(uid: String): LiveData<List<String>> =
        FirebaseLiveData(database.child("images").child(uid)).map{
            //кастим список стрингов к стринг
            it.children.map { it.getValue(String::class.java)!! }
        }

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
    override fun currentUid() = auth.currentUser?.uid


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

    //загружаем фото профиля юзера в сторедж и получаем на него ссылку
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

    //обновляем фото профиля юзера
    override fun updateUserPhoto(downloadUrl: Uri): Task<Unit> =
            //не умеет firebase разбирать uri в данном методе. он принимает Any. поэтому надо преобразовать в строку
        database.child("users/${currentUid()!!}/photo").setValue(downloadUrl.toString()).toUnit()

    override fun getUser(): LiveData<User> =
            database.child("users").child(currentUid()!!).liveData().map { it.asUser()!! }
}
