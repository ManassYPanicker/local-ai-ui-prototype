package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CodeSnippet
import com.example.ui.theme.CodeBlockBackground
import com.example.ui.theme.CodeBlockBorder
import com.example.ui.theme.CodeTextStyle
import com.example.ui.theme.Teal400
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CodeBlockView(
  snippet: CodeSnippet,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var isCopied by remember { mutableStateOf(false) }

  val lines = remember(snippet.code) { snippet.code.lines() }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(CodeBlockBackground)
      .border(1.dp, CodeBlockBorder, RoundedCornerShape(12.dp))
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF131A29))
        .padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.DataObject,
          contentDescription = null,
          tint = Teal400,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = snippet.language.uppercase(),
          style = MaterialTheme.typography.labelMedium,
          color = Color(0xFFCBD5E1),
          fontFamily = FontFamily.Monospace
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        AnimatedVisibility(
          visible = isCopied,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Text(
            text = "Copied",
            style = MaterialTheme.typography.labelSmall,
            color = Teal400,
            modifier = Modifier.padding(end = 4.dp)
          )
        }
        IconButton(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("code", snippet.code)
            clipboard.setPrimaryClip(clip)
            isCopied = true
            coroutineScope.launch {
              delay(2000)
              isCopied = false
            }
          },
          modifier = Modifier
            .size(32.dp)
            .testTag("copy_code_button")
        ) {
          Icon(
            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
            contentDescription = "Copy code",
            tint = if (isCopied) Teal400 else Color(0xFF94A3B8),
            modifier = Modifier.size(15.dp)
          )
        }
      }
    }

    // Code Body with Line Numbers
    val scrollState = rememberScrollState()
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp)
    ) {
      // Line Numbers Column
      Column(
        modifier = Modifier
          .padding(start = 12.dp, end = 12.dp)
      ) {
        lines.indices.forEach { idx ->
          Text(
            text = "${idx + 1}",
            style = CodeTextStyle.copy(
              fontSize = 12.sp,
              lineHeight = 19.sp,
              color = Color(0xFF475569)
            ),
            modifier = Modifier.align(Alignment.End)
          )
        }
      }

      // Code text scrollable horizontally
      Box(
        modifier = Modifier
          .weight(1f)
          .horizontalScroll(scrollState)
          .padding(end = 12.dp)
      ) {
        Column {
          lines.forEach { line ->
            Text(
              text = if (line.isEmpty()) " " else line,
              style = CodeTextStyle.copy(
                color = Color(0xFFE2E8F0),
                fontSize = 12.sp,
                lineHeight = 19.sp
              )
            )
          }
        }
      }
    }
  }
}
