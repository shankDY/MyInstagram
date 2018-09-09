package com.shank.myinstagram.utils

import android.app.Activity
import com.google.firebase.database.DatabaseReference

class FirebaseHelper(private val activity: Activity){



    // в firebase берем users и в ней берем по id юзеров
    fun currentUserReference(): DatabaseReference =
            database.child("users").child(currentUid()!!)

}