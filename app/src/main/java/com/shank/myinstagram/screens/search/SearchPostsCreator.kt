package com.shank.myinstagram.screens.search

import android.arch.lifecycle.Observer
import android.util.Log
import com.shank.myinstagram.common.BaseEventListener
import com.shank.myinstagram.common.Event
import com.shank.myinstagram.common.EventBus
import com.shank.myinstagram.data.SearchRepository
import com.shank.myinstagram.model.SearchPost

class SearchPostsCreator(searchRepo: SearchRepository): BaseEventListener(){

    init {
        EventBus.events.observe(this, Observer {
            it?.let {event ->
                when(event){
                    is Event.CreateFeedPost -> {
                        //создали пост
                        val searchPost = with(event.post){
                            SearchPost(
                                    image = image,
                                    caption = caption,
                                    postId = id)
                        }
                        //засунули в репозиторий
                        searchRepo.createPost(searchPost).addOnFailureListener {
                            Log.d(TAG, " Failed to create search post for event: $event", it)
                        }
                    }
                    else -> { }
                }
            }
        })
    }

    companion object {
        const val TAG = "SearchPostCreator"
    }
}