package com.example.kkbox_open_api.model

import com.kkbox.openapideveloper.api.Api

interface IPlayListAPI {
    interface LoadAPICallBack {
        fun onGetResult(playListResponse: ArrayList<PlayListResponse>)
    }

    fun getPlayList(api: Api, id: String, loadAPICallBack: LoadAPICallBack)
}

class PlayListAPI: IPlayListAPI {

    override fun getPlayList(api: Api, id: String, loadAPICallBack: IPlayListAPI.LoadAPICallBack) {
        val playListResponse =  ArrayList<PlayListResponse>()
        val searchResult = api.featuredPlaylistFetcher.setPlaylistId(id).fetchFeaturedPlaylist().get()
        val data = searchResult.getAsJsonObject("tracks").getAsJsonArray("data")
        val images = searchResult.getAsJsonArray("images")

        for (i in data) {
            val playList = PlayListResponse()
            playList.name = i.asJsonObject.get("name").asString
            playList.artist = i.asJsonObject.getAsJsonObject("album").getAsJsonObject("artist").get("name").asString
            playList.releaseDate = i.asJsonObject.getAsJsonObject("album").get("release_date").asString
            playList.songImageUrl = i.asJsonObject.getAsJsonObject("album").getAsJsonArray("images").get(1).asJsonObject.get("url").asString
            playList.coverImageUrl = images.get(2).asJsonObject.get("url").asString
            playList.songUrl = i.asJsonObject.get("url").asString
            playListResponse.add(playList)
        }

        loadAPICallBack.onGetResult(playListResponse)
    }
}

