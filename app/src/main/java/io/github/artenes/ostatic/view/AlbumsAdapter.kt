package io.github.artenes.ostatic.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.TopAlbumView
import kotlinx.android.synthetic.main.full_album_item.view.*

class AlbumsAdapter(val listener: OnAlbumClickListener) : RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder>() {

    interface OnAlbumClickListener {
        fun onAlbumClicked(album: TopAlbumView)
    }

    var albums: MutableList<TopAlbumView> = mutableListOf()

    fun setData(newAlbums: List<TopAlbumView>) {
        albums.clear()
        albums.addAll(newAlbums)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.full_album_item, parent, false)
        return AlbumsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onBindViewHolder(holder: AlbumsViewHolder, position: Int) {
        val album = albums[position]
        holder.bind(album)
    }

    inner class AlbumsViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        fun bind(album: TopAlbumView) {
            itemView.albumTitle.text = album.name
            itemView.albumSongs.text = itemView.context.getString(R.string.number_songs, album.files)
            if (album.files == 0) {
                itemView.albumSongs.visibility = View.GONE
            }
            val albumUrl =
                if (album.cover.isNullOrEmpty()) "android.resource://io.github.artenes.ostatic/drawable/album" else album.cover
            Picasso.get()
                .load(albumUrl)
                .resize(120, 120)
                .centerCrop()
                .placeholder(R.drawable.album)
                .error(R.drawable.album)
                .into(itemView.albumCover)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val album = albums[adapterPosition]
            listener.onAlbumClicked(album)
        }
    }

}