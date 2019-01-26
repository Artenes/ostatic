package io.github.artenes.ostatic.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import io.github.artenes.ostatic.R
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
import io.github.artenes.ostatic.db.SongView
import io.github.artenes.ostatic.service.MusicPlayerService
import io.github.artenes.ostatic.service.MusicSession
import kotlinx.android.synthetic.main.album_view.view.*
import kotlinx.coroutines.*

class AlbumFragment : Fragment(), ServiceConnection, SongsAdapter.OnSongClickListener, View.OnClickListener {

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
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val serviceIntent = Intent(requireActivity(), MusicPlayerService::class.java)
        requireActivity().bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)

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

        val album = withContext(Dispatchers.IO) {
            repo.getAlbum(id)
        }
        val songs = withContext(Dispatchers.IO) {
            repo.getSongs(id)
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
        requireActivity().unbindService(this)
    }

    private fun startSession(position: Int) {
        musicSession = service.getSession(id)
        if (musicSession == null) {
            musicSession = service.createSession(id, adapter.songs, position)
            musicSession?.addListener(Observer {
                when {
                    it.isBuffering -> {
                        adapter.buffer(it.currentIndex)
                        view?.playButton?.isEnabled = false
                    }
                    it.isPlaying -> {
                        adapter.play(it.currentIndex)
                        view?.playButton?.isEnabled = true
                        view?.playButton?.text = getString(R.string.pause)
                    }
                    else -> {
                        view?.playButton?.isEnabled = true
                        view?.playButton?.text = getString(R.string.play)
                        adapter.pause(it.currentIndex)
                    }
                }
            })
        }
    }

    override fun onSongClick(position: Int, song: SongView) {
        startSession(position)
        musicSession?.playMusic(position)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this.service =(service as MusicPlayerService.MusicPlayerBinder).service
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        //do nothing
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.playButton) {
            startSession(0)
            musicSession?.playOrPause()
        }
    }

}