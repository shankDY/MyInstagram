package com.shank.myinstagram.screens.addfriends

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.shank.myinstagram.R
import com.shank.myinstagram.model.User
import com.shank.myinstagram.screens.common.BaseActivity
import com.shank.myinstagram.screens.common.setupAuthGuard
import kotlinx.android.synthetic.main.activity_add_friends.*

class AddFriendsActivity : BaseActivity(), FriendsAdapter.Listener {
    private lateinit var mUser: User
    private lateinit var mAdapter: FriendsAdapter
    private lateinit var mViewModel: AddFriendsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        //по клику на кнопку назад, убиваем активти и возвращаемся на экран профиля
        back_image.setOnClickListener {
            finish()
        }

        mAdapter = FriendsAdapter(this)

        setupAuthGuard {


            // ViewModelProvider - будет отвественен за создание вьюмодели,
            // что мы ему указали. для той активити , что мы ему передали.
            //т.е при первом создании активити, создается viewModel, а при следущих уничтожениях
            // и возобновлениях работы будет возвращать туже модель
            //Т.к активити не надежная, ее может убить система и создать заново,
            //например при смене ориентации система убьет активити и создаст новое, и все данные будут
            // загружаться заново
            mViewModel = initViewModel()

            add_friends_recycler.adapter = mAdapter
            add_friends_recycler.layoutManager = LinearLayoutManager(this)


            //вычитываем юзеров и их друзей с бд
            mViewModel.userAndFriends.observe(this, Observer {
                it?.let { (user, otherUsers) ->
                    mUser = user//наш пользователь

                    //передаем в адаптер всех пользователей и тех пользователей ,
                    // которых мы фоловим(на которых подписались)
                    mAdapter.update(otherUsers, mUser.follows)
                }
            })
        }

    }



    override fun follow(uid: String) {
        setFollow(uid, true) {
            mAdapter.followed(uid)
        }
    }

    override fun unfollow(uid: String) {
        setFollow(uid, false) {
            mAdapter.unfollowed(uid)
        }
    }

    //если нажали следовать, то информация добавляется в бд по адрессу(users/mUser.uid/follows/uid)
    private fun setFollow(uid: String, follow: Boolean, onSuccess: () -> Unit) {

        mViewModel.setFollow(mUser.uid, uid, follow)
                .addOnSuccessListener { onSuccess() }

    }
}