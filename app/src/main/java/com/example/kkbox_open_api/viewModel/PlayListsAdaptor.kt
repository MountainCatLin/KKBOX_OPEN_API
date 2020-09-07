package com.example.kkbox_open_api.viewModel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kkbox_open_api.model.PlayListsResponse
import com.example.kkbox_open_api.databinding.PlaylistsItemBinding

class PlayListsAdaptor(private val viewModel: PlayListsViewModel) :
    RecyclerView.Adapter<PlayListsAdaptor.ViewHolder>() {

    var list: ArrayList<PlayListsResponse>? = viewModel.listLiveData.value

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list!![position]
        holder.bind(viewModel, item)
    }

    override fun getItemCount(): Int {
        return list?.count() ?: 0
    }

    class ViewHolder private constructor(private val binding: PlaylistsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: PlayListsViewModel, playLists: PlayListsResponse) {
            binding.playListsViewModel = viewModel
            binding.playLists = playLists
            val d = binding.title.text
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