package com.example.features.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MotionPhotosOff
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.guidance.GuidanceSelectionMode
import com.example.core.guidance.GuidanceState
import com.example.core.guidance.GuidanceTier
import com.example.core.localization.ForgeLanguage
import com.example.core.localization.ForgeStrings
import com.example.ui.components.TrafficSignalType
import com.example.ui.components.UniversalTrafficPill
import com.example.ui.theme.ForgeThemeMode

@Composable
fun SettingsScreen(
  currentTheme: ForgeThemeMode,
  currentLanguage: ForgeLanguage,
  reducedMotion: Boolean,
  fontScale: Float,
  guidanceState: GuidanceState,
  onThemeChanged: (ForgeThemeMode) -> Unit,
  onLanguageChanged: (ForgeLanguage) -> Unit,
  onReducedMotionChanged: (Boolean) -> Unit,
  onFontScaleChanged: (Float) -> Unit,
  onOpenGuidancePicker: () -> Unit,
  onResetGuidanceToAdaptive: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isFa = (currentLanguage == ForgeLanguage.FA)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(16.dp)
        ) {
          Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = ForgeStrings.get("settings_title", currentLanguage),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (isFa) "تنظیمات سطح کمک، دسترسی‌پذیری و ظاهر" else "Preferences, Guidance Level & Accessibility",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Guidance System Configuration Card
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (isFa) "سیستم راهنمایی (Guidance System)" else "Guidance System",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
              )
            }
            UniversalTrafficPill(
              signal = TrafficSignalType.READY,
              label = if (guidanceState.selectionMode == GuidanceSelectionMode.ADAPTIVE) "هوشمند" else "دستی"
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = if (isFa)
              "سطح فعلی: ${guidanceState.currentTier.faTitle} (${guidanceState.currentTier.enTitle})"
            else
              "Active Tier: ${guidanceState.currentTier.enTitle}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = if (isFa) guidanceState.currentTier.faDesc else guidanceState.currentTier.enDesc,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onOpenGuidancePicker,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_change_tier")
            ) {
              Text(if (isFa) "تغییر سطح کمک" else "Change Tier", fontSize = 12.sp)
            }

            OutlinedButton(
              onClick = onResetGuidanceToAdaptive,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_make_adaptive")
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(if (isFa) "تنظیم هوشمند" else "Adaptive Auto", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // Accessibility: Reduced Motion
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.MotionPhotosOff, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = if (isFa) "کاهش حرکت (Reduced Motion / ADHD)" else "Reduced Motion",
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.titleMedium
                )
                Text(
                  text = if (isFa) "غیرفعال‌سازی انیمیشن‌های غیرضروری برای تمرکز بهتر" else "Minimizes unnecessary screen movement for focus",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Switch(
              checked = reducedMotion,
              onCheckedChange = onReducedMotionChanged,
              modifier = Modifier.testTag("switch_reduced_motion")
            )
          }
        }
      }
    }

    // Accessibility: Text Size Scale
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.FormatSize, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = ForgeStrings.get("settings_font_scale", currentLanguage),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
              )
            }
            Text("${(fontScale * 100).toInt()}%", fontWeight = FontWeight.Bold)
          }

          Slider(
            value = fontScale,
            onValueChange = onFontScaleChanged,
            valueRange = 0.85f..1.35f,
            steps = 4,
            modifier = Modifier.testTag("slider_font_scale")
          )
        }
      }
    }

    // Theme Mode
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ColorLens, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = ForgeStrings.get("settings_theme", currentLanguage),
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleMedium
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          ThemeOptionRow(
            title = ForgeStrings.get("theme_dark", currentLanguage),
            selected = (currentTheme == ForgeThemeMode.DARK),
            onClick = { onThemeChanged(ForgeThemeMode.DARK) }
          )

          ThemeOptionRow(
            title = ForgeStrings.get("theme_light", currentLanguage),
            selected = (currentTheme == ForgeThemeMode.LIGHT),
            onClick = { onThemeChanged(ForgeThemeMode.LIGHT) }
          )

          ThemeOptionRow(
            title = ForgeStrings.get("theme_high_contrast", currentLanguage),
            selected = (currentTheme == ForgeThemeMode.HIGH_CONTRAST),
            onClick = { onThemeChanged(ForgeThemeMode.HIGH_CONTRAST) }
          )
        }
      }
    }

    // Language Selector
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = ForgeStrings.get("settings_language", currentLanguage),
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleMedium
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          ForgeLanguage.values().forEach { lang ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onLanguageChanged(lang) }
                .padding(vertical = 4.dp)
                .testTag("lang_select_${lang.code}")
            ) {
              RadioButton(
                selected = (currentLanguage == lang),
                onClick = { onLanguageChanged(lang) }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "${lang.nativeName} (${lang.englishName})",
                style = MaterialTheme.typography.bodyMedium
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ThemeOptionRow(
  title: String,
  selected: Boolean,
  onClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(vertical = 4.dp)
  ) {
    RadioButton(selected = selected, onClick = onClick)
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = title, style = MaterialTheme.typography.bodyMedium)
  }
}
