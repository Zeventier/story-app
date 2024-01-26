package com.example.storyapp.view.upload

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.model.UserPreference
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.data.remote.response.PostAddStoryResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.view.Event
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadViewModel(private val pref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    private val _isUploaded = MutableLiveData<Event<Boolean>>()
    val isUploaded: LiveData<Event<Boolean>> = _isUploaded

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

    fun postUploadStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: Double?, lon: Double?) {
        _isLoading.value = true
        val service =
            ApiConfig.getApiService().postAddStory("Bearer ".plus(token), file, description, lat, lon)
        service.enqueue(object : Callback<PostAddStoryResponse> {
            override fun onResponse(
                call: Call<PostAddStoryResponse>,
                response: Response<PostAddStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _snackbarText.value = Event(response.body()?.message.toString())
                        _isUploaded.value = Event(true)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostAddStoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "UploadViewModel"
    }
}