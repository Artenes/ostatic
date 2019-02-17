package io.github.artenes.ostatic.view

import android.content.ComponentName
import android.content.Context
import android.net.Uri
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
import io.github.artenes.ostatic.db.TopAlbumView
import io.github.artenes.ostatic.model.AlbumWithCategory
import io.github.artenes.ostatic.player.MusicBrowser
import io.github.artenes.ostatic.player.MusicPlayerService
import kotlinx.android.synthetic.main.preload_list.view.*

class HomeFragment : Fragment(), AlbumListAdapter.OnAlbumClickListener {

    companion object {
        private const val ALBUM_LIMIT_PER_CATEGORY = 7
    }

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
            bundleOf(MusicBrowser.BUNDLE_KEY_LIMIT to ALBUM_LIMIT_PER_CATEGORY),
            object : MediaBrowserCompat.SubscriptionCallback() {

                override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>, options: Bundle) {
                    adapter.setData(makeListOfSections(children))
                    view.content.visibility = View.VISIBLE
                    view.progress.visibility = View.GONE
                }

            })

    }

    override fun onAlbumClick(album: TopAlbumView) {
        //work around to use what exists already
        if (album.size == TopAlbumView.NEXT_PAGE_ID) {
            (requireActivity() as MainActivity).openAllAlbumsFromHome(album.added, album.id)
        } else {
            (requireActivity() as MainActivity).openAlbumFromHome(album.id)
        }
    }

    private fun makeListOfSections(albums: MutableList<MediaBrowserCompat.MediaItem>): List<AlbumSection> {
        val sections = mutableListOf<AlbumSection>()

        val recents = albums.filter {
            it.description.extras?.getString(MusicBrowser.BUNDLE_KEY_CATEGORY)?.equals(
                AlbumWithCategory.CATEGORY_RECENT
            ) ?: false
        }.map {
            TopAlbumView(
                it.mediaId as String,
                it.description.title as String,
                "",
                "",
                "",
                0,
                0,
                it.description.iconUri.toString()
            )
        }.toMutableList()
        if (recents.isNotEmpty()) {
            if (recents.size == ALBUM_LIMIT_PER_CATEGORY) {
                recents.add(
                    TopAlbumView.makeNextPageAlbum(
                        AlbumsFragment.TOP_RECENT,
                        getString(R.string.all_albums),
                        getString(R.string.top_recent_soundtracks)
                    )
                )
            }
            sections.add(
                AlbumSection(
                    getString(R.string.top_recent_soundtracks),
                    getString(R.string.top_recent_soundtracks_subtitle),
                    recents,
                    false
                )
            )
        }

        val top40 = albums.filter {
            it.description.extras?.getString(MusicBrowser.BUNDLE_KEY_CATEGORY)?.equals(
                AlbumWithCategory.CATEGORY_TOP_40
            ) ?: false
        }.map {
            TopAlbumView(
                it.mediaId as String,
                it.description.title as String,
                "",
                "",
                "",
                0,
                0,
                it.description.iconUri.toString()
            )
        }.toMutableList()
        if (top40.isNotEmpty()) {
            top40.add(
                TopAlbumView.makeNextPageAlbum(
                    AlbumsFragment.TOP_40,
                    getString(R.string.all_albums),
                    getString(R.string.top_40_soundtracks)
                )
            )
            sections.add(
                AlbumSection(
                    getString(R.string.top_40_soundtracks),
                    getString(R.string.top_40_soundtracks_subtitle),
                    top40,
                    false
                )
            )
        }

        val topNewly = albums.filter {
            it.description.extras?.getString(MusicBrowser.BUNDLE_KEY_CATEGORY)?.equals(
                AlbumWithCategory.CATEGORY_TOP_NEWLY
            ) ?: false
        }.map {
            TopAlbumView(
                it.mediaId as String,
                it.description.title as String,
                "",
                "",
                "",
                0,
                0,
                it.description.iconUri.toString()
            )
        }.toMutableList()
        if (topNewly.isNotEmpty()) {
            topNewly.add(
                TopAlbumView.makeNextPageAlbum(
                    AlbumsFragment.TOP_NEWLY,
                    getString(R.string.all_albums),
                    getString(R.string.top_newly_soundtracks)
                )
            )
            sections.add(
                AlbumSection(
                    getString(R.string.top_newly_soundtracks),
                    getString(R.string.top_newly_soundtracks_subtitle),
                    topNewly,
                    false
                )
            )
        }

        val top6Months = albums.filter {
            it.description.extras?.getString(MusicBrowser.BUNDLE_KEY_CATEGORY)?.equals(
                AlbumWithCategory.CATEGORY_TOP_6_MONTHS
            ) ?: false
        }.map {
            TopAlbumView(
                it.mediaId as String,
                it.description.title as String,
                "",
                "",
                "",
                0,
                0,
                it.description.iconUri.toString()
            )
        }.toMutableList()
        if (top6Months.isNotEmpty()) {
            top6Months.add(
                TopAlbumView.makeNextPageAlbum(
                    AlbumsFragment.TOP_LAST,
                    getString(R.string.all_albums),
                    getString(R.string.top_months_soundtracks)
                )
            )
            sections.add(
                AlbumSection(
                    getString(R.string.top_months_soundtracks),
                    getString(R.string.top_months_soundtracks_subtitle),
                    top6Months,
                    false
                )
            )
        }

        val topAllTime = albums.filter {
            it.description.extras?.getString(MusicBrowser.BUNDLE_KEY_CATEGORY)?.equals(
                AlbumWithCategory.CATEGORY_TOP_ALL
            ) ?: false
        }.map {
            TopAlbumView(
                it.mediaId as String,
                it.description.title as String,
                "",
                "",
                "",
                0,
                0,
                it.description.iconUri.toString()
            )
        }.toMutableList()
        if (topAllTime.isNotEmpty()) {
            topAllTime.add(
                TopAlbumView.makeNextPageAlbum(
                    AlbumsFragment.TOP_ALL,
                    getString(R.string.all_albums),
                    getString(R.string.top_all_soundtracks)
                )
            )
            sections.add(
                AlbumSection(
                    getString(R.string.top_all_soundtracks),
                    getString(R.string.top_all_soundtracks_subtitle),
                    topAllTime,
                    false
                )
            )
        }

        return sections
    }

    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            loadAndBind()
        }
    }

}