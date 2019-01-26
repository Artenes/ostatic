package io.github.artenes.ostatic.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import androidx.annotation.Nullable
import androidx.lifecycle.Observer
import io.github.artenes.ostatic.db.SongView

class MusicPlayerService : Service() {

    companion object {

        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"

        fun bind(context: Context, listener: ServiceConnection) {
            context.startService(Intent(context, MusicPlayerService::class.java))

            val serviceIntent = Intent(context, MusicPlayerService::class.java)
            context.bindService(serviceIntent, listener, Context.BIND_AUTO_CREATE)
        }

    }

    private val mBinder = MusicPlayerBinder()
    private lateinit var mPlayer: MusicPlayer
    private lateinit var mNotification: PlayerNotification
    private var mSession: MusicSession? = null

    override fun onCreate() {
        super.onCreate()
        mPlayer = MusicPlayer(this, "Ostatic/0.0.1")
        mNotification = PlayerNotification(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_PREVIOUS -> {
                mSession?.previous()
            }
            ACTION_PLAY_PAUSE -> {
                mSession?.playOrPause()
            }
            ACTION_NEXT -> {
                mSession?.next()
            }
            else -> {}
        }
        return START_NOT_STICKY
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    fun getSession(id: String): MusicSession? {
        return if (mSession?.id == id) mSession else null
    }

    fun createSession(id: String, songs: List<SongView>, currentIndex: Int): MusicSession {
        mSession?.playOrPause()
        mSession?.clearListeners()
        mSession = MusicSession(songs, currentIndex, mPlayer, id)
        mNotification.attachSession(mSession as MusicSession)
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
