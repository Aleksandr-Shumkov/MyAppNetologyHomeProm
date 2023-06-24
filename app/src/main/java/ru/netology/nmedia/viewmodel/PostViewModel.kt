package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {

        // Начинаем загрузку
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.GetCallback<List<Post>> {
            override fun onSuccess(value: List<Post>) {
                _data.postValue(FeedModel(posts = value, empty = value.isEmpty()))
            }

            override fun onError() {
                _data.postValue(FeedModel(error = true))
            }

        })

    }

    fun save() {
        edited.value?.let {

            repository.saveAsync(it, object : PostRepository.GetCallback<Unit> {
                override fun onSuccess(value: Unit) {
                    _postCreated.postValue(Unit)
                }

                override fun onError() {
                    _data.postValue(FeedModel(error = true))
                }

            })
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(post: Post) {

        repository.likeByIdAsync(post, (object : PostRepository.GetCallback<Unit> {

            override fun onSuccess(value: Unit) {
                _data.postValue(_data.value?.copy(posts = _data.value?.posts.orEmpty().map {
                    if (it.id == post.id) {
                        post.copy(
                            likes = if (post.likedByMe) post.likes - 1 else post.likes + 1,
                            likedByMe = !post.likedByMe
                        )
                    } else {
                        it
                    }
                }

                ))
            }

            override fun onError() {
                _data.postValue(FeedModel(error = true))
            }

        }))

    }

    fun removeById(id: Long) {

        repository.removeByIdAsync(id, object : PostRepository.GetCallback<Unit> {
            override fun onSuccess(value: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }

            override fun onError() {
                _data.postValue(FeedModel(error = true))
            }

        })
    }
}
