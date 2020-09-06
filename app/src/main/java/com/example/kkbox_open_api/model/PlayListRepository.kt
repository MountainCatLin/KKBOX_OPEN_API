package com.example.kkbox_open_api.model

import com.kkbox.openapideveloper.api.Api

interface IPlayListRepository {
    fun getPlayList(api: Api, id: String, loadPlayListCallback: LoadPlayListCallback)

    interface LoadPlayListCallback {

        fun onPlayListResult(playListResponse: ArrayList<PlayListResponse>)
    }

}

class PlayListRepository(private val playListAPI: IPlayListAPI) :
    IPlayListRepository {
    override fun getPlayList(api: Api, id: String, loadPlayListCallback: IPlayListRepository.LoadPlayListCallback) {

        playListAPI.getPlayList(api, id, object : IPlayListAPI.LoadAPICallBack {
            override fun onGetResult(playListResponse: ArrayList<PlayListResponse>) {
                loadPlayListCallback.onPlayListResult(playListResponse)
            }
        })
    }
}