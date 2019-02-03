package io.github.artenes.ostatic.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.util.Log

class BecomingNoisyReceiver(val service: MusicPlayerService) : BroadcastReceiver() {

    private val noisyFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private var registered: Boolean = false

    fun register() {
        if (!registered) {
            service.registerReceiver(this, noisyFilter)
            registered = true
        }
    }

    fun unregister() {
        if (registered) {
            service.unregisterReceiver(this)
            registered = false
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("lolcats", "onReceive: ")
        if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            service.getSession()?.pause()
        }
    }

}