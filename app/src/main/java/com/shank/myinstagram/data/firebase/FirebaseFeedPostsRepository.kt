package com.shank.myinstagram.data.firebase

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.shank.myinstagram.common.*
import com.shank.myinstagram.data.FeedPostsRepository
import com.shank.myinstagram.data.FeedPostLike
import com.shank.myinstagram.data.common.map
import com.shank.myinstagram.data.firebase.common.*
import com.shank.myinstagram.model.Comment
import com.shank.myinstagram.model.FeedPost

//реализация фйнкция FeedPostRepository
class FirebaseFeedPostsRepository: FeedPostsRepository {

    //создаем комментарий
    override fun createComment(postId: String, comment: Comment): Task<Unit> =
        database.child("comments").child(postId).push().setValue(comment).toUnit()
                .addOnSuccessListener {
                    //создаем ивент
                    EventBus.publish(Event.CreateComment(postId, comment))
                }

    //вычитываем наши комменты
    override fun getComments(postId: String): LiveData<List<Comment>> =
        FirebaseLiveData(database.child("comments").child(postId)).map {
            //получили список коментиков и замапили его
            it.children.map {it.asComment()!!}
        }

    //создаем feedPosts
    override fun createFeedpost(uid: String, feedpost: FeedPost): Task<Unit> {
        val reference = database.child("feed-posts").child(uid).push()
        return reference.setValue(feedpost).toUnit()
                .addOnSuccessListener {
                    // новый ключ, который мы засунули в firebase будет индификатором feedPosts
                    EventBus.publish(Event.CreateFeedPost(feedpost.copy(id = reference.key!!)))
                }
    }

    //читаем лайки
    override fun getLikes(postId: String): LiveData<List<FeedPostLike>> =
        FirebaseLiveData(database.child("likes").child(postId)).map {
            //когда мы вычитываем посты, то нам приходя ключи от uid
            //берем всех юзеров и мапим на FeedPostLike(делаем из них класс,
            // чтобы не возвращть просто стринг)
            it.children.map { FeedPostLike(it.key!!) }
        }

    //переключатель лайков
    override fun toogleLike(postId: String, uid: String): Task<Unit> {
        //получаем ссылку на место хранения лайков
        val reference = database.child("likes").child(postId).child(uid)
        return task { taskSource ->
            reference.addListenerForSingleValueEvent(ValueEventListenerAdapter {like ->
                //если нода существует(поставили уже лайк), то удаляем лайк, иначе
                // записываем его в бд
                if (!like.exists()){
                    reference.setValue(true).addOnSuccessListener {
                        EventBus.publish(Event.CreateLike(postId, uid))
                    }
                }else{
                    reference.removeValue()
                }
                taskSource.setResult(Unit)
            })
        }
    }


    //получаем Feedpost юзера
    override fun getFeedPost(uid: String, postId: String): LiveData<FeedPost> =
        FirebaseLiveData(database.child("feed-posts").child(uid).child(postId)).map{
             it.asFeedPost()!!
        }

    //получаем Feedposts юзера
    override fun getFeedPosts(uid: String): LiveData<List<FeedPost>> =
            FirebaseLiveData(database.child("feed-posts").child(uid)).map{
                it.children.map { it.asFeedPost()!! }
            }


    //копируем посты юзеров на которые подписался
    override fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
            task { taskSource ->

                //вычитаем посты юзеров, для дальнейшей работы с ними(например показ в ленте)
                database.child("feed-posts").child(postsAuthorUid)
                        //фильтруем посты таким образом, чтобы скопировать только те посты,
                        // который написал автор поста
                        .orderByChild("uid")
                        .equalTo(postsAuthorUid)
                        .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                            //карта, которая содержит посты юзеров
                            val postsMap = it.children.map { it.key to it.value }.toMap()

                            //вычитываем посты юзера на которого подписался пользователь и
                            // кладем на ленту юзеру, который подписался
                            database.child("feed-posts").child(uid)
                                    .updateChildren(postsMap)
                                    .toUnit()
                                    .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                        })
            }




   override fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
           task { taskSource ->

               //вычитаем все feed-посты нашего uid, у которых childUid равен postAuthorUid
               // и удаляем их
               database.child("feed-posts").child(postsAuthorUid)
                       .orderByChild("uid")
                       .equalTo(postsAuthorUid)
                       .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                           //карта, которая содержит посты юзеров
                           val postsMap = it.children.map { it.key to null }.toMap()

                           //удаляем посты юзверя, от которого отписался пользователь
                           database.child("feed-posts").child(uid)
                                   .updateChildren(postsMap)
                                   .toUnit()
                                   .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                       })
           }


    //функция расширения, с помощью которой получаем замапенный список постов, где id поста - ключ
    private fun DataSnapshot.asFeedPost(): FeedPost? =
            getValue(FeedPost::class.java)?.copy(id = key!!)

    // // функция расширения, с помощью которой получаем замапенный список комментов,
    // где id комента - ключ
    fun DataSnapshot.asComment(): Comment? =
            getValue(Comment::class.java)?.copy(id = key!!)
}