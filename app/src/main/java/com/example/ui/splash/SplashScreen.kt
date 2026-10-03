package com.example.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.brand.AppIconView
import com.example.ui.theme.AmiriFontFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  onSplashFinished: () -> Unit,
  modifier: Modifier = Modifier
) {
  val alphaAnim = remember { Animatable(0f) }
  val scaleAnim = remember { Animatable(0.94f) }

  LaunchedEffect(Unit) {
    // Elegant, calm fade-in under 1.2 seconds
    alphaAnim.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
    )
    scaleAnim.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
    )
    delay(350)
    onSplashFinished()
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("splash_screen")
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF0F1B26),
            Color(0xFF081017),
            Color(0xFF03060A)
          )
        )
      )
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        onSplashFinished()
      },
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .alpha(alphaAnim.value)
        .scale(scaleAnim.value)
        .padding(horizontal = 28.dp)
    ) {
      // New Luxury Brand Mark
      AppIconView(size = 118.dp)

      Spacer(modifier = Modifier.height(28.dp))

      // Arabic Calligraphic Title
      Text(
        text = "القرآن الكريم",
        fontFamily = AmiriFontFamily,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFDFC07C)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Official App Name: "Quran by Word English"
      Text(
        text = "Quran by Word English",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = Color.White,
        letterSpacing = 0.4.sp,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Brand Tagline: "Read. Understand. Reflect."
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF142230).copy(alpha = 0.7f))
          .padding(horizontal = 18.dp, vertical = 7.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Read",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color(0xFFFAF6ED)
          )
          Text(
            text = "  •  ",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFDFC07C)
          )
          Text(
            text = "Understand",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color(0xFFFAF6ED)
          )
          Text(
            text = "  •  ",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFDFC07C)
          )
          Text(
            text = "Reflect",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color(0xFFFAF6ED)
          )
        }
      }
    }
  }
}
