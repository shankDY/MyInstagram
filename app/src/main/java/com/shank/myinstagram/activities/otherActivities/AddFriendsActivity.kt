package com.shank.myinstagram.activities.otherActivities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.android.gms.tasks.Tasks
import com.shank.myinstagram.R
import com.shank.myinstagram.activities.recycler_adapters.FriendsAdapter
import com.shank.myinstagram.model.Users
import com.shank.myinstagram.utils.*
import kotlinx.android.synthetic.main.activity_add_friends.*

class AddFriendsActivity : AppCompatActivity(), FriendsAdapter.Listener {
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: Users
    private lateinit var mUsers: List<Users>
    private lateinit var mAdapter: FriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        //по клику на кнопку назад, убиваем активти и возвращаемся на экран профиля
        back_image.setOnClickListener {
            finish()
        }

        mFirebase = FirebaseHelper(this)
        mAdapter = FriendsAdapter(this)

        val uid = mFirebase.currentUid()!!

        add_friends_recycler.adapter = mAdapter
        add_friends_recycler.layoutManager = LinearLayoutManager(this)

        mFirebase.database.child("users").addValueEventListener(ValueEventListenerAdapter {
            val allUsers = it.children.map { it.asUser()!! }
            //отфильтруем наших юзеров(по uid). Чтобы узнать где наш, а где остальные
            //если у юзера uid == нашему uid, то это наш пользователь
            val (userList, otherUsersList) = allUsers.partition { it.uid == uid }

            mUser = userList.first()//наш пользователь
            mUsers = otherUsersList // остальные юзеры

            //передаем в адаптер всех пользователей и тех пользователей ,
            // которых мы фоловим(на которых подписались)
            mAdapter.update(mUsers, mUser.follows)
        })
    }

    override fun follow(uid: String) {
        setFollow(uid, true) {
            mAdapter.followed(uid)
        }
    }

    override fun unfollow(uid: String) {
        setFollow(uid, false) {
            mAdapter.unfollowed(uid)
        }
    }

    //если нажали следовать, то информация добавляется в бд по адрессу(users/mUser.uid/follows/uid)
    private fun setFollow(uid: String, follow: Boolean, onSuccess: () -> Unit) {

        //если следуем за юзером, то true(добавляем значение в бд), иначе удаляем значение их бд
        val followsTask = mFirebase.database.child("users").child(mUser.uid).child("follows")
                .child(uid).setValueTrue0rRemove(follow)

        // для второго человека, которого фоловят, появляется инфа в бд
        // по адресу(users/uid/followers/mUser.uid)
        val followersTask  = mFirebase.database.child("users").child(uid).child("followers")
                .child(mUser.uid).setValueTrue0rRemove(follow)

        //получаем таск, который заполним внутри addListenerForSingleValueEvent
        val feedPostTask  = task<Void> { taskSource ->

            //вычитаем посты юзеров, для дальнейшей работы с ними(например показ в ленте)
            mFirebase.database.child("feed-posts").child(uid)
                    .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                        //карта, которая содержит посты юзеров
                        val postsMap = if (follow) {
                            it.children.map { it.key to it.value }.toMap()
                        } else {
                            it.children.map { it.key to null }.toMap()
                        }

                        //скопируем ее для нашего юзера(если follow true, если false - удаляем)
                        mFirebase.database.child("feed-posts").child(mUser.uid)
                                .updateChildren(postsMap).addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                    })
        }

        //данная функция выполнит все таски паралельно
        Tasks.whenAll(followsTask, followersTask, feedPostTask).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }

    }
}