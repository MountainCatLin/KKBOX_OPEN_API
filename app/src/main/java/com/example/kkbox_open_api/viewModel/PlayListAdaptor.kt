package com.example.kkbox_open_api.viewModel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kkbox_open_api.model.PlayListResponse
import com.example.kkbox_open_api.databinding.PlaylistItemBinding

class PlayListAdaptor(private val viewModel: PlayListViewModel) :
    RecyclerView.Adapter<PlayListAdaptor.ViewHolder>() {

    var list: ArrayList<PlayListResponse>? = viewModel.listLiveData.value

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list!![position]
        holder.bind(viewModel, item)
    }

    override fun getItemCount(): Int {
        return list?.count() ?: 0
    }

    class ViewHolder private constructor(private val binding: PlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: PlayListViewModel, playList: PlayListResponse) {
            binding.playListViewModel = viewModel
            binding.playList = playList
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PlaylistItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}