package com.shank.myinstagram.screens.notification

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.util.Log
import com.shank.myinstagram.common.BaseEventListener
import com.shank.myinstagram.common.Event
import com.shank.myinstagram.common.EventBus
import com.shank.myinstagram.data.FeedPostsRepository
import com.shank.myinstagram.data.NotificationsRepository
import com.shank.myinstagram.data.UsersRepository
import com.shank.myinstagram.data.common.observeFirstNotNull
import com.shank.myinstagram.data.common.zip
import com.shank.myinstagram.model.Notification
import com.shank.myinstagram.model.NotificationType


//custom lifecycleOwner
//данный класс будет слушать наши events
class NotificationsCreator(private val notificationsRepo: NotificationsRepository,
                           private val usersRepo: UsersRepository,
                           private val feedPostsRepo: FeedPostsRepository) : BaseEventListener() {

    init {

        //слушаем события
        EventBus.events.observe(this, Observer {
            it?.let{event ->
                when(event){
                    //follow
                    is Event.CreateFollow -> {
                        //получаем нашего юзера, но не каждый раз, а один раз
                        getUser(event.fromUid).observeFirstNotNull(this) {user ->
                            //создаем нотификацию для follow
                            val notification = Notification(
                                    uid = user.uid,
                                    username = user.username,
                                    photo = user.photo,
                                    type = NotificationType.Follow)
                            //передаем нотификацию в репозиторий
                            notificationsRepo.createNotification(event.toUid, notification)
                                    .addOnFailureListener {
                                        Log.d(TAG, "Failed to create notification", it)
                                    }
                        }
                    }
                    //like
                    is Event.CreateLike -> {
                        val userData = usersRepo.getUser(event.uid)
                        val postData = feedPostsRepo.getFeedPost(uid = event.uid, postId = event.postId)

                        //объединяем два liveData, чтобы у них получить одно значение
                        userData.zip(postData).observeFirstNotNull(this) { (user, post) ->
                            //создали нотификацию для лайка
                            val notification = Notification(
                                    uid = user.uid,
                                    username = user.username,
                                    photo = user.photo,
                                    postId = post.id,
                                    postImage = post.image,
                                    type = NotificationType.Like)

                            notificationsRepo.createNotification(post.uid, notification)
                                    .addOnFailureListener {
                                        Log.d(TAG, "Failed to create notification", it)
                                    }
                        }
                    }

                    //comment
                    is Event.CreateComment -> {
                        feedPostsRepo.getFeedPost(uid = event.comment.uid, postId = event.postId)
                                .observeFirstNotNull(this) {post ->

                                    //создали нотификацию для comments
                                    val notification = Notification(
                                            uid = event.comment.uid,
                                            username = event.comment.username,
                                            photo = event.comment.photo,
                                            postId = event.postId,
                                            postImage = post.image,
                                            commentText = event.comment.text,
                                            type = NotificationType.Comment)

                                    notificationsRepo.createNotification(post.uid, notification)
                                            .addOnFailureListener {
                                                Log.d(TAG, "Failed to create notification", it)
                                            }
                                }
                    }
                }
            }
        })
    }

    //получаем пользователя по uid
    private fun getUser(uid: String) = usersRepo.getUser(uid)

    companion object {
        const val TAG = "NotificationsCreator"
    }
}