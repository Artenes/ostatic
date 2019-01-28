package io.github.artenes.ostatic.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import io.github.artenes.ostatic.R

class PlayerNotification(val context: Context) {

    companion object {

        const val NOTIFICATION_SESSION_ID = 7592
        const val NOTIFICATION_ID = "PLAYER_NOTIFICATION"

    }

    init {
        createChannel()
    }

    private val manager = NotificationManagerCompat.from(context)

    val layout = RemoteViews(context.packageName, R.layout.notification_layout)

    private val builder = NotificationCompat.Builder(context, NOTIFICATION_ID).apply {

        priority = NotificationCompat.PRIORITY_MAX

        layout.setOnClickPendingIntent(R.id.previousButton, createPendingIntent(MusicPlayerService.ACTION_PREVIOUS))
        layout.setOnClickPendingIntent(R.id.playPauseButton, createPendingIntent(MusicPlayerService.ACTION_PLAY_PAUSE))
        layout.setOnClickPendingIntent(R.id.nextButton, createPendingIntent(MusicPlayerService.ACTION_NEXT))

        setSmallIcon(R.drawable.ic_play)
        setStyle(androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
        setCustomContentView(layout)
        setDeleteIntent(createPendingIntent(MusicPlayerService.ACTION_EXIT))
    }

    fun attachSession(session: MusicSession) {
        session.addListener(notificationObserver)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val res = context.resources
            Notification.createChannel(
                context,
                NOTIFICATION_ID,
                res.getString(R.string.notification_player_channel_name),
                res.getString(R.string.notification_player_channel_description),
                NotificationManager.IMPORTANCE_LOW
            )
        }
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(action)
        intent.component = ComponentName(context, MusicPlayerService::class.java)
        return PendingIntent.getService(context, 0, intent, 0)
    }

    private fun refreshState(musicTitle: String, albumName: String, isPlaying: Boolean, isBuffering: Boolean) {
        layout.setTextViewText(R.id.songTitle, musicTitle)
        layout.setTextViewText(R.id.songAlbum, albumName)
        if (isBuffering) {
            layout.setViewVisibility(R.id.buttons, View.GONE)
            layout.setViewVisibility(R.id.progressBarContainer, View.VISIBLE)
            builder.setOngoing(true)
        } else {
            layout.setViewVisibility(R.id.progressBarContainer, View.GONE)
            layout.setViewVisibility(R.id.buttons, View.VISIBLE)
            layout.setImageViewResource(R.id.playPauseButton, if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
            builder.setOngoing(isPlaying)
        }
    }

    private val notificationObserver = Observer<MusicPlayerState> {
        if (it.hasFinished) {
            return@Observer
        }
        val song = it.playlist[it.currentIndex]
        refreshState(song.name, song.albumName, it.isPlaying, it.isBuffering)
        manager.notify(NOTIFICATION_SESSION_ID, builder.build())
    }

}