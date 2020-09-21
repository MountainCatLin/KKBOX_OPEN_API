package com.example.kkbox_open_api.model

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter

class PlayListResponse {
    lateinit var name : String
    lateinit var artist : String
    lateinit var releaseDate : String
    lateinit var songImageUrl : String
    lateinit var coverImageUrl : String
    lateinit var songUrl : String
}

@BindingAdapter("songImageBitmap")
fun bindSongImage(iv: ImageView, bitmap: Bitmap) {
    iv.setImageBitmap(bitmap)
}

