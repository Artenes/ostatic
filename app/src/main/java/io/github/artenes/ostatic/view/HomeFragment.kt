package io.github.artenes.ostatic.view

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.artenes.ostatic.MainActivity
import io.github.artenes.ostatic.OstaticApplication
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.model.*
import io.github.artenes.ostatic.player.MusicBrowser
import io.github.artenes.ostatic.player.MusicPlayerService
import kotlinx.android.synthetic.main.preload_list.view.*

class HomeFragment : Fragment(), AlbumListAdapter.OnAlbumClickListener {

    private lateinit var mediaBrowser: MediaBrowserCompat

    lateinit var adapter: TopAlbumsAdapter
    val repo = OstaticApplication.REPOSITORY

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context: Context = container?.context!!

        val view = inflater.inflate(R.layout.preload_list, container, false)

        adapter = TopAlbumsAdapter(this)
        view.mainList.layoutManager = LinearLayoutManager(context)
        view.mainList.adapter = adapter

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mediaBrowser = MediaBrowserCompat(
            requireContext(),
            ComponentName(requireContext(), MusicPlayerService::class.java),
            connectionCallbacks,
            null
        )
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        mediaBrowser.disconnect()
    }

    fun loadAndBind() {
        val view: View = view as View

        view.content.visibility = View.GONE
        view.progress.visibility = View.VISIBLE

        mediaBrowser.subscribe(
            mediaBrowser.root,
            bundleOf(MusicBrowser.BUNDLE_KEY_LIMIT to ViewConstants.TOP_ALBUM_DEFAULT_LIMIT),
            object : MediaBrowserCompat.SubscriptionCallback() {

                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>,
                    options: Bundle
                ) {
                    adapter.setData(makeListOfSections(children))
                    view.content.visibility = View.VISIBLE
                    view.progress.visibility = View.GONE
                }

            })

    }

    override fun onAlbumClick(album: AlbumForShowcase) {
        //work around to use what exists already
        if (album.isCategory()) {
            (requireActivity() as MainActivity).openAllAlbumsFromHome(album.uri)
        } else {
            (requireActivity() as MainActivity).openAlbumFromHome(album.uri)
        }
    }

    private fun MutableList<MediaBrowserCompat.MediaItem>.getCategory(category: String): MutableList<AlbumForShowcase> {
        return this.filter {
            it.description.extras?.getString(MusicBrowser.BUNDLE_KEY_CATEGORY)?.equals(category) ?: false
        }.map {
            AlbumForShowcase(
                it.mediaId as String,
                it.description.title as String,
                it.description.iconUri.toString(),
                it.description.mediaUri.toString()
            )
        }.toMutableList()
    }

    private fun createSection(
        albums: MutableList<MediaBrowserCompat.MediaItem>, category: String, title: String, subtitle: String
    ): AlbumSection {
        val albumsFromCategory = albums.getCategory(category)

        if (albumsFromCategory.size == ViewConstants.TOP_ALBUM_DEFAULT_LIMIT) {
            albumsFromCategory.add(
                AlbumForShowcase(
                    category,
                    getString(R.string.all_albums),
                    ViewConstants.MORE_ALBUMS_COVER,
                    Ostatic.makeAlbumPath(category)
                )
            )
        }

        return AlbumSection(title, subtitle, albumsFromCategory, false)
    }

    private fun makeListOfSections(albums: MutableList<MediaBrowserCompat.MediaItem>): List<AlbumSection> {
        val sections = mutableListOf<AlbumSection>()

        createSection(
            albums,
            AlbumCategory.CATEGORY_RECENT,
            getString(R.string.top_recent_soundtracks),
            getString(R.string.top_recent_soundtracks_subtitle)
        ).takeIf { it.albums.isNotEmpty() }?.apply {
            sections.add(this)
        }

        createSection(
            albums,
            AlbumCategory.CATEGORY_TOP_40,
            getString(R.string.top_40_soundtracks),
            getString(R.string.top_40_soundtracks_subtitle)
        ).takeIf { it.albums.isNotEmpty() }?.apply {
            sections.add(this)
        }

        createSection(
            albums,
            AlbumCategory.CATEGORY_TOP_NEWLY,
            getString(R.string.top_newly_soundtracks),
            getString(R.string.top_newly_soundtracks_subtitle)
        ).takeIf { it.albums.isNotEmpty() }?.apply {
            sections.add(this)
        }

        createSection(
            albums,
            AlbumCategory.CATEGORY_TOP_6_MONTHS,
            getString(R.string.top_months_soundtracks),
            getString(R.string.top_months_soundtracks_subtitle)
        ).takeIf { it.albums.isNotEmpty() }?.apply {
            sections.add(this)
        }

        createSection(
            albums,
            AlbumCategory.CATEGORY_TOP_ALL,
            getString(R.string.top_all_soundtracks),
            getString(R.string.top_all_soundtracks_subtitle)
        ).takeIf { it.albums.isNotEmpty() }?.apply {
            sections.add(this)
        }

        return sections
    }

    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            loadAndBind()
        }
    }

}