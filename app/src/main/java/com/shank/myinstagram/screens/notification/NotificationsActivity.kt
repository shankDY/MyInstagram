package com.shank.myinstagram.screens.notification

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.screens.common.BaseActivity
import com.shank.myinstagram.screens.common.setupAuthGuard
import com.shank.myinstagram.screens.common.setupBottomNavigation
import kotlinx.android.synthetic.main.activity_notifications.*

class NotificationsActivity : BaseActivity() {

    private lateinit var mAdapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        Log.d(TAG, "onCreate")
        mAdapter = NotificationsAdapter()
        setupAuthGuard {uid ->

            setupBottomNavigation(uid,3)

            //инициализация recyclerView
            notification_recycler.layoutManager = LinearLayoutManager(this)
            //инициализация адаптера
            notification_recycler.adapter = mAdapter

            //создаем viewModel
            val viewModel = initViewModel<NotificationsViewModel>()
            //инициализация notifications
            viewModel.init(uid)
            viewModel.notifications.observe(this, Observer{
                it?.let {
                    mAdapter.updateNotifications(it)
                    //передаем список notifications
                    viewModel.setNotificationsRead(it)
                }
            })

        }
    }

    companion object {
        const val TAG = "LikesActivity"
    }
}
