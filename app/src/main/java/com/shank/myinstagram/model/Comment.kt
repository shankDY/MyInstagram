package com.shank.myinstagram.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*

data class  Comment(val uid: String ="", val username: String="", val text: String="",
                    val photo: String? = null, val timestamp:Any = ServerValue.TIMESTAMP,
                    @get:Exclude val id: String = ""){
    fun timestampDate(): Date = Date(timestamp as Long)
}
