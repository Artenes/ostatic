package io.github.artenes.ostatic.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.annotation.Nullable
import io.github.artenes.ostatic.db.SongView

class MusicPlayerService : Service() {

    private val mBinder = MusicPlayerBinder()
    private lateinit var mPlayer: MusicPlayer
    private var mSession: MusicSession? = null

    override fun onCreate() {
        super.onCreate()
        mPlayer = MusicPlayer(this, "Ostatic/0.0.1")
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    fun getSession(id: String): MusicSession? {
        return if (mSession?.id == id) mSession else null
    }

    fun createSession(id: String, songs: List<SongView>, currentIndex: Int): MusicSession {
        mSession?.clearListeners()
        mSession = MusicSession(songs, currentIndex, mPlayer, id)
        return mSession as MusicSession
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer.release()
    }

    inner class MusicPlayerBinder : Binder() {

        val service: MusicPlayerService
            get() = this@MusicPlayerService

    }

}
