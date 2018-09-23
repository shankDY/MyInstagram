package com.shank.myinstagram.screens.share

import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks
import com.shank.myinstagram.data.FeedPostsRepository
import com.shank.myinstagram.data.UsersRepository
import com.shank.myinstagram.model.FeedPost
import com.shank.myinstagram.model.User
import com.shank.myinstagram.screens.common.BaseViewModel

class ShareViewModel(private val feedPostRepo: FeedPostsRepository,
                     private val usersRepo: UsersRepository,
                     onFailureListener: OnFailureListener): BaseViewModel(onFailureListener) {

    val user = usersRepo.getUser()

    fun share(user: User, imageUri: Uri?, caption: String) {
        if (imageUri != null) {
            usersRepo.uploadUserImage(user.uid, imageUri).onSuccessTask { downloadUrl ->
                Tasks.whenAll(
                        usersRepo.setUserImage(user.uid, downloadUrl!!),
                        feedPostRepo.createFeedpost(user.uid, mkFeedPost(user, caption,
                                downloadUrl.toString()))
                )
            }.addOnFailureListener(onFailureListener)
        }
    }


    //заполняем наш dataClass feedposts
    private fun mkFeedPost(user: User,caption: String, imageDownloadUrl: String): FeedPost {
        return FeedPost(
                uid = user.uid,
                username = user.username,
                image = imageDownloadUrl,
                caption = caption,
                photo = user.photo
        )
    }
}