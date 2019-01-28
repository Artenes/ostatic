package io.github.artenes.ostatic

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.artenes.ostatic.service.MusicPlayerService
import io.github.artenes.ostatic.service.MusicPlayerState
import io.github.artenes.ostatic.service.MusicSession
import io.github.artenes.ostatic.view.HomeFragment
import io.github.artenes.ostatic.view.AboutFragment
import io.github.artenes.ostatic.view.AlbumActivity
import io.github.artenes.ostatic.view.SearchFragment
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, ServiceConnection, View.OnClickListener {

    var service: MusicPlayerService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        changeToHomeTab()
        MusicPlayerService.bind(this, this)
        songAndAlbumTitle.isSelected = true
        playPauseButton.setOnClickListener(this)
        songAndAlbumTitle.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        bindToCurrentSession(service?.getSession())
    }

    fun changeToHomeTab() {
        replaceFragment(HomeFragment())
    }

    fun changeToSearchTab() {
        replaceFragment(SearchFragment())
    }

    fun changeToAboutTab() {
        replaceFragment(AboutFragment())
    }

    fun showPlayerLoading() {
        progressBar.visibility = View.VISIBLE
        playPauseButton.visibility = View.GONE
    }

    fun playPauseMusic() {
        service?.getSession()?.playOrPause()
    }

    fun openAlbum() {
        val state = service?.getSession()?.getCurrentState()
        if (state != null) {
            val song = state.playlist[state.currentIndex]
            AlbumActivity.start(this, song.albumId)
        }
    }

    fun togglePlayerStatus(playing: Boolean) {
        progressBar.visibility = View.GONE
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
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> changeToHomeTab()
            R.id.action_search -> changeToSearchTab()
            R.id.action_about -> changeToAboutTab()
        }
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this.service = (service as MusicPlayerService.MusicPlayerBinder).service
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
        when(v?.id) {
            R.id.playPauseButton -> playPauseMusic()
            R.id.songAndAlbumTitle -> openAlbum()
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        //do nothing
    }

}