package com.shank.myinstagram.utils

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

//Тип с одним значением: объект Unit. Этот тип соответствует void типу в Java.
class ValueEventListenerAdapter(val handler:(DataSnapshot)-> Unit): ValueEventListener {
    private val TAG = "ValueEventListenerAdapter"

    override fun onDataChange(data: DataSnapshot) {
        handler(data)
    }

    @SuppressLint("LongLogTag")
    override fun onCancelled(error: DatabaseError) {
        Log.d(TAG, "onCancelled", error.toException())
    }

}