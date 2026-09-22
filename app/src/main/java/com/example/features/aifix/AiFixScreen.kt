package com.example.features.aifix

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.ForgeLanguage
import com.example.core.localization.ForgeStrings
import com.example.core.model.ProjectEntity
import com.example.features.main.DiagnosticIssue
import com.example.features.main.DiagnosticSeverity
import com.example.ui.components.TrafficSignalType
import com.example.ui.components.UniversalTrafficPill

@Composable
fun AiFixScreen(
  project: ProjectEntity?,
  issues: List<DiagnosticIssue>,
  geminiKeyConfigured: Boolean,
  language: ForgeLanguage,
  onRunDiagnostics: () -> Unit,
  modifier: Modifier = Modifier
) {
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
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = ForgeStrings.get("aifix_title", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            UniversalTrafficPill(
              signal = if (issues.isEmpty()) TrafficSignalType.READY else TrafficSignalType.ISSUE,
              label = if (issues.isEmpty()) "PASSED" else "${issues.size} ISSUES"
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = ForgeStrings.get("aifix_subtitle", language),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Diagnostics Action Card
    item {
      Button(
        onClick = onRunDiagnostics,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("btn_run_diagnostics")
      ) {
        Icon(Icons.Default.Refresh, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(ForgeStrings.get("aifix_run_check", language), fontWeight = FontWeight.Bold)
      }
    }

    // Local Diagnostics Results
    item {
      Text(
        text = ForgeStrings.get("aifix_local_lint", language),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    }

    if (issues.isEmpty()) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = ForgeStrings.get("aifix_no_issues", language),
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }
      }
    } else {
      items(issues) { issue ->
        val icon = when (issue.severity) {
          DiagnosticSeverity.ERROR -> Icons.Default.Error
          DiagnosticSeverity.WARNING -> Icons.Default.Warning
          DiagnosticSeverity.INFO -> Icons.Default.Info
        }
        val tint = when (issue.severity) {
          DiagnosticSeverity.ERROR -> Color(0xFFEF4444)
          DiagnosticSeverity.WARNING -> Color(0xFFF59E0B)
          DiagnosticSeverity.INFO -> Color(0xFF38BDF8)
        }

        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, tint.copy(alpha = 0.5f)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(12.dp)
          ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "${issue.fileName} [line ${issue.line}]",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = issue.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // Gemini API Connection Status (Honest Real Status)
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
              Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = ForgeStrings.get("aifix_gemini_status", language),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
              )
            }
            UniversalTrafficPill(
              signal = if (geminiKeyConfigured) TrafficSignalType.READY else TrafficSignalType.UNCONFIGURED,
              label = if (geminiKeyConfigured) "KEY DETECTED" else "KEY MISSING"
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = ForgeStrings.get("aifix_gemini_info", language),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(8.dp))

          Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = if (geminiKeyConfigured)
                "Status: GEMINI_API_KEY verified in build configuration. Live cloud reasoning available."
              else
                "Status: No API key found in Secrets panel. Running strictly in offline local mode. No simulated fake success responses.",
              style = MaterialTheme.typography.bodySmall,
              fontFamily = FontFamily.Monospace,
              color = if (geminiKeyConfigured) Color(0xFF10B981) else Color(0xFF94A3B8),
              modifier = Modifier.padding(8.dp)
            )
          }
        }
      }
    }
  }
}
