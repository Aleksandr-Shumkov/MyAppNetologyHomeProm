package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    /*fun getAll(): List<Post>
    fun likeById(post: Post): Post
    fun save(post: Post)
    fun removeById(id: Long)*/

    fun getAllAsync(callback: GetCallback<List<Post>>)
    fun likeByIdAsync(post: Post, callback: GetCallback<Unit>)
    fun removeByIdAsync(id: Long, callback: GetCallback<Unit>)
    fun saveAsync(post: Post, callback: GetCallback<Unit>)

    interface GetCallback<A> {
        fun onSuccess(value: A)
        fun onError()
    }
}
