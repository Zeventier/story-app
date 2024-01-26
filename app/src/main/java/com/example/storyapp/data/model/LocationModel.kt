package com.example.storyapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationModel(
    var lat: Double?,
    var lon: Double?
) : Parcelable