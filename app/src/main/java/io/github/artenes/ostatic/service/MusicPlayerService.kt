package io.github.artenes.ostatic.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.annotation.Nullable
import androidx.lifecycle.Observer
import io.github.artenes.ostatic.MainActivity
import io.github.artenes.ostatic.db.SongView

class MusicPlayerService : Service() {

    interface OnSessionChangedListener {

        fun onSessionChanged(session: MusicSession)

    }

    companion object {

        const val TAG = "MusicPlayerService"
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val ACTION_EXIT = "ACTION_EXIT"
        const val ACTION_OPEN_ALBUM = "ACTION_OPEN_ALBUM"

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
    private lateinit var mWakeLock: WakeLock
    var mSessionListener: OnSessionChangedListener? = null

    override fun onCreate() {
        mPlayer = MusicPlayer(this, "Ostatic/0.0.1")
        mNotification = PlayerNotification(this)
        mWakeLock = WakeLock(this)
        Log.i(TAG, "Created music player service because some component needed to play some music")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PREVIOUS -> {
                mSession?.previous()
            }
            ACTION_PLAY_PAUSE -> {
                mSession?.playOrPause()
            }
            ACTION_NEXT -> {
                mSession?.next()
            }
            ACTION_OPEN_ALBUM -> {
                val state = mSession?.getCurrentState()
                if (state != null) {
                    val albumIntent = Intent(this, MainActivity::class.java)
                    albumIntent.data = Uri.parse("https://ostatic.artenes.github.io/player")
                    startActivity(albumIntent)
                }
            }
            ACTION_EXIT -> {
                mSession?.alertFinished()
                mSession?.clearListeners()
                mSession?.pause()
                mWakeLock.releaseBecause("notification is being swiped away")
                mSession = null
                stopSelf()
            }
            else -> {
            }
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

    fun getSession(): MusicSession? {
        return mSession
    }

    fun createSession(id: String, songs: List<SongView>, currentIndex: Int): MusicSession {
        mSession?.playOrPause()
        mSession?.clearListeners()
        mSession = MusicSession(songs, currentIndex, mPlayer, id)
        mNotification.attachSession(mSession as MusicSession)
        mSession?.addListener(sessionListener)
        mWakeLock.acquireBecause("a new session is being created")
        mSessionListener?.onSessionChanged(mSession as MusicSession)
        return mSession as MusicSession
    }

    override fun onDestroy() {
        Log.i(
            TAG,
            "The music player service is being destroyed because it is not in use or the system does not like it"
        )
        mSession?.clearListeners()
        mSession?.pause()
        mPlayer.release()
    }

    inner class MusicPlayerBinder : Binder() {

        val service: MusicPlayerService
            get() = this@MusicPlayerService

    }

    private val sessionListener = Observer<MusicPlayerState> {
        if (!it.isBuffering && !it.isPlaying) {
            mWakeLock.releaseBecause("music has paused")
        }

        if (!it.isBuffering && it.isPlaying) {
            mWakeLock.acquireBecause("music is playing")
        }
    }

}

class WakeLock(context: Context) {

    companion object {

        const val WIFI_LOCK_NAME = "io.github.artenes.ostatic.BATTERY_WIFI_LOCK"
        const val TAG = "WakeLock"

    }

    private val wifiLock =
        (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).createWifiLock(
            WifiManager.WIFI_MODE_FULL,
            WIFI_LOCK_NAME
        )

    private val batteryLock = (context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager)
        .newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
            MusicPlayerService::class.java.name
        )

    fun acquireBecause(reason: String) {
        if (!batteryLock.isHeld) {
            batteryLock.acquire(10 * 60 * 1000L /*10 minutes*/)
            Log.d(TAG, "Acquired battery lock because $reason ($batteryLock)")
        }
        if (!wifiLock.isHeld) {
            wifiLock.acquire()
            Log.d(TAG, "Acquired wifi lock because $reason ($wifiLock)")
        }
    }

    fun releaseBecause(reason: String) {
        if (batteryLock.isHeld) {
            batteryLock.release()
            Log.d(TAG, "Released battery lock because $reason ($batteryLock)")
        }
        if (wifiLock.isHeld) {
            wifiLock.release()
            Log.d(TAG, "Released wifi lock because $reason ($wifiLock)")
        }

    }

}