package com.alonalbert.calendaralarm.media

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_ALARM
import android.media.MediaPlayer
import android.os.Build.VERSION_CODES.S
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.annotation.GuardedBy
import com.alonalbert.calendaralarm.utils.isAtLeast
import kotlin.LazyThreadSafetyMode.SYNCHRONIZED

class MediaManager private constructor() {
  @GuardedBy("this")
  private var mediaPlayer: MediaPlayer? = null

  private var vibrator: Vibrator? = null

  fun start(context: Context) {
    val applicationContext = context.applicationContext
    synchronized(this) {
      mediaPlayer = startMediaPlayer(applicationContext)
      vibrator = startVibrator(applicationContext)
    }
  }

  fun stop() {
    synchronized(this) {
      mediaPlayer?.release()
      vibrator?.cancel()
    }
  }

  companion object {
    val instance by lazy(SYNCHRONIZED) { MediaManager() }
  }
}

private fun startMediaPlayer(applicationContext: Context): MediaPlayer {
  val mediaPlayer = MediaPlayer()
  mediaPlayer.setAudioAttributes(
    AudioAttributes.Builder()
      .setContentType(CONTENT_TYPE_SONIFICATION)
      .setUsage(USAGE_ALARM)
      .build(),
  )
  mediaPlayer.isLooping = true
  mediaPlayer.setDataSource(applicationContext, Settings.System.DEFAULT_ALARM_ALERT_URI)
  mediaPlayer.prepare()
  mediaPlayer.start()
  return mediaPlayer
}

private fun startVibrator(applicationContext: Context): Vibrator {
  val vibrator = when {
    isAtLeast(S) -> applicationContext.getSystemService(VibratorManager::class.java).defaultVibrator
    else -> applicationContext.getSystemService(Vibrator::class.java)
  }
  vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 1000), 0))
  return vibrator
}



