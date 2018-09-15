package com.shank.myinstagram.screens.profilesettings

import android.arch.lifecycle.ViewModel
import com.shank.myinstagram.common.AuthManager

class ProfileSettingsViewModel(private val authManager: AuthManager) : ViewModel(),
        //делегация на authManager
        AuthManager by authManager

