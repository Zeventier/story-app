package com.example.storyapp.view.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.model.UserPreference
import com.example.storyapp.data.remote.response.PostRegisterResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.view.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val pref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    private val _isRegister = MutableLiveData<Event<Boolean>>()
    val isRegister: LiveData<Event<Boolean>> = _isRegister

    fun postRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().postRegister(name, email, password)
        client.enqueue(object : Callback<PostRegisterResponse> {
            override fun onResponse(
                call: Call<PostRegisterResponse>,
                response: Response<PostRegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    if (!response.body()!!.error) {
                        _isRegister.value = Event(true)
                    }
                    _snackbarText.value = Event(response.body()?.message.toString())
                } else {
                    Log.e(TAG, "notSuccesful: ${response.message()}")
                    _snackbarText.value = Event(response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<PostRegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _snackbarText.value = Event(t.message.toString())
            }
        })
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}