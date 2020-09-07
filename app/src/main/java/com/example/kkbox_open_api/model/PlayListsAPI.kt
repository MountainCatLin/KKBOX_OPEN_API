package com.example.kkbox_open_api.model

import com.example.kkbox_open_api.AppInfo.HIGH_WIDTH
import com.example.kkbox_open_api.AppInfo.LOW_IMAGE_RESOLUTION_FILE
import com.example.kkbox_open_api.AppInfo.LOW_WIDTH
import com.kkbox.openapideveloper.api.Api

interface IPlayListsAPI {
    interface LoadAPICallBack {
        fun onGetResult(playListsResponse: ArrayList<PlayListsResponse>)
    }

    fun getPlayLists(api: Api, offset:Int, limit:Int, resolution: String, loadAPICallBack: LoadAPICallBack)
}

class PlayListsAPI: IPlayListsAPI {
    override fun getPlayLists(api: Api, offset: Int, limit: Int, resolution: String, loadAPICallBack: IPlayListsAPI.LoadAPICallBack) {
        val playListResponse =  ArrayList<PlayListsResponse>()
        val searchResult = api.featuredPlaylistFetcher.fetchAllFeaturedPlaylists(limit=limit, offset=offset).get()
        val data = searchResult.getAsJsonArray("data")

        for (i in data) {
            val playList = PlayListsResponse()
            playList.id = i.asJsonObject.get("id").asString
            playList.title = i.asJsonObject.get("title").asString
            playList.owner = i.asJsonObject.getAsJsonObject("owner").get("name").asString
            playList.updateAt = i.asJsonObject.get("updated_at").asString.split("T")[0]
            if (resolution.contains(LOW_WIDTH)) {
                val rowUrlString : String = i.asJsonObject.getAsJsonArray("images").get(0).asJsonObject.get("url").asString
                val splitArray = rowUrlString.split("/")
                val replacedString : String = splitArray[splitArray.size - 1]
                playList.imageUrl = rowUrlString.replace(replacedString, LOW_IMAGE_RESOLUTION_FILE)
            } else if (resolution.contains(HIGH_WIDTH)) {
                playList.imageUrl = i.asJsonObject.getAsJsonArray("images").get(2).asJsonObject.get("url").asString
            }
            playListResponse.add(playList)
        }

        loadAPICallBack.onGetResult(playListResponse)
    }
}

