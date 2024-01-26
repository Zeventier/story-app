package com.example.storyapp.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.JsonToken
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.model.StoryModel
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.detail.DetailActivity
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.view.map.MapsActivity
import com.example.storyapp.view.upload.UploadActivity
import kotlinx.android.synthetic.main.item_row_story.view.*

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvUser.layoutManager = layoutManager

        setupViewModel()
    }

    private fun setupViewModel() {
        mainViewModel.getUser().observe(this) { user ->
            if (user.userId == "" && user.name == "" && user.token == "") {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                mainViewModel.setToken(user.token)
                setStoryData(user.token)
            }
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

//        mainViewModel.listStory.observe(this) { listStory ->
//            setStoryData(listStory)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addStoryButton -> {
                val moveUploadActivity = Intent(this, UploadActivity::class.java)
                startActivity(moveUploadActivity)
            }
            R.id.mapButton -> {
                val moveMapsActivity = Intent(this, MapsActivity::class.java)
                startActivity(moveMapsActivity)
            }
            R.id.logoutButton -> {
                mainViewModel.logout()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setStoryData(token: String) {
        val adapter = ListStoryAdapter()
        binding.rvUser.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        mainViewModel.listStory(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }

        adapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(storyDetail: Story) {
                val story = StoryModel(
                    storyDetail.id,
                    storyDetail.name,
                    storyDetail.description,
                    storyDetail.photoUrl
                )
                val moveDetailActivity = Intent(this@MainActivity, DetailActivity::class.java)
                moveDetailActivity.putExtra("Story", story)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@MainActivity,

                        Pair(binding.root.photoImageView, "photo"),
                        Pair(binding.root.nameTextView, "name"),
                        Pair(binding.root.descTextView, "description"),
                    )
                startActivity(moveDetailActivity, optionsCompat.toBundle())
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}