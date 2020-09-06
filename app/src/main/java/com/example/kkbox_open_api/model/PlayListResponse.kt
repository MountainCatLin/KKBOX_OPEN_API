package com.example.kkbox_open_api.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.koushikdutta.ion.Ion

class PlayListResponse {
    lateinit var name : String
    lateinit var artist : String
    lateinit var releaseDate : String
    lateinit var songImageUrl : String
    lateinit var coverImageUrl : String
    lateinit var songUrl : String
}

@BindingAdapter("songImageUrl")
fun bindSongImage(imageView: ImageView, imageUrl: String) {

    Ion.with(imageView)
        .load(imageUrl)
}