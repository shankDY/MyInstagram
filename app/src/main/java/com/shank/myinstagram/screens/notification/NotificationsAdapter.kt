package com.shank.myinstagram.screens.notification

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shank.myinstagram.R
import com.shank.myinstagram.model.Notification
import com.shank.myinstagram.model.NotificationType
import com.shank.myinstagram.screens.common.SimpleCallback
import com.shank.myinstagram.screens.common.loadImageOrHide
import com.shank.myinstagram.screens.common.loadUserPhoto
import com.shank.myinstagram.screens.common.setCaptionText
import kotlinx.android.synthetic.main.notification_item.view.*

class NotificationsAdapter : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var notifications = listOf<Notification>()

    //данный метод подгружает и создает наши view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    //данный инициализирует наши view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //значение нотификаций по позиции
        val notification = notifications[position]

        //обращаемся к viewHolder напрямую. Вызывает указанный функциональный блок с данным
        // приемником в качестве приемника и возвращает его результат.
        with(holder.view) {
            user_photo.loadUserPhoto(notification.photo)

            //получаем текст нотификации
            val notificationText = when (notification.type) {
                NotificationType.Comment -> context.getString(R.string.commented)
                NotificationType.Like -> context.getString(R.string.liked_your_post)
                NotificationType.Follow -> context.getString(R.string.started_following_you)
            }

            //задаем notificationText
            notification_text.setCaptionText(notification.username, notificationText,
                    notification.timestampDate())
            //если картинка поста есть, то мы ее показываем, если ее нет, то прячим
            post_image.loadImageOrHide(notification.postImage)
        }
    }

    //размер спискс нотификаций
    override fun getItemCount(): Int = notifications.size


    //обновление нотификаций
    fun updateNotifications(newNotifications: List<Notification>) {
        //считает разницу между старым и новым значением аргумента, находит те, что изменились
        // и перерисовываем только те, что изменились, а не весь адаптер
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(notifications, newNotifications) { it.id })

        //обновление нотификаций
        this.notifications = newNotifications
        //diffResult внутри себя считает, какие позиции изменились и
        // затем обновляет наш recyclerView
        diffResult.dispatchUpdatesTo(this)
    }
}