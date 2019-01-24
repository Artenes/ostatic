package io.github.artenes.ostatic.view

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.TopAlbumView
import kotlinx.android.synthetic.main.album_section.view.*

data class Album(val title: String, val cover: String)

data class AlbumSection(val title: String, val subtitle: String, val albums: List<TopAlbumView>, val isHighlight: Boolean)

class TopAlbumsAdapter : RecyclerView.Adapter<TopAlbumsAdapter.AlbumSectionViewHolder>() {

    var sections: MutableList<AlbumSection> = mutableListOf()

    fun setData(newSections: List<AlbumSection>) {
        sections.clear()
        sections.addAll(newSections)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumSectionViewHolder {
        return AlbumSectionViewHolder.make(parent)
    }

    override fun getItemCount(): Int {
        return sections.size
    }

    override fun onBindViewHolder(holder: AlbumSectionViewHolder, position: Int) {
        val section = sections[position]
        holder.bind(section, position == 0)
    }

    class AlbumSectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {

            fun make(parent: ViewGroup): AlbumSectionViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.album_section, parent, false)
                return AlbumSectionViewHolder(view)
            }

        }

        fun bind(albumSection: AlbumSection, isFirst: Boolean) {
            val adapter = AlbumListAdapter(albumSection.isHighlight)
            val sidesMargins = itemView.context.resources.getDimensionPixelSize(R.dimen.album_items_sides_space)
            val bottomMargin = itemView.context.resources.getDimensionPixelSize(R.dimen.album_items_bottom_space)

            if (isFirst) {
                val vg = itemView.sectionTitle.layoutParams as ViewGroup.MarginLayoutParams
                vg.setMargins(0, itemView.context.resources.getDimensionPixelSize(R.dimen.top_margin_first_section), 0, 0)
                itemView.sectionTitle.requestLayout()
            } else {
                val vg = itemView.sectionTitle.layoutParams as ViewGroup.MarginLayoutParams
                vg.setMargins(0, 0, 0, 0)
                itemView.sectionTitle.requestLayout()
            }

            itemView.sectionTitle.text = albumSection.title
            itemView.sectionSubtitle.text = albumSection.subtitle
            itemView.sectionList.addItemDecoration(ItemOffsetDecorator(sidesMargins, bottomMargin))
            itemView.sectionList.adapter = adapter
            itemView.sectionList.setHasFixedSize(true)
            itemView.sectionList.layoutManager = if (albumSection.isHighlight) {
                GridLayoutManager(itemView.context, 2)
            } else {
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            }

            adapter.setData(albumSection.albums)
        }

    }

}

class AlbumListAdapter(val isHighlight: Boolean) : RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder>() {

    var albums: MutableList<TopAlbumView> = mutableListOf()

    fun setData(newAlbums: List<TopAlbumView>) {
        albums.clear()
        albums.addAll(newAlbums)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout = if (isHighlight) R.layout.album_highlight_item else R.layout.album_item
        val view = inflater.inflate(layout, parent, false)
        return AlbumViewHolder(view)
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albums[position]
        holder.bind(album)
    }

    inner class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(album: TopAlbumView) {
            itemView.findViewById<TextView>(R.id.itemTitle).text = album.name
            if (!album.cover.isNullOrEmpty()) {
                Picasso.get()
                    .load(album.cover)
                    .into(itemView.findViewById<ImageView>(R.id.itemAlbumCover))
            }
        }

    }

}

class ItemOffsetDecorator(val offset: Int, val bottom: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(offset, offset, offset, bottom)
    }

}