package io.github.artenes.ostatic.service

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import io.github.artenes.ostatic.OstaticApplication
import io.github.artenes.ostatic.db.SongView

data class MusicPlayerState(
    val isPlaying: Boolean,
    val isBuffering: Boolean,
    val playlist: List<SongView>,
    val currentIndex: Int,
    val hasFinished: Boolean = false
)

class MusicSession(playList: List<SongView>, currentIndex: Int, private val player: MusicPlayer, val id: String) :
    Player.EventListener {

    companion object {

        const val TAG = "MusicSession"

    }

    private val liveState: MutableLiveData<MusicPlayerState> = MutableLiveData()
    private var observers: MutableList<Observer<MusicPlayerState>> = mutableListOf()

    init {
        player.setListener(this)
        player.prepare(playList, currentIndex)
        liveState.value = MusicPlayerState(false, false, playList.toList(), currentIndex)
        player.pause()
    }

    fun getCurrentState(): MusicPlayerState? {
        return liveState.value
    }

    fun alertFinished() {
        liveState.value = liveState.value?.copy(hasFinished = true)
    }

    fun addListener(observer: Observer<MusicPlayerState>) {
        observers.add(observer)
        liveState.observeForever(observer)
    }

    fun removeListener(observer: Observer<MusicPlayerState>) {
        liveState.removeObserver(observer)
    }

    fun clearListeners() {
        for (observer in observers) {
            liveState.removeObserver(observer)
        }
    }

    fun playMusic(index: Int) {
        player.playOrPauseOnIndex(index)
    }

    fun playOrPause() {
        player.playOrPause()
    }

    fun pause() {
        player.pause()
    }

    fun next() {
        player.next()
    }

    fun previous() {
        player.previous()
    }

    fun seekTo(time: Long) {
        player.seekTo(time)
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        liveState.value = liveState.value?.copy(
            isBuffering = false,
            isPlaying = player.isPlaying(),
            currentIndex = player.currentIndex()
        )
        Log.d(TAG, "Changed to position: ${player.currentIndex()}")
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

        if (playbackState == Player.STATE_BUFFERING) {
            liveState.value =
                    liveState.value?.copy(isBuffering = true, isPlaying = false, currentIndex = player.currentIndex())
            Log.d(TAG, "Buffering for position: ${player.currentIndex()}")
            return
        }

        if (playbackState == Player.STATE_READY) {
            liveState.value = liveState.value?.copy(
                isBuffering = false,
                isPlaying = playWhenReady,
                currentIndex = player.currentIndex()
            )
            Log.d(TAG, "${if (playWhenReady) "playing" else "paused"} for position: ${player.currentIndex()}")
            return
        }

        if (playbackState == Player.STATE_ENDED) {
            liveState.value =
                    liveState.value?.copy(isBuffering = false, isPlaying = false, currentIndex = player.currentIndex())
            Log.d(TAG, "Ended in position: ${player.currentIndex()}")
            return
        }

    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {}

    override fun onLoadingChanged(isLoading: Boolean) {}

    override fun onRepeatModeChanged(repeatMode: Int) {}

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}

    override fun onPlayerError(error: ExoPlaybackException?) {}

    override fun onPositionDiscontinuity(reason: Int) {}

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}

    override fun onSeekProcessed() {}

}

class MusicPlayer(context: Context, userAgent: String) {

    private val player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context)
    private val sourceFactory: DefaultHttpDataSourceFactory = DefaultHttpDataSourceFactory(userAgent)

    fun setListener(listener: Player.EventListener) {
        player.removeListener(listener)
        player.addListener(listener)
    }

    fun prepare(playList: List<SongView>, currentIndex: Int) {
        val mediaSources = ConcatenatingMediaSource()
        for (song in playList) {
            val uri = if (song.url.isEmpty()) UrlUpdatingDataSource.makeNoSongUri(song.id) else Uri.parse(song.url)
            mediaSources.addMediaSource(
                ExtractorMediaSource.Factory(
                    UrlUpdatingDataSource.Factory(
                        sourceFactory,
                        OstaticApplication.REPOSITORY
                    )
                ).createMediaSource(uri)
            )
        }
        player.prepare(mediaSources)
        player.seekTo(currentIndex, 0)
    }

    fun playOrPauseOnIndex(index: Int) {
        if (player.currentWindowIndex != index) {
            seekToMusic(index)
            player.playWhenReady = true
        } else {
            playOrPause()
        }
    }

    fun isPlaying(): Boolean {
        return player.playWhenReady
    }

    fun currentIndex(): Int {
        return player.currentWindowIndex
    }

    fun playOrPause() {
        player.playWhenReady = !player.playWhenReady
    }

    fun pause() {
        player.playWhenReady = false
    }

    fun previous() {
        player.previous()
    }

    fun next() {
        player.next()
    }

    fun seekTo(time: Long) {
        player.seekTo(time)
    }

    fun seekToMusic(index: Int) {
        player.seekTo(index, 0)
    }

    fun release() {
        player.release()
    }

}