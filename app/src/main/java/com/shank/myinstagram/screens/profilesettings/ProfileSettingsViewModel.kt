package com.shank.myinstagram.screens.profilesettings

import com.google.android.gms.tasks.OnFailureListener
import com.shank.myinstagram.common.AuthManager
import com.shank.myinstagram.screens.common.BaseViewModel

class ProfileSettingsViewModel(private val authManager: AuthManager,
                               onFailureListener: OnFailureListener) :
        BaseViewModel(onFailureListener),
        //делегация на authManager
        AuthManager by authManager

