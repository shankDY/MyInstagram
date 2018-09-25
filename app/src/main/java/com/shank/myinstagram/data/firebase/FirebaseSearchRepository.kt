package com.shank.myinstagram.data.firebase

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.shank.myinstagram.common.toUnit
import com.shank.myinstagram.data.SearchRepository
import com.shank.myinstagram.data.common.map
import com.shank.myinstagram.data.firebase.common.FirebaseLiveData
import com.shank.myinstagram.data.firebase.common.database
import com.shank.myinstagram.model.SearchPost

class FirebaseSearchRepository : SearchRepository {

    //мы создадим отдельную ноду. Где посты будут хранится, как список без индификатора юзеров,
    // чтобы была возможность поиска по captions
    override fun createPost(post: SearchPost): Task<Unit> =
            searchRef().push().setValue(post.copy(caption = post.caption.toLowerCase())).toUnit()

    override fun searchPosts(text: String): LiveData<List<SearchPost>> {

        //формируем запрос
        val query = if (text.isEmpty()) {
            //если юзер ничего не ввел, то возвращаем все посты
            searchRef()
        } else {

            /** Символ, \uf8ffиспользуемый в запросе, является очень высокой точкой кода в диапазоне
             *  Unicode (это код частной области использования [PUA]). Поскольку это происходит
             *  после большинства обычных символов в Юникоде,
             *  запрос соответствует всем значениям,
             *  которые начинаются с запроса введенного юзером(text)**/
            // поиск по caption(описанию) поста, который начинается с введенного юзером запроса
            searchRef().orderByChild("caption").startAt(text.toLowerCase())
                    .endAt("${text.toLowerCase()}\\uf8ff")
        }

        //возвращаем юзеру ответ в зависимости от сформированного query
       return FirebaseLiveData(query).map {
            it.children.map {
                it.asSearchPost()!!
            }
        }
    }


    //ссылка на хранилище, в котором будут храниться наши посты для поиска
    private fun searchRef() =
            database.child("search-posts")

    private fun DataSnapshot.asSearchPost() =
            getValue(SearchPost::class.java)?.copy(id = key!!)
}