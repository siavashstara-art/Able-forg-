package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.ForgeLanguage

/**
 * Status of any online operation (Strictly avoiding fake successes):
 * - SYNCED: verified online sync complete
 * - PENDING: waiting for network, stored locally in SQLite
 * - FAILED: network unreachable or server rejected
 */
enum class OnlineSyncStatus(
  val faLabel: String,
  val enLabel: String,
  val signal: TrafficSignalType
) {
  SYNCED("همگام‌شده (SYNCED)", "SYNCED", TrafficSignalType.READY),
  PENDING("در انتظار اتصال (PENDING)", "PENDING", TrafficSignalType.MODIFIED),
  FAILED("خطای اتصال (FAILED)", "FAILED", TrafficSignalType.ISSUE),
  OFFLINE_LOCAL("کاملاً محلی و امن", "OFFLINE LOCAL", TrafficSignalType.UNCONFIGURED)
}

/**
 * 1. Offline Notice Banner with transparent explanation:
 * «اینترنت قطع است. این کار فعلاً آنلاین است، اما این بخش‌ها هنوز در دسترس هستند.»
 */
@Composable
fun OfflineNoticeBanner(
  isOnline: Boolean,
  language: ForgeLanguage,
  modifier: Modifier = Modifier
) {
  if (isOnline) return

  val isFa = (language == ForgeLanguage.FA)

  Card(
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag("offline_notice_banner")
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(32.dp)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
        ) {
          Icon(Icons.Default.CloudOff, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isFa) "اینترنت قطع است (کارگاه آفلاین فعال است)" else "Offline Mode Active",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = if (isFa)
          "«اینترنت قطع است. این کار فعلاً آنلاین است، اما این بخش‌ها هنوز در دسترس هستند: پروژه‌ها، ویرایشگر، فایل‌های محلی، آموزشگاه، راهنمای صوتی و تنظیمات دسترسی‌پذیری.»"
        else
          "No internet connection. Online features are paused, but local modules remain fully accessible: Projects, Editor, Files, Academy, Voice Readout, and Accessibility.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 19.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        UniversalTrafficPill(signal = TrafficSignalType.UNCONFIGURED, label = if (isFa) "آفلاین ۱۰۰٪" else "OFFLINE 100%")
        Text(
          text = if (isFa) "ذخیره‌سازی در حافظه دستگاه" else "Saved locally in SQLite",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

/**
 * 2. Multi-channel Live Voice Caption Box (Low Vision, Hard of Hearing, ADHD)
 */
@Composable
fun VoiceCaptionBar(
  caption: String,
  isPlaying: Boolean,
  playbackSpeed: Float,
  onPause: () -> Unit,
  onResume: () -> Unit,
  onStop: () -> Unit,
  onSpeedChange: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  if (caption.isBlank() && !isPlaying) return

  Surface(
    color = MaterialTheme.colorScheme.surfaceVariant,
    tonalElevation = 6.dp,
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 4.dp)
      .testTag("voice_caption_bar")
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Subtitles, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "زیرنویس همزمان صوتی (Voice Caption)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Play / Pause / Resume
          if (isPlaying) {
            IconButton(onClick = onPause, modifier = Modifier.size(28.dp).testTag("btn_tts_pause")) {
              Icon(Icons.Default.Pause, contentDescription = "توقف موقت", modifier = Modifier.size(16.dp))
            }
          } else {
            IconButton(onClick = onResume, modifier = Modifier.size(28.dp).testTag("btn_tts_resume")) {
              Icon(Icons.Default.PlayArrow, contentDescription = "ادامه پخش", modifier = Modifier.size(16.dp))
            }
          }

          IconButton(onClick = onStop, modifier = Modifier.size(28.dp).testTag("btn_tts_stop")) {
            Icon(Icons.Default.Stop, contentDescription = "قطع کامل", modifier = Modifier.size(16.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Live Captions Text
      Text(
        text = caption,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 22.sp,
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
          .padding(8.dp)
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Speed Slider: 0.75x to 1.5x
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "سرعت صدا: ${String.format("%.2f", playbackSpeed)}x", style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.width(10.dp))
        Slider(
          value = playbackSpeed,
          onValueChange = onSpeedChange,
          valueRange = 0.75f..1.5f,
          steps = 2,
          modifier = Modifier.weight(1f).testTag("slider_tts_speed")
        )
      }
    }
  }
}

/**
 * 3. Speech-to-Text Input Dialog / Bar (Handles Questions, Errors, Commands, Text, Search)
 */
@Composable
fun SttVoiceInputDialog(
  isOpen: Boolean,
  targetTitle: String,
  isListening: Boolean,
  transcribedText: String,
  lastError: String?,
  onStartListening: () -> Unit,
  onStopListening: () -> Unit,
  onConfirmText: (String) -> Unit,
  onDismiss: () -> Unit
) {
  if (!isOpen) return

  androidx.compose.material3.AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(16.dp),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "ورودی صوتی گفتار به متن: $targetTitle",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "با میکروفون صحبت کن تا کلمات به صورت متن تایپ شوند (مناسب برای افراد با محدودیت حرکتی و ADHD).",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            .padding(12.dp)
        ) {
          if (transcribedText.isNotBlank()) {
            Text(
              text = transcribedText,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          } else if (isListening) {
            Text(
              text = "🎙️ در حال شنیدن صدای شما... صحبت کنید",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.primary
            )
          } else {
            Text(
              text = "برای شروع دکمه «شروع ضبط صدا» را لمس کنید.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (lastError != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "⚠️ $lastError",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirmText(transcribedText) },
        enabled = transcribedText.isNotBlank(),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("تأیید و درج در $targetTitle")
      }
    },
    dismissButton = {
      Row {
        if (!isListening) {
          OutlinedButton(
            onClick = onStartListening,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("btn_stt_listen")
          ) {
            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("شروع ضبط")
          }
        } else {
          Button(
            onClick = onStopListening,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("btn_stt_stop")
          ) {
            Icon(Icons.Default.MicOff, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("پایان گفتار")
          }
        }
        Spacer(modifier = Modifier.width(6.dp))
        TextButton(onClick = onDismiss) {
          Text("بستن")
        }
      }
    }
  )
}
