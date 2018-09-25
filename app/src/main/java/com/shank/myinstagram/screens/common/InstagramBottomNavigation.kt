package com.shank.myinstagram.screens.common

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.nhaarman.supertooltips.ToolTip
import com.nhaarman.supertooltips.ToolTipRelativeLayout
import com.nhaarman.supertooltips.ToolTipView
import com.shank.myinstagram.R
import com.shank.myinstagram.model.Notification
import com.shank.myinstagram.model.NotificationType
import com.shank.myinstagram.screens.home.HomeActivity
import com.shank.myinstagram.screens.notification.NotificationsActivity
import com.shank.myinstagram.screens.notification.NotificationsViewModel
import com.shank.myinstagram.screens.profile.ProfileActivity
import com.shank.myinstagram.screens.search.SearchActivity
import com.shank.myinstagram.screens.share.ShareActivity
import kotlinx.android.synthetic.main.bottom_navigation_view.*
import kotlinx.android.synthetic.main.notifications_tooltip_content.view.*

//данный класс отвечает за навигацию по bottomNavigation
class InstagramBottomNavigation(private val uid: String,
                                private val bnv: BottomNavigationViewEx,
                                private val tooltipLayout: ToolTipRelativeLayout,
                                private val navNumber: Int,
                                private val activity: BaseActivity): LifecycleObserver {


    private lateinit var mViewModel: NotificationsViewModel
    private lateinit var mNotificationsContentView: View
    private var lasttooltipView: ToolTipView? = null

    //lifecycler метод
    //когда активити возобновляет свою работу, то выполняется след метод
    //OnResume, onCreate, onDestroy и тд - это все LiveCycler
    //таким образом мы отвязали код,выполнемый на callback от активити
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        mViewModel = activity.initViewModel()
        mViewModel.init(uid)
        mViewModel.notifications.observe(activity, Observer {
            it?.let {
                showNotifications(it)
            }
        })

        //инициализация разметки для tooltips_content
        mNotificationsContentView = activity.layoutInflater.inflate(
                R.layout.notifications_tooltip_content,null,false)
    }

    //показываем наши уведомляшки
    private fun showNotifications(notifications: List<Notification>) {
        //если мы дважды получили нотификацию, одну, а потом другую и
        // пользователь первую еще не закрыл, то  нам надо удалить предыдущий
        if (lasttooltipView != null){
            val parent = mNotificationsContentView.parent
            if (parent != null){
                //viewGroup - вьюшка,которая может содержать другие вьюшки
                //удаляем контент тултипа
                (parent as ViewGroup).removeView(mNotificationsContentView)
                //удаляем сам тултип
                lasttooltipView?.remove()
            }
            lasttooltipView = null
        }
        //берем непрочитанные уведомления
        val newNotifications = notifications.filter { !it.read }
        //берем нотификации, группируем их по типу и говорим, что для каждого типа,
        // мы должны знать количество нотификаций
        val newNotificationsMap = newNotifications
                .groupBy { it.type }
                .mapValues{ (_,values) -> values .size}

        fun setCount(image: ImageView, textView: TextView, type: NotificationType){

            //количество нотификаций для каждого типа
            val count = newNotificationsMap[type]?: 0
            //если count == null, то прячем элементы
            if (count == 0){
                image.visibility = View.GONE
                textView.visibility = View.GONE
            }else{
                image.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                textView.text = count.toString()
            }
        }

        //вызываем setCount для всех типов уведомлений
        with(mNotificationsContentView){

            setCount(likes_image,likes_count_text, NotificationType.Like)
            setCount(follows_image,follows_count_text, NotificationType.Follow)
            setCount(comments_image,comments_count_text, NotificationType.Comment)
        }

        //если есть уведомления, то показываем tooltips
        if (newNotifications.isNotEmpty()){
            //создание tooltip
            val tooltip = ToolTip()
                    //цвет тултипа
                    .withColor(ContextCompat.getColor(activity,R.color.red))
                    //разметка
                    .withContentView(mNotificationsContentView)
                    //анимация появления тултипа
                    .withAnimationType(ToolTip.AnimationType.FROM_TOP)
                    //тень
                    .withShadow()
            //показываем тултип(первый параметр tooltip,
            // второй вьюшка для которой надо показывать тултип)
            // поэтому получим нашу иконку навигейтионБотом по позиции
            lasttooltipView = tooltipLayout
                    .showToolTipForView(tooltip,bnv.getIconAt(NOTIFICATIONS_ICON_POS))

            //по клику на нотификейшен отметить все уведомления прочитанными
            // и открыть notificationsActivity
            lasttooltipView?.setOnClickListener {
                //отмечаем все уведомления прочитанными
                mViewModel.setNotificationsRead(newNotifications)

                //открываем активити
                bnv.getBottomNavigationItemView(NOTIFICATIONS_ICON_POS).callOnClick()
            }
        }
    }

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
                        R.id.nav_item_likes -> NotificationsActivity::class.java
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

    companion object {
        const val NOTIFICATIONS_ICON_POS = 3
    }
}


//даннвя функция принимает номер навигации bottomActivity
fun BaseActivity.setupBottomNavigation(uid: String, navNumber: Int){
    //создаем в bottomNavigation
    val bnv = InstagramBottomNavigation(uid, bottom_navigation_view,tooltip_layout, navNumber, this)
    //добавляем его в lifecycle активити(чтобы bnv получал колбеки)
    this.lifecycle.addObserver(bnv)
}