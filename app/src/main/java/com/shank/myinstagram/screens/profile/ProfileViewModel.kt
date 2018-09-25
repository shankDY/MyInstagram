package com.shank.myinstagram.screens.profile

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.shank.myinstagram.data.UsersRepository
import com.shank.myinstagram.screens.common.BaseViewModel

class ProfileViewModel(private val usersRepo:UsersRepository,
                       onFailureListener: OnFailureListener): BaseViewModel(onFailureListener) {
    val user = usersRepo.getUser()
    lateinit var  images: LiveData<List<String>>

    fun init(uid: String) {
        if (!this::images.isInitialized) {
            //создаем getImagesLiveData
            images = usersRepo.getImages(uid)
        }
    }
}