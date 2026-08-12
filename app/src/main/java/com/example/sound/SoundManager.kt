package com.example.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SoundManager(private val context: Context) {

    fun playSound(toneType: String = "Digital") {
        if (toneType.equals("none", ignoreCase = true)) return
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val mediaPlayer = MediaPlayer.create(context.applicationContext, uri)
                if (mediaPlayer != null) {
                    mediaPlayer.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    mediaPlayer.setOnCompletionListener { mp ->
                        try {
                            mp.release()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    mediaPlayer.start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun vibrate(durationMs: Long = 500) {
        try {
            val appContext = context.applicationContext
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val defaultVibrator = vibratorManager?.defaultVibrator
                if (defaultVibrator?.hasVibrator() == true) {
                    defaultVibrator.vibrate(
                        VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                }
            } else {
                @Suppress("DEPRECATION")
                val vibrator = appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (vibrator?.hasVibrator() == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(durationMs)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


