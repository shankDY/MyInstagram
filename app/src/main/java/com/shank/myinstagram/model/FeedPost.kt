package com.shank.myinstagram.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*
//@get: Exclude - чтобы Firebase игнорировала данное поле, не охраняла в бд
// если указать @Exclude - то игнорируется проперти класса , а не поле
data class FeedPost(val uid: String = "", val username: String = "",
                    val image: String = "", val caption: String = "",
                    val comments: List<Comment> = emptyList(),
                    val timestamp: Any = ServerValue.TIMESTAMP, val photo: String? = null,
                    @get:Exclude val id: String = "", @get:Exclude val commentsCount: Int = 0) {
    //получаем наш timeStamp кастим его к лонгу(best практика, в бд проставлять таймстемп,
    // чтобы не зависить от клиента)
    fun timestampDate(): Date = Date(timestamp as Long)
}