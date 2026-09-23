package com.example.core.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Target input category for Speech-to-Text.
 * Handles: Question (سؤال), Error (خطا), Command (دستور), Text/Code (متن), Search (جستجو)
 */
enum class SttTargetField(val faLabel: String, val enLabel: String) {
  QUESTION("سؤال و راهنمایی", "Question / Help"),
  ERROR("خطا یا توضیح اشکال", "Error / Issue"),
  COMMAND("دستور کنسول", "Console Command"),
  TEXT("متن یا کد ویرایشگر", "Code / Text"),
  SEARCH("جستجو در فایل‌ها و آموزش", "Search")
}

/**
 * Modular Audio Provider interfaces.
 * Never locks to a single cloud provider; decoupled for native Android, offline engines, or fallback services.
 */
interface SpeechToTextProvider {
  val isListening: StateFlow<Boolean>
  val transcribedText: StateFlow<String>
  val lastError: StateFlow<String?>
  val isOfflineLanguageSupported: Boolean

  fun startListening(target: SttTargetField, languageCode: String, onResult: (String) -> Unit)
  fun stopListening()
  fun destroy()
}

interface TextToSpeechProvider {
  val isPlaying: StateFlow<Boolean>
  val isPaused: StateFlow<Boolean>
  val currentCaption: StateFlow<String>
  val playbackSpeed: StateFlow<Float>

  fun speak(text: String, languageCode: String, onComplete: () -> Unit = {})
  fun pause()
  fun resume()
  fun stop()
  fun setSpeed(speed: Float)
  fun isLanguageAvailableLocally(languageCode: String): Boolean
  fun destroy()
}

/**
 * Native Android implementation of Modular Speech-To-Text Provider.
 * Checks real offline availability on device.
 */
class AndroidSpeechToTextProvider(private val context: Context) : SpeechToTextProvider {
  private var speechRecognizer: SpeechRecognizer? = null
  private val _isListening = MutableStateFlow(false)
  override val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

  private val _transcribedText = MutableStateFlow("")
  override val transcribedText: StateFlow<String> = _transcribedText.asStateFlow()

  private val _lastError = MutableStateFlow<String?>(null)
  override val lastError: StateFlow<String?> = _lastError.asStateFlow()

  override val isOfflineLanguageSupported: Boolean
    get() {
      // Real check: SpeechRecognizer service existence
      return SpeechRecognizer.isRecognitionAvailable(context)
    }

  init {
    if (SpeechRecognizer.isRecognitionAvailable(context)) {
      try {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
      } catch (_: Exception) {
        speechRecognizer = null
      }
    }
  }

  override fun startListening(target: SttTargetField, languageCode: String, onResult: (String) -> Unit) {
    if (speechRecognizer == null) {
      _lastError.value = "سرویس تشخیص گفتار روی این دستگاه یافت نشد."
      return
    }

    _lastError.value = null
    _transcribedText.value = ""

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
      putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
      putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (languageCode == "fa") "fa-IR" else "en-US")
      putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
      // Request offline speech recognition if supported by OS
      putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
    }

    speechRecognizer?.setRecognitionListener(object : RecognitionListener {
      override fun onReadyForSpeech(params: Bundle?) {
        _isListening.value = true
      }
      override fun onBeginningOfSpeech() {}
      override fun onRmsChanged(rmsdB: Float) {}
      override fun onBufferReceived(buffer: ByteArray?) {}
      override fun onEndOfSpeech() {
        _isListening.value = false
      }
      override fun onError(error: Int) {
        _isListening.value = false
        val message = when (error) {
          SpeechRecognizer.ERROR_NO_MATCH -> "صدایی تشخیص داده نشد."
          SpeechRecognizer.ERROR_NETWORK -> "تشخیص صدا برای این زبان نیاز به شبکه یا بارگیری مدل دارد."
          SpeechRecognizer.ERROR_AUDIO -> "خطای ضبط صدا."
          else -> "خطای میکروفون یا سرویس گفتار ($error)"
        }
        _lastError.value = message
      }
      override fun onResults(results: Bundle?) {
        _isListening.value = false
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text = matches?.firstOrNull() ?: ""
        _transcribedText.value = text
        if (text.isNotBlank()) {
          onResult(text)
        }
      }
      override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text = matches?.firstOrNull() ?: ""
        _transcribedText.value = text
      }
      override fun onEvent(eventType: Int, params: Bundle?) {}
    })

    try {
      speechRecognizer?.startListening(intent)
    } catch (e: Exception) {
      _isListening.value = false
      _lastError.value = "خطا در شروع میکروفون: ${e.localizedMessage}"
    }
  }

  override fun stopListening() {
    try {
      speechRecognizer?.stopListening()
    } catch (_: Exception) {}
    _isListening.value = false
  }

  override fun destroy() {
    try {
      speechRecognizer?.destroy()
    } catch (_: Exception) {}
    speechRecognizer = null
  }
}

