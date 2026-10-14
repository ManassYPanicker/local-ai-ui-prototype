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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DemoDataProvider
import com.example.model.PairedComputer
import com.example.ui.theme.Dimens
import com.example.ui.theme.Indigo500
import com.example.ui.theme.StatusError
import com.example.ui.theme.Teal500

enum class AppThemeMode {
  SYSTEM,
  LIGHT,
  DARK
}

@Composable
fun SettingsScreen(
  currentThemeMode: AppThemeMode,
  onThemeModeChange: (AppThemeMode) -> Unit,
  onBackClick: () -> Unit,
  onNavigateToPairing: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBackClick() }

  val pairedComputers = remember {
    mutableStateListOf<PairedComputer>().apply { addAll(DemoDataProvider.sampleComputers) }
  }

  var showLicensesDialog by remember { mutableStateOf(false) }
  var showPrivacyDialog by remember { mutableStateOf(false) }
  var showAboutDialog by remember { mutableStateOf(false) }
  var computerToForget by remember { mutableStateOf<PairedComputer?>(null) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
      .padding(horizontal = Dimens.spacing20),
    verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = Dimens.spacing8),
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
          text = "Settings",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    // 1. Appearance Section
    item {
      SettingsSectionHeader(title = "Appearance", icon = Icons.Default.Palette)
    }

    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
      ) {
        Column(modifier = Modifier.padding(Dimens.spacing14)) {
          Text(
            text = "Theme Preference",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(Dimens.spacing10))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
          ) {
            listOf(
              AppThemeMode.SYSTEM to "System",
              AppThemeMode.LIGHT to "Light",
              AppThemeMode.DARK to "Dark"
            ).forEach { (mode, label) ->
              val isSelected = currentThemeMode == mode
              Surface(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { onThemeModeChange(mode) }
                  .testTag("theme_button_${label.lowercase()}"),
                color = if (isSelected) Indigo500 else MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.spacing8),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }
        }
      }
    }

    // 2. Connected Computers Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        SettingsSectionHeader(title = "Paired Computers", icon = Icons.Default.Computer)
        Text(
          text = "+ Pair New",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = Indigo500,
          modifier = Modifier
            .clickable { onNavigateToPairing() }
            .padding(Dimens.spacing4)
            .testTag("settings_pair_new_link")
        )
      }
    }

    items(pairedComputers) { computer ->
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spacing12),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = computer.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              if (computer.isCurrent) {
                Spacer(modifier = Modifier.width(Dimens.spacing8))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Teal500.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Teal500
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = computer.statusDescription,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = { computerToForget = computer },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Forget computer",
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }

    // 3. Architecture & Privacy Information
    item {
      SettingsSectionHeader(title = "Connection Architecture", icon = Icons.Default.Security)
    }

    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .clickable { showPrivacyDialog = true },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spacing14),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Personal Computer Transport",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Inference is executed by your paired computer. No developer cloud inference backend is used.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Details",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // 4. About & Licenses
    item {
      SettingsSectionHeader(title = "About", icon = Icons.Default.Info)
    }

    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
      ) {
        Column {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showAboutDialog = true }
              .padding(Dimens.spacing14),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Local AI Assistant",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
            thickness = 0.5.dp
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showLicensesDialog = true }
              .padding(Dimens.spacing14),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Open Source Licenses",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
              imageVector = Icons.Default.Description,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(Dimens.spacing32))
    }
  }

  // Dialogs
  if (showPrivacyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyDialog = false },
      title = { Text("Connection Architecture") },
      text = {
        Text(
          "How this application connects to your computer:\n\n" +
            "• Paired Transport: The phone connects to your PC connector companion service over encrypted transport.\n" +
            "• Local LM Studio Forwarding: The connector communicates with LM Studio at localhost:1234 on your computer.\n" +
            "• Personal Models: Model processing runs on your own hardware rather than a developer-hosted cloud backend."
        )
      },
      confirmButton = {
        TextButton(onClick = { showPrivacyDialog = false }) {
          Text("Done")
        }
      }
    )
  }

  if (showLicensesDialog) {
    AlertDialog(
      onDismissRequest = { showLicensesDialog = false },
      title = { Text("Open Source Licenses") },
      text = {
        Text(
          "• Jetpack Compose & Material 3 (Apache 2.0)\n" +
            "• AndroidX Lifecycle (Apache 2.0)\n" +
            "• Kotlin Coroutines (Apache 2.0)"
        )
      },
      confirmButton = {
        TextButton(onClick = { showLicensesDialog = false }) {
          Text("Close")
        }
      }
    )
  }

  if (showAboutDialog) {
    AlertDialog(
      onDismissRequest = { showAboutDialog = false },
      title = { Text("Local AI Assistant") },
      text = {
        Text(
          "LM Studio Remote Visual Prototype\n" +
            "Designed for personal AI models running on your computer.\n\n" +
            "Package: com.lmstudio.remote.prototype"
        )
      },
      confirmButton = {
        TextButton(onClick = { showAboutDialog = false }) {
          Text("OK")
        }
      }
    )
  }

  if (computerToForget != null) {
    val comp = computerToForget!!
    AlertDialog(
      onDismissRequest = { computerToForget = null },
      title = { Text("Forget Computer?") },
      text = { Text("Remove '${comp.name}'? You can re-pair at any time.") },
      confirmButton = {
        TextButton(
          onClick = {
            pairedComputers.remove(comp)
            computerToForget = null
          }
        ) {
          Text("Forget", color = StatusError)
        }
      },
      dismissButton = {
        TextButton(onClick = { computerToForget = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun SettingsSectionHeader(
  title: String,
  icon: ImageVector
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(top = Dimens.spacing8, bottom = Dimens.spacing2)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = Indigo500,
      modifier = Modifier.size(15.dp)
    )
    Spacer(modifier = Modifier.width(Dimens.spacing8))
    Text(
      text = title,
      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
