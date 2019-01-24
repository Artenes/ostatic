package io.github.artenes.ostatic.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.TopAlbumView
import kotlinx.android.synthetic.main.full_album_item.view.*

class AlbumsAdapter : RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder>() {

    var albums: MutableList<TopAlbumView> = mutableListOf()

    fun setData(newAlbums: List<TopAlbumView>) {
        albums.clear()
        albums.addAll(newAlbums)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumsViewHolder {
        return AlbumsViewHolder.make(parent)
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onBindViewHolder(holder: AlbumsViewHolder, position: Int) {
        val album = albums[position]
        holder.bind(album)
    }

    class AlbumsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun make(parent: ViewGroup): AlbumsViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.full_album_item, parent, false)
                return AlbumsViewHolder(view)
            }
        }

        fun bind(album: TopAlbumView) {
            itemView.albumTitle.text = album.name
            itemView.albumSongs.text = itemView.context.getString(R.string.number_songs, album.files)
            if (!album.cover.isNullOrEmpty()) {
                Picasso.get()
                    .load(album.cover)
                    .into(itemView.albumCover)
            } else {
                itemView.albumCover.setImageDrawable(ColorDrawable(Color.WHITE))
            }
        }

    }

}