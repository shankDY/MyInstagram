package com.shank.myinstagram.activities.otherActivities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shank.myinstagram.R
import com.shank.myinstagram.utils.FirebaseHelper
import com.shank.myinstagram.utils.auth
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity: AppCompatActivity() {
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        mFirebase = FirebaseHelper(this)
        //выход из аккаунта
        sign_out_text.setOnClickListener { auth.signOut() }
        //выход из активти и возвращаемся на предыдущее активити
        back_image.setOnClickListener { finish() }
    }
}
