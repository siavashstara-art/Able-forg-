package com.example.features.projects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import com.example.core.model.ProjectEntity
import com.example.ui.components.TrafficSignalType
import com.example.ui.components.UniversalTrafficPill

@Composable
fun ProjectsScreen(
  projects: List<ProjectEntity>,
  activeProject: ProjectEntity?,
  language: ForgeLanguage,
  onSelectProject: (ProjectEntity) -> Unit,
  onCreateProject: (name: String, description: String, template: String) -> Unit,
  onDeleteProject: (ProjectEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  var showCreateDialog by remember { mutableStateOf(false) }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = ForgeStrings.get("projects_title", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              UniversalTrafficPill(
                signal = if (activeProject != null) TrafficSignalType.READY else TrafficSignalType.UNCONFIGURED,
                label = if (activeProject != null) activeProject.name else "NO ACTIVE"
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = ForgeStrings.get("projects_desc", language),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
          }
        }
      }

      if (projects.isEmpty()) {
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
              Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = ForgeStrings.get("projects_empty", language),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(projects, key = { it.id }) { project ->
          val isActive = (activeProject?.id == project.id)
          ProjectCard(
            project = project,
            isActive = isActive,
            language = language,
            onOpen = { onSelectProject(project) },
            onDelete = { onDeleteProject(project) }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(72.dp)) // Padding for FAB
      }
    }

    FloatingActionButton(
      onClick = { showCreateDialog = true },
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("btn_create_project")
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = "New Project")
        Spacer(modifier = Modifier.width(8.dp))
        Text(ForgeStrings.get("action_new_project", language), fontWeight = FontWeight.Bold)
      }
    }
  }

  if (showCreateDialog) {
    CreateProjectDialog(
      language = language,
      onDismiss = { showCreateDialog = false },
      onConfirm = { name, desc, template ->
        onCreateProject(name, desc, template)
        showCreateDialog = false
      }
    )
  }
}

@Composable
private fun ProjectCard(
  project: ProjectEntity,
  isActive: Boolean,
  language: ForgeLanguage,
  onOpen: () -> Unit,
  onDelete: () -> Unit
) {
  val icon: ImageVector = when (project.templateType) {
    "WEB_APP" -> Icons.Default.Language
    "KOTLIN_SCRIPT" -> Icons.Default.Code
    "JSON_API" -> Icons.Default.Layers
    else -> Icons.Default.LibraryBooks
  }

  val borderColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

  Card(
    colors = CardDefaults.cardColors(
      containerColor = if (isActive) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(if (isActive) 2.dp else 1.dp, borderColor),
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onOpen() }
      .testTag("project_card_${project.id}")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(44.dp)
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = project.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          if (isActive) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(2.dp)
            ) {
              Text(
                text = "ACTIVE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = project.description.ifEmpty { "Template: ${project.templateType}" },
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onOpen,
          modifier = Modifier.testTag("open_project_${project.id}")
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = "Open", tint = MaterialTheme.colorScheme.primary)
        }
        IconButton(
          onClick = onDelete,
          modifier = Modifier.testTag("delete_project_${project.id}")
        ) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
        }
      }
    }
  }
}

@Composable
private fun CreateProjectDialog(
  language: ForgeLanguage,
  onDismiss: () -> Unit,
  onConfirm: (name: String, desc: String, template: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var desc by remember { mutableStateOf("") }
  var selectedTemplate by remember { mutableStateOf("WEB_APP") }

  val templates = listOf(
    Pair("WEB_APP", ForgeStrings.get("project_template_web", language)),
    Pair("KOTLIN_SCRIPT", ForgeStrings.get("project_template_kotlin", language)),
    Pair("JSON_API", ForgeStrings.get("project_template_api", language)),
    Pair("MARKDOWN_DOC", ForgeStrings.get("project_template_markdown", language))
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = ForgeStrings.get("action_new_project", language),
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text(ForgeStrings.get("project_name_hint", language)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("input_project_name")
        )

        OutlinedTextField(
          value = desc,
          onValueChange = { desc = it },
          label = { Text("Description (Optional)") },
          maxLines = 2,
          modifier = Modifier.fillMaxWidth().testTag("input_project_desc")
        )

        Text(
          text = "Select Template:",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.bodyMedium
        )

        templates.forEach { (type, label) ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedTemplate = type }
              .padding(vertical = 4.dp)
          ) {
            RadioButton(
              selected = (selectedTemplate == type),
              onClick = { selectedTemplate = type }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(name, desc, selectedTemplate) },
        enabled = name.isNotBlank(),
        modifier = Modifier.testTag("btn_confirm_create_project")
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
