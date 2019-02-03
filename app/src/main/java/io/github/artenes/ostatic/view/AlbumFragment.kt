package io.github.artenes.ostatic.view

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import io.github.artenes.ostatic.OstaticApplication
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.AlbumView
import io.github.artenes.ostatic.db.SongView
import io.github.artenes.ostatic.service.MusicPlayerService
import io.github.artenes.ostatic.service.MusicPlayerState
import io.github.artenes.ostatic.service.MusicSession
import kotlinx.android.synthetic.main.album_view.view.*
import kotlinx.android.synthetic.main.player_activity.*
import kotlinx.coroutines.*

class AlbumFragment : Fragment(), ServiceConnection, SongsAdapter.OnSongClickListener, View.OnClickListener {

    companion object {

        const val ALBUM_ID = "ALBUM_ID"

    }

    val id: String by lazy {
        arguments?.getString(ALBUM_ID) ?: ""
    }

    val repo = OstaticApplication.REPOSITORY

    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)

    val adapter = SongsAdapter(this)

    lateinit var service: MusicPlayerService
    var musicSession: MusicSession? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.album_view, container, false)
        view.songsList.adapter = adapter
        view.songsList.layoutManager = LinearLayoutManager(view.context)
        view.songsList.itemAnimator = null
        view.playButton.setOnClickListener(this)
        view.nextButton.setOnClickListener(this)
        view.previousButton.setOnClickListener(this)
        view.favorite.setOnClickListener(this)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = this.activity as AppCompatActivity

        activity.setSupportActionBar(view?.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeButtonEnabled(true)
        activity.supportActionBar?.title = ""

        loadAndBind()
    }

    fun loadAndBind() = scope.launch {

        val view = this@AlbumFragment.view as View

        view.songsList.visibility = View.GONE
        view.playButton.visibility = View.GONE
        view.progressBar.visibility = View.VISIBLE
        view.favorite.visibility = View.GONE

        val albumAndSongs = withContext(Dispatchers.IO) {
            repo.getAlbumAndSongs(id)
        }

        val isFavorite = withContext(Dispatchers.IO) {
            repo.getFavorite(id) != null
        }

        val album = albumAndSongs.first
        val songs = albumAndSongs.second

        view.albumTitle.text = album.name
        view.albumSongs.text = getString(R.string.number_songs, album.files)
        adapter.setData(songs)

        val albumUrl =
            if (album.cover.isNullOrEmpty()) "android.resource://io.github.artenes.ostatic/drawable/album" else album.cover
        Picasso.get()
            .load(albumUrl)
            .resize(280, 280)
            .centerCrop()
            .placeholder(R.drawable.album)
            .error(R.drawable.album)
            .into(view.albumCover)

        view.songsList.visibility = View.VISIBLE
        view.playButton.visibility = View.VISIBLE
        view.progressBar.visibility = View.GONE
        view.favorite.visibility = View.VISIBLE

        view.favorite.setImageResource(if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_not)
        view.favorite.tag = isFavorite

        MusicPlayerService.bind(requireContext(), this@AlbumFragment)
    }

    fun toggleFavorite() = scope.launch {
        withContext(Dispatchers.IO) {
            repo.toggleFavorite(id, "album")
        }
        val isFavorite = view?.favorite?.tag == true
        if (isFavorite) {
            view?.favorite?.setImageResource(R.drawable.ic_favorite_not)
            view?.favorite?.tag = false
        } else {
            view?.favorite?.setImageResource(R.drawable.ic_favorite)
            view?.favorite?.tag = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        requireActivity().unbindService(this)
    }

    private fun bindToCurrentSession() {
        musicSession = service.getSession(id)
        if (musicSession != null) {
            musicSession?.addListener(musicStateObserver)
        }
    }

    private fun createNewSession(position: Int) {
        musicSession = service.getSession(id)
        if (musicSession == null) {
            val session = service.createSession(id, adapter.songs, position)
            session.addListener(musicStateObserver)
            val state = session.getCurrentState() as MusicPlayerState
            scope.launch { repo.markAsRecent(state.currentAlbum()) }
            musicSession = session
        }
    }

    override fun onSongClick(position: Int, song: SongView) {
        createNewSession(position)
        musicSession?.playMusic(position)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this.service = (service as MusicPlayerService.MusicPlayerBinder).service
        bindToCurrentSession()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicSession?.removeListener(musicStateObserver)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.playButton -> {
                createNewSession(0)
                musicSession?.playOrPause()
            }
            R.id.previousButton -> {
                musicSession?.previous()
            }
            R.id.nextButton -> {
                musicSession?.next()
            }
            R.id.favorite -> {
                toggleFavorite()
            }
        }
    }

    val musicStateObserver = Observer<MusicPlayerState> {
        when {
            it.isBuffering -> {
                adapter.buffer(it.currentIndex)
                view?.playButton?.isEnabled = false
            }
            it.isPlaying -> {
                adapter.play(it.currentIndex)
                view?.playButton?.isEnabled = true
                view?.playButton?.text = getString(R.string.pause)
                view?.nextButton?.visibility = View.VISIBLE
                view?.previousButton?.visibility = View.VISIBLE
            }
            else -> {
                view?.playButton?.isEnabled = true
                view?.playButton?.text = getString(R.string.play)
                adapter.pause(it.currentIndex)
            }
        }
    }

}