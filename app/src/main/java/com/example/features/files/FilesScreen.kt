package com.example.features.files

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.Javascript
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.ForgeLanguage
import com.example.core.localization.ForgeStrings
import com.example.core.model.FileEntity
import com.example.core.model.ProjectEntity

@Composable
fun FilesScreen(
  project: ProjectEntity?,
  files: List<FileEntity>,
  activeFile: FileEntity?,
  language: ForgeLanguage,
  onOpenFile: (FileEntity) -> Unit,
  onCreateFile: (name: String) -> Unit,
  onDeleteFile: (FileEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  var showCreateDialog by remember { mutableStateOf(false) }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Folder,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = project?.name ?: "No Project Selected",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${files.size} tracked files in local workspace",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      if (files.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
            ) {
              Text(
                text = ForgeStrings.get("files_empty", language),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(files, key = { it.id }) { file ->
          val isCurrent = (activeFile?.id == file.id)
          FileItemCard(
            file = file,
            isCurrent = isCurrent,
            onOpen = { onOpenFile(file) },
            onDelete = { onDeleteFile(file) }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(72.dp))
      }
    }

    FloatingActionButton(
      onClick = { showCreateDialog = true },
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("btn_create_file")
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = "New File")
        Spacer(modifier = Modifier.width(8.dp))
        Text(ForgeStrings.get("action_new_file", language), fontWeight = FontWeight.Bold)
      }
    }
  }

  if (showCreateDialog) {
    CreateFileDialog(
      language = language,
      onDismiss = { showCreateDialog = false },
      onConfirm = { name ->
        onCreateFile(name)
        showCreateDialog = false
      }
    )
  }
}

@Composable
private fun FileItemCard(
  file: FileEntity,
  isCurrent: Boolean,
  onOpen: () -> Unit,
  onDelete: () -> Unit
) {
  val icon: ImageVector = when {
    file.name.endsWith(".html") -> Icons.Default.Html
    file.name.endsWith(".js") -> Icons.Default.Javascript
    file.name.endsWith(".kt") -> Icons.Default.Code
    else -> Icons.Default.Description
  }

  val lineCount = file.content.lines().size
  val charCount = file.content.length

  Card(
    colors = CardDefaults.cardColors(
      containerColor = if (isCurrent) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(
      if (isCurrent) 1.5.dp else 1.dp,
      if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    ),
    shape = RoundedCornerShape(10.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onOpen() }
      .testTag("file_item_${file.id}")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(36.dp)
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
      ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = file.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          if (isCurrent) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(3.dp),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(1.dp)
            ) {
              Text(
                text = "OPEN",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
        }
        Text(
          text = "$lineCount lines • $charCount chars",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      IconButton(onClick = onOpen) {
        Icon(Icons.Default.Edit, contentDescription = "Edit File", tint = MaterialTheme.colorScheme.primary)
      }

      IconButton(onClick = onDelete) {
        Icon(Icons.Default.Delete, contentDescription = "Delete File", tint = MaterialTheme.colorScheme.error)
      }
    }
  }
}

@Composable
private fun CreateFileDialog(
  language: ForgeLanguage,
  onDismiss: () -> Unit,
  onConfirm: (name: String) -> Unit
) {
  var fileName by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(ForgeStrings.get("action_new_file", language), fontWeight = FontWeight.Bold)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = fileName,
          onValueChange = { fileName = it },
          label = { Text(ForgeStrings.get("file_create_hint", language)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("input_filename")
        )
        Text(
          text = "Supported: .html, .js, .kt, .json, .md, .txt",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(fileName) },
        enabled = fileName.isNotBlank(),
        modifier = Modifier.testTag("btn_confirm_create_file")
      ) {
        Text(ForgeStrings.get("action_create", language))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(ForgeStrings.get("action_cancel", language))
      }
    }
  )
}
