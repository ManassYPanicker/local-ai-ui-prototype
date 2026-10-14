package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes =
  Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(30.dp),
  )

val BubbleUserShape = RoundedCornerShape(
  topStart = 20.dp,
  topEnd = 20.dp,
  bottomStart = 20.dp,
  bottomEnd = 4.dp
)

val BubbleAssistantShape = RoundedCornerShape(
  topStart = 20.dp,
  topEnd = 20.dp,
  bottomStart = 4.dp,
  bottomEnd = 20.dp
)
