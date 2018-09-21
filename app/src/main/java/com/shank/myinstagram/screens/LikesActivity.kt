package com.shank.myinstagram.screens

import android.os.Bundle
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.screens.common.BaseActivity
import com.shank.myinstagram.screens.common.setupAuthGuard
import com.shank.myinstagram.screens.common.setupBottomNavigation

class LikesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation(3)
        Log.d(TAG, "onCreate")

        setupAuthGuard {

        }
    }

    companion object {
        const val TAG = "LikesActivity"
    }
}
