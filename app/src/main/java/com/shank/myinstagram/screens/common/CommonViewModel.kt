package com.shank.myinstagram.screens.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import java.lang.Exception

class CommonViewModel: ViewModel(), OnFailureListener {

    private val _errorMessage = MutableLiveData<String>()
    //ссылка на список ошибок
    val errorMessage: LiveData<String> = _errorMessage

    //ловим ошибку и запихиваем в наш список. для дальнейшей обработки
    override fun onFailure(e: Exception) {
        _errorMessage.value = e.message
    }
}