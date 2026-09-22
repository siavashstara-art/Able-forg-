package com.example.features.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.ForgeLanguage
import com.example.core.localization.ForgeStrings
import com.example.core.model.FileEntity
import com.example.ui.components.MobileSymbolQuickBar
import com.example.ui.components.TrafficSignalType
import com.example.ui.components.UniversalTrafficPill

@Composable
fun EditorScreen(
  activeFile: FileEntity?,
  editorText: String,
  isDirty: Boolean,
  language: ForgeLanguage,
  onTextChanged: (String) -> Unit,
  onSave: () -> Unit,
  onSymbolClick: (String) -> Unit,
  onJumpToConsole: () -> Unit,
  modifier: Modifier = Modifier
) {
  val lines = editorText.lines()
  val lineCount = lines.size
  val charCount = editorText.length

  Column(modifier = modifier.fillMaxSize()) {
    // Top Editor Toolbar
    Surface(
      color = MaterialTheme.colorScheme.surfaceVariant,
      tonalElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Code,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 6.dp)
          )
          Column {
            Text(
              text = activeFile?.name ?: "No file open",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "$lineCount L • $charCount C",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          UniversalTrafficPill(
            signal = if (isDirty) TrafficSignalType.MODIFIED else TrafficSignalType.CLEAN,
            label = if (isDirty) ForgeStrings.get("editor_unsaved", language) else ForgeStrings.get("status_clean", language)
          )

          Button(
            onClick = onSave,
            enabled = (activeFile != null),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isDirty) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("btn_save_file")
          ) {
            Icon(Icons.Default.Save, contentDescription = "Save")
            Spacer(modifier = Modifier.width(4.dp))
            Text(ForgeStrings.get("action_save", language), fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Code area with line numbers
    if (activeFile == null) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = ForgeStrings.get("editor_no_file", language),
            modifier = Modifier.padding(24.dp),
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    } else {
      val verticalScrollState = rememberScrollState()
      val horizontalScrollState = rememberScrollState()

      Row(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.background)
          .verticalScroll(verticalScrollState)
      ) {
        // Line number gutter
        Column(
          modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 12.dp, horizontal = 10.dp)
            .fillMaxHeight(),
          horizontalAlignment = Alignment.End
        ) {
          for (i in 1..maxOf(1, lineCount)) {
            Text(
              text = "$i",
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
              lineHeight = 20.sp
            )
          }
        }

        // Editable Code Text Area
        Box(
          modifier = Modifier
            .weight(1f)
            .horizontalScroll(horizontalScrollState)
            .padding(12.dp)
        ) {
          BasicTextField(
            value = editorText,
            onValueChange = onTextChanged,
            textStyle = androidx.compose.ui.text.TextStyle(
              fontFamily = FontFamily.Monospace,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onBackground,
              lineHeight = 20.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("code_editor_input")
          )
        }
      }
    }

    // Mobile Symbol Quick Bar
    MobileSymbolQuickBar(
      onSymbolClick = onSymbolClick
    )

    // Quick Test Run Action
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 1.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Text(
          text = "Universal Traffic: Test & Verify",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
          onClick = onJumpToConsole,
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("btn_quick_test")
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
          Spacer(modifier = Modifier.width(4.dp))
          Text("Test in Console", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }
  }
}
