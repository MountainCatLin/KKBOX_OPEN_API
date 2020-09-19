package com.example.kkbox_open_api.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kkbox_open_api.R
import com.example.kkbox_open_api.AppInfo.CLIENT_ID
import com.example.kkbox_open_api.AppInfo.CLIENT_SECRET
import com.example.kkbox_open_api.model.PlayListAPI
import com.example.kkbox_open_api.model.PlayListRepository
import com.example.kkbox_open_api.model.PlayListResponse
import com.example.kkbox_open_api.viewModel.PlayListAdaptor
import com.example.kkbox_open_api.viewModel.PlayListViewModel
import com.example.kkbox_open_api.viewModel.PlayListViewModelFactory
import com.kkbox.openapideveloper.api.Api
import com.kkbox.openapideveloper.auth.Auth
import com.koushikdutta.ion.Ion
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

class PlayListActivity : AppCompatActivity() {
    companion object {
        val inferenceThread = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
    }
    private lateinit var playListViewModel: PlayListViewModel
    private lateinit var auth: Auth
    private lateinit var accessToken: String
    private lateinit var api: Api
    private lateinit var adapter: PlayListAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        auth = Auth(CLIENT_ID, CLIENT_SECRET, this)
        accessToken = auth.clientCredentialsFlow.fetchAccessToken().get().get("access_token").asString
        api = Api(accessToken, "TW", this)
        val playListAPI = PlayListAPI()
        val playListRepository = PlayListRepository(playListAPI)
        playListViewModel = ViewModelProvider(this, PlayListViewModelFactory(playListRepository)).get(PlayListViewModel::class.java)

        playListViewModel.getPlayList(api, intent.getStringExtra("playListId"));
        adapter = PlayListAdaptor(playListViewModel)
        val recyclerView: RecyclerView = findViewById(R.id.viewSongs)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        playListViewModel.listLiveData.observe(this,
            Observer<ArrayList<PlayListResponse>> {
                val coverImageView : ImageView = findViewById(R.id.coverImageView)
                Ion.with(coverImageView)
                    .load(it[0].coverImageUrl)
                adapter.list = it
                recyclerView.post {
                    adapter.notifyDataSetChanged()
                }
            })

        playListViewModel.imageLiveData.observe(this,
            Observer<CopyOnWriteArrayList<Bitmap>> {
                adapter.imageList = it
                recyclerView.post {
                    adapter.notifyDataSetChanged()
                }
            })

        playListViewModel.openSongEvent.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                val songUrl = it
                val intent = Intent(this, SongActivity::class.java)
                intent.putExtra("songUrl", songUrl)
                startActivity(intent)
            }
        })
    }
}
