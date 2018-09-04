package com.shank.myinstagram.activities.bottomActivities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.shank.myinstagram.R
import com.shank.myinstagram.activities.otherActivities.AddFriendsActivity
import com.shank.myinstagram.activities.otherActivities.EditProfileActivity
import com.shank.myinstagram.activities.otherActivities.ProfileSettingsActivity
import com.shank.myinstagram.model.Users
import com.shank.myinstagram.utils.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(4) {
    private val TAG = "ProfileActivity"
    private lateinit var mFirebase: FirebaseHelper

    private lateinit var mUser: Users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation()
        Log.d(TAG, "onCreate")

        edit_profile_btn.setOnClickListener {
            //получаем ссылку на нужный нам класс с помощью :: (рефлексия котлин)
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        settings_image.setOnClickListener{
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }

        add_friends_image.setOnClickListener {

            val intent = Intent(this, AddFriendsActivity::class.java)
            startActivity(intent)
        }

        mFirebase = FirebaseHelper(this)
        //помещаем данные с базы на экран профиля
        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter{
            mUser = it.asUser()!!
            profile_image.loadUserPhoto(mUser.photo)
            username_text.text = mUser.username
        })

        //создали табличку на 3 колонки
        //layoutManager отвечает за отображение Recycler(кастомизировать отображение)
        images_recycler.layoutManager = GridLayoutManager(this,3 )

        /*мы запрашиваем список (пути картинок в хранилище), у каждого списка ,
        есть уникальныйи ключ(id юзера) и значение(пути картинок),
        мы берем и  кастим значения к String. */
        mFirebase.database.child("images").child(mFirebase.currentUid()!!).
                addValueEventListener(ValueEventListenerAdapter{ it ->
                    val images = it.children.map { it.getValue(String::class.java)!!}
                    images_recycler.adapter = ImagesAdapter(images)
        })
    }
}

//ImagesAdapter помогает засовывать данные в наш Recycler
class  ImagesAdapter(private val images: List<String>):
        RecyclerView.Adapter<ImagesAdapter.ViewHolder>(){

    //патерн, который кеширует наши view в памяти, чтобы не искать его в лайуте и делать ссылку
    class ViewHolder(val image: ImageView): RecyclerView.ViewHolder(image)

    //создает наш viewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val image = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item, parent, false) as ImageView
        return ViewHolder(image)
    }

    // в данном методе мы в холдер засовываем данные
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(images[position])
    }

    //количество элементов в Recycler
    override fun getItemCount(): Int = images.size
}

//вспомогательный класс, который будет делать наши картинки квадратными
class SquareImageView(context: Context, attrs: AttributeSet): ImageView(context, attrs){
    //кастомизация размеров нашей картинки
    //данный метод вызывается, когда layout хочет измерить размер картинки
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //передаем две ширины, что означает, что картинка будет квадратная
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

    }
}
