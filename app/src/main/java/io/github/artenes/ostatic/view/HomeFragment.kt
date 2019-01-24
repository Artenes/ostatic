package io.github.artenes.ostatic.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.ApplicationDatabase
import io.github.artenes.ostatic.db.TopAlbumView

import kotlinx.android.synthetic.main.preload_list.view.*
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)

    lateinit var adapter: TopAlbumsAdapter
    lateinit var db: ApplicationDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val context: Context = container?.context!!

        val view = inflater.inflate(R.layout.preload_list, container, false)

        adapter = TopAlbumsAdapter()
        view.mainList.layoutManager = LinearLayoutManager(context)
        view.mainList.adapter = adapter

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        db = ApplicationDatabase.getInstance(requireContext())
        loadAndBind()
    }

    fun loadAndBind() = scope.launch {
        val view: View = this@HomeFragment.view as View

        view.content.visibility = View.GONE
        view.progress.visibility = View.VISIBLE

        val sections = makeListOfSections()

        adapter.setData(sections)

        //delay by 1 second to give time to the view to render fully
        delay(1000)

        view.content.visibility = View.VISIBLE
        view.progress.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun makeListOfSections(): List<AlbumSection> = withContext(Dispatchers.IO) {

        val sections = mutableListOf<AlbumSection>()
        val limit = 7

        val top40 = db.albumDao().getTop40(limit).toMutableList()
        top40.add(TopAlbumView.makeNextPageAlbum(getString(R.string.all_albums)))
        sections.add(
            AlbumSection(
                getString(R.string.top_40_soundtracks),
                getString(R.string.top_40_soundtracks_subtitle),
                top40,
                true
            )
        )

        val newly = db.albumDao().getTop100NewlyAdded(limit).toMutableList()
        newly.add(TopAlbumView.makeNextPageAlbum(getString(R.string.all_albums)))
        sections.add(
            AlbumSection(
                getString(R.string.top_newly_soundtracks),
                getString(R.string.top_newly_soundtracks_subtitle),
                newly,
                false
            )
        )

        val months = db.albumDao().getTop100Last6Months(limit).toMutableList()
        months.add(TopAlbumView.makeNextPageAlbum(getString(R.string.all_albums)))
        sections.add(
            AlbumSection(
                getString(R.string.top_months_soundtracks),
                getString(R.string.top_newly_soundtracks_subtitle),
                months,
                false
            )
        )

        val year = db.albumDao().getTop100AllTime(limit).toMutableList()
        year.add(TopAlbumView.makeNextPageAlbum(getString(R.string.all_albums)))
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