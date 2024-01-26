package com.example.storyapp.view.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import com.example.storyapp.R
import com.example.storyapp.data.model.StoryModel
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.detail.DetailActivity
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.view.main.MainViewModel
import com.example.storyapp.view.main.dataStore
import com.example.storyapp.view.upload.UploadActivity

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.item_row_story.view.*

class MapsActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this, dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupViewModel()
    }

    private fun setupViewModel() {
        mapsViewModel.getUser().observe(this) { user ->
            if (user.userId == "" && user.name == "" && user.token == "") {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                mapsViewModel.getAllStory(user.token)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val mapButton = menu.findItem(R.id.mapButton)
        mapButton.isVisible = false

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.addStoryButton -> {
                val moveUploadActivity = Intent(this, UploadActivity::class.java)
                startActivity(moveUploadActivity)
            }
            R.id.logoutButton -> {
                mapsViewModel.logout()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mapsViewModel.listStory.observe(this) { listStory ->
           listStory.forEach {
               val marker = mMap.addMarker(
                   MarkerOptions()
                       .position(LatLng(it.lat, it.lon))
                       .title(it.name)
                       .snippet(it.description)
                       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
               )
               marker?.tag = it
           }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(listStory[0].lat, listStory[0].lon), 16f))
        }

        mMap.setOnMarkerClickListener(this)
        getMyLocation()
        setMapStyle()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val story = marker.tag as Story
        val storyDetail = StoryModel(
            story.id,
            story.name,
            story.description,
            story.photoUrl
        )
        val moveDetailActivity = Intent(this@MapsActivity, DetailActivity::class.java)
        moveDetailActivity.putExtra("Story", storyDetail)
        startActivity(moveDetailActivity)

        return false
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}