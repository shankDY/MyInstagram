package com.shank.myinstagram.screens.comments

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.shank.myinstagram.data.FeedPostsRepository
import com.shank.myinstagram.data.UsersRepository
import com.shank.myinstagram.model.Comment
import com.shank.myinstagram.model.User
import com.shank.myinstagram.screens.common.BaseViewModel

class CommentsViewModel(private val feedPostsRepo: FeedPostsRepository,
                        usersRepo: UsersRepository,
                        onFailureListener: OnFailureListener): BaseViewModel(onFailureListener) {
    //список комментов
    lateinit var comments: LiveData<List<Comment>>
    //наш юзер
    val user: LiveData<User> = usersRepo.getUser()
    // id поста
    private lateinit var postId: String

    fun init(postId: String) {
        if (!this::postId.isInitialized) {
            this.postId = postId
            //иннициализация списка комеентариев
            comments = feedPostsRepo.getComments(postId)
        }
    }

    fun createComment(text: String, user: User){
        val comment = Comment(
                uid = user.uid,
                username = user.username,
                photo = user.photo,
                text = text)
        feedPostsRepo.createComment(postId,comment).addOnFailureListener(onFailureListener)
    }

}