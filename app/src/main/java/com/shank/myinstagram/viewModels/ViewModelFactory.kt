package com.shank.myinstagram.viewModels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.shank.myinstagram.repositories.FireBaseEditProfileRepository
import com.shank.myinstagram.repositories.FirebaseAddFriendsRepository

@Suppress("UNCHECKED_CAST")
//ссылаемся на firebase только в ViewModelFactory,
// и никто больше не знает(ни один класс, кроме ViewModelFactory), что мы юзаем firebase
class ViewModelFactory: ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)){
            return AddFriendsViewModel(FirebaseAddFriendsRepository()) as T
        }else if(modelClass.isAssignableFrom(EditProfileViewModel::class.java)){
            return EditProfileViewModel(FireBaseEditProfileRepository()) as T
        }else{
            error("Unknow view model class $modelClass")
        }
    }


}