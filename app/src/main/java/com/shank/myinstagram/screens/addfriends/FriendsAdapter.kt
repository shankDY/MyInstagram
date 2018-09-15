package com.shank.myinstagram.screens.addfriends

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shank.myinstagram.R
import com.shank.myinstagram.model.User
import com.shank.myinstagram.screens.common.SimpleCallback
import com.shank.myinstagram.screens.common.loadUserPhoto
import kotlinx.android.synthetic.main.add_friends_item.view.*

//адаптер для друзей(активити , где добавляешь себе друзей)
class FriendsAdapter(private val listener: Listener)
    : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface Listener {
        fun follow(uid: String)
        fun unfollow(uid: String)
    }

    private var mUsers = listOf<User>() //список пользователей
    private var mPositions = mapOf<String, Int>()
    //картаФоловеров(ключ - uid, значени true или false)
    private var mFollows = mapOf<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.add_friends_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.view) {
            val user = mUsers[position]
            photo_image.loadUserPhoto(user.photo)
            username_text.text = user.username
            name_text.text = user.name
            //передаем активити наши клики по кнопкам
            follow_btn.setOnClickListener { listener.follow(user.uid) }
            unfollow_btn.setOnClickListener { listener.unfollow(user.uid) }

            //наша карта(если следуем за юзером, то true)
            val follows = mFollows[user.uid] ?: false
            //если мы зафоловили уже юзера, то кнопка follow скрыта, а кнопка unfollow видна,
            // иначе наоборот
            if (follows) {
                follow_btn.visibility = View.GONE
                unfollow_btn.visibility = View.VISIBLE
            } else {
                follow_btn.visibility = View.VISIBLE
                unfollow_btn.visibility = View.GONE
            }
        }
    }

    //размер списка
    override fun getItemCount() = mUsers.size

    fun update(users: List<User>, follows: Map<String, Boolean>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(mUsers,users) {it.uid})
        mUsers = users // получаем список пользователей
        //сохраняем позиции . юзеров(индексы в списке.) в карту. т.е ключи у нас будут uid юзера
        //значение позиция юзера в списке
        mPositions = users.withIndex().map { (idx, user) -> user.uid to idx }.toMap()
        mFollows = follows // получаем карту подписок(follows)

        //далле в зависимости от данных отрисовывает наш адаптер отрисовывает правильно кнопки
        // и по кликам на эти кнопкам делает follow или unfollow
        diffResult.dispatchUpdatesTo(this)
    }

    //когда мы сделали follow
    // то в карте фоловеры меняем значение на true
    fun followed(uid: String) {
        mFollows += (uid to true)
        //получаем позицию по uid и обновляем значение в recyclerView
        notifyItemChanged(mPositions[uid]!!)
    }

    //если нажали отписка, то удаляем ключ из карты
    fun unfollowed(uid: String) {
        mFollows -= uid
        notifyItemChanged(mPositions[uid]!!)
    }
}