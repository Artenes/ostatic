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
import io.github.artenes.ostatic.model.AlbumCategory
import io.github.artenes.ostatic.player.MusicBrowser
import io.github.artenes.ostatic.player.MusicPlayerService
import io.github.artenes.ostatic.player.isACategory
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

    override fun onAlbumClick(album: MediaBrowserCompat.MediaItem) {
        if (album.isACategory()) {
            (requireActivity() as MainActivity).openAllAlbumsFromHome(album.description.mediaUri.toString())
        } else {
            (requireActivity() as MainActivity).openAlbumFromHome(album.description.mediaUri.toString())
        }
    }

    private fun MutableList<MediaBrowserCompat.MediaItem>.getOfCategory(category: String): MutableList<MediaBrowserCompat.MediaItem> {
        return this.filter {
            it.description.extras?.getString(MusicBrowser.BUNDLE_KEY_CATEGORY)?.equals(category) ?: false
        }.toMutableList()
    }

    private fun makeListOfSections(albums: MutableList<MediaBrowserCompat.MediaItem>): List<AlbumSection> {
        val sections = mutableListOf<AlbumSection>()

        val categoriesToDisplay = listOf(
            AlbumCategory.CATEGORY_RECENT,
            AlbumCategory.CATEGORY_TOP_40,
            AlbumCategory.CATEGORY_TOP_NEWLY,
            AlbumCategory.CATEGORY_TOP_6_MONTHS,
            AlbumCategory.CATEGORY_TOP_ALL
        )

        categoriesToDisplay.forEach { category ->
            AlbumSection(
                getSectionTitle(category),
                getSectionSubtitle(category),
                albums.getOfCategory(category),
                category
            ).takeIf { it.albums.isNotEmpty() }?.apply { sections.add(this) }
        }

        return sections
    }

    private fun getSectionTitle(category: String): String {
        return when (category) {
            AlbumCategory.CATEGORY_RECENT -> getString(R.string.top_recent_soundtracks)
            AlbumCategory.CATEGORY_TOP_40 -> getString(R.string.top_40_soundtracks)
            AlbumCategory.CATEGORY_TOP_6_MONTHS -> getString(R.string.top_months_soundtracks)
            AlbumCategory.CATEGORY_TOP_NEWLY -> getString(R.string.top_newly_soundtracks)
            AlbumCategory.CATEGORY_TOP_ALL -> getString(R.string.top_all_soundtracks)
            else -> ""
        }
    }

    private fun getSectionSubtitle(category: String): String {
        return when (category) {
            AlbumCategory.CATEGORY_RECENT -> getString(R.string.top_recent_soundtracks_subtitle)
            AlbumCategory.CATEGORY_TOP_40 -> getString(R.string.top_40_soundtracks_subtitle)
            AlbumCategory.CATEGORY_TOP_6_MONTHS -> getString(R.string.top_months_soundtracks_subtitle)
            AlbumCategory.CATEGORY_TOP_NEWLY -> getString(R.string.top_newly_soundtracks_subtitle)
            AlbumCategory.CATEGORY_TOP_ALL -> getString(R.string.top_all_soundtracks_subtitle)
            else -> ""
        }
    }

    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            loadAndBind()
        }
    }

}