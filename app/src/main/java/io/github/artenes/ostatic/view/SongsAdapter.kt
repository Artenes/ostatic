package io.github.artenes.ostatic.view

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.SongView
import kotlinx.android.synthetic.main.song_item.view.*

class SongsAdapter(val listener: OnSongClickListener) : RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

    companion object {

        const val PLAY = 1
        const val BUFFER = 2
        const val PAUSE = 3

    }

    interface OnSongClickListener {
        fun onSongClick(position: Int, song: SongView)
    }

    var songs: MutableList<SongView> = mutableListOf()

    var currentIndex: Int = -1
    var state: Int = PLAY

    fun setData(newSongs: List<SongView>) {
        songs.clear()
        songs.addAll(newSongs)
        notifyDataSetChanged()
    }

    fun buffer(position: Int) {
        state = BUFFER
        refreshItems(position)
    }

    fun play(position: Int) {
        state = PAUSE
        refreshItems(position)
    }

    fun pause(position: Int) {
        state = PLAY
        refreshItems(position)
    }

    private fun isInRange(position: Int): Boolean {
        return position >= 0 && position < songs.size
    }

    private fun refreshItems(position: Int) {
        val previousPosition = currentIndex
        currentIndex = position
        if (isInRange(previousPosition)) {
            notifyItemChanged(previousPosition)
        }
        notifyItemChanged(currentIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.song_item, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(position, song)
    }

    inner class SongViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        fun bind(position: Int, song: SongView) {
            val resources = itemView.context.resources
            itemView.setOnClickListener(this)
            itemView.songName.text = song.name
            itemView.songTime.text = song.time

            if (position == currentIndex) {
                when (state) {
                    PLAY -> {
                        itemView.stateIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_play, null))
                        itemView.stateIcon.visibility = View.VISIBLE
                        itemView.progressBar.visibility = View.GONE
                        itemView.background = ColorDrawable(resources.getColor(R.color.colorAccentLight, null))
                    }
                    BUFFER -> {
                        itemView.stateIcon.visibility = View.GONE
                        itemView.progressBar.visibility = View.VISIBLE
                        itemView.background = null
                    }
                    else -> {
                        itemView.stateIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_pause, null))
                        itemView.stateIcon.visibility = View.VISIBLE
                        itemView.progressBar.visibility = View.GONE
                        itemView.background = ColorDrawable(resources.getColor(R.color.colorAccentLight, null))
                    }
                }
            } else {
                itemView.stateIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_play, null))
                itemView.stateIcon.visibility = View.VISIBLE
                itemView.progressBar.visibility = View.GONE
                itemView.background = null
            }

        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            val song = songs[position]
            listener.onSongClick(position, song)
        }

    }

}