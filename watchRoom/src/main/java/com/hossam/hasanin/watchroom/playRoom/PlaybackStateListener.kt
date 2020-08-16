package com.hossam.hasanin.watchroom.playRoom

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import javax.inject.Inject

class PlaybackStateListener @Inject constructor(): Player.EventListener {
    var trackState: (Int) -> Unit = {}
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        trackState(playbackState)
    }
}