package com.shank.myinstagram.data.firebase

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.shank.myinstagram.common.TaskSourceOnCompleteListener
import com.shank.myinstagram.common.ValueEventListenerAdapter
import com.shank.myinstagram.data.FeedPostsRepository
import com.shank.myinstagram.common.task
import com.shank.myinstagram.common.toUnit
import com.shank.myinstagram.data.FeedPostLike
import com.shank.myinstagram.data.common.map
import com.shank.myinstagram.data.firebase.common.*
import com.shank.myinstagram.model.FeedPost

<<<<<<< HEAD
//реализация фйнкция FeedPostRepository
=======
>>>>>>> f9f35d23b66606e41c731143864e04ee19934201
class FirebaseFeedPostsRepository: FeedPostsRepository {

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
            reference.addListenerForSingleValueEvent(ValueEventListenerAdapter {
                //если нода существует(поставили уже лайк), то удаляем лайк, иначе
                // записываем его в бд
                reference.setValueTrue0rRemove(!it.exists())
                taskSource.setResult(Unit)
            })
        }
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




}