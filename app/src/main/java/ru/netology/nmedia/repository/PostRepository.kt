package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun getAllAsync(callback: GetCallback<List<Post>>)
    fun likeByIdAsync(post: Post, callback: GetCallback<Post>): Post
    fun removeByIdAsync(id: Long, callback: GetCallback<Unit>)
    fun saveAsync(post: Post, callback: GetCallback<Post>): Post

    interface GetCallback<A> {
        fun onSuccess(value: A)
        fun onError()
    }
}
