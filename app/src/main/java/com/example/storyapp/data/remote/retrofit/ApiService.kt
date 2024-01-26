package com.example.storyapp.data.remote.retrofit

import com.example.storyapp.data.remote.response.PostAddStoryResponse
import com.example.storyapp.data.remote.response.PostLoginResponse
import com.example.storyapp.data.remote.response.PostRegisterResponse
import com.example.storyapp.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.function.DoublePredicate

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<PostRegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<PostLoginResponse>

    @Multipart
    @POST("stories")
    fun postAddStory(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): Call<PostAddStoryResponse>

    //@Headers("Authorization: Bearer {token}")
    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoriesResponse

    @GET("stories?location=1")
    fun getStoryWithLocation(
        @Header("Authorization") authorization: String
    ): Call<StoriesResponse>
}
