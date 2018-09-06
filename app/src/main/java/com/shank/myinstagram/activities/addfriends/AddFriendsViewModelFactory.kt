package com.shank.myinstagram.activities.addfriends

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
//ссылаемся на firebase только в AddFriendsViewModelFactory,
// и никто больше не знает(ни один класс, кроме AddFriendsViewModelFactory), что мы юзаем firebase
class AddFriendsViewModelFactory: ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddFriendsViewModel(FirebaseAddFriendsRepository()) as T
    }
}