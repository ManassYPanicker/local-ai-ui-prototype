package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DemoDataProvider
import com.example.model.PairedComputer
import com.example.ui.components.FingerprintCard
import com.example.ui.theme.Dimens
import com.example.ui.theme.Indigo500
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.Teal500
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PairingScreen(
  onBackClick: () -> Unit,
  onPairingComplete: (PairedComputer) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBackClick() }

  val coroutineScope = rememberCoroutineScope()
  var currentStep by remember { mutableIntStateOf(0) }
  val targetComputer = remember { DemoDataProvider.sampleComputers.first() }

  var scanMethodTab by remember { mutableIntStateOf(0) } // 0: QR Code, 1: Manual lmspair payload
  var manualPayload by remember { mutableStateOf("lmspair:v1:desk-9f2a?key=7F4A91E28851302C") }
  var isEnrolling by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding()
      .imePadding()
      .padding(horizontal = Dimens.spacing20)
  ) {
    // Focused Top Navigation Bar (Predictable Back and Cancel)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = Dimens.spacing8),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      IconButton(
        onClick = {
          if (currentStep > 0) {
            currentStep -= 1
          } else {
            onBackClick()
          }
        },
        modifier = Modifier.size(40.dp)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = MaterialTheme.colorScheme.onSurface
        )
      }

      Text(
        text = "Pair Computer",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )

      IconButton(
        onClick = onBackClick,
        modifier = Modifier.size(40.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Cancel",
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Step Progress Indicator
    PairingStepIndicator(
      currentStep = currentStep,
      modifier = Modifier.padding(vertical = Dimens.spacing8)
    )

    Spacer(modifier = Modifier.height(Dimens.spacing12))

    // Focused Step Content in Scrollable Column (Guaranteed no overflow on small screens)
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .verticalScroll(scrollState)
    ) {
      AnimatedContent(
        targetState = currentStep,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "pairingStepContent"
      ) { step ->
        when (step) {
          0 -> StageConnectComputer(
            onContinue = { currentStep = 1 }
          )
          1 -> StageScanOrEnterPayload(
            scanMethod = scanMethodTab,
            onScanMethodChange = { scanMethodTab = it },
            payloadValue = manualPayload,
            onPayloadChange = { manualPayload = it },
            onContinue = { currentStep = 2 }
          )
          2 -> StageVerifyIdentity(
            computer = targetComputer,
            onConfirm = {
              currentStep = 3
              coroutineScope.launch {
                isEnrolling = true
                delay(1200) // Simulated enrollment progression
                isEnrolling = false
              }
            },
            onReject = { currentStep = 1 }
          )
          3 -> StageEnrollmentComplete(
            computer = targetComputer,
            isEnrolling = isEnrolling,
            onDone = { onPairingComplete(targetComputer) }
          )
        }
      }
    }
  }
}

@Composable
private fun PairingStepIndicator(
  currentStep: Int,
  modifier: Modifier = Modifier
) {
  val stepTitles = listOf("1. Connect", "2. Scan", "3. Verify", "4. Complete")
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    stepTitles.forEachIndexed { index, title ->
      val isActive = index == currentStep
      val isDone = index < currentStep
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
          fontSize = 12.sp
        ),
        color = when {
          isActive -> Indigo500
          isDone -> MaterialTheme.colorScheme.onSurface
          else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        }
      )
    }
  }
}

@Composable
private fun StageConnectComputer(
  onContinue: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = Dimens.spacing8)
  ) {
    Text(
      text = "Open PC Connector",
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(Dimens.spacing8))
    Text(
      text = "Ensure LM Studio is running on your computer, open the companion connector, and choose 'Pair New Device' to display your pairing QR code or payload.",
      style = MaterialTheme.typography.bodyLarge.copy(
        lineHeight = 22.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    )

    Spacer(modifier = Modifier.height(Dimens.spacing28))

    ElevatedButton(
      onClick = onContinue,
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("pairing_step1_next_button"),
      colors = ButtonDefaults.elevatedButtonColors(
        containerColor = Indigo500,
        contentColor = Color.White
      ),
      shape = RoundedCornerShape(12.dp)
    ) {
      Text("I Have the Pairing Code Ready", fontWeight = FontWeight.SemiBold)
    }
  }
}

