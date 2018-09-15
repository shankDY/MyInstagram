package com.shank.myinstagram.screens.profilesettings

import android.os.Bundle
import com.shank.myinstagram.R
import com.shank.myinstagram.screens.common.BaseActivity
import com.shank.myinstagram.screens.common.setupAuthGuard
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        setupAuthGuard { it ->

            val viewModel = initViewModel<ProfileSettingsViewModel>()
            //выход из аккаунта
            sign_out_text.setOnClickListener { viewModel.signOut() }
            //выход из активти и возвращаемся на предыдущее активити
            back_image.setOnClickListener { finish() }
        }



    }
}
