package io.github.artenes.ostatic.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import io.github.artenes.ostatic.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import io.github.artenes.ostatic.db.ApplicationDatabase
import kotlinx.android.synthetic.main.album_view.view.*
import kotlinx.coroutines.*

class AlbumFragment : Fragment() {

    companion object {

        fun make(id: String): AlbumFragment {
            val fragment = AlbumFragment()
            val bundle = Bundle()
            bundle.putString(AlbumActivity.ALBUM_ID, id)
            fragment.arguments = bundle
            return fragment
        }

    }

    val id: String by lazy {
        arguments?.getString(AlbumActivity.ALBUM_ID) ?: ""
    }

    lateinit var db: ApplicationDatabase

    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)
    val adapter = SongsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.album_view, container, false)
        view.songsList.adapter = adapter
        view.songsList.layoutManager = LinearLayoutManager(view.context)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = this.activity as AppCompatActivity

        activity.setSupportActionBar(view?.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeButtonEnabled(true)
        activity.supportActionBar?.title = ""

        db = ApplicationDatabase.getInstance(requireContext())
        loadAndBind()
    }

    fun loadAndBind() = scope.launch {

        val view = this@AlbumFragment.view as View

        view.songsList.visibility = View.GONE
        view.playButton.visibility = View.GONE
        view.progressBar.visibility = View.VISIBLE

        val album = withContext(Dispatchers.IO) {
            db.albumDao().getAlbum(id)
        }
        val songs = withContext(Dispatchers.IO) {
            db.albumDao().getSongs(id)
        }

        view.albumTitle.text = album.name
        view.albumSongs.text = getString(R.string.number_songs, album.files)
        adapter.setData(songs)

        if (!album.cover.isNullOrEmpty()) {
            Picasso.get()
                .load(album.cover)
                .into(view.albumCover)
        } else {
            view.albumCover.setImageDrawable(ColorDrawable(Color.WHITE))
        }

        view.songsList.visibility = View.VISIBLE
        view.playButton.visibility = View.VISIBLE
        view.progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}