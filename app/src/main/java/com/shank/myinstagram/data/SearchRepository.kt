package com.shank.myinstagram.data

import android.arch.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.shank.myinstagram.model.SearchPost

interface SearchRepository {
    fun createPost(post: SearchPost): Task<Unit>
    fun searchPosts(text: String): LiveData<List<SearchPost>>
}