package com.shank.myinstagram.screens.comments

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.shank.myinstagram.R
import com.shank.myinstagram.model.User
import com.shank.myinstagram.screens.common.BaseActivity
import com.shank.myinstagram.screens.common.loadUserPhoto
import com.shank.myinstagram.screens.common.setupAuthGuard
import kotlinx.android.synthetic.main.activity_comments.*

//данный класс отвечает за свой старт, и он знает , что надо незабыдь передать  EXTRA_POST_ID,
// поэтому он требует его в аргументе
class CommentsActivity: BaseActivity(){

    private lateinit var mAdapter: CommentsAdapter
    private lateinit var mUser: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        back_image.setOnClickListener { finish() }

        //получаем id поста, если его нет, то завершаем активити
        val postId = intent.getStringExtra(EXTRA_POST_ID)?: return finish()

        setupAuthGuard {
            //инициализация адаптера
            comments_recycler.layoutManager = LinearLayoutManager(this)
            mAdapter = CommentsAdapter()
            comments_recycler.adapter = mAdapter

            val viewModel = initViewModel<CommentsViewModel>()
            viewModel.init(postId)
            viewModel.user.observe(this, Observer{
                it?.let {
                    mUser = it
                    //подгружаем фотку юзера в imagView, перед вводом коммента
                    user_photo.loadUserPhoto(mUser.photo)
                }
            })
            viewModel.comments.observe(this, Observer {
                it.let {
                    mAdapter.updateComments(it!!)
                }
            })

            post_comment_text.setOnClickListener {
                viewModel.createComment(comment_text.text.toString(), mUser)
                //обнуляем наш текствью
                comment_text.setText("")
            }
        }
    }


    companion object {
        private  const val EXTRA_POST_ID = "POST_ID"
        fun start(context: Context,postId: String){
            val intent = Intent(context, CommentsActivity::class.java)
            //передаем id поста
            intent.putExtra(EXTRA_POST_ID, postId)
            context.startActivity(intent)
        }
    }
}