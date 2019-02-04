package io.github.artenes.ostatic.view

import android.Manifest
import android.app.DownloadManager
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.Player
import com.squareup.picasso.Picasso
import io.github.artenes.ostatic.OstaticApplication
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.loadAlbumCover
import io.github.artenes.ostatic.service.DownloadSongTask
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
    private val REQUEST_PERMISSION = 123

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
        download.setOnClickListener(this)
        seekBar.isEnabled = false
    }

    fun bind(state: MusicPlayerState) {
        val song = state.currentSong()

        songTitle.text = song.name
        albumTitle.text = song.albumName
        songEndTime.text = song.time.padStart(5, '0')
        playPause.setImageResource(if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)

        if (state.isBuffering) {
            bufferingSong.visibility = View.VISIBLE
            albumCover.alpha = 0.5f
        } else {
            bufferingSong.visibility = View.GONE
            albumCover.alpha = 1f
        }

        //load only when the album changes
        if (albumCover.tag != song.albumName) {
            Picasso.get().loadAlbumCover(song.albumCover, albumCover)
            albumCover.tag = song.albumName
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

    private fun downloadSong() {
        val song = service.getSession()?.getCurrentState()?.currentSong()
        if (song != null) {
            DownloadSongTask(getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager, resources).execute(song)
        }
    }

    private fun checkPermissionAndDownloadSong() {

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            downloadSong()
            return
        }

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION && grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            downloadSong()
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
            R.id.download -> checkPermissionAndDownloadSong()
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