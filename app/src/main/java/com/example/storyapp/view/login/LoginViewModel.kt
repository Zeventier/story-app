package com.example.storyapp.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.UserPreference
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.data.remote.response.PostLoginResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.view.Event
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    private val _isLogin = MutableLiveData<Event<Boolean>>()
    val isLogin: LiveData<Event<Boolean>> = _isLogin

    fun login(user: LoginResult) {
        viewModelScope.launch {
            val result = pref.login(user)
            _isLogin.value = Event(result)
        }
    }

    fun postLogin(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().postLogin(email, password)
        client.enqueue(object : Callback<PostLoginResponse> {
            override fun onResponse(
                call: Call<PostLoginResponse>,
                response: Response<PostLoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    if (!response.body()!!.error) {
                        val loginResult = response.body()!!.loginResult
                        login(loginResult)
                    }
                    _snackbarText.value = Event(response.body()!!.message)
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostLoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}