package com.example.storyapp.view.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.model.StoryModel
import com.example.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setupData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val logoutButton = menu.findItem(R.id.logoutButton)
        logoutButton.isVisible = false

        val mapButton = menu.findItem(R.id.mapButton)
        mapButton.isVisible = false

        val addButton = menu.findItem(R.id.addStoryButton)
        addButton.isVisible = false


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupData() {
        val story = intent.getParcelableExtra<StoryModel>("Story") as StoryModel
        Glide.with(applicationContext)
            .load(story.photoUrl)
            .into(binding.photoImageView)
        binding.apply {
            nameTextView.text = story.name
            descTextView.text = story.description
        }
    }
}