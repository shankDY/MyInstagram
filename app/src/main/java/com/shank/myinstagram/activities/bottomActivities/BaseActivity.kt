package com.shank.myinstagram.activities.bottomActivities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.activities.otherActivities.LikesActivity
import kotlinx.android.synthetic.main.bottom_navigation_view.*


// потом переделать все это с помощью фрагментов
abstract class BaseActivity(val navNumber: Int) : AppCompatActivity() {
    private val TAG = "BaseActivity"

    fun setupBottomNavigation() {
        bottom_navigation_view.setIconSize(29f, 29f) //размер иконки navigationBottom
        bottom_navigation_view.setTextVisibility(false)// отключаем показ текста под иконкой
        bottom_navigation_view.enableItemShiftingMode(false)//отключаем смещение элементов
        bottom_navigation_view.enableShiftingMode(false)
        bottom_navigation_view.enableAnimation(false)//отключаем анимацию элементов

        //отключаем выделение
        for (i in 0 until bottom_navigation_view.menu.size()) {
            bottom_navigation_view.setIconTintList(i, null)
        }

        //вешаем слушатель на navigationBottom. И с помощью when делаем выбор
        bottom_navigation_view.setOnNavigationItemSelectedListener {
            val nextActivity =
                    when (it.itemId) {
                        R.id.nav_item_home -> HomeActivity::class.java
                        R.id.nav_item_search -> SearchActivity::class.java
                        R.id.nav_item_share -> ShareActivity::class.java
                        R.id.nav_item_likes -> LikesActivity::class.java
                        R.id.nav_item_profile -> ProfileActivity::class.java
                        else -> {
                            Log.e(TAG, "unknown nav item clicked $it")
                            null
                        }
                    }
            if (nextActivity != null) {
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                //убираем анимацию переключения экранов
                overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }

    }

    //когда активити возобновляет свою работу, то выполняется след метод
    override fun onResume() {
        super.onResume()
        if(bottom_navigation_view != null){
            //делает текущим тот навигейшен итем,который сейчас открыт
            bottom_navigation_view.menu.getItem(navNumber).isChecked = true
        }
    }
}