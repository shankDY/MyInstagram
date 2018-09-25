package com.shank.myinstagram.screens.search

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.google.android.gms.tasks.OnFailureListener
import com.shank.myinstagram.data.SearchRepository
import com.shank.myinstagram.model.SearchPost
import com.shank.myinstagram.screens.common.BaseViewModel

class SearchViewModel(searchRepo: SearchRepository,
                      onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener) {
    private val searchText = MutableLiveData<String>()

    //данная кострукция означает, что наш репозиторий с функцией поиска будет вызыван только тогда,
    // когда запрос юзера отличается от придыдущего
    val posts: LiveData<List<SearchPost>> =Transformations.switchMap(searchText) { text ->
        searchRepo.searchPosts(text)
    }

    fun setSearchText(text: String) {
        //получили значение введенное юзером и загрузили его в наш список
        searchText.value = text
    }
}