@Composable
private fun StageScanOrEnterPayload(
  scanMethod: Int,
  onScanMethodChange: (Int) -> Unit,
  payloadValue: String,
  onPayloadChange: (String) -> Unit,
  onContinue: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = Dimens.spacing8)
  ) {
    Text(
      text = "Scan QR or Enter Payload",
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(Dimens.spacing6))
    Text(
      text = "Scan the QR code displayed on your PC or paste the lmspair:v1 payload below.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(Dimens.spacing16))

    TabRow(
      selectedTabIndex = scanMethod,
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
      contentColor = Indigo500
    ) {
      Tab(
        selected = scanMethod == 0,
        onClick = { onScanMethodChange(0) },
        text = { Text("Scan QR Code") },
        icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp)) }
      )
      Tab(
        selected = scanMethod == 1,
        onClick = { onScanMethodChange(1) },
        text = { Text("Manual Payload") },
        icon = { Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp)) }
      )
    }

    Spacer(modifier = Modifier.height(Dimens.spacing20))

    if (scanMethod == 0) {
      // Quiet viewfinder mock
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF0A0F1D))
          .border(1.5.dp, Teal500.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(130.dp)
            .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.QrCode,
            contentDescription = null,
            tint = Color(0xFF38BDF8).copy(alpha = 0.35f),
            modifier = Modifier.size(75.dp)
          )

          val transition = rememberInfiniteTransition(label = "laser")
          val laserY by transition.animateFloat(
            initialValue = -50f,
            targetValue = 50f,
            animationSpec = infiniteRepeatable(
              animation = tween(1400),
              repeatMode = RepeatMode.Reverse
            ),
            label = "laserY"
          )

          Box(
            modifier = Modifier
              .width(110.dp)
              .height(2.dp)
              .offset(y = laserY.dp)
              .background(Teal500)
          )
        }
      }
    } else {
      // Real protocol compliance: accepts lmspair:v1 payload
      Column {
        OutlinedTextField(
          value = payloadValue,
          onValueChange = onPayloadChange,
          label = { Text("Pairing Payload or Code") },
          placeholder = { Text("e.g. lmspair:v1:desk-9f2a?key=...") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("manual_pairing_code_input"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true,
          textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Indigo500
          )
        )
        Spacer(modifier = Modifier.height(Dimens.spacing6))
        Text(
          text = "Supports standard lmspair:v1 enrollment payloads generated by the connector.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(Dimens.spacing24))

    ElevatedButton(
      onClick = onContinue,
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("simulate_detection_button"),
      colors = ButtonDefaults.elevatedButtonColors(
        containerColor = Indigo500,
        contentColor = Color.White
      ),
      shape = RoundedCornerShape(12.dp)
    ) {
      Text("Continue", fontWeight = FontWeight.SemiBold)
    }
  }
}

@Composable
private fun StageVerifyIdentity(
  computer: PairedComputer,
  onConfirm: () -> Unit,
  onReject: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = Dimens.spacing8)
  ) {
    Text(
      text = "Verify Computer Identity",
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(Dimens.spacing6))
    Text(
      text = "Compare the identity fingerprint with the one displayed on your computer:",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(Dimens.spacing16))

    // Summary Card
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
      Column(modifier = Modifier.padding(Dimens.spacing14)) {
        Text(
          text = computer.name,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "PC Connector companion endpoint",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(Dimens.spacing14))

    // Prominent PC Identity Fingerprint Card
    FingerprintCard(
      fingerprint = computer.pcIdentityFingerprint,
      computerName = computer.name
    )

    Spacer(modifier = Modifier.height(Dimens.spacing24))

    ElevatedButton(
      onClick = onConfirm,
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("confirm_fingerprint_button"),
      colors = ButtonDefaults.elevatedButtonColors(
        containerColor = Indigo500,
        contentColor = Color.White
      ),
      shape = RoundedCornerShape(12.dp)
    ) {
      Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(Dimens.spacing8))
      Text("Confirm Identity & Pair", fontWeight = FontWeight.SemiBold)
    }

    Spacer(modifier = Modifier.height(Dimens.spacing8))

    OutlinedButton(
      onClick = onReject,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp)
    ) {
      Text("Does Not Match")
    }
  }
}

@Composable
private fun StageEnrollmentComplete(
  computer: PairedComputer,
  isEnrolling: Boolean,
  onDone: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = Dimens.spacing24),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    if (isEnrolling) {
      CircularProgressIndicator(
        modifier = Modifier.size(52.dp),
        color = Indigo500,
        strokeWidth = 3.dp
      )
      Spacer(modifier = Modifier.height(Dimens.spacing20))
      Text(
        text = "Enrolling Device...",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(Dimens.spacing6))
      Text(
        text = "Registering credentials with ${computer.name}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    } else {
      Box(
        modifier = Modifier
          .size(68.dp)
          .clip(CircleShape)
          .background(StatusConnected.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          tint = StatusConnected,
          modifier = Modifier.size(44.dp)
        )
      }

      Spacer(modifier = Modifier.height(Dimens.spacing16))

      Text(
        text = "Computer Paired",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(Dimens.spacing8))

      Text(
        text = "Your phone is now linked with ${computer.name}. Inquiries will route to your personal models.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = Dimens.spacing20)
      )

      Spacer(modifier = Modifier.height(Dimens.spacing32))

      ElevatedButton(
        onClick = onDone,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("start_chatting_after_pairing_button"),
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = Indigo500,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Done", fontWeight = FontWeight.SemiBold)
      }
    }
  }
}
