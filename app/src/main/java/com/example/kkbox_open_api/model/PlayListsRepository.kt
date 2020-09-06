package com.example.kkbox_open_api.model

import com.kkbox.openapideveloper.api.Api


interface IPlayListsRepository {
    fun getPlayLists(api: Api, offset: Int, limit: Int, resolution: String, loadPlayListsCallback: LoadPlayListsCallback)

    interface LoadPlayListsCallback {

        fun onPlayListsResult(playListResponse: ArrayList<PlayListsResponse>)
    }

}

class PlayListsRepository(private val playListsAPI: IPlayListsAPI) :
    IPlayListsRepository {
    override fun getPlayLists(api: Api, offset: Int, limit: Int, resolution: String, loadPlayListsCallback: IPlayListsRepository.LoadPlayListsCallback) {

        playListsAPI.getPlayLists(api, offset, limit, resolution, object : IPlayListsAPI.LoadAPICallBack {
            override fun onGetResult(playListsResponse: ArrayList<PlayListsResponse>) {
                loadPlayListsCallback.onPlayListsResult(playListsResponse)
            }
        })
    }
}