package com.shank.myinstagram.activities.bottomActivities

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.database.ValueEventListener
import com.shank.myinstagram.R
import com.shank.myinstagram.activities.authentication.LoginActivity
import com.shank.myinstagram.activities.recycler_adapters.FeedAdapter
import com.shank.myinstagram.utils.FirebaseHelper
import com.shank.myinstagram.utils.ValueEventListenerAdapter
import com.shank.myinstagram.utils.asFeedPost
import com.shank.myinstagram.utils.setValueTrue0rRemove
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(0), FeedAdapter.Listener {

    private val TAG = "HomeActivity"
    // ленивая инициализация( т.е инициализация произойдет позже)
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mAdapter: FeedAdapter // recyclerView adapter для постов юзеров
    //карта слушателей(для проверки, вызван листанер или нет)
    private var mLikesListeners: Map<String, ValueEventListener> = emptyMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()

        mFirebase = FirebaseHelper(this)



        mFirebase.auth.addAuthStateListener {
            if ( mFirebase.auth.currentUser == null){
                startActivity(Intent(this, LoginActivity::class.java))
                finish() // убиваем активити
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mFirebase.auth.currentUser
        if ( currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // убиваем активити
        }else{
            mFirebase.database.child("feed-posts").child(currentUser.uid)
                    .addValueEventListener(ValueEventListenerAdapter{
                        val posts = it.children.map { it.asFeedPost()!! }
                                //сортируем посты по дате добавления
                                .sortedByDescending { it.timestampDate() }
                        mAdapter = FeedAdapter(this, posts)
                        feed_recycler.adapter = mAdapter
                        feed_recycler.layoutManager = LinearLayoutManager(this)
                    })
        }
    }

    override fun toogleLike(postId: String) {
        Log.d(TAG, "toogleLike: $postId")
        //получаем ссылку на место хранения лайков
        val reference = mFirebase.database.child("likes").child(postId)
                .child(mFirebase.currentUid()!!)
        reference.addListenerForSingleValueEvent(ValueEventListenerAdapter{
            //если нода существует(поставили уже лайк), то удаляем лайк, иначе
            // записываем его в бд
            reference.setValueTrue0rRemove(!it.exists())
        })
    }

    //подгрузка лайков
    override fun loadLikes(postId: String, position: Int) {

        fun createListener() =
        //вычитываем позиции лайков для постов
                mFirebase.database.child("likes").child(postId).addValueEventListener(
                        ValueEventListenerAdapter{

                            //id юзеров, пролайковших пост(количество id будет соответствовать
                            // количеству лайков)
                            val userLikes = it.children.map {it.key}.toSet()

                            //если id юзера находится в множестве id , кто пролайкал этот пост,
                            // значит мы пролайкали этот пост
                            val postLikes = FeedPostLikes(
                                    userLikes.size,
                                    userLikes.contains(mFirebase.currentUid()))
                            mAdapter.updatePostLikes( position, postLikes)
                        })


        //если слушатель не вызван, то вызываем его и добавляем в карту по позиции
        val createNewListener = mLikesListeners[postId] == null
        Log.d(TAG, "loadLikes: $position $createNewListener")
        if (createNewListener){
            mLikesListeners += (postId to createListener())
        }
    }


    //вызывается при уничтожении активити
    override fun onDestroy() {
        super.onDestroy()
        //получаем список наших лисенеров и удаляем их при разрушении активити
        mLikesListeners.values.forEach { mFirebase.database.removeEventListener(it) }
    }
}

//отдельный класс, для подсчета лайков. первый параметр количество лайков,
// второй поставлен лайк или нет
data class FeedPostLikes(val likesCount: Int, val likedByUser: Boolean)
