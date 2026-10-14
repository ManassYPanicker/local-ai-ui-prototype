package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LocalModel
import com.example.ui.theme.Dimens
import com.example.ui.theme.Indigo500
import com.example.ui.theme.StatusError

@Composable
fun ChatComposer(
  text: String,
  onTextChange: (String) -> Unit,
  onSend: () -> Unit,
  onStop: () -> Unit,
  isStreaming: Boolean,
  currentModel: LocalModel,
  onModelClick: () -> Unit,
  suggestedPrompts: List<String> = emptyList(),
  onSelectPrompt: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .imePadding()
      .navigationBarsPadding()
      .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing8)
  ) {
    // Optional prompt suggestions horizontal row when input is empty
    AnimatedVisibility(
      visible = text.isEmpty() && !isStreaming && suggestedPrompts.isNotEmpty(),
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = Dimens.spacing8),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
      ) {
        items(suggestedPrompts) { prompt ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
              .clickable { onSelectPrompt(prompt) }
              .padding(horizontal = Dimens.spacing12, vertical = Dimens.spacing6)
              .testTag("prompt_suggestion_chip")
          ) {
            Text(
              text = prompt,
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }

    // Deliberately designed floating composer capsule
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(22.dp))
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
          shape = RoundedCornerShape(22.dp)
        ),
      shape = RoundedCornerShape(22.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp
    ) {
      Column(
        modifier = Modifier.padding(
          start = Dimens.spacing16,
          end = Dimens.spacing8,
          top = Dimens.spacing10,
          bottom = Dimens.spacing8
        )
      ) {
        // Multiline Text Input field
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 28.dp, max = 110.dp)
        ) {
          if (text.isEmpty()) {
            Text(
              text = "Ask a question...",
              style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontSize = 15.sp,
                lineHeight = 22.sp
              )
            )
          }

          BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("chat_input_field"),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 15.sp,
              lineHeight = 22.sp
            ),
            cursorBrush = SolidColor(Indigo500),
            maxLines = 5
          )
        }

        Spacer(modifier = Modifier.height(Dimens.spacing6))

        // Bottom Controls: Compact Model Selector & Action Button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Minimal Model Chip
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable { onModelClick() }
              .padding(horizontal = Dimens.spacing8, vertical = Dimens.spacing4)
              .testTag("model_selector_chip"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = currentModel.name,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
              imageVector = Icons.Default.KeyboardArrowDown,
              contentDescription = "Switch model",
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              modifier = Modifier.size(15.dp)
            )
          }

          // Send / Stop Action Button
          if (isStreaming) {
            IconButton(
              onClick = onStop,
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(StatusError.copy(alpha = 0.15f))
                .testTag("stop_generation_button")
            ) {
              Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Stop generating",
                tint = StatusError,
                modifier = Modifier.size(17.dp)
              )
            }
          } else {
            val canSend = text.isNotBlank()
            IconButton(
              onClick = { if (canSend) onSend() },
              enabled = canSend,
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                  if (canSend) Indigo500 else Color.Transparent
                )
                .testTag("send_message_button")
            ) {
              Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Send",
                tint = if (canSend) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}
