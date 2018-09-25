package com.shank.myinstagram.data.firebase.common

import android.arch.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Query
import com.shank.myinstagram.common.ValueEventListenerAdapter

//класс расширения(принимаем ссылку на бд, которую мы будем слушать.(в нашем случае на юзеров ))
//таким образом мы получем обстракцию для работы с firebase, которая выставляет себя на ружу, как LiveData,
// т.е мы конвертируем firebaseIvante в liveDataIvent
class FirebaseLiveData(private val query: Query): LiveData<DataSnapshot>(){

    //будет принимать snapshot и задавать значения liveData объекта
    private val listener = ValueEventListenerAdapter {
        // когда приходит snapshot, то мы засовываем его в переменную value ,
        // после чего будет вызван viewModel.userAndFriends.observe с этим новым значением
        value = it
    }


    // вызывается у liveData , когда к нему подсоединен хотябы один
    // observer(т.е активити или фрагмент), после чего мы добавим listener наш к query(
    // в нашем случаи к  ссылке на наших юзеров)
    override fun onActive() {
        super.onActive()
        query.addValueEventListener(listener)
    }

    //данный метод вызывается,когда не остается ни одного observers
    //в нем мы убираем наши слушатели
    override fun onInactive() {
        super.onInactive()
        query.removeEventListener(listener)
    }
}