package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Conversation
import com.example.model.PairedComputer
import com.example.model.PcConnectionStatus
import com.example.ui.components.ConnectionStatusCard
import com.example.ui.theme.Dimens
import com.example.ui.theme.Indigo500

@Composable
fun HomeScreen(
  connectionStatus: PcConnectionStatus,
  currentComputer: PairedComputer?,
  recentConversations: List<Conversation>,
  onToggleDemoState: () -> Unit,
  onNewChat: () -> Unit,
  onOpenConversation: (Conversation) -> Unit,
  onNavigateToHistory: () -> Unit,
  onNavigateToPairing: () -> Unit,
  onNavigateToSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
      .padding(horizontal = Dimens.spacing20),
    verticalArrangement = Arrangement.spacedBy(Dimens.spacing20)
  ) {
    // 1. Refined App Header with Generous Breathing Room
    item {
      Spacer(modifier = Modifier.height(Dimens.spacing8))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Personal Workspace",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = (-0.3).sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(Dimens.spacing4))
          Text(
            text = "Local AI models powered by your computer",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(
          onClick = onNavigateToSettings,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .testTag("settings_button")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // 2. Simplified Computer Connection Card
    item {
      ConnectionStatusCard(
        connectionStatus = connectionStatus,
        computer = currentComputer,
        onToggleDemoState = onToggleDemoState,
        onPairNewComputer = onNavigateToPairing,
        onManageComputers = onNavigateToSettings
      )
    }

    // 3. Obvious & Inviting New Chat Action
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(Dimens.cardRadius))
          .clickable { onNewChat() }
          .testTag("start_new_chat_card"),
        shape = RoundedCornerShape(Dimens.cardRadius),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacing20, vertical = Dimens.spacing18),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Indigo500),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(Dimens.spacing14))
            Column {
              Text(
                text = "New Conversation",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Ask a question or explore code",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Start conversation",
            tint = Indigo500,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }

    // 4. Recent Conversations Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = Dimens.spacing8),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Recent Sessions",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = "View all",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = Indigo500,
          modifier = Modifier
            .clickable { onNavigateToHistory() }
            .padding(Dimens.spacing4)
            .testTag("view_all_history_link")
        )
      }
    }

    // Recent items: Airy, light editorial presentation without heavy card borders
    items(recentConversations.take(3)) { conversation ->
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .clickable { onOpenConversation(conversation) }
          .testTag("recent_conversation_${conversation.id}"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
      ) {
        Column(
          modifier = Modifier.padding(horizontal = Dimens.spacing14, vertical = Dimens.spacing12)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = conversation.title,
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
              text = conversation.timestamp,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
          }

          Spacer(modifier = Modifier.height(Dimens.spacing4))

          Text(
            text = conversation.previewText,
            style = MaterialTheme.typography.bodySmall.copy(
              lineHeight = 18.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            maxLines = 2
          )

          Spacer(modifier = Modifier.height(Dimens.spacing8))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = conversation.modelName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

    item {
      Spacer(modifier = Modifier.height(Dimens.spacing24))
    }
  }
}
