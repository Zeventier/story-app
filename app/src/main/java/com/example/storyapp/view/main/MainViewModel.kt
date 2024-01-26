package com.example.storyapp.view.main

import android.util.Log
import android.widget.MultiAutoCompleteTextView
import androidx.lifecycle.*
import androidx.paging.*
import com.example.storyapp.data.StoryPagingSource
import com.example.storyapp.data.model.UserPreference
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.data.remote.response.Story
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference) : ViewModel() {

//    private val _listStory = MutableLiveData<List<Story>>()
//    val listStory: LiveData<List<Story>> = _listStory

    private var token = "bruh"

    fun setToken(token: String) {
        this.token = token
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun listStory(token: String): LiveData<PagingData<Story>> {
        return getStory(token).cachedIn(viewModelScope)
    }

    private fun getStory(token: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(token)
            }
        ).liveData
    }

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

//    fun getAllStory(token: String) {
//        _isLoading.value = true
//        val client = ApiConfig.getApiService().getStory("Bearer ".plus(token))
//        client.enqueue(object : Callback<StoriesResponse> {
//            override fun onResponse(
//                call: Call<StoriesResponse>,
//                response: Response<StoriesResponse>
//            ) {
//                _isLoading.value = false
//                if (response.isSuccessful) {
//                    _listStory.value = response.body()?.listStory
//                } else {
//                    Log.e(TAG, "onFailure: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
//                _isLoading.value = false
//                Log.e(TAG, "onFailure: ${t.message.toString()}")
//            }
//        })
//    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}