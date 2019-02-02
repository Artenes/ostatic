package io.github.artenes.ostatic.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.artenes.ostatic.MainActivity
import io.github.artenes.ostatic.OstaticApplication
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.TopAlbumView

import kotlinx.android.synthetic.main.preload_list.view.*
import kotlinx.coroutines.*

class HomeFragment : Fragment(), AlbumListAdapter.OnAlbumClickListener {

    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)

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
        loadAndBind()
    }

    fun loadAndBind() = scope.launch {
        val view: View = this@HomeFragment.view as View

        view.content.visibility = View.GONE
        view.progress.visibility = View.VISIBLE

        val sections = makeListOfSections()

        adapter.setData(sections)

        view.content.visibility = View.VISIBLE
        view.progress.visibility = View.GONE
    }

    override fun onAlbumClick(album: TopAlbumView) {
        //work around to use what exists already
        if (album.size == TopAlbumView.NEXT_PAGE_ID) {
            (requireActivity() as MainActivity).openAllAlbumsFromHome(album.added, album.id)
        } else {
            (requireActivity() as MainActivity).openAlbumFromHome(album.id)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun makeListOfSections(): List<AlbumSection> = withContext(Dispatchers.IO) {

        val sections = mutableListOf<AlbumSection>()
        val limit = 7

        val topRecent = repo.getRecentAlbums(limit).toMutableList()
        if (topRecent.isNotEmpty()) {
            if (topRecent.size == limit) {
                topRecent.add(TopAlbumView.makeNextPageAlbum(AlbumsFragment.TOP_RECENT, getString(R.string.all_albums), getString(R.string.top_recent_soundtracks)))
            }
            sections.add(
                AlbumSection(
                    getString(R.string.top_recent_soundtracks),
                    getString(R.string.top_recent_soundtracks_subtitle),
                    topRecent,
                    false
                )
            )
        }

        val top40 = repo.getTop40(limit).toMutableList()
        top40.add(TopAlbumView.makeNextPageAlbum(AlbumsFragment.TOP_40, getString(R.string.all_albums), getString(R.string.top_40_soundtracks)))
        sections.add(
            AlbumSection(
                getString(R.string.top_40_soundtracks),
                getString(R.string.top_40_soundtracks_subtitle),
                top40,
                false
            )
        )

        val newly = repo.getTop100NewlyAdded(limit).toMutableList()
        newly.add(TopAlbumView.makeNextPageAlbum(AlbumsFragment.TOP_NEWLY, getString(R.string.all_albums), getString(R.string.top_newly_soundtracks)))
        sections.add(
            AlbumSection(
                getString(R.string.top_newly_soundtracks),
                getString(R.string.top_newly_soundtracks_subtitle),
                newly,
                false
            )
        )

        val months = repo.getTop100Last6Months(limit).toMutableList()
        months.add(TopAlbumView.makeNextPageAlbum(AlbumsFragment.TOP_LAST, getString(R.string.all_albums), getString(R.string.top_months_soundtracks)))
        sections.add(
            AlbumSection(
                getString(R.string.top_months_soundtracks),
                getString(R.string.top_newly_soundtracks_subtitle),
                months,
                false
            )
        )

        val year = repo.getTop100AllTime(limit).toMutableList()
        year.add(TopAlbumView.makeNextPageAlbum(AlbumsFragment.TOP_ALL, getString(R.string.all_albums), getString(R.string.top_all_soundtracks)))
        sections.add(
            AlbumSection(
                getString(R.string.top_all_soundtracks),
                getString(R.string.top_newly_soundtracks_subtitle),
                year,
                false
            )
        )

        sections

    }

}