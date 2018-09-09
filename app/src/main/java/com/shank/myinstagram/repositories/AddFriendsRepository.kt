package com.shank.myinstagram.repositories

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.shank.myinstagram.model.User
import com.shank.myinstagram.utils.*

interface AddFriendsRepository {
    fun getUsers(): LiveData<List<User>>
    fun currentUid(): String?
    fun addFollow(fromUid: String, toUid: String): Task<Unit>
    fun deleteFollow(fromUid: String, toUid: String): Task<Unit>
    fun addFollower(fromUid: String, toUid: String): Task<Unit>
    fun deleteFollower(fromUid: String, toUid: String): Task<Unit>
    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun deleteFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
}

class FirebaseAddFriendsRepository: AddFriendsRepository {

    override fun getUsers(): LiveData<List<User>> = database.child("users").liveData().map{
                it.children.map { it.asUser()!! }
            }

    //добавить подписку(подписаться на юзера)
    //fromUid -наш Юзер, toUid - другие пользователи
    override fun addFollow(fromUid: String, toUid: String): Task<Unit> =
            getFollowsRef(fromUid, toUid).setValue(true).toUnit()

    //удалить подписку(отписаться от юзверя)
    override fun deleteFollow(fromUid: String, toUid: String): Task<Unit> =
            getFollowsRef(fromUid, toUid).removeValue().toUnit()

    //добавить подписчика(подписанный на юзера юзверь)
    override fun addFollower(fromUid: String, toUid: String): Task<Unit> =
            getFollowersRef(fromUid, toUid).setValue(true).toUnit()

    //удалить подписчика(юзверь отписался от юзера)
    override fun deleteFollower(fromUid: String, toUid: String): Task<Unit> =
            getFollowersRef(fromUid, toUid).removeValue().toUnit()

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



    //получаем ссылку на бд с подписками
    private fun getFollowsRef(fromUid: String, toUid: String)=
            database.child("users").child(fromUid).child("follows").child(toUid)

    //получаем ссылку на бд с подписчиками
    private fun getFollowersRef(fromUid: String, toUid: String)=
            database.child("users").child(toUid).child("followers").child(fromUid)

    //ссылка на авторизованного юзера
   override fun currentUid() = FirebaseAuth.getInstance().currentUser?.uid
}