package com.example.storyapp.view.map

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.example.storyapp.data.StoryPagingSource
import com.example.storyapp.data.model.UserPreference
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.data.remote.response.StoriesResponse
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreference) : ViewModel()  {
    private val _listStory = MutableLiveData<List<Story>>()
    val listStory: LiveData<List<Story>> = _listStory


    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

    fun getAllStory(token: String) {
        val client = ApiConfig.getApiService().getStoryWithLocation("Bearer ".plus(token))
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful) {
                    _listStory.value = response.body()?.listStory
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    companion object {
        private const val TAG = "MapsViewModel"
    }
}