package com.shank.myinstagram.activities.otherActivities

import android.os.Bundle
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.activities.bottomActivities.BaseActivity
import com.shank.myinstagram.views.setupBottomNavigation

class LikesActivity : BaseActivity() {
    private val TAG = "LikesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation(3)
        Log.d(TAG, "onCreate")
    }
}
