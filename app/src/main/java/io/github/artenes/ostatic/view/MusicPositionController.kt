package io.github.artenes.ostatic.view

import android.os.Handler
import android.widget.SeekBar
import android.widget.TextView
import io.github.artenes.ostatic.service.MusicSession

/**
 * A controller that updates a seek bar and a text view
 * to display the progress of a music
 */
class MusicPositionController(
    private val seekBar: SeekBar,
    private val currentTime: TextView,
    private val session: MusicSession
) : SeekBar.OnSeekBarChangeListener {

    private val handler = Handler()
    private val runnable = UpdateProgressBarRunnable()
    private var isSeeking = false

    init {
        seekBar.setOnSeekBarChangeListener(this)
    }

    /**
     * When the player changes state
     * this method should be called
     * to refresh the view's state
     */
    fun playMusic() {
        seekBar.max = session.getDuration().toInt()
        startPollingPlayerPosition()
    }

    /**
     * The player does not provides a callback to notify about
     * the music progression, so we have to poll to update
     * the seek bar and the current time label
     * It will poll once each second
     */
    private fun startPollingPlayerPosition() {

        stopPolling()

        val position = session.getCurrentPosition().toInt()

        updateSeekBarPosition(position, false)
        updateTimeLabel(position)

        if (session.getCurrentState()?.isPlaying == true && !isSeeking) {
            handler.postDelayed(runnable, 1000)
        }

    }

    /**
     * The user touched the seek bar
     */
    private fun onUserStartSeeking() {
        isSeeking = true
        stopPolling()
    }

    /**
     * The user is moving the seek bar
     */
    private fun onUserSeeking(position: Int) {
        updateTimeLabel(position)
    }

    /**
     *
     * The user release the seek bar
     */
    private fun onUserStopSeeking(position: Int) {
        isSeeking = false
        updateSeekBarPosition(position, true)
    }

    /**
     * Don't poll anymore (it will keep running any polling happening right now)
     */
    private fun stopPolling() {
        handler.removeCallbacks(runnable)
    }

    //region BOILERPLATE

    /**
     * Moves the seek bar to the current time of the music
     * or to the current position seek by the user
     */
    private fun updateSeekBarPosition(position: Int, fromUser: Boolean) {
        seekBar.progress = position
        if (fromUser) {
            session.seekTo(position.toLong())
        }
    }

    /**
     * Sets the current playing time to the time label
     */
    private fun updateTimeLabel(position: Int) {
        currentTime.text = millisecondsToTimeStamp(position)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        onUserSeeking(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        onUserStartSeeking()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        val position = seekBar?.progress ?: 0
        onUserStopSeeking(position)
    }

    private fun millisecondsToTimeStamp(milliseconds: Int): String {
        val totalSeconds = milliseconds / 1000

        //get all full minutes from the total of seconds
        val minutes = totalSeconds / 60
        //then get the remaining seconds
        val seconds = totalSeconds - (minutes * 60)

        //adds a padding to make them have two digits
        val minutesFormatted = minutes.toString().padStart(2, '0')
        val secondsFormatted = seconds.toString().padStart(2, '0')

        return "$minutesFormatted:$secondsFormatted"
    }

    /**
     * Runnable used to poll the player position and then update
     * the seek bar and time label
     */
    private inner class UpdateProgressBarRunnable : Runnable {

        override fun run() {
            startPollingPlayerPosition()
        }

    }

    //endregion

}