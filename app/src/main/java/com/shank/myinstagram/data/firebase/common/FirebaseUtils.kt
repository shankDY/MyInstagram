package com.shank.myinstagram.data.firebase.common

import android.arch.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.shank.myinstagram.model.Comment
import com.shank.myinstagram.model.FeedPost
import com.shank.myinstagram.model.User

//получаем экземпляр класса. Который поможет нам с авторизацией пользователей
val auth: FirebaseAuth = FirebaseAuth.getInstance()
//ссылка на базу данных. Данный класс поможет нам работать с базой данных
val database: DatabaseReference = FirebaseDatabase.getInstance().reference
//ссылка на хранилище. Данный класс поможет нам работать с хранилищем
val storage: StorageReference = FirebaseStorage.getInstance().reference

//получаем нашего зареганного юзера
fun currentUid(): String? = auth.currentUser?.uid

//функция расширения, с помощью которой получаем uid юзера
fun DataSnapshot.asUser(): User? =
        getValue(User::class.java)?.copy(uid = key!!)

//функция расширения, с помощью которой получаем пост по  uid юзера
fun DataSnapshot.asFeedPost(): FeedPost? =
        getValue(FeedPost::class.java)?.copy(id = key!!)

fun DataSnapshot.asComment(): Comment? =
        getValue(Comment::class.java)?.copy(id = key!!)

//функция расширения , которая добавляет юзера в фоловеры, либо удаляет его
fun DatabaseReference.setValueTrue0rRemove(value: Boolean) =
        if (value) setValue(true) else removeValue()

//возвращает нам liveDATA
fun DatabaseReference.liveData(): LiveData<DataSnapshot> = FirebaseLiveData(this)

