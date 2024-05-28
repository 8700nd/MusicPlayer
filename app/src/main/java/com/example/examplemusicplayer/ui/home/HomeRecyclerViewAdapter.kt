package com.example.examplemusicplayer.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.examplemusicplayer.data.models.Song
import com.example.examplemusicplayer.databinding.SongItemBinding

class HomeRecyclerViewAdapter(private val onUserClicked: (Song, Int) -> Unit) :
    RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>() {

    private var songList: List<Song> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = songList[position]
        holder.bind(user, position)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    fun updateData(list: List<Song>) {
        songList = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: SongItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song, position: Int) {
            binding.songName.text = song.title
            binding.artistName.text = song.artist

            setListener(song, position)
        }

        private fun setListener(song: Song, position: Int) {
            itemView.setOnClickListener {
                onUserClicked(song, position)
            }
        }
    }
}