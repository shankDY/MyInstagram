package com.shank.myinstagram.activities.addfriends

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.shank.myinstagram.R
import com.shank.myinstagram.model.User
import kotlinx.android.synthetic.main.activity_add_friends.*

class AddFriendsActivity : AppCompatActivity(), FriendsAdapter.Listener {
    private lateinit var mUser: User
    private lateinit var mUsers: List<User>
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

        // ViewModelProvider - будет отвественен за создание вьюмодели,
        // что мы ему указали. для той активити , что мы ему передали.
        //т.е при первом создании активити, создается viewModel, а при следущих уничтожениях
        // и возобновлениях работы будет возвращать туже модель
        //Т.к активити не надежная, ее может убить система и создать заново,
        //например при смене ориентации система убьет активити и создаст новое, и все данные будут
        // загружаться заново
        mViewModel = ViewModelProviders.of(this, AddFriendsViewModelFactory())
                .get(AddFriendsViewModel::class.java)

        add_friends_recycler.adapter = mAdapter
        add_friends_recycler.layoutManager = LinearLayoutManager(this)


        //вычитываем юзеров и их друзей с бд
        mViewModel.userAndFriends.observe(this, Observer{it?.let{ (user, otherUsers)->
            mUser = user//наш пользователь
            mUsers = otherUsers // остальные юзеры

            //передаем в адаптер всех пользователей и тех пользователей ,
            // которых мы фоловим(на которых подписались)
            mAdapter.update(mUsers, mUser.follows)
        }})

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
                .addOnFailureListener { it.message!! }

    }
}