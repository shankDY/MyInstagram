package com.shank.myinstagram.screens.search

import android.os.Bundle
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.screens.common.BaseActivity
import com.shank.myinstagram.screens.common.setupAuthGuard
import com.shank.myinstagram.screens.common.setupBottomNavigation

class SearchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation(1)
        Log.d(TAG, "onCreate")

        setupAuthGuard {  }
    }

    companion object {
        const val TAG = "SearchActivity"
    }
}
