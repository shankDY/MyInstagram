package com.shank.myinstagram.model

import com.google.firebase.database.Exclude


//caption - текст запроса
data class SearchPost(val image: String = "", val caption: String = "", val postId: String ="",
                      @get:Exclude val id: String = "") {
}