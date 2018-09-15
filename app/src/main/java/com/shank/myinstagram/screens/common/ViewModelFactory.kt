package com.shank.myinstagram.screens.common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnFailureListener
import com.shank.myinstagram.common.firebase.FirebaseAuthManager
import com.shank.myinstagram.data.firebase.FireBaseUsersRepository
import com.shank.myinstagram.data.firebase.FirebaseFeedPostsRepository
import com.shank.myinstagram.screens.addfriends.AddFriendsViewModel
import com.shank.myinstagram.screens.editprofile.EditProfileViewModel
import com.shank.myinstagram.screens.home.HomeViewModel
import com.shank.myinstagram.screens.profilesettings.ProfileSettingsViewModel

@Suppress("UNCHECKED_CAST")
//ссылаемся на firebase только в ViewModelFactory,
// и никто больше не знает(ни один класс, кроме ViewModelFactory), что мы юзаем firebase
class ViewModelFactory(private val onFailureListener: OnFailureListener): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)){
            return AddFriendsViewModel(onFailureListener, FireBaseUsersRepository(), FirebaseFeedPostsRepository()) as T
        }else if(modelClass.isAssignableFrom(EditProfileViewModel::class.java)){
            return EditProfileViewModel(onFailureListener, FireBaseUsersRepository()) as T
        }else if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(onFailureListener, FirebaseFeedPostsRepository()) as T
        }else if (modelClass.isAssignableFrom(ProfileSettingsViewModel::class.java)){
            return ProfileSettingsViewModel(FirebaseAuthManager()) as T

        }else{
            error("Unknow view model class $modelClass")
        }
    }


}