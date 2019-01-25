package io.github.artenes.ostatic.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.SongEntity
import kotlinx.android.synthetic.main.song_item.view.*

class SongsAdapter : RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

    var songs: MutableList<SongEntity> = mutableListOf()

    fun setData(newSongs: List<SongEntity>) {
        songs.clear()
        songs.addAll(newSongs)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder.make(parent)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song)
    }

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun make(parent: ViewGroup): SongViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.song_item, parent, false)
                return SongViewHolder(view)
            }
        }

        fun bind(song: SongEntity) {
            itemView.songName.text = song.name
            itemView.songTime.text = song.time
        }

    }

}