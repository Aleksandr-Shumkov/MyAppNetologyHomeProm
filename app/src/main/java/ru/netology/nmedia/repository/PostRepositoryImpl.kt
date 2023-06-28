package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: PostRepository.GetCallback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback{

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }

            })

    }


    override fun likeByIdAsync(post: Post, callback: PostRepository.GetCallback<Post>): Post {
        if (post.likedByMe) {

            val request: Request = Request.Builder()
                .delete(EMPTY_REQUEST)
                .url("${BASE_URL}/api/posts/${post.id}/likes")
                .build()

            return refreshLike(request, callback)

        } else {

            val request: Request = Request.Builder()
                .post(EMPTY_REQUEST)
                .url("${BASE_URL}/api/posts/${post.id}/likes")
                .build()

            return refreshLike(request, callback)

        }
    }

    private fun refreshLike(request: Request, callback: PostRepository.GetCallback<Post>): Post {

        client.newCall(request)
            .enqueue(object : Callback{

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        return callback.onSuccess(gson.fromJson(body, Post::class.java))
                    } catch (e: Exception) {
                        callback.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }

            })

        return Post(0, "", "", "", false)
    }

    override fun saveAsync(post: Post, callback: PostRepository.GetCallback<Post>): Post {

        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback{

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        return callback.onSuccess(gson.fromJson(body, Post::class.java))
                    } catch (e: Exception) {
                        callback.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }

            })

        return Post(0, "", "", "", false)
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.GetCallback<Unit>) {

        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    try {
                        return callback.onSuccess(Unit)
                    } catch (e: Exception) {
                        callback.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }

            })

    }
}
