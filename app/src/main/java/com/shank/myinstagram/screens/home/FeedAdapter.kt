package com.shank.myinstagram.screens.home

import android.graphics.Typeface
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.shank.myinstagram.R
import com.shank.myinstagram.model.FeedPost
import com.shank.myinstagram.screens.common.SimpleCallback
import com.shank.myinstagram.screens.common.loadImage
import com.shank.myinstagram.screens.common.loadUserPhoto
import com.shank.myinstagram.screens.common.showToast
import kotlinx.android.synthetic.main.feed_item.view.*

//адаптер для постов
class FeedAdapter(private val listener: Listener)
    : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    interface Listener{
        //функция , которая позволит переключать лайки, вкл или выкл
        fun toogleLike(postId: String)

        fun loadLikes(id: String, position: Int): Any
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    //список постов
    private var posts = listOf<FeedPost>()
    // карта содержит позицию(индекс в списке) юзера и лайк
    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()
    //дефолтные значения в карте
    private val defaultPostLikes = FeedPostLikes(0, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    fun updatePostLikes(position: Int, likes: FeedPostLikes) {
        //передаем в карту position и лайк
        postLikes += (position to likes)
        //говорим нашему viewHolder перерисовать вьюшку по позиции
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        //рендерим наши лайки(если они есть в карте, если нет берем дефолтные значения)
        val likes = postLikes[position]?: defaultPostLikes
        //обращаемся к viewHolder напрямую. Вызывает указанный функциональный блок с данным
        // приемником в качестве приемника и возвращает его результат.
        with(holder.view){
            holder.view.user_photo_image.loadUserPhoto(post.photo)
            username_text.text = post.username
            holder.view.post_image.loadImage(posts[position].image)
            if (likes.likesCount == 0) {
                likes_text.visibility = View.GONE
            }else{
                likes_text.visibility = View.VISIBLE
                //для соглосовывания окончаний при разном количестве(1 like, 2 likes)
                val likesCountText = holder.view.context.resources.getQuantityString(
                        R.plurals.likes_count,likes.likesCount, likes.likesCount)

                likes_text.text = likesCountText

            }

            caption_text.setCaptionText(post.username, post.caption)

            //если лайкнули , то ставим черное сердечко, если нет прозрачное
            like_image.setImageResource(
                    if(likes.likedByUser) R.drawable.ic_likes_active
                    else R.drawable.ic_likes_border)

            //лайкаем посты
            like_image.setOnClickListener{listener.toogleLike(post.id)}
            listener.loadLikes(post.id, position)
        }

    }

    //создаем Spannable text
    private fun TextView.setCaptionText(username: String, caption: String){

        //spannable: username(bold, clickable) caption
        val usernameSpannable = SpannableString(username)
        //мы выделяем часть текста(username). Делаем его жирным
        usernameSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, usernameSpannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        usernameSpannable.setSpan(object: ClickableSpan(){
            override fun updateDrawState(ds: TextPaint?) {}

            override fun onClick(widget: View) {
                widget.context.showToast(context.getString(R.string.username_is_clicked))
            }
        },0, usernameSpannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        /*spannable -> username  text -> caption -> SpannableStringBuilder*/
        text = SpannableStringBuilder().append(usernameSpannable).append(" ")
                .append(caption)
        //делаем текст кликабельным
        movementMethod = LinkMovementMethod.getInstance()
    }

    //количество постов
    override fun getItemCount() = posts.size

    fun updatePosts(newPosts: List<FeedPost>) {
        //считает разницу между старым и новым значением аргумента, находит те, что изменились
        // и перерисовываем только те, что изменились, а не весь адаптер
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.posts, newPosts) { it.id })

        //обновление постов
        this.posts = newPosts
        //diffResult внутри себя считает, какие позиции изменились и
        // затем обновляет наш recyclerView
        diffResult.dispatchUpdatesTo(this)
    }

}