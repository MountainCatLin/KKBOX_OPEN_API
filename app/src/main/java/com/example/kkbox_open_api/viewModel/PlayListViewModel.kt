package com.example.kkbox_open_api.viewModel

import androidx.lifecycle.*
import com.example.kkbox_open_api.view.Event
import com.example.kkbox_open_api.model.IPlayListRepository
import com.example.kkbox_open_api.model.PlayListResponse
import com.kkbox.openapideveloper.api.Api
import kotlinx.coroutines.launch

class PlayListViewModel(private val playListRepository: IPlayListRepository): ViewModel() {
    var listLiveData : MutableLiveData<ArrayList<PlayListResponse>> = MutableLiveData()
    var openSongEvent: MutableLiveData<Event<String>> = MutableLiveData()

    fun getPlayList(api: Api, id: String) {
        playListRepository.getPlayList(api, id, object :
            IPlayListRepository.LoadPlayListCallback {
            override fun onPlayListResult(playListResponse: ArrayList<PlayListResponse>) {
                viewModelScope.launch {
                    listLiveData.value = playListResponse
                }
            }
        })
    }

    fun openSong(url: String) {
        openSongEvent.value = Event(url)
    }
}