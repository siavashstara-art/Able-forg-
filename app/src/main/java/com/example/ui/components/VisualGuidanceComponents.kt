package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.features.main.ForgeSection
import com.example.ui.theme.SignalAmber
import com.example.ui.theme.SignalGreen
import com.example.ui.theme.SignalMuted
import com.example.ui.theme.SignalRed

enum class TrafficSignalType {
  READY,
  MODIFIED,
  ISSUE,
  UNCONFIGURED,
  CLEAN
}

@Composable
fun UniversalTrafficPill(
  signal: TrafficSignalType,
  label: String,
  modifier: Modifier = Modifier
) {
  val (color, icon, tag) = when (signal) {
    TrafficSignalType.READY, TrafficSignalType.CLEAN -> Triple(
      SignalGreen,
      Icons.Filled.CheckCircle,
      "READY"
    )
    TrafficSignalType.MODIFIED -> Triple(
      SignalAmber,
      Icons.Filled.Warning,
      "MODIFIED"
    )
    TrafficSignalType.ISSUE -> Triple(
      SignalRed,
      Icons.Filled.Error,
      "ISSUE"
    )
    TrafficSignalType.UNCONFIGURED -> Triple(
      SignalMuted,
      Icons.Filled.RadioButtonUnchecked,
      "UNCONFIGURED"
    )
  }

  Surface(
    shape = RoundedCornerShape(20.dp),
    color = color.copy(alpha = 0.12f),
    border = BorderStroke(1.5.dp, color),
    modifier = modifier
      .semantics { contentDescription = "Status: $tag - $label" }
      .testTag("status_traffic_pill")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(14.dp)
      )
      Text(
        text = "[$tag] $label",
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun TrafficStepFlowIndicator(
  currentSection: ForgeSection,
  onStepClick: (ForgeSection) -> Unit,
  modifier: Modifier = Modifier
) {
  val steps = listOf(
    Triple(ForgeSection.PROJECTS, "1", "Project"),
    Triple(ForgeSection.FILES, "2", "Files"),
    Triple(ForgeSection.EDITOR, "3", "Editor"),
    Triple(ForgeSection.CONSOLE, "4", "Console"),
    Triple(ForgeSection.GIT, "5", "Git")
  )

  Row(
    modifier = modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(horizontal = 12.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    steps.forEachIndexed { index, (section, number, title) ->
      val isActive = (currentSection == section)
      val bg = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
      val contentColor = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
      val borderColor = if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .border(1.dp, borderColor, RoundedCornerShape(8.dp))
          .background(bg, RoundedCornerShape(8.dp))
          .clickable { onStepClick(section) }
          .padding(horizontal = 8.dp, vertical = 6.dp)
          .testTag("step_indicator_$number")
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(20.dp)
            .background(if (isActive) MaterialTheme.colorScheme.primary else contentColor.copy(alpha = 0.2f), CircleShape)
        ) {
          Text(
            text = number,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) MaterialTheme.colorScheme.onPrimary else contentColor
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = title,
          fontSize = 12.sp,
          fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
          color = contentColor
        )
      }

      if (index < steps.lastIndex) {
        Text(
          text = "→",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
      }
    }
  }
}

@Composable
fun MobileSymbolQuickBar(
  onSymbolClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val symbols = listOf(
    "{", "}", "(", ")", "[", "]", "=", ":", ";", "\"", "'",
    "<", ">", "/", "\\", "_", "$", ".", "&", "|", "!", "?"
  )

  Surface(
    color = MaterialTheme.colorScheme.surfaceVariant,
    tonalElevation = 2.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 6.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      symbols.forEach { sym ->
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(width = 38.dp, height = 36.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(6.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .clickable { onSymbolClick(sym) }
            .testTag("symbol_btn_$sym")
        ) {
          Text(
            text = sym,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp
          )
        }
      }
    }
  }
}
