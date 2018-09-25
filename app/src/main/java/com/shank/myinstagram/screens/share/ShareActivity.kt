package com.shank.myinstagram.screens.share

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.data.firebase.common.*
import com.shank.myinstagram.model.User
import com.shank.myinstagram.screens.common.*
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity() {

    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User

    private lateinit var mViewModel: ShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate")


        setupAuthGuard {
            mViewModel = initViewModel()
            mFirebase = FirebaseHelper(this)

            back_image.setOnClickListener { finish() }// кнопка назад.
            share_text.setOnClickListener { share() }

            //по старту активити будет открываться камера. и получать фото
            mCamera = CameraHelper(this)
            mCamera.takeCameraPicture()

            mViewModel.user.observe(this, android.arch.lifecycle.Observer {
                it?.let{
                    mUser = it
                }
            })

            mViewModel.shareCompletedEvent.observe(this, Observer { finish() })
        }
    }


    //если requestCode соответствует , то рисуем картинку и отображаем наше фото на экране постинга
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                post_image.loadImage(mCamera.imageUri?.toString())
            } else {
                // если юзер нажал отмена. то мы сразу убиваем активити
                finish()
            }
        }
    }

    //постим фоточку
    private fun share() {
        mViewModel.share(mUser,mCamera.imageUri, caption_input.text.toString())
    }


    companion object {
        const val TAG = "ShareActivity"
    }
}


