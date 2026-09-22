package com.example.features.console

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.ForgeLanguage
import com.example.core.localization.ForgeStrings
import com.example.core.model.ConsoleEntryEntity
import com.example.core.model.ProjectEntity

@Composable
fun ConsoleScreen(
  project: ProjectEntity?,
  history: List<ConsoleEntryEntity>,
  language: ForgeLanguage,
  onRunCommand: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var commandText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  val quickCommands = listOf(
    "help",
    "ls",
    "pwd",
    "build",
    "test",
    "git status",
    "stat index.html",
    "cat app.js",
    "clear"
  )

  LaunchedEffect(history.size) {
    if (history.isNotEmpty()) {
      listState.animateScrollToItem(history.size - 1)
    }
  }

  Column(modifier = modifier.fillMaxSize()) {
    // Console Header
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
          .padding(horizontal = 14.dp, vertical = 8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Terminal, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = ForgeStrings.get("console_title", language),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Working in: ${project?.name ?: "No project"}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        IconButton(onClick = { onRunCommand("clear") }) {
          Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }

    // Terminal Output Box
    Card(
      colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
      shape = RoundedCornerShape(0.dp),
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
      LazyColumn(
        state = listState,
        modifier = Modifier
          .fillMaxSize()
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          Text(
            text = ForgeStrings.get("console_welcome", language),
            color = Color(0xFF38BDF8),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
        }

        items(history, key = { it.id }) { entry ->
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "forge:~$ ",
                color = Color(0xFFF59E0B),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
              Text(
                text = entry.command,
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
              )
            }
            if (entry.output.isNotEmpty()) {
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = entry.output,
                color = if (entry.isError) Color(0xFFF87171) else Color(0xFF94A3B8),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
              )
            }
          }
        }
      }
    }

    // Quick Command Pills
    Surface(
      color = MaterialTheme.colorScheme.surfaceVariant,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        quickCommands.forEach { cmd ->
          Box(
            modifier = Modifier
              .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
              .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
              .padding(horizontal = 10.dp, vertical = 5.dp)
              .testTag("quick_cmd_$cmd")
          ) {
            Button(
              onClick = { onRunCommand(cmd) },
              colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
              modifier = Modifier.height(20.dp)
            ) {
              Text(
                text = cmd,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // Command Input Row
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)
      ) {
        OutlinedTextField(
          value = commandText,
          onValueChange = { commandText = it },
          placeholder = { Text(ForgeStrings.get("console_hint", language), fontSize = 12.sp) },
          singleLine = true,
          textStyle = androidx.compose.ui.text.TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
          ),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
          keyboardActions = KeyboardActions(
            onSend = {
              if (commandText.isNotBlank()) {
                onRunCommand(commandText)
                commandText = ""
              }
            }
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("input_console_cmd")
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
          onClick = {
            if (commandText.isNotBlank()) {
              onRunCommand(commandText)
              commandText = ""
            }
          },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("btn_send_cmd")
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = "Run")
        }
      }
    }
  }
}
