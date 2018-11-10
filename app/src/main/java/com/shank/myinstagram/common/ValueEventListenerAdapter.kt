package com.shank.myinstagram.common

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

//Тип с одним значением: объект Unit. Этот тип соответствует void типу в Java.
class ValueEventListenerAdapter(val handler:(DataSnapshot)-> Unit): ValueEventListener {
    private val TAG = "ValueEventListenerAdapter"

    //вызывается, когда происходит выбор данных
    override fun onDataChange(data: DataSnapshot) {
        handler(data)
    }

    //вызывается, когда происходит ошибка или отмена действия по каким-то причинам
    @SuppressLint("LongLogTag")
    override fun onCancelled(error: DatabaseError) {
        Log.d(TAG, "onCancelled", error.toException())
    }

}