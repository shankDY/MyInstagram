package com.shank.myinstagram.activities.bottomActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.model.FeedPost
import com.shank.myinstagram.model.User
import com.shank.myinstagram.utils.*
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity(2) {
    private val TAG = "ShareActivity"
    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        Log.d(TAG, "onCreate")

        mFirebase = FirebaseHelper(this)

        //по старту активити будет открываться камера. и получать фото
        mCamera = CameraHelper(this)
        mCamera.takeCameraPicture()

        back_image.setOnClickListener { finish() }// кнопка назад.
        share_text.setOnClickListener { share() }

        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter{
            mUser = it.asUser()!!
        })
    }

    private fun share() {
        val imageUri = mCamera.imageUri
        if (imageUri != null) {
            //upload image to user folder <- storage
            //lastPathSegment - имя файла
            val uid = currentUid()!!
            val ref_users_image = storage.child("users").child(uid).child("images")
                    .child(imageUri.lastPathSegment)
            ref_users_image.putFile(imageUri).addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    ref_users_image.downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            //получаем ссылку на загруженные фотки в storage
                            val url_users_images = it.result!!.toString()
                            //add image to user images <- db
                            database.child("images").child(uid).push()
                                    .setValue(url_users_images).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    database.child("feed-posts").child(uid)
                                            .push()
                                            .setValue(mkFeedPost(uid, url_users_images))
                                            .addOnCompleteListener {
                                                if(it.isSuccessful){
                                                    startActivity(Intent(this,
                                                            ProfileActivity::class.java))
                                                    finish()
                                                }

                                            }


                                } else {
                                    showToast(it.exception!!.message!!)
                                }
                            }
                        } else {
                            showToast(it.exception!!.message!!)
                        }
                    }
                }
            }
        }
    }

    //заполняем наш dataClass
    private fun mkFeedPost(uid: String, url_users_images: String): FeedPost {
        return FeedPost(
                uid = uid,
                username = mUser.username,
                image = url_users_images,
                caption = caption_input.text.toString(),
                photo = mUser.photo
        )
    }


    //если requestCode соответствует , то рисуем картинку и отображаем наше фото на экране постинга
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == mCamera.REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    //centerCrop сделает нашу картинку квадратной.
                    GlideApp.with(this).load(mCamera.imageUri).centerCrop().into(post_image)
                } else {
                    // если юзер нажал отмена. то мы сразу убиваем активити
                    finish()
                }
            }
        }
    }


