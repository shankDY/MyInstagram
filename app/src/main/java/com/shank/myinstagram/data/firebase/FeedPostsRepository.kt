package com.shank.myinstagram.data.firebase

import com.google.android.gms.tasks.Task
import com.shank.myinstagram.data.FeedPostsRepository
import com.shank.myinstagram.utils.*

class FirebaseFeedPostsRepository: FeedPostsRepository {




    //копируем посты юзеров на которые подписался
    override fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task{ taskSource ->

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
        task{ taskSource ->

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