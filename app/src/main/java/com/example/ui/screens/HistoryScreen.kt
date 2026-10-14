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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Conversation
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.Dimens
import com.example.ui.theme.Indigo500
import com.example.ui.theme.StatusError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
  conversations: List<Conversation>,
  onOpenConversation: (Conversation) -> Unit,
  onBackClick: () -> Unit,
  onNewChat: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBackClick() }

  val conversationList = remember(conversations) {
    mutableStateListOf<Conversation>().apply { addAll(conversations) }
  }

  var searchQuery by remember { mutableStateOf("") }
  var selectedModelFilter by remember { mutableStateOf<String?>(null) }
  var conversationToDelete by remember { mutableStateOf<Conversation?>(null) }

  val filteredConversations = conversationList.filter { conv ->
    val matchesSearch = searchQuery.isBlank() ||
      conv.title.contains(searchQuery, ignoreCase = true) ||
      conv.previewText.contains(searchQuery, ignoreCase = true)

    val matchesFilter = selectedModelFilter == null || conv.modelName.contains(selectedModelFilter!!, ignoreCase = true)

    matchesSearch && matchesFilter
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = Dimens.spacing8, vertical = Dimens.spacing8),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBackClick,
        modifier = Modifier.size(40.dp)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = MaterialTheme.colorScheme.onSurface
        )
      }
      Spacer(modifier = Modifier.width(Dimens.spacing4))
      Text(
        text = "Conversation History",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    // Search Box
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = Dimens.spacing16)
        .testTag("history_search_field"),
      placeholder = {
        Text(
          "Search conversations...",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
          )
        )
      },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp)
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear search", modifier = Modifier.size(16.dp))
          }
        }
      },
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Indigo500,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
      ),
      singleLine = true
    )

    // Filter Chips
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing8),
      horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
    ) {
      item {
        FilterChip(
          selected = selectedModelFilter == null,
          onClick = { selectedModelFilter = null },
          label = { Text("All Models") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Indigo500,
            selectedLabelColor = Color.White
          )
        )
      }
      val filters = listOf("Qwen", "DeepSeek", "Llama")
      items(filters) { modelKey ->
        FilterChip(
          selected = selectedModelFilter == modelKey,
          onClick = {
            selectedModelFilter = if (selectedModelFilter == modelKey) null else modelKey
          },
          label = { Text(modelKey) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Indigo500,
            selectedLabelColor = Color.White
          )
        )
      }
    }

    // Conversation List or Empty State (No heavy card boxes, airy list items)
    if (filteredConversations.isEmpty()) {
      EmptyStateView(
        icon = Icons.Default.ChatBubbleOutline,
        title = if (searchQuery.isEmpty()) "No Conversations Yet" else "No Matching Sessions",
        description = if (searchQuery.isEmpty()) {
          "Start your first session to begin talking with your local computer models."
        } else {
          "No chats found matching '$searchQuery'."
        },
        actionLabel = "Start New Chat",
        onActionClick = onNewChat
      )
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = Dimens.spacing16)
      ) {
        items(filteredConversations, key = { it.id }) { conv ->
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .clickable { onOpenConversation(conv) }
              .testTag("history_item_${conv.id}"),
            color = Color.Transparent
          ) {
            Column(
              modifier = Modifier.padding(vertical = Dimens.spacing12, horizontal = Dimens.spacing4)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = conv.title,
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                  ),
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.weight(1f),
                  maxLines = 1
                )
                Spacer(modifier = Modifier.width(Dimens.spacing8))
                Text(
                  text = conv.timestamp,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
              }

              Spacer(modifier = Modifier.height(Dimens.spacing4))

              Text(
                text = conv.previewText,
                style = MaterialTheme.typography.bodySmall.copy(
                  lineHeight = 18.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 2
              )

              Spacer(modifier = Modifier.height(Dimens.spacing8))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = conv.modelName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                IconButton(
                  onClick = { conversationToDelete = conv },
                  modifier = Modifier
                    .size(28.dp)
                    .testTag("delete_conversation_${conv.id}")
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete conversation",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }

          HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
            thickness = 0.5.dp
          )
        }
      }
    }
  }

  // Safe Deletion Confirmation Dialog
  if (conversationToDelete != null) {
    val target = conversationToDelete!!
    AlertDialog(
      onDismissRequest = { conversationToDelete = null },
      title = { Text("Delete Conversation?") },
      text = {
        Text("Delete '${target.title}'? This action cannot be undone.")
      },
      confirmButton = {
        TextButton(
          onClick = {
            conversationList.remove(target)
            conversationToDelete = null
          }
        ) {
          Text("Delete", color = StatusError)
        }
      },
      dismissButton = {
        TextButton(onClick = { conversationToDelete = null }) {
          Text("Cancel")
        }
      }
    )
  }
}
