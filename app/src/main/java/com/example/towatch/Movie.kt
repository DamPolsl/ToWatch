package com.example.towatch

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    var poster: String,
    var year: String,
    var title: String,
    var plot: String,
    var watched: Boolean,
    var id: String
) : Parcelable {
    constructor(): this("", "", "", "",false, "")
}