package com.example.ui.brand

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun AppIconView(
  size: Dp,
  isCircular: Boolean = false,
  modifier: Modifier = Modifier
) {
  val shape = if (isCircular) CircleShape else RoundedCornerShape(size * 0.22f)

  Box(
    modifier = modifier
      .size(size)
      .clip(shape)
      .border(
        width = (size * 0.018f).coerceAtLeast(1.dp),
        color = Color(0xFFDFC07C).copy(alpha = 0.45f),
        shape = shape
      )
      .background(Color(0xFF081017)),
    contentAlignment = Alignment.Center
  ) {
    // Background layer
    Image(
      painter = painterResource(id = R.drawable.ic_launcher_background),
      contentDescription = null,
      modifier = Modifier.fillMaxSize()
    )
    // Foreground luxury artwork layer
    Image(
      painter = painterResource(id = R.drawable.ic_launcher_foreground),
      contentDescription = "Quran by Word English App Icon",
      modifier = Modifier.fillMaxSize()
    )
  }
}
