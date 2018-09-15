package com.shank.myinstagram.data

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.shank.myinstagram.model.FeedPost

//данный репозиторий работает с feedPosts
interface FeedPostsRepository {

    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun getFeedPosts(uid: String): LiveData<List<FeedPost>>
    fun toogleLike(postId: String, uid: String): Task<Unit>
    fun getLikes(postId: String): LiveData<List<FeedPostLike>>

}

data class FeedPostLike(val userId: String)