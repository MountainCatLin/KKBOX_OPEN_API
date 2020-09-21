package com.example.kkbox_open_api.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.example.kkbox_open_api.view.Event
import com.example.kkbox_open_api.model.IPlayListRepository
import com.example.kkbox_open_api.model.PlayListResponse
import com.example.kkbox_open_api.view.MainActivity
import com.kkbox.openapideveloper.api.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList

class PlayListViewModel(private val playListRepository: IPlayListRepository): ViewModel() {
    var listLiveData: MutableLiveData<ArrayList<PlayListResponse>> = MutableLiveData()
    private var imageArray: CopyOnWriteArrayList<Bitmap> = CopyOnWriteArrayList<Bitmap>()
    var imageLiveData: MutableLiveData<CopyOnWriteArrayList<Bitmap>> = MutableLiveData()
    var openSongEvent: MutableLiveData<Event<String>> = MutableLiveData()

    fun getPlayList(api: Api, id: String) {
        playListRepository.getPlayList(api, id, object :
            IPlayListRepository.LoadPlayListCallback {
            override fun onPlayListResult(playListResponse: ArrayList<PlayListResponse>) {
                viewModelScope.launch {
                    getData(playListResponse)
                }
            }
        })
    }

    fun openSong(url: String) {
        openSongEvent.value = Event(url)
    }

    suspend fun getData(playListResponse: ArrayList<PlayListResponse>) = withContext(Dispatchers.IO) {
        listLiveData.postValue(playListResponse)
        val playListsResponseThreadArray = CopyOnWriteArrayList<PlayListResponse>(playListResponse)
        playListsResponseThreadArray.stream().forEach {
            var ordinaryImage = Glide.with(MainActivity.context!!).asBitmap().load(it.songImageUrl).submit().get()
            imageArray.add(ordinaryImage)
            imageLiveData.postValue(imageArray)
        }
    }
}