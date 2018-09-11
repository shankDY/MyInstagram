package com.shank.myinstagram.data

import com.google.android.gms.tasks.Task

//данный репозиторий работает с feedPosts
interface FeedPostsRepository {

    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
}