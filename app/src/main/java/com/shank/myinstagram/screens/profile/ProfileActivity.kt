package com.shank.myinstagram.screens.profile

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.shank.myinstagram.R
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

        //создали табличку на 3 колонки
        //layoutManager отвечает за отображение Recycler(кастомизировать отображение)
        images_recycler.layoutManager = GridLayoutManager(this,3 )
        mAdapter = ImagesAdapter()
        images_recycler.adapter = mAdapter


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

    companion object {
        const val TAG = "ProfileActivity"
    }
}

