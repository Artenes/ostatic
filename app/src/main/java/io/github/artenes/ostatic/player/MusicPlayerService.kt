package io.github.artenes.ostatic.player

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import io.github.artenes.ostatic.OstaticApplication
import kotlinx.coroutines.*

class MusicPlayerService : MediaBrowserServiceCompat() {

    companion object {

        private const val TAG = "MusicPlayerService"

    }

    private lateinit var musicBrowser: MusicBrowser
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        musicBrowser = MusicBrowser(OstaticApplication.REPOSITORY)

        // Create a MediaSessionCompat
        mediaSession = MediaSessionCompat(baseContext, TAG).apply {

            // Enable callbacks from MediaButtons and TransportControls
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())

            // MySessionCallback() has methods that handle callbacks from a media controller
            setCallback(SessionCallback())

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
        }

    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return musicBrowser.getRoot()
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.detach()
        onLoadChildrenSuspend(parentId, result)
    }

    private fun onLoadChildrenSuspend(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) =
        scope.launch {
            val mediaItems = withContext(Dispatchers.IO) {
                musicBrowser.getChild(parentId)
            }
            result.sendResult(mediaItems)
        }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private class SessionCallback : MediaSessionCompat.Callback() {


    }

}