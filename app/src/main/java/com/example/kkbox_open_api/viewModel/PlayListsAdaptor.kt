package com.example.kkbox_open_api.viewModel

import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kkbox_open_api.AppInfo.WHITE_IMAGE_SIZE
import com.example.kkbox_open_api.model.PlayListsResponse
import com.example.kkbox_open_api.databinding.PlaylistsItemBinding
import java.util.concurrent.CopyOnWriteArrayList

class PlayListsAdaptor(private val viewModel: PlayListsViewModel) :
    RecyclerView.Adapter<PlayListsAdaptor.ViewHolder>() {

    var list: ArrayList<PlayListsResponse>? = viewModel.listLiveData.value
    var imageList: CopyOnWriteArrayList<Bitmap>? = viewModel.imageLiveData.value

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list!![position]
        if (imageList == null) {
            holder.bind(viewModel, item, Bitmap.createBitmap(WHITE_IMAGE_SIZE, WHITE_IMAGE_SIZE, Bitmap.Config.RGB_565))
        } else if (imageList != null && position >= imageList!!.size) {
            holder.bind(viewModel, item, Bitmap.createBitmap(WHITE_IMAGE_SIZE, WHITE_IMAGE_SIZE, Bitmap.Config.RGB_565))
        }  else {
            val imageItem = imageList!![position]
            holder.bind(viewModel, item, imageItem)
        }
    }

    override fun getItemCount(): Int {
        return list?.count() ?: 0
    }

    class ViewHolder private constructor(private val binding: PlaylistsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: PlayListsViewModel, playLists: PlayListsResponse, imageItem: Bitmap) {
            binding.playListsViewModel = viewModel
            binding.playLists = playLists
            if (imageItem.width == WHITE_IMAGE_SIZE && imageItem.height == WHITE_IMAGE_SIZE) {
                imageItem.eraseColor(Color.WHITE)
            }
            binding.bitmap = imageItem
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PlaylistsItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}