package com.example.towatch

import android.content.Context
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

class ProgressDrawable(context: Context) : CircularProgressDrawable(context) {
    init {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }
}