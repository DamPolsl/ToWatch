package com.example.towatch

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    @SerializedName("Poster") var poster: String,
    @SerializedName("Year") var year: String,
    @SerializedName("Title") var title: String,
    @SerializedName("Plot") var plot: String,
    var watched: Boolean,
    @SerializedName("imdbID") var id: String
) : Parcelable {
    constructor(): this("", "", "", "",false, "")
    constructor(
        title: String = "",
        poster: String = "",
        year: String = "",
        plot: String = "",
        id: String = "",
    ): this(poster, year, title, plot,false, id)
}