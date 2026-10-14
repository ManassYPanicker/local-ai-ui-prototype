package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
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
import com.example.model.PairedComputer
import com.example.model.PcConnectionStatus
import com.example.ui.theme.Dimens
import com.example.ui.theme.Indigo500
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusDisconnected
import com.example.ui.theme.StatusOffline

@Composable
fun ConnectionStatusCard(
  connectionStatus: PcConnectionStatus,
  computer: PairedComputer?,
  onToggleDemoState: () -> Unit,
  onPairNewComputer: () -> Unit,
  onManageComputers: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Clean, unified surface with quiet elegance and minimal nesting
  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(Dimens.cardRadius),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    tonalElevation = 1.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(Dimens.spacing16)
    ) {
      // Main row: Icon + Computer Identity + Status Dot
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(
                when (connectionStatus) {
                  PcConnectionStatus.CONNECTED -> StatusConnected.copy(alpha = 0.12f)
                  PcConnectionStatus.CONNECTING,
                  PcConnectionStatus.DISCONNECTED -> StatusDisconnected.copy(alpha = 0.12f)
                  PcConnectionStatus.NOT_PAIRED -> StatusOffline.copy(alpha = 0.12f)
                }
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Computer,
              contentDescription = null,
              tint = when (connectionStatus) {
                PcConnectionStatus.CONNECTED -> StatusConnected
                PcConnectionStatus.CONNECTING,
                PcConnectionStatus.DISCONNECTED -> StatusDisconnected
                PcConnectionStatus.NOT_PAIRED -> StatusOffline
              },
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(Dimens.spacing12))

          Column {
            Text(
              text = when (connectionStatus) {
                PcConnectionStatus.CONNECTED -> computer?.name ?: "Studio Desktop"
                PcConnectionStatus.CONNECTING -> computer?.name ?: "Studio Desktop"
                PcConnectionStatus.DISCONNECTED -> computer?.name ?: "Studio Desktop"
                PcConnectionStatus.NOT_PAIRED -> "No Computer Paired"
              },
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(
                    when (connectionStatus) {
                      PcConnectionStatus.CONNECTED -> StatusConnected
                      PcConnectionStatus.CONNECTING,
                      PcConnectionStatus.DISCONNECTED -> StatusDisconnected
                      PcConnectionStatus.NOT_PAIRED -> StatusOffline
                    }
                  )
              )
              Spacer(modifier = Modifier.width(Dimens.spacing6))
              Text(
                text = when (connectionStatus) {
                  PcConnectionStatus.CONNECTED -> computer?.statusDescription ?: "Ready for requests"
                  PcConnectionStatus.CONNECTING -> "Connecting..."
                  PcConnectionStatus.DISCONNECTED -> "Disconnected"
                  PcConnectionStatus.NOT_PAIRED -> "Ready to pair"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Quiet, unobtrusive prototype preview state toggle
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggleDemoState() }
            .padding(horizontal = Dimens.spacing8, vertical = Dimens.spacing4)
            .testTag("toggle_demo_state_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Demo state",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(Dimens.spacing4))
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Toggle simulated state",
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              modifier = Modifier.size(12.dp)
            )
          }
        }
      }

      // Secondary technical details (quiet presentation) or direct action
      AnimatedContent(
        targetState = connectionStatus,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "connectionSubContent"
      ) { status ->
        when (status) {
          PcConnectionStatus.CONNECTED -> {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.spacing10),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Paired PC Connector • Private Transport",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
              )
              Text(
                text = "Manage",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Medium,
                  color = Indigo500
                ),
                modifier = Modifier
                  .clickable { onManageComputers() }
                  .padding(Dimens.spacing4)
              )
            }
          }
          PcConnectionStatus.CONNECTING -> {
            Text(
              text = "Connecting to companion connector...",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(top = Dimens.spacing8)
            )
          }
          PcConnectionStatus.DISCONNECTED -> {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.spacing10),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "PC is unreachable. Ensure connector is running.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
              )
              Text(
                text = "Retry",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = Indigo500
                ),
                modifier = Modifier
                  .clickable { onToggleDemoState() }
                  .padding(start = Dimens.spacing12, top = Dimens.spacing4, bottom = Dimens.spacing4)
                  .testTag("reconnect_button")
              )
            }
          }
          PcConnectionStatus.NOT_PAIRED -> {
            ElevatedButton(
              onClick = onPairNewComputer,
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.spacing12)
                .testTag("pair_first_pc_button"),
              colors = ButtonDefaults.elevatedButtonColors(
                containerColor = Indigo500,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(Dimens.spacing8))
              Text("Pair Personal Computer")
            }
          }
        }
      }
    }
  }
}
