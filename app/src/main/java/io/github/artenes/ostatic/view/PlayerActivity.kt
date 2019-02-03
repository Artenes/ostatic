package io.github.artenes.ostatic.view

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.Player
import com.squareup.picasso.Picasso
import io.github.artenes.ostatic.OstaticApplication
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.loadAlbumCover
import io.github.artenes.ostatic.service.MusicPlayerService
import io.github.artenes.ostatic.service.MusicPlayerState
import io.github.artenes.ostatic.service.MusicSession
import kotlinx.android.synthetic.main.player_activity.*
import kotlinx.coroutines.*

class PlayerActivity : AppCompatActivity(), ServiceConnection, View.OnClickListener {

    lateinit var service: MusicPlayerService

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val repo = OstaticApplication.REPOSITORY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity)
        MusicPlayerService.bind(this, this)
        playPause.setOnClickListener(this)
        next.setOnClickListener(this)
        previous.setOnClickListener(this)
        repeat.setOnClickListener(this)
        shuffle.setOnClickListener(this)
        favorite.setOnClickListener(this)
    }

    fun bind(state: MusicPlayerState) {
        val song = state.currentSong()

        songTitle.text = song.name
        albumTitle.text = song.albumName
        playPause.setImageResource(if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)

        if (state.isBuffering) {
            bufferingSong.visibility = View.VISIBLE
            albumCover.alpha = 0.5f
        } else {
            bufferingSong.visibility = View.GONE
            albumCover.alpha = 1f
        }

        //avoid loading multiple times
        if (albumCover.tag != true) {
            Picasso.get().loadAlbumCover(song.albumCover, albumCover)
        }

        if (state.isRandomMode) {
            shuffle.alpha = 1f
        } else {
            shuffle.alpha = 0.5f
        }

        when (state.repeatMode) {
            Player.REPEAT_MODE_ALL -> {
                repeat.setImageResource(R.drawable.ic_repeat)
                repeat.alpha = 1f
            }
            Player.REPEAT_MODE_ONE -> {
                repeat.setImageResource(R.drawable.ic_repeat_one)
                repeat.alpha = 1f
            }
            else -> {
                repeat.setImageResource(R.drawable.ic_repeat);
                repeat.alpha = 0.5f
            }
        }

        bindFavorite(song.id)
    }

    fun bindFavorite(id: String) = scope.launch {
        val isFavorite = withContext(Dispatchers.IO) {
            repo.getFavorite(id) != null
        }
        favorite.setImageResource(if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_not)
        favorite.tag = isFavorite
    }

    fun toggleFavorite() = scope.launch {
        val id = service.getSession()?.getCurrentState()?.currentSong()?.id ?: return@launch
        withContext(Dispatchers.IO) {
            repo.toggleFavorite(id, "song")
        }
        val isFavorite = favorite.tag == true
        if (isFavorite) {
            favorite.setImageResource(R.drawable.ic_favorite_not)
            favorite.tag = false
        } else {
            favorite.setImageResource(R.drawable.ic_favorite)
            favorite.tag = true
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.playPause -> service.getSession()?.playOrPause()
            R.id.next -> service.getSession()?.next()
            R.id.previous -> service.getSession()?.previous()
            R.id.repeat -> service.getSession()?.toggleRepeatMode()
            R.id.shuffle -> service.getSession()?.toggleRandomMode()
            R.id.favorite -> toggleFavorite()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this.service = (service as MusicPlayerService.MusicPlayerBinder).service
        val session = this.service.getSession() as MusicSession
        session.addListener(playerListener)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service.getSession()?.removeListener(playerListener)
    }

    val playerListener = Observer<MusicPlayerState> {
        bind(it)
    }

}