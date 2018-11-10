package com.shank.myinstagram.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.shank.myinstagram.model.Comment
import com.shank.myinstagram.model.FeedPost

object EventBus {
    private val liveDataBus = MutableLiveData<Event>()

    val events: LiveData<Event> = liveDataBus
    //публикуем event
    fun publish(event: Event){
        liveDataBus.value = event
    }
}


/**
 * Изолированные классы используются для отражения ограниченных иерархий классов,
 * когда значение может иметь тип только из ограниченного набора, и никакой другой.
 * Они являются, по сути, расширением enum-классов:
 * набор значений enum типа также ограничен,
 * но каждая enum-константа существует только в единственном экземпляре,
 * в то время как наследник изолированного класса может иметь множество
 * экземпляров, которые могут нести в себе какое-то состояние.
 *  **/
sealed class Event{

    data class CreateComment(val postId: String, val comment: Comment):Event()
    data class CreateLike(val postId: String, val uid: String):Event()
    data class CreateFollow(val fromUid: String, val toUid: String):Event()
    data class CreateFeedPost(val post:FeedPost):Event()
}