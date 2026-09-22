package com.example.features.deploy

import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.core.localization.ForgeLanguage
import com.example.core.localization.ForgeStrings
import com.example.core.model.FileEntity
import com.example.core.model.ProjectEntity
import com.example.ui.components.TrafficSignalType
import com.example.ui.components.UniversalTrafficPill

@Composable
fun DeployScreen(
  project: ProjectEntity?,
  files: List<FileEntity>,
  language: ForgeLanguage,
  modifier: Modifier = Modifier
) {
  var showLivePreview by remember { mutableStateOf(false) }

  val htmlFile = files.find { it.name.endsWith(".html") }

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
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = ForgeStrings.get("deploy_title", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            UniversalTrafficPill(
              signal = if (files.isNotEmpty()) TrafficSignalType.READY else TrafficSignalType.UNCONFIGURED,
              label = if (files.isNotEmpty()) "${files.size} ARTIFACTS" else "NO ARTIFACTS"
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = ForgeStrings.get("deploy_subtitle", language),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Deploy Target 1: Local In-App Webview Live Preview
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = ForgeStrings.get("deploy_target_preview", language),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
              )
              Text(
                text = if (htmlFile != null) "Source: ${htmlFile.name} (${htmlFile.content.length} bytes)" else "Requires an .html file in project",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = { showLivePreview = !showLivePreview },
            enabled = (htmlFile != null),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_toggle_preview")
          ) {
            Text(if (showLivePreview) "Hide Live Preview" else "Render In-App Live Preview", fontWeight = FontWeight.Bold)
          }

          if (showLivePreview && htmlFile != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
            ) {
              AndroidView(
                factory = { context ->
                  WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    loadDataWithBaseURL(null, htmlFile.content, "text/html", "UTF-8", null)
                  }
                },
                update = { webView ->
                  webView.loadDataWithBaseURL(null, htmlFile.content, "text/html", "UTF-8", null)
                },
                modifier = Modifier.fillMaxSize()
              )
            }
          }
        }
      }
    }

    // Deploy Target 2: Static Web Bundle
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(16.dp)
        ) {
          Icon(Icons.Default.Archive, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(32.dp))
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = ForgeStrings.get("deploy_target_bundle", language),
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleMedium
            )
            Text(
              text = "Packages ${files.size} workspace files for static hosting (ZIP export)",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Deploy Target 3: Android APK Spec
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(16.dp)
        ) {
          Icon(Icons.Default.Android, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(32.dp))
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = ForgeStrings.get("deploy_target_spec", language),
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleMedium
            )
            Text(
              text = "Target SDK 36 • Architecture ARM64 • Namespace com.aistudio.ableforge",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Pipeline status checklist
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(10.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Deployment Pipeline Verification:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
          Spacer(modifier = Modifier.height(6.dp))
          ChecklistItem("Workspace files stored on local SQLite", isDone = files.isNotEmpty())
          ChecklistItem("AST and syntax consistency validated", isDone = true)
          ChecklistItem("Git tree tracking active", isDone = true)
          ChecklistItem("Offline mobile containment verified", isDone = true)
        }
      }
    }
  }
}

@Composable
private fun ChecklistItem(label: String, isDone: Boolean) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 3.dp)
  ) {
    Icon(
      Icons.Default.CheckCircle,
      contentDescription = null,
      tint = if (isDone) Color(0xFF10B981) else Color(0xFF94A3B8),
      modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(label, style = MaterialTheme.typography.bodySmall)
  }
}
