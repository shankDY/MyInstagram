package com.shank.myinstagram.utils

import android.app.Activity
import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseHelper(private val activity: Activity){
    //получаем экземпляр класса. Который поможет нам с авторизацией пользователей
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    //ссылка на базу данных. Данный класс поможет нам работать с базой данных
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    //ссылка на хранилище. Данный класс поможет нам работать с хранилищем
    val storage: StorageReference = FirebaseStorage.getInstance().reference


    // в firebase берем users и в ней берем по id юзеров
    fun currentUserReference(): DatabaseReference =
            database.child("users").child(currentUid()!!)

    fun currentUid(): String? = auth.currentUser?.uid

    //получаем ссылку на наше хранилище(т.е путь по которому будет лежать наша фотка)
    fun storageRef(): StorageReference = storage.child("users/${currentUid()!!}/photo")

    /** функции расширения   которые обрабатывает наши колбеки  и сообщают об успешности действия**/

    // данная функция сообщает об успешном изменении профиля юзера
    fun updateUser(updates: Map<String, Any?>, onSuccess: () -> Unit){
        database.child("users").child(currentUid()!!).updateChildren(updates).addOnCompleteListener {
            if (it.isSuccessful){
                onSuccess()
            }else{
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    //данная функция сообщает об успешности повторной аутентификации юзера для изменения email
    fun reauthenticate(credential: AuthCredential, onSuccess:() -> Unit){
        auth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    //данная функция сообщает об успешности изменения email в профиле юзера
    fun updateEmail(email: String, onSuccess:() -> Unit){
        auth.currentUser!!.updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    //данная функция говорит об успешности получения ссылки на фото юзера из хранилища
    fun getUserPhotoOfStorage(onSuccess: (Uri) -> Unit ){
        storageRef().downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess(it.result)
            } else {
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    //данная функция сообщает об успешной закачки фото с хранилища в профиль юзера
    fun updateUserPhoto( photoUrl:String, onSuccess: () -> Unit){
        database.child("users/${currentUid()!!}/photo").setValue(photoUrl).addOnCompleteListener{
            if (it.isSuccessful){
                onSuccess()
            }else{
                activity.showToast(it.exception!!.message!!)
            }
        }
    }



    //данная функция сообщает нам об успешности загрузк
    fun downLoadUserPhotoInStorage(photo: Uri, onSuccess: () -> Unit){
        storageRef().putFile(photo).addOnCompleteListener{ it ->
            if(it.isSuccessful){
                onSuccess()
            }else{
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

}