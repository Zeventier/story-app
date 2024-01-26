package com.example.storyapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.data.remote.response.StoriesResponse
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.view.main.MainViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryPagingSource(private val token: String) : PagingSource<Int, Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = ApiConfig.getApiService().getStory("Bearer ".plus(token), position, params.loadSize).listStory

            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        private const val TAG = "StoryPagingSource"
    }
}