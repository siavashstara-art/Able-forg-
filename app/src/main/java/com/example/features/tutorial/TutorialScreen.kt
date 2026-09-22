package com.example.features.tutorial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.guidance.ForgeVoiceReader
import com.example.core.guidance.rememberForgeVoiceReader
import com.example.core.localization.ForgeLanguage
import com.example.features.main.ForgeSection
import com.example.ui.components.TrafficSignalType
import com.example.ui.components.UniversalTrafficPill

@Composable
fun TutorialScreen(
  language: ForgeLanguage,
  onNavigateSection: (ForgeSection) -> Unit,
  modifier: Modifier = Modifier
) {
  val chapters = remember { TutorialRepository.chapters }
  var expandedChapterId by remember { mutableStateOf<String?>(chapters.firstOrNull()?.id) }
  val voiceReader = rememberForgeVoiceReader()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Header Banner
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
              Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (language == ForgeLanguage.FA) "آموزش گام‌به‌گام و خودمانی کارگاه" else "Practical Interactive Academy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (language == ForgeLanguage.FA) "آموزش عملی و ساده؛ از صفر تا انتشار بدون استرس" else "Hands-on, plain language software creation from ground up",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            UniversalTrafficPill(signal = TrafficSignalType.READY, label = "۱۲ درس")
          }
        }
      }
    }

    // List of Interactive Tutorial Chapters
    items(chapters) { chapter ->
      val isExpanded = expandedChapterId == chapter.id
      val title = if (language == ForgeLanguage.FA) chapter.titleFa else chapter.titleEn
      val shortDesc = if (language == ForgeLanguage.FA) chapter.shortDescFa else chapter.shortDescEn
      val content = if (language == ForgeLanguage.FA) chapter.contentFa else chapter.contentEn
      val simpleExplain = chapter.simpleExplainFa
      val actionLabel = if (language == ForgeLanguage.FA) chapter.actionLabelFa else chapter.actionLabelEn

      Card(
        colors = CardDefaults.cardColors(
          containerColor = if (isExpanded) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
          width = if (isExpanded) 1.5.dp else 1.dp,
          color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            expandedChapterId = if (isExpanded) null else chapter.id
          }
          .testTag("tutorial_chapter_${chapter.id}")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(36.dp)
                .background(
                  if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                  CircleShape
                )
            ) {
              Icon(
                chapter.icon,
                contentDescription = null,
                tint = if (isExpanded) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
              )
              Text(
                text = shortDesc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            IconButton(onClick = { expandedChapterId = if (isExpanded) null else chapter.id }) {
              Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = "Expand"
              )
            }
          }

          AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
              // Plain language content
              Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
              )

              Spacer(modifier = Modifier.height(8.dp))

              // Simple child-friendly callout
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Text(
                  text = simpleExplain,
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onTertiaryContainer
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Actions: Speak out loud & Direct Practical Navigation
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                OutlinedButton(
                  onClick = {
                    voiceReader.speak(content)
                  },
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.testTag("btn_speak_${chapter.id}")
                ) {
                  Icon(Icons.Default.VolumeUp, contentDescription = "Voice readout", modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(if (language == ForgeLanguage.FA) "شنیدن صوتی" else "Read aloud", fontSize = 12.sp)
                }

                Button(
                  onClick = { onNavigateSection(chapter.actionTarget) },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                  modifier = Modifier.testTag("btn_action_${chapter.id}")
                ) {
                  Text(actionLabel, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
              }
            }
          }
        }
      }
    }
  }
}
