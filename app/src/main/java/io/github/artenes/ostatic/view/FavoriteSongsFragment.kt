package io.github.artenes.ostatic.view

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.artenes.ostatic.OstaticApplication
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.SongView
import io.github.artenes.ostatic.service.MusicPlayerService
import io.github.artenes.ostatic.service.MusicPlayerState
import io.github.artenes.ostatic.service.MusicSession
import kotlinx.android.synthetic.main.favorite_list.view.*
import kotlinx.coroutines.*

class FavoriteSongsFragment : Fragment(), ServiceConnection, FavoriteSongsAdapter.OnSongClickListener {

    companion object {

        const val FAVORITE_SONGS_SESSION = "FAVORITE_SONGS"

    }

    val repo = OstaticApplication.REPOSITORY

    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)

    val adapter = FavoriteSongsAdapter(this)

    var service: MusicPlayerService? = null
    var musicSession: MusicSession? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.favorite_list, container, false)
        view.mainList.adapter = adapter
        view.mainList.layoutManager = LinearLayoutManager(view.context)
        view.mainList.itemAnimator = null
        view.noFavorites.text = getString(R.string.no_favorite_songs_yet)
        view.noFavorites.setCompoundDrawables(null, resources.getDrawable(R.drawable.ic_music_note, null), null, null)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MusicPlayerService.bind(requireContext(), this@FavoriteSongsFragment)
    }

    fun loadAndBind() = scope.launch {
        val view = this@FavoriteSongsFragment.view as View

        view.mainList.visibility = View.GONE
        view.progress.visibility = View.VISIBLE
        view.noFavorites.visibility = View.GONE

        val songs = withContext(Dispatchers.IO) {
            repo.getFavoriteSongs()
        }

        adapter.setData(songs)

        view.mainList.visibility = View.VISIBLE
        view.progress.visibility = View.GONE

        if (songs.isEmpty()) {
            view.noFavorites.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        //if the session changed while we were out, reset the state of the list
        if (service?.getSession(FAVORITE_SONGS_SESSION) == null) {
            loadAndBind()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        requireActivity().unbindService(this)
    }

    private fun bindToCurrentSession() {
        musicSession = service?.getSession(FAVORITE_SONGS_SESSION)
        if (musicSession != null) {
            musicSession?.addListener(musicStateObserver)
        }
    }

    private fun createNewSession(position: Int) {
        musicSession = service?.getSession(FAVORITE_SONGS_SESSION)
        if (musicSession == null) {
            val session = service?.createSession(FAVORITE_SONGS_SESSION, adapter.songs, position)
            session?.addListener(musicStateObserver)
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

    val musicStateObserver = Observer<MusicPlayerState> {
        when {
            it.isBuffering -> {
                adapter.buffer(it.currentIndex)
            }
            it.isPlaying -> {
                adapter.play(it.currentIndex)
            }
            else -> {
                adapter.pause(it.currentIndex)
            }
        }
    }

}