package com.shank.myinstagram.screens.home

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.shank.myinstagram.common.SingleLiveEvent
import com.shank.myinstagram.data.FeedPostsRepository
import com.shank.myinstagram.data.common.map
import com.shank.myinstagram.model.FeedPost
import com.shank.myinstagram.screens.common.BaseViewModel

class HomeViewModel(onFailureListener: OnFailureListener,
                    private val feedPostsRepo: FeedPostsRepository) : BaseViewModel(onFailureListener){
    private lateinit var  uid: String
    lateinit var feedPosts: LiveData<List<FeedPost>>
    //карта лайков, где ключ uid , value - лайки
    private var loadedLikes = mapOf<String, LiveData<FeedPostLikes>>()
    private val _goToCommentsScreen = SingleLiveEvent<String>()
    val goToCommentsScreen = _goToCommentsScreen

    fun init(uid: String) {
        //делаем проверку, что наши lateinit переменные инициализированы или нет,
        // чтобы не создавать лишних объектов
        if (!this::uid.isInitialized) {

            //инифциализация uid
            this.uid = uid

            //загружает feedposts и сортирует их по дате добавления
            feedPosts = feedPostsRepo.getFeedPosts(uid).map {
                //сортируем посты по дате добавления
                it.sortedByDescending { it.timestampDate() }
            }
        }
    }
    //переключатель лайков
    fun toogleLike(postId: String) {
        feedPostsRepo.toogleLike(postId, uid).addOnFailureListener(onFailureListener)
    }

    //считываем лайки юзеров
    fun getLikes(postId: String): LiveData<FeedPostLikes>? = loadedLikes[postId]

    //подгружаем лайки
    fun loadLikes(postId: String): LiveData<FeedPostLikes> {
        val existingLoadedLikes = loadedLikes[postId]
        if (loadedLikes[postId] == null){
            val liveData = feedPostsRepo.getLikes(postId).map { likes ->
                FeedPostLikes(
                        //id юзеров, пролайковших пост(количество id будет соответствовать
                        // количеству лайков)
                        likesCount = likes.size,

                        //если id юзера находится в множестве id , кто пролайкал этот пост,
                        // значит мы пролайкали этот пост
                        likedByUser = likes.find { it.userId == uid } != null)
            }
            //заполняем нашу карту
            loadedLikes += postId to liveData
            return liveData
        }else{

            return existingLoadedLikes!!
        }

    }

    //открываем окно для написания комментария
    fun openComments(postid: String) {
        _goToCommentsScreen.value = postid
    }
}
