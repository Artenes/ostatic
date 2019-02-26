package io.github.artenes.ostatic.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.artenes.ostatic.MainActivity
import io.github.artenes.ostatic.OstaticApplication
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.TopAlbumView
import io.github.artenes.ostatic.model.AlbumCategory
import kotlinx.android.synthetic.main.album_view.view.*
import kotlinx.android.synthetic.main.preload_list.view.*
import kotlinx.coroutines.*

class AlbumsFragment : Fragment(), AlbumsAdapter.OnAlbumClickListener {

    companion object {

        const val CATEGORY = "CATEGORY"

    }

    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)

    val category by lazy {
        val args = arguments as Bundle
        args.getString(CATEGORY)
    }

    val title by lazy {
        when (category) {
            AlbumCategory.CATEGORY_RECENT -> getString(R.string.top_recent_soundtracks)
            AlbumCategory.CATEGORY_TOP_40 -> getString(R.string.top_40_soundtracks)
            AlbumCategory.CATEGORY_TOP_6_MONTHS -> getString(R.string.top_months_soundtracks)
            AlbumCategory.CATEGORY_TOP_ALL -> getString(R.string.top_all_soundtracks)
            AlbumCategory.CATEGORY_TOP_NEWLY -> getString(R.string.top_newly_soundtracks)
            else -> ""
        }
    }

    val repo = OstaticApplication.REPOSITORY
    lateinit var adapter: AlbumsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context: Context = container?.context!!

        val view = inflater.inflate(R.layout.list, container, false)

        adapter = AlbumsAdapter(this)
        view.mainList.layoutManager = LinearLayoutManager(context)
        view.mainList.adapter = adapter

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = this.activity as AppCompatActivity

        activity.setSupportActionBar(view?.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeButtonEnabled(true)
        activity.supportActionBar?.title = title

        loadAndBind()
    }

    fun loadAndBind() = scope.launch {
        val view: View = this@AlbumsFragment.view as View

        view.mainList.visibility = View.GONE
        view.progress.visibility = View.VISIBLE

        val albums = withContext(Dispatchers.IO) {
            when (category) {
                AlbumCategory.CATEGORY_TOP_40 -> repo.getTop40()
                AlbumCategory.CATEGORY_TOP_ALL -> repo.getTop100AllTime()
                AlbumCategory.CATEGORY_TOP_6_MONTHS -> repo.getTop100Last6Months()
                AlbumCategory.CATEGORY_TOP_NEWLY -> repo.getTop100NewlyAdded()
                AlbumCategory.CATEGORY_RECENT -> repo.getRecentAlbums()
                AlbumCategory.CATEGORY_FAVORITES -> repo.getFavoriteAlbums()
                else -> emptyList()
            }
        }

        adapter.setData(albums)

        view.mainList.visibility = View.VISIBLE
        view.progress.visibility = View.GONE
    }

    override fun onAlbumClicked(album: TopAlbumView) {
        (requireActivity() as MainActivity).openAlbumFromList(album.id)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}