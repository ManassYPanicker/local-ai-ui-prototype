package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.model.PcConnectionStatus
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusDisconnected
import com.example.ui.theme.StatusOffline

@Composable
fun ConnectionStatusBadge(
  status: PcConnectionStatus,
  modifier: Modifier = Modifier,
  showLabel: Boolean = true
) {
  val (dotColor, bgColor, textColor, label) = when (status) {
    PcConnectionStatus.CONNECTED -> Quad(
      StatusConnected,
      StatusConnected.copy(alpha = 0.15f),
      StatusConnected,
      "Connected"
    )
    PcConnectionStatus.CONNECTING -> Quad(
      StatusDisconnected,
      StatusDisconnected.copy(alpha = 0.15f),
      StatusDisconnected,
      "Connecting"
    )
    PcConnectionStatus.DISCONNECTED -> Quad(
      StatusDisconnected,
      StatusDisconnected.copy(alpha = 0.15f),
      StatusDisconnected,
      "Disconnected"
    )
    PcConnectionStatus.NOT_PAIRED -> Quad(
      StatusOffline,
      StatusOffline.copy(alpha = 0.15f),
      StatusOffline,
      "No PC Paired"
    )
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Box(
      modifier = Modifier
        .size(7.dp)
        .clip(CircleShape)
        .background(dotColor)
    )
    if (showLabel) {
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = textColor
      )
    }
  }
}

@Composable
fun PrototypeDemoBadge(
  modifier: Modifier = Modifier,
  text: String = "Prototype Demo Data"
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant)
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

private data class Quad<A, B, C, D>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D
)
