package com.shank.myinstagram.screens.search

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.shank.myinstagram.R
import com.shank.myinstagram.screens.common.BaseActivity
import com.shank.myinstagram.screens.common.ImagesAdapter
import com.shank.myinstagram.screens.common.setupAuthGuard
import com.shank.myinstagram.screens.common.setupBottomNavigation
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity(), TextWatcher {

    private lateinit var mAdapter: ImagesAdapter
    private lateinit var mViewModel: SearchViewModel
    //данный флаг означает, что юзер начал(перестал) вводить текст в поиск(по умолчанию false)
    private var isSearchEntered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupBottomNavigation(1)
        Log.d(TAG, "onCreate")

        setupAuthGuard {
            mAdapter = ImagesAdapter()
            search_recycler.layoutManager = GridLayoutManager(this, 3)
            search_recycler.adapter = mAdapter

            mViewModel = initViewModel()
            mViewModel.posts.observe(this, android.arch.lifecycle.Observer {
                it?.let { posts ->
                    mAdapter.updateImages(posts.map { it.image })
                }
            })

            search_input.addTextChangedListener(this)
            //ставим пустой текст, чтобы подгрузились все посты
            mViewModel.setSearchText("")
        }
    }



    //что делать после изменения текста
    override fun afterTextChanged(p0: Editable?) {
        if (!isSearchEntered){

            isSearchEntered = true

            //делаем задержу перед тем , как начать начать поиск(т.е вызывать наш репозиторий),
            // чтобы юзер ввел всю фразу целиком(т.е ждем,
            // что юзер уже не вводить текст в течении 500млсекунд). И по исстечении этого времени
            // отправляем запрос
            Handler().postDelayed({
                isSearchEntered = false
                mViewModel.setSearchText(search_input.text.toString())
            }, 500)
        }
    }

    //что делать до изменения текст
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    //что делать, когда изменяется текст
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}


    companion object {
        const val TAG = "SearchActivity"
    }
}
