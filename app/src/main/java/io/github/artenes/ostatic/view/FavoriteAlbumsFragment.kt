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
import kotlinx.android.synthetic.main.favorite_list.view.*
import kotlinx.coroutines.*

class FavoriteAlbumsFragment : Fragment(), AlbumsAdapter.OnAlbumClickListener {

    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)

    val repo = OstaticApplication.REPOSITORY
    lateinit var adapter: AlbumsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context: Context = container?.context!!

        val view = inflater.inflate(R.layout.favorite_list, container, false)

        adapter = AlbumsAdapter(this)
        view.mainList.layoutManager = LinearLayoutManager(context)
        view.mainList.adapter = adapter

        view.noFavorites.text = getString(R.string.no_favorite_songs_yet)
        view.noFavorites.setCompoundDrawables(null, resources.getDrawable(R.drawable.ic_music_note, null), null, null)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadAndBind()
    }

    private fun loadAndBind() = scope.launch {
        val view: View = this@FavoriteAlbumsFragment.view as View

        view.mainList.visibility = View.GONE
        view.progress.visibility = View.VISIBLE
        view.noFavorites.visibility = View.GONE

        val albums = withContext(Dispatchers.IO) {
            repo.getFavoriteAlbums()
        }

        adapter.setData(albums)

        view.mainList.visibility = View.VISIBLE
        view.progress.visibility = View.GONE

        if (albums.isEmpty()) {
            view.noFavorites.visibility = View.VISIBLE
        }
    }

    override fun onAlbumClicked(album: TopAlbumView) {
        (requireActivity() as MainActivity).openAlbumFromLibrary(album.id)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}