package com.shank.myinstagram.utils

import android.arch.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

//возвращает нам liveDATA
fun DatabaseReference.liveData(): LiveData<DataSnapshot> = FirebaseLiveData(this)