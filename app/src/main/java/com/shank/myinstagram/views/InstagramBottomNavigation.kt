package com.shank.myinstagram.views

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import android.util.Log
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.shank.myinstagram.R
import com.shank.myinstagram.activities.bottomActivities.*
import com.shank.myinstagram.activities.otherActivities.LikesActivity
import kotlinx.android.synthetic.main.bottom_navigation_view.*

//данный класс отвечает за навигацию по bottomNavigation
class InstagramBottomNavigation(private val bnv: BottomNavigationViewEx,private val navNumber: Int,
                                activity: Activity): LifecycleObserver {

    //lifecycler метод
    //когда активити возобновляет свою работу, то выполняется след метод
    //OnResume, onCreate, onDestroy и тд - это все LiveCycler
    //таким образом мы отвязали код,выполнемый на callback от активити
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
            //делает текущим тот навигейшен итем,который сейчас открыт
            bnv.menu.getItem(navNumber).isChecked = true
    }

    //кастомный конструктор
    init{
        bnv.setIconSize(29f, 29f) //размер иконки navigationBottom
        bnv.setTextVisibility(false)// отключаем показ текста под иконкой
        bnv.enableItemShiftingMode(false)//отключаем смещение элементов
        bnv.enableShiftingMode(false)
        bnv.enableAnimation(false)//отключаем анимацию элементов

        //отключаем выделение
        for (i in 0 until bnv.menu.size()) {
            bnv.setIconTintList(i, null)
        }

        //вешаем слушатель на navigationBottom. И с помощью when делаем выбор
       bnv.setOnNavigationItemSelectedListener {
            val nextActivity =
                    when (it.itemId) {
                        R.id.nav_item_home -> HomeActivity::class.java
                        R.id.nav_item_search -> SearchActivity::class.java
                        R.id.nav_item_share -> ShareActivity::class.java
                        R.id.nav_item_likes -> LikesActivity::class.java
                        R.id.nav_item_profile -> ProfileActivity::class.java
                        else -> {
                            Log.e(BaseActivity.TAG, "unknown nav item clicked $it")
                            null
                        }
                    }
            if (nextActivity != null) {
                val intent = Intent(activity, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                activity.startActivity(intent)
                //убираем анимацию переключения экранов
                activity.overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }

    }

}

//даннвя функция принимает номер навигации bottomActivity
fun BaseActivity.setupBottomNavigation(navNumber: Int){
    InstagramBottomNavigation(bottom_navigation_view,navNumber, this)
}