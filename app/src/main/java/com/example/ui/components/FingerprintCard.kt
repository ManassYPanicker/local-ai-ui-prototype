package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Dimens
import com.example.ui.theme.Teal500

@Composable
fun FingerprintCard(
  fingerprint: String,
  computerName: String,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(Dimens.cardRadius))
      .background(Color(0xFF0F172A))
      .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(Dimens.cardRadius))
      .padding(Dimens.spacing16)
      .testTag("fingerprint_verification_card")
  ) {
    // Security Badge Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Teal500.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = null,
            tint = Teal500,
            modifier = Modifier.size(16.dp)
          )
        }
        Spacer(modifier = Modifier.width(Dimens.spacing8))
        Text(
          text = "PC Identity Fingerprint",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = Color(0xFFE2E8F0)
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(Color(0xFF1E293B))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = "Identity Key",
          style = MaterialTheme.typography.labelSmall,
          color = Teal500,
          fontFamily = FontFamily.Monospace
        )
      }
    }

    Spacer(modifier = Modifier.height(Dimens.spacing12))

    // Formatted Fingerprint Blocks
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF090D16))
        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
        .padding(Dimens.spacing12)
    ) {
      Text(
        text = fingerprint,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp,
          lineHeight = 18.sp,
          color = Color(0xFF38BDF8)
        )
      )
    }

    Spacer(modifier = Modifier.height(Dimens.spacing10))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Workstation: $computerName",
        style = MaterialTheme.typography.labelSmall,
        color = Color(0xFF94A3B8)
      )
      Text(
        text = "Enrollment verification",
        style = MaterialTheme.typography.labelSmall,
        color = Color(0xFF94A3B8)
      )
    }
  }
}
