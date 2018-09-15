package com.shank.myinstagram.screens.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.database.ValueEventListener
import com.shank.myinstagram.R
import com.shank.myinstagram.screens.common.BaseActivity
import com.shank.myinstagram.screens.common.setupAuthGuard
import com.shank.myinstagram.screens.common.setupBottomNavigation
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), FeedAdapter.Listener {

    // ленивая инициализация( т.е инициализация произойдет позже)
    private lateinit var mAdapter: FeedAdapter // recyclerView adapter для постов юзеров
    //карта слушателей(для проверки, вызван листанер или нет)
    private var mLikesListeners: Map<String, ValueEventListener> = emptyMap()
    private lateinit var mViewModel: HomeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation(0)

        mAdapter = FeedAdapter(this)
        feed_recycler.adapter = mAdapter
        feed_recycler.layoutManager = LinearLayoutManager(this)
        setupAuthGuard { uid ->
            //создали viewModel
            mViewModel = initViewModel()
            //вызываем метод init и передаем uid юзера авторизованного
            mViewModel.init(uid)

            mViewModel.feedPosts.observe(this, Observer{it?.let{
                mAdapter.updatePosts(it)

            }})

        }
    }


    //переключатель лайков
    override fun toogleLike(postId: String) {
        Log.d(TAG, "toogleLike: $postId")
        mViewModel.toogleLike(postId)
    }

    //подгрузка лайков
    override fun loadLikes(postId: String, position: Int) {

        if(mViewModel.getLikes(postId) == null){
            mViewModel.loadLikes(postId).observe(this, Observer {
                it?.let{ postLikes ->
                    mAdapter.updatePostLikes(position, postLikes)
                }
            })
        }
    }

    companion object {
        const val TAG = "HomeActivity"
    }
}