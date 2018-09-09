package com.shank.myinstagram.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

//получаем экземпляр класса. Который поможет нам с авторизацией пользователей
val auth: FirebaseAuth = FirebaseAuth.getInstance()
//ссылка на базу данных. Данный класс поможет нам работать с базой данных
val database: DatabaseReference = FirebaseDatabase.getInstance().reference
//ссылка на хранилище. Данный класс поможет нам работать с хранилищем
val storage: StorageReference = FirebaseStorage.getInstance().reference

fun currentUid(): String? = auth.currentUser?.uid