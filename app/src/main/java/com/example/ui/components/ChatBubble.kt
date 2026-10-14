package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.ui.theme.BubbleUserShape
import com.example.ui.theme.Dimens
import com.example.ui.theme.Teal500
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatBubble(
  message: ChatMessage,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var isCopied by remember { mutableStateOf(false) }

  if (message.isUser) {
    // Restrained, attractive user message bubble
    Row(
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing8),
      horizontalArrangement = Arrangement.End
    ) {
      Box(
        modifier = Modifier
          .widthIn(max = 500.dp)
          .clip(BubbleUserShape)
          .background(MaterialTheme.colorScheme.primary)
          .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing12)
          .testTag("user_message_bubble")
      ) {
        Text(
          text = message.text,
          style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onPrimary,
            lineHeight = 23.sp
          )
        )
      }
    }
  } else {
    // Spacious assistant message with minimal container (editorial reading layout)
    Column(
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing10)
        .testTag("assistant_message_container")
    ) {
      // Quiet Model Attribution Header
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = Dimens.spacing6)
      ) {
        Box(
          modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Teal500.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Memory,
            contentDescription = null,
            tint = Teal500,
            modifier = Modifier.size(11.dp)
          )
        }
        Spacer(modifier = Modifier.width(Dimens.spacing6))
        Text(
          text = message.modelUsed ?: "Local Model",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Main Text Body (Direct text flow with rich line height, no heavy enclosed box)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 2.dp, end = Dimens.spacing8)
      ) {
        Text(
          text = message.text,
          style = MaterialTheme.typography.bodyLarge.copy(
            lineHeight = 24.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        )

        // Subtle, quiet generating cursor
        if (message.isStreaming) {
          Spacer(modifier = Modifier.height(Dimens.spacing6))
          QuietGeneratingCursor()
        }

        // Embedded Code Snippet if present
        if (message.codeSnippet != null) {
          Spacer(modifier = Modifier.height(Dimens.spacing12))
          CodeBlockView(snippet = message.codeSnippet)
        }

        // Action Row: Quiet copy button with visual feedback
        Spacer(modifier = Modifier.height(Dimens.spacing6))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          AnimatedVisibility(visible = isCopied, enter = fadeIn(), exit = fadeOut()) {
            Text(
              text = "Copied",
              style = MaterialTheme.typography.labelSmall,
              color = Teal500,
              modifier = Modifier.padding(end = Dimens.spacing4)
            )
          }
          IconButton(
            onClick = {
              val fullContent = buildString {
                append(message.text)
                if (message.codeSnippet != null) {
                  append("\n\n```${message.codeSnippet.language}\n")
                  append(message.codeSnippet.code)
                  append("\n```")
                }
              }
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("response", fullContent)
              clipboard.setPrimaryClip(clip)
              isCopied = true
              coroutineScope.launch {
                delay(2000)
                isCopied = false
              }
            },
            modifier = Modifier
              .size(28.dp)
              .testTag("copy_message_button")
          ) {
            Icon(
              imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
              contentDescription = "Copy message",
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun QuietGeneratingCursor() {
  val transition = rememberInfiniteTransition(label = "pulse")
  val alpha by transition.animateFloat(
    initialValue = 0.2f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(600),
      repeatMode = RepeatMode.Reverse
    ),
    label = "cursorAlpha"
  )
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .alpha(alpha)
        .clip(CircleShape)
        .background(Teal500)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = "Thinking...",
      style = MaterialTheme.typography.labelSmall,
      color = Teal500.copy(alpha = 0.8f)
    )
  }
}
