package com.example.kkbox_open_api.model

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter

class PlayListsResponse {
    lateinit var id : String
    lateinit var title : String
    lateinit var owner : String
    lateinit var updateAt : String
    lateinit var imageUrl : String
}


@BindingAdapter("playListImageBitmap")
fun bindPlayListImage(iv: ImageView, bitmap: Bitmap) {
    iv.setImageBitmap(bitmap)
}


