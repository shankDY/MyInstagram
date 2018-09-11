package com.shank.myinstagram.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.shank.myinstagram.data.FeedPostsRepository
import com.shank.myinstagram.data.UsersRepository
import com.shank.myinstagram.model.User
import com.shank.myinstagram.utils.map

//ViewModel для AddFriendActivity
class AddFriendsViewModel(private val onFailureListener: OnFailureListener,private val usersRepo: UsersRepository, private val feedPostsRepo: FeedPostsRepository): ViewModel(){

    //получаем список юзеров и их друзей, из viewModel
    val userAndFriends: LiveData<Pair<User, List<User>>> =
            usersRepo.getUsers().map{ allUsers ->

                /**
                отфильтруем наших юзеров(по uid). Чтобы узнать где наш, а где остальные

                 * Разделяет исходную коллекцию на пару списков,
                  * где * first * list содержит элементы, для которых [предикат] дал true,
                  * while * second * list содержит элементы, для которых [предикат] дал `false`.

                если у юзера uid == нашему uid, то это наш пользователь
                **/
                val (userList, otherUsersList) = allUsers.partition {
                    it.uid == usersRepo.currentUid()
                }
                userList.first() to otherUsersList
            }

    fun setFollow(currentUid: String, uid: String, follow: Boolean): Task<Void> {
        // currentUid наш авторизованный пользователь
        // whenAll, данная функция выполнит все таски паралельно
        return (if (follow){
            Tasks.whenAll(
                    usersRepo.addFollow(currentUid, uid),
                    usersRepo.addFollower(currentUid, uid),
                    feedPostsRepo.copyFeedPosts(postsAuthorUid = uid, uid = currentUid))
        }else{
            Tasks.whenAll(
                    usersRepo.deleteFollow(currentUid, uid),
                    usersRepo.deleteFollower(currentUid, uid),
                    feedPostsRepo.deleteFeedPosts(postsAuthorUid = uid, uid = currentUid))
        }).addOnFailureListener(onFailureListener)
    }
}