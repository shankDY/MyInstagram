package com.shank.myinstagram.activities.otherActivities

import android.os.Bundle
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.activities.bottomActivities.BaseActivity

class LikesActivity : BaseActivity(3) {
    private val TAG = "LikesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()
        Log.d(TAG, "onCreate")
    }
}
