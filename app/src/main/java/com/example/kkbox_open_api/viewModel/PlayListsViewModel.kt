package com.example.kkbox_open_api.viewModel

import androidx.lifecycle.*
import com.example.kkbox_open_api.view.Event
import com.example.kkbox_open_api.model.IPlayListsRepository
import com.example.kkbox_open_api.model.PlayListsResponse
import com.kkbox.openapideveloper.api.Api
import kotlinx.coroutines.launch

class PlayListsViewModel(private val playListsRepository: IPlayListsRepository): ViewModel() {
    private var playListsResponseArray : ArrayList<PlayListsResponse> = ArrayList<PlayListsResponse>()
    var listLiveData : MutableLiveData<ArrayList<PlayListsResponse>> = MutableLiveData()
    var openPlayListEvent: MutableLiveData<Event<String>> = MutableLiveData()


    fun getPlayLists(api: Api, offset: Int, limit: Int, resolution: String) {
        playListsRepository.getPlayLists(api, offset, limit, resolution, object :
            IPlayListsRepository.LoadPlayListsCallback {
            override fun onPlayListsResult(playListsResponse: ArrayList<PlayListsResponse>) {
                viewModelScope.launch {
                    if (offset == playListsResponseArray.size) {
                        playListsResponseArray.addAll(playListsResponse)
                        listLiveData.value = playListsResponseArray
                    }
                }
            }
        })
    }

    fun openPlayList(id: String) {
        openPlayListEvent.value = Event(id)
    }
}