package com.shank.myinstagram.screens.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.shank.myinstagram.data.UsersRepository

class ProfileViewModel(private val usersRepo:UsersRepository): ViewModel() {
    val user = usersRepo.getUser()
    lateinit var  images: LiveData<List<String>>

    fun init(uid: String){
        //создаем getImagesLiveData
        images = usersRepo.getImages(uid)
    }
}