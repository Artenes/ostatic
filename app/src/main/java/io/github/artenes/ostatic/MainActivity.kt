package io.github.artenes.ostatic

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import io.github.artenes.ostatic.service.MusicPlayerService
import io.github.artenes.ostatic.service.MusicPlayerState
import io.github.artenes.ostatic.service.MusicSession
import io.github.artenes.ostatic.view.AlbumFragment
import io.github.artenes.ostatic.view.AlbumsFragment
import io.github.artenes.ostatic.view.PlayerActivity
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), ServiceConnection, View.OnClickListener,
    MusicPlayerService.OnSessionChangedListener {

    var service: MusicPlayerService? = null
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        MusicPlayerService.bind(this, this)
        songAndAlbumTitle.isSelected = true
        playPauseButton.setOnClickListener(this)
        songAndAlbumTitle.setOnClickListener(this)

        navController = findNavController(R.id.content)
        bottomNavigationView.setupWithNavController(navController)
    }

    fun openAllAlbumsFromHome(title: String, category: String) {
        val bundle = bundleOf(AlbumsFragment.TITLE to title, AlbumsFragment.CATEGORY to category)
        navController.navigate(R.id.action_homeFragment_to_albumsFragment, bundle)
    }

    fun openAlbumFromHome(id: String) {
        openAlbum(id, R.id.action_homeFragment_to_albumFragment)
    }

    fun openAlbumFromList(id: String) {
        openAlbum(id, R.id.action_albumsFragment_to_albumFragment)
    }

    fun openAlbumFromSearch(id: String) {
        openAlbum(id, R.id.action_searchFragment_to_albumFragment)
    }

    fun openAlbumFromLibrary(id: String) {
        openAlbum(id, R.id.action_libraryFragment_to_albumFragment)
    }

    private fun openAlbum(id: String, action: Int) {
        val bundle = bundleOf(AlbumFragment.ALBUM_ID to id)
        navController.navigate(action, bundle)
    }

    fun showPlayerLoading() {
        loadingSpinner.visibility = View.VISIBLE
        playPauseButton.visibility = View.GONE
    }

    fun playPauseMusic() {
        service?.getSession()?.playOrPause()
    }

    fun openPlayer() {
        startActivity(Intent(this, PlayerActivity::class.java))
    }

    fun togglePlayerStatus(playing: Boolean) {
        loadingSpinner.visibility = View.GONE
        playPauseButton.visibility = View.VISIBLE
        playPauseButton.setImageResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play)
    }

    fun updatePlayerSongAlbumTitle(song: String, album: String) {
        val title = getString(R.string.song_album_name, song, album)
        if (title != songAndAlbumTitle.text.toString()) {
            songAndAlbumTitle.text = title
        }
    }

    fun hidePlayer() {
        player.visibility = View.GONE
        divider.visibility = View.GONE
    }

    fun showPlayer() {
        player.visibility = View.VISIBLE
        divider.visibility = View.VISIBLE
    }

    fun bindToCurrentSession(session: MusicSession?) {
        if (session == null) {
            hidePlayer()
        } else {
            session.addListener(musicStateObserver)
            showPlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)
        this.service?.mSessionListener = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this.service = (service as MusicPlayerService.MusicPlayerBinder).service
        this.service?.mSessionListener = this
        bindToCurrentSession(this.service?.getSession())
    }

    private val musicStateObserver = Observer<MusicPlayerState> {

        if (it.hasFinished) {
            hidePlayer()
            return@Observer
        }

        val song = it.playlist[it.currentIndex]

        if (it.isBuffering) {
            showPlayerLoading()
        } else {
            togglePlayerStatus(it.isPlaying)
        }

        updatePlayerSongAlbumTitle(song.name, song.albumName)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.playPauseButton -> playPauseMusic()
            R.id.songAndAlbumTitle -> openPlayer()
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        //do nothing
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onSessionChanged(session: MusicSession) {
        bindToCurrentSession(session)
    }

    override fun onNewIntent(intent: Intent?) {
        val isToOpenPlayer = intent?.data?.toString()?.equals("https://ostatic.artenes.github.io/player") ?: false
        if (isToOpenPlayer) {
            openPlayer()
        }
    }

}