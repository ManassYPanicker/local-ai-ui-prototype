package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DemoDataProvider
import com.example.model.ChatMessage
import com.example.model.Conversation
import com.example.model.LocalModel
import com.example.ui.components.ChatBubble
import com.example.ui.components.ChatComposer
import com.example.ui.components.ModelSelectorSheet
import com.example.ui.theme.Dimens
import com.example.ui.theme.Teal500
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
  conversation: Conversation?,
  availableModels: List<LocalModel>,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBackClick() }

  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  var inputText by remember { mutableStateOf("") }
  var isStreaming by remember { mutableStateOf(false) }
  var streamingJob by remember { mutableStateOf<Job?>(null) }
  var showModelSelector by remember { mutableStateOf(false) }
  var showOptionsMenu by remember { mutableStateOf(false) }
  var showClearDialog by remember { mutableStateOf(false) }

  var activeModel by remember {
    mutableStateOf(
      availableModels.firstOrNull { it.isLoaded } ?: availableModels.first()
    )
  }

  // Active messages list
  val messages = remember(conversation?.id) {
    mutableStateListOf<ChatMessage>().apply {
      if (conversation != null) {
        addAll(conversation.messages)
      }
    }
  }

  // Stable scrolling during streaming and on message additions
  LaunchedEffect(messages.size, messages.lastOrNull()?.text?.length) {
    if (messages.isNotEmpty()) {
      listState.scrollToItem(messages.size - 1)
    }
  }

  val suggestedPrompts = remember {
    listOf(
      "How does LM Studio serve local models?",
      "Show a quick Python script to query the local server",
      "Explain KV-cache quantization tradeoffs"
    )
  }

  // Simulated prototype response generator (strictly isolated demonstration)
  fun triggerStreamResponse(userQuery: String) {
    if (isStreaming) return
    isStreaming = true

    val userMsg = ChatMessage(
      id = "user_${System.currentTimeMillis()}",
      isUser = true,
      text = userQuery,
      timestamp = "Just now"
    )
    messages.add(userMsg)
    inputText = ""

    val fullResponse = DemoDataProvider.sampleStreamingPromptResponses[userQuery]
      ?: "This is a demonstration response using **${activeModel.name}**.\n\nIn the production application, requests route through the companion PC connector over encrypted transport to your PC's localhost LM Studio instance."

    val assistantMsgId = "assistant_${System.currentTimeMillis()}"
    val assistantMsg = ChatMessage(
      id = assistantMsgId,
      isUser = false,
      text = "",
      timestamp = "Just now",
      modelUsed = activeModel.name,
      isStreaming = true
    )
    messages.add(assistantMsg)

    streamingJob = coroutineScope.launch {
      val words = fullResponse.split(" ")
      val builder = StringBuilder()
      for (word in words) {
        builder.append(word).append(" ")
        val lastIdx = messages.indexOfFirst { it.id == assistantMsgId }
        if (lastIdx != -1) {
          messages[lastIdx] = assistantMsg.copy(
            text = builder.toString().trimEnd(),
            isStreaming = true
          )
        }
        delay(40) // Reading cadence for prototype preview
      }
      val lastIdx = messages.indexOfFirst { it.id == assistantMsgId }
      if (lastIdx != -1) {
        messages[lastIdx] = messages[lastIdx].copy(isStreaming = false)
      }
      isStreaming = false
    }
  }

  Scaffold(
    topBar = {
      // Minimal, quiet conversation header
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .statusBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacing8, vertical = Dimens.spacing4),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            IconButton(
              onClick = onBackClick,
              modifier = Modifier
                .size(40.dp)
                .testTag("chat_back_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
              )
            }

            Spacer(modifier = Modifier.width(Dimens.spacing4))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = conversation?.title ?: "New Session",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
              )

              // Quiet model tag with picker action
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .clickable { showModelSelector = true }
                  .testTag("header_model_picker")
              ) {
                Text(
                  text = activeModel.name,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                  imageVector = Icons.Default.KeyboardArrowDown,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(13.dp)
                )
              }
            }
          }

          // Options menu
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { showOptionsMenu = true },
              modifier = Modifier
                .size(40.dp)
                .testTag("chat_more_options")
            ) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
            }

            DropdownMenu(
              expanded = showOptionsMenu,
              onDismissRequest = { showOptionsMenu = false }
            ) {
              DropdownMenuItem(
                text = { Text("Model Catalog") },
                onClick = {
                  showOptionsMenu = false
                  showModelSelector = true
                },
                leadingIcon = {
                  Icon(Icons.Default.Memory, contentDescription = null, tint = Teal500)
                }
              )
              DropdownMenuItem(
                text = { Text("Clear Messages") },
                onClick = {
                  showOptionsMenu = false
                  showClearDialog = true
                },
                leadingIcon = {
                  Icon(Icons.Default.DeleteOutline, contentDescription = null)
                }
              )
            }
          }
        }

        HorizontalDivider(
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
          thickness = 0.5.dp
        )
      }
    },
    bottomBar = {
      ChatComposer(
        text = inputText,
        onTextChange = { inputText = it },
        onSend = {
          if (inputText.isNotBlank()) {
            triggerStreamResponse(inputText.trim())
          }
        },
        onStop = {
          streamingJob?.cancel()
          isStreaming = false
          val lastIdx = messages.indexOfLast { !it.isUser }
          if (lastIdx != -1) {
            messages[lastIdx] = messages[lastIdx].copy(isStreaming = false)
          }
        },
        isStreaming = isStreaming,
        currentModel = activeModel,
        onModelClick = { showModelSelector = true },
        suggestedPrompts = if (messages.size <= 2) suggestedPrompts else emptyList(),
        onSelectPrompt = { prompt ->
          triggerStreamResponse(prompt)
        }
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    // Spacious message list area — conversation is the primary visual focus
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      verticalArrangement = Arrangement.spacedBy(Dimens.spacing4)
    ) {
      item {
        Spacer(modifier = Modifier.height(Dimens.spacing8))
      }

      items(messages, key = { it.id }) { message ->
        ChatBubble(message = message)
      }

      item {
        Spacer(modifier = Modifier.height(Dimens.spacing16))
      }
    }
  }

  // Model Selector Bottom Sheet
  if (showModelSelector) {
    ModelSelectorSheet(
      models = availableModels,
      selectedModel = activeModel,
      onModelSelected = { activeModel = it },
      onDismiss = { showModelSelector = false }
    )
  }

  // Clear dialog
  if (showClearDialog) {
    AlertDialog(
      onDismissRequest = { showClearDialog = false },
      title = { Text("Clear Messages?") },
      text = { Text("This will reset messages in this demonstration session.") },
      confirmButton = {
        TextButton(
          onClick = {
            messages.clear()
            showClearDialog = false
          }
        ) {
          Text("Clear", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
