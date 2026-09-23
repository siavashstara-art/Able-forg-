package com.example.features.git

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import com.example.core.localization.ForgeLanguage
import com.example.core.localization.ForgeStrings
import com.example.core.model.GitCommitEntity
import com.example.core.model.ProjectEntity
import com.example.ui.components.TrafficSignalType
import com.example.ui.components.UniversalTrafficPill

@Composable
fun GitScreen(
  project: ProjectEntity?,
  commits: List<GitCommitEntity>,
  isDirty: Boolean,
  hasAbleFlag: Boolean,
  language: ForgeLanguage,
  onCommit: (message: String) -> Unit,
  onAddAll: () -> Unit = {},
  onPush: () -> Unit = {},
  onPull: () -> Unit = {},
  onClone: (url: String) -> Unit = {},
  onTestConcurrencyCheck: () -> Unit = {},
  onToggleAbleFlag: () -> Unit = {},
  concurrencyErrorText: String? = null,
  modifier: Modifier = Modifier
) {
  var commitMessage by remember { mutableStateOf("") }
  var cloneUrlInput by remember { mutableStateOf("") }
  var showCloneField by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header & Branch Status
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
              Icon(Icons.AutoMirrored.Filled.CallSplit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "${ForgeStrings.get("git_branch", language)}: main",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            UniversalTrafficPill(
              signal = if (isDirty) TrafficSignalType.MODIFIED else TrafficSignalType.CLEAN,
              label = if (isDirty) "UNCOMMITTED EDITS" else "CLEAN"
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Remote status
          Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(10.dp)
            ) {
              Icon(Icons.Default.CloudOff, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = ForgeStrings.get("git_remote_status", language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // ABLE Flag Participation Card
    item {
      Card(
        colors = CardDefaults.cardColors(
          containerColor = if (hasAbleFlag) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (hasAbleFlag) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Flag, contentDescription = null, tint = if (hasAbleFlag) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "ABLE Participation Flag",
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.bodyLarge
                )
                Text(
                  text = if (hasAbleFlag) "Active participation • Eligible for Collaboration XP" else "Standard standalone project",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
            Switch(
              checked = hasAbleFlag,
              onCheckedChange = { onToggleAbleFlag() }
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "IP Protection Notice: The author/creator strictly retains all intellectual property and financial ownership. The ABLE Flag does not transfer IP rights.",
              style = MaterialTheme.typography.bodySmall,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(8.dp)
            )
          }
        }
      }
    }

    // Git Operations Toolbar (Add, Commit, Push, Pull, Clone)
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Git Operations (Native Filesystem Abstraction)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = onAddAll,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Stage All")
            }

            OutlinedButton(
              onClick = onPush,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Push")
            }

            OutlinedButton(
              onClick = onPull,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Pull")
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Commit Box
          OutlinedTextField(
            value = commitMessage,
            onValueChange = { commitMessage = it },
            placeholder = { Text(ForgeStrings.get("git_commit_msg", language)) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_commit_message")
          )
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = {
              onCommit(commitMessage)
              commitMessage = ""
            },
            enabled = (project != null && commitMessage.isNotBlank()),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_submit_commit")
          ) {
            Icon(Icons.Default.Done, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(ForgeStrings.get("action_commit", language), fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Clone Section
          if (!showCloneField) {
            OutlinedButton(
              onClick = { showCloneField = true },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Clone External Repository...")
            }
          } else {
            Column {
              OutlinedTextField(
                value = cloneUrlInput,
                onValueChange = { cloneUrlInput = it },
                placeholder = { Text("https://github.com/user/repo.git") },
                modifier = Modifier.fillMaxWidth()
              )
              Spacer(modifier = Modifier.height(8.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                  onClick = {
                    if (cloneUrlInput.isNotBlank()) {
                      onClone(cloneUrlInput)
                      cloneUrlInput = ""
                      showCloneField = false
                    }
                  },
                  modifier = Modifier.weight(1f)
                ) {
                  Text("Clone")
                }
                OutlinedButton(
                  onClick = { showCloneField = false },
                  modifier = Modifier.weight(1f)
                ) {
                  Text("Cancel")
                }
              }
            }
          }
        }
      }
    }

    // GitHub Online Commits & Concurrency Detection Policy
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "GitHub Online Commits & Concurrency Protocol",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "• Enforces Base64 UTF-8 encoding, duplicate path rejection, empty change rejection, and 2MB file size limits.\n• Checks expectedHeadOid against remote HEAD to detect simultaneous changes.\n• Strict v0.1 Policy: If HEAD changed, ConcurrentModificationError is thrown. Auto-retry is explicitly disabled.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedButton(
            onClick = onTestConcurrencyCheck,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.SyncProblem, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Simulate Concurrency Conflict Check")
          }

          if (concurrencyErrorText != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              color = Color(0xFFEF4444).copy(alpha = 0.15f),
              shape = RoundedCornerShape(6.dp),
              border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
            ) {
              Text(
                text = concurrencyErrorText,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFEF4444),
                modifier = Modifier.padding(8.dp)
              )
            }
          }
        }
      }
    }

    // Git Commits Timeline
    item {
      Text(
        text = ForgeStrings.get("git_history", language),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
      )
    }

    if (commits.isEmpty()) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text(
            text = "No commits recorded yet. Create your first commit above.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    } else {
      items(commits, key = { it.id }) { commit ->
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("commit_card_${commit.hash}")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(34.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = commit.message,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
              )
              Text(
                text = "hash: ${commit.hash} • ${commit.branch}",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }
  }
}
