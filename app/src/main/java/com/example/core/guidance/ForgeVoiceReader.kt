package com.example.core.guidance

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Clean multi-channel Text-To-Speech reader for Accessibility, ADHD, and Universal Guidance.
 */
class ForgeVoiceReader(context: Context) {
  private var tts: TextToSpeech? = null
  private var isInitialized = false

  init {
    tts = TextToSpeech(context.applicationContext) { status ->
      if (status == TextToSpeech.SUCCESS) {
        isInitialized = true
        // Try Persian, fallback to English
        val result = tts?.setLanguage(Locale("fa"))
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          tts?.setLanguage(Locale.US)
        }
      }
    }
  }

  fun speak(text: String, isReducedMotionOrSpeechMuted: Boolean = false) {
    if (!isInitialized || isReducedMotionOrSpeechMuted || text.isBlank()) return
    tts?.stop()
    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "forge_guidance_${System.currentTimeMillis()}")
  }

  fun stop() {
    tts?.stop()
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
    tts = null
  }
}

@Composable
fun rememberForgeVoiceReader(): ForgeVoiceReader {
  val context = LocalContext.current
  val reader = remember { ForgeVoiceReader(context) }
  DisposableEffect(reader) {
    onDispose {
      reader.shutdown()
    }
  }
  return reader
}
