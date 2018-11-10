package com.shank.myinstagram.screens.common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnFailureListener
import com.shank.myinstagram.screens.InstagramApp
import com.shank.myinstagram.screens.addfriends.AddFriendsViewModel
import com.shank.myinstagram.screens.comments.CommentsViewModel
import com.shank.myinstagram.screens.editprofile.EditProfileViewModel
import com.shank.myinstagram.screens.home.HomeViewModel
import com.shank.myinstagram.screens.login.LoginViewModel
import com.shank.myinstagram.screens.notification.NotificationsViewModel
import com.shank.myinstagram.screens.profile.ProfileViewModel
import com.shank.myinstagram.screens.profilesettings.ProfileSettingsViewModel
import com.shank.myinstagram.screens.register.RegisterViewModel
import com.shank.myinstagram.screens.search.SearchViewModel
import com.shank.myinstagram.screens.share.ShareViewModel

@Suppress("UNCHECKED_CAST")
//ссылаемся на firebase только в ViewModelFactory,
// и никто больше не знает(ни один класс, кроме ViewModelFactory), что мы юзаем firebase
class ViewModelFactory(private val app: InstagramApp,
                       private val commonViewModel: CommonViewModel,
                       private val onFailureListener: OnFailureListener): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        //т.к не во всех случаях он используется. поэтому объявляем в одном месте, а вызываем там ,
        //где понадобится
        val usersRepo = app.usersRepo
        val feedPostsRepo = app.feedPostsRepo
        val notificationRepo = app.notificationRepo
        val searchRepo = app.searchRepo
        val authManager = app.authManager

        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)){
            return AddFriendsViewModel(onFailureListener, usersRepo, feedPostsRepo) as T
        }else if(modelClass.isAssignableFrom(EditProfileViewModel::class.java)){
            return EditProfileViewModel(onFailureListener, usersRepo) as T
        }else if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(onFailureListener, feedPostsRepo) as T
        }else if (modelClass.isAssignableFrom(ProfileSettingsViewModel::class.java)){
                return ProfileSettingsViewModel(authManager,onFailureListener) as T

        }else if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(authManager, app,
                    commonViewModel, onFailureListener) as T
        }else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)){
            return ProfileViewModel(usersRepo,onFailureListener) as T

        }else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(commonViewModel, app,onFailureListener, usersRepo) as T
        }else if (modelClass.isAssignableFrom(ShareViewModel::class.java)){
            return ShareViewModel(feedPostsRepo, usersRepo, onFailureListener) as T
        }else if (modelClass.isAssignableFrom(CommentsViewModel::class.java)){
            return CommentsViewModel(feedPostsRepo,usersRepo,onFailureListener) as T
        }else  if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)){
            return NotificationsViewModel(notificationRepo, onFailureListener) as T
        }else if (modelClass.isAssignableFrom(SearchViewModel::class.java)){
            return SearchViewModel(searchRepo, onFailureListener) as T
        }else{
            error("Unknow view model class $modelClass")
        }
    }
}