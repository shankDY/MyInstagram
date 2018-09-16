package com.shank.myinstagram.screens

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.shank.myinstagram.R
<<<<<<< HEAD:app/src/main/java/com/shank/myinstagram/screens/ProfileActivity.kt
=======
import com.shank.myinstagram.common.ValueEventListenerAdapter
import com.shank.myinstagram.data.firebase.common.FirebaseHelper
import com.shank.myinstagram.data.firebase.common.asUser
import com.shank.myinstagram.data.firebase.common.currentUid
import com.shank.myinstagram.data.firebase.common.database
import com.shank.myinstagram.model.User
>>>>>>> f9f35d23b66606e41c731143864e04ee19934201:app/src/main/java/com/shank/myinstagram/screens/ProfileActivity.kt
import com.shank.myinstagram.screens.addfriends.AddFriendsActivity
import com.shank.myinstagram.screens.common.*
import com.shank.myinstagram.screens.editprofile.EditProfileActivity
import com.shank.myinstagram.screens.profilesettings.ProfileSettingsActivity
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity() {


    private lateinit var mAdapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation(4)
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

<<<<<<< HEAD:app/src/main/java/com/shank/myinstagram/screens/ProfileActivity.kt
=======
        mFirebase = FirebaseHelper(this)
        //помещаем данные с базы на экран профиля
        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter {
            mUser = it.asUser()!!
            profile_image.loadUserPhoto(mUser.photo)
            username_text.text = mUser.username
        })

>>>>>>> f9f35d23b66606e41c731143864e04ee19934201:app/src/main/java/com/shank/myinstagram/screens/ProfileActivity.kt
        //создали табличку на 3 колонки
        //layoutManager отвечает за отображение Recycler(кастомизировать отображение)
        images_recycler.layoutManager = GridLayoutManager(this,3 )
        mAdapter = ImagesAdapter()


        setupAuthGuard {uid ->
            val viewModel = initViewModel<ProfileViewModel>()
            //передаем  uid viewModel
            viewModel.init(uid)
            viewModel.user.observe(this, Observer {
                it?.let {
                    profile_image.loadUserPhoto(it.photo)
                    username_text.text = it.username
                }
            })

            //слушаем images, если они пришли, то передаем в адаптер
            viewModel.images.observe(this, Observer {
                it?.let{images ->
                  mAdapter.updateImages(images)
                }
            })
        }
    }

<<<<<<< HEAD:app/src/main/java/com/shank/myinstagram/screens/ProfileActivity.kt
    companion object {
        const val TAG = "ProfileActivity"
=======
        /*мы запрашиваем список (пути картинок в хранилище), у каждого списка ,
        есть уникальныйи ключ(id юзера) и значение(пути картинок),
        мы берем и  кастим значения к String. */
        database.child("images").child(currentUid()!!).
                addValueEventListener(ValueEventListenerAdapter { it ->
                    val images = it.children.map { it.getValue(String::class.java)!! }
                    images_recycler.adapter = ImagesAdapter(images)
                })
>>>>>>> f9f35d23b66606e41c731143864e04ee19934201:app/src/main/java/com/shank/myinstagram/screens/ProfileActivity.kt
    }
}

//ImagesAdapter помогает засовывать данные в наш Recycler
class  ImagesAdapter: RecyclerView.Adapter<ImagesAdapter.ViewHolder>(){

    private var images = listOf<String>()

    fun updateImages(newImages: List<String>){
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(images,newImages) {it})
        this.images = newImages
        diffResult.dispatchUpdatesTo(this)
    }

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

