package com.example.kkbox_open_api.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.example.kkbox_open_api.R

class SongActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_song)
        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.loadUrl(intent.getStringExtra("songUrl"))

    }
}
