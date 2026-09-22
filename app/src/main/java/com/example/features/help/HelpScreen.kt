package com.example.features.help

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.ForgeLanguage
import com.example.core.localization.ForgeStrings
import com.example.ui.theme.SignalAmber
import com.example.ui.theme.SignalGreen
import com.example.ui.theme.SignalMuted
import com.example.ui.theme.SignalRed

@Composable
fun HelpScreen(
  language: ForgeLanguage,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = ForgeStrings.get("help_title", language),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = ForgeStrings.get("help_subtitle", language),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Traffic Signals Legend
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Universal Traffic Signs Legend:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
          Spacer(modifier = Modifier.height(12.dp))

          SignalLegendRow(
            icon = Icons.Default.CheckCircle,
            color = SignalGreen,
            title = "READY / CLEAN",
            description = ForgeStrings.get("help_traffic_green", language)
          )

          Spacer(modifier = Modifier.height(10.dp))

          SignalLegendRow(
            icon = Icons.Default.Warning,
            color = SignalAmber,
            title = "MODIFIED / UNSAVED",
            description = ForgeStrings.get("help_traffic_amber", language)
          )

          Spacer(modifier = Modifier.height(10.dp))

          SignalLegendRow(
            icon = Icons.Default.Error,
            color = SignalRed,
            title = "ISSUE / SYNTAX ERROR",
            description = ForgeStrings.get("help_traffic_red", language)
          )

          Spacer(modifier = Modifier.height(10.dp))

          SignalLegendRow(
            icon = Icons.Default.RadioButtonUnchecked,
            color = SignalMuted,
            title = "OFFLINE / UNCONFIGURED",
            description = ForgeStrings.get("help_traffic_gray", language)
          )
        }
      }
    }

    // Mobile Keyboard Symbols Guide
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Mobile Touch Coding Guide:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "• Quick Symbol Bar: Insert { } ( ) [ ] = : ; \" ' without toggling soft keyboard pages.\n• Local SQLite Persistence: Code is preserved safely across app restarts.\n• True Offline Execution: Shell commands, syntax checks, and git commits operate without network.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
private fun SignalLegendRow(
  icon: ImageVector,
  color: Color,
  title: String,
  description: String
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth()
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(34.dp)
        .background(color.copy(alpha = 0.15f), CircleShape)
    ) {
      Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = color)
      Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
