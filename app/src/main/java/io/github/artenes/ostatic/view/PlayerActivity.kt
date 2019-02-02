package io.github.artenes.ostatic.view

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.loadAlbumCover
import io.github.artenes.ostatic.service.MusicPlayerService
import io.github.artenes.ostatic.service.MusicPlayerState
import io.github.artenes.ostatic.service.MusicSession
import kotlinx.android.synthetic.main.player_activity.*

class PlayerActivity : AppCompatActivity(), ServiceConnection, View.OnClickListener {

    lateinit var service: MusicPlayerService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity)
        MusicPlayerService.bind(this, this)
        playPause.setOnClickListener(this)
        next.setOnClickListener(this)
        previous.setOnClickListener(this)
    }

    fun bind(state: MusicPlayerState) {
        val song = state.currentSong()
        songTitle.text = song.name
        albumTitle.text = song.albumName
        bufferingSong.visibility = if (state.isBuffering) View.VISIBLE else View.GONE
        playPause.setImageResource(if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
        //avoid loading multiple times
        if (albumCover.tag != true) {
            Picasso.get().loadAlbumCover(song.albumCover, albumCover)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.playPause -> service.getSession()?.playOrPause()
            R.id.next -> service.getSession()?.next()
            R.id.previous -> service.getSession()?.previous()
        }
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