/**
 * Native Android implementation of Modular Text-To-Speech with:
 * - Play / Pause / Resume / Stop / Speed
 * - Real offline language check (isLanguageAvailableLocally)
 * - Captions / Subtitles live streaming
 */
class AndroidTextToSpeechProvider(context: Context) : TextToSpeechProvider {
  private var tts: TextToSpeech? = null
  private var isInitialized = false

  private val _isPlaying = MutableStateFlow(false)
  override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

  private val _isPaused = MutableStateFlow(false)
  override val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

  private val _currentCaption = MutableStateFlow("")
  override val currentCaption: StateFlow<String> = _currentCaption.asStateFlow()

  private val _playbackSpeed = MutableStateFlow(1.0f)
  override val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

  private var pendingCompletion: (() -> Unit)? = null
  private var currentUtteranceText: String = ""

  init {
    tts = TextToSpeech(context.applicationContext) { status ->
      if (status == TextToSpeech.SUCCESS) {
        isInitialized = true
        tts?.setSpeechRate(_playbackSpeed.value)
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
          override fun onStart(utteranceId: String?) {
            _isPlaying.value = true
            _isPaused.value = false
          }

          override fun onDone(utteranceId: String?) {
            _isPlaying.value = false
            _isPaused.value = false
            _currentCaption.value = ""
            pendingCompletion?.invoke()
            pendingCompletion = null
          }

          override fun onError(utteranceId: String?) {
            _isPlaying.value = false
            _isPaused.value = false
            _currentCaption.value = ""
          }
        })
      }
    }
  }

  override fun isLanguageAvailableLocally(languageCode: String): Boolean {
    if (!isInitialized || tts == null) return false
    val loc = if (languageCode == "fa") Locale("fa") else Locale.US
    val avail = tts?.isLanguageAvailable(loc) ?: TextToSpeech.LANG_NOT_SUPPORTED
    return avail >= TextToSpeech.LANG_AVAILABLE
  }

  override fun speak(text: String, languageCode: String, onComplete: () -> Unit) {
    if (!isInitialized || text.isBlank()) return
    currentUtteranceText = text
    pendingCompletion = onComplete
    _currentCaption.value = text
    _isPlaying.value = true
    _isPaused.value = false

    val loc = if (languageCode == "fa") Locale("fa") else Locale.US
    val result = tts?.setLanguage(loc)
    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
      tts?.setLanguage(Locale.US)
    }

    tts?.setSpeechRate(_playbackSpeed.value)
    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_${System.currentTimeMillis()}")
  }

  override fun pause() {
    if (_isPlaying.value) {
      tts?.stop()
      _isPlaying.value = false
      _isPaused.value = true
    }
  }

  override fun resume() {
    if (_isPaused.value && currentUtteranceText.isNotBlank()) {
      speak(currentUtteranceText, "fa", pendingCompletion ?: {})
    }
  }

  override fun stop() {
    tts?.stop()
    _isPlaying.value = false
    _isPaused.value = false
    _currentCaption.value = ""
    currentUtteranceText = ""
  }

  override fun setSpeed(speed: Float) {
    _playbackSpeed.value = speed
    tts?.setSpeechRate(speed)
  }

  override fun destroy() {
    tts?.stop()
    tts?.shutdown()
    tts = null
  }
}
