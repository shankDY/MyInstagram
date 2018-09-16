package com.shank.myinstagram.screens

import android.os.Bundle
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.screens.common.BaseActivity
<<<<<<< HEAD:app/src/main/java/com/shank/myinstagram/screens/SearchActivity.kt
import com.shank.myinstagram.screens.common.setupAuthGuard
=======
>>>>>>> f9f35d23b66606e41c731143864e04ee19934201:app/src/main/java/com/shank/myinstagram/screens/SearchActivity.kt
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
