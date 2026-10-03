package com.example.ui.about

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.brand.AppIconView
import com.example.ui.theme.AmiriFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler {
    onNavigateBack()
  }

  var selectedQualityBgTab by remember { mutableIntStateOf(0) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Quran by Word English",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("about_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    modifier = modifier
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // Brand Hero Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .border(
              width = 1.dp,
              color = MaterialTheme.colorScheme.outlineVariant,
              shape = RoundedCornerShape(22.dp)
            )
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            AppIconView(size = 96.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = "القرآن الكريم",
              fontFamily = AmiriFontFamily,
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Quran by Word English",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "Read  •  Understand  •  Reflect",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.secondary,
              letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = "Complete 114 Surahs & 30 Juz Offline",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        }
      }

      // Implemented Features Summary
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(
              width = 1.dp,
              color = MaterialTheme.colorScheme.outlineVariant,
              shape = RoundedCornerShape(18.dp)
            )
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "IMPLEMENTED CORE FEATURES",
              style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.secondary
            )
          }

          FeatureRow(
            icon = Icons.Default.Translate,
            title = "Word-by-Word Translation",
            description = "Individual chips for every Arabic word paired with its contextual English meaning and transliteration."
          )
          FeatureRow(
            icon = Icons.Default.MenuBook,
            title = "Full Contextual Translation",
            description = "Complete Sahih International English translation for thorough comprehension of every verse."
          )
          FeatureRow(
            icon = Icons.Default.ViewCarousel,
            title = "Continuous & Swipe Reading Modes",
            description = "Easily toggle between continuous vertical scrolling or focused single-verse horizontal pagination."
          )
          FeatureRow(
            icon = Icons.Default.Speed,
            title = "Automated Smooth Scroll",
            description = "Hands-free auto-scrolling with play/pause and incremental speed controls from 0.5x to 3.0x."
          )
          FeatureRow(
            icon = Icons.Default.Bookmark,
            title = "Bookmarks & Last Read Tracking",
            description = "Persistent local Room database storage for bookmarked verses and automatic progress resumption."
          )
          FeatureRow(
            icon = Icons.Default.ColorLens,
            title = "Light & Dark Themes",
            description = "A warm spiritual light palette and an obsidian-emerald dark palette with persistent preferences."
          )
          FeatureRow(
            icon = Icons.Default.OfflinePin,
            title = "100% Offline Access",
            description = "All 6,236 verses across all 30 Juz and 114 Surahs load entirely from local device storage."
          )
        }
      }

      // Quality Check: App Icon & Brand Showcase across sizes and backgrounds
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(
              width = 1.dp,
              color = MaterialTheme.colorScheme.outlineVariant,
              shape = RoundedCornerShape(18.dp)
            )
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ICON QUALITY CHECK & CONTRAST TEST",
              style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.secondary
            )
          }

          Text(
            text = "Verified at 120dp large display, 72dp tablet launcher, and 48dp standard launcher across light, dark, and emerald wallpapers:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          // Background selector tabs: Light / Dark / Colored
          TabRow(
            selectedTabIndex = selectedQualityBgTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
          ) {
            Tab(
              selected = selectedQualityBgTab == 0,
              onClick = { selectedQualityBgTab = 0 },
              text = { Text("Light BG") }
            )
            Tab(
              selected = selectedQualityBgTab == 1,
              onClick = { selectedQualityBgTab = 1 },
              text = { Text("Dark BG") }
            )
            Tab(
              selected = selectedQualityBgTab == 2,
              onClick = { selectedQualityBgTab = 2 },
              text = { Text("Twilight BG") }
            )
          }

          val previewBgColor = when (selectedQualityBgTab) {
            0 -> Color(0xFFF6F3EB)
            1 -> Color(0xFF101614)
            else -> Color(0xFF081017)
          }

          // Icon preview canvas
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(previewBgColor)
              .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
              .padding(20.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
              // Large size (120dp)
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppIconView(size = 112.dp, isCircular = false)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Large (Store / Showcase)",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (selectedQualityBgTab == 0) Color(0xFF333333) else Color(0xFFD4AF37)
                )
              }

              // Smaller Launcher sizes (72dp and 48dp) - both square and circular
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  AppIconView(size = 64.dp, isCircular = false)
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "72dp Squircle",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = if (selectedQualityBgTab == 0) Color(0xFF444444) else Color(0xFFCCCCCC)
                  )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  AppIconView(size = 64.dp, isCircular = true)
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "72dp Round",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = if (selectedQualityBgTab == 0) Color(0xFF444444) else Color(0xFFCCCCCC)
                  )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  AppIconView(size = 48.dp, isCircular = false)
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "48dp Standard",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = if (selectedQualityBgTab == 0) Color(0xFF444444) else Color(0xFFCCCCCC)
                  )
                }
              }
            }
          }
        }
      }

      // Play Store Feature Graphic & Screenshots Showcase
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(
              width = 1.dp,
              color = MaterialTheme.colorScheme.outlineVariant,
              shape = RoundedCornerShape(18.dp)
            )
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.MenuBook,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "GOOGLE PLAY ASSETS & CAPTIONS",
              style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.secondary
            )
          }

          // Feature Graphic (1024x500)
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_feature_graphic),
              contentDescription = "1024x500 Feature Graphic",
              modifier = Modifier.fillMaxWidth(),
              contentScale = ContentScale.FillWidth
            )
          }

          Text(
            text = "Feature Graphic (1024x500): Pure vector-rendered Google Play banner featuring Quran by Word English luxury branding.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Google Play Store Captions (4 Screenshots):",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )

          ScreenshotCaptionItem(
            number = "1",
            title = "Word-by-Word Meanings",
            description = "Understand every Arabic word with precise translations and transliteration chips."
          )
          ScreenshotCaptionItem(
            number = "2",
            title = "Full Contextual Translation",
            description = "Clear Sahih International English translation for full contextual clarity."
          )
          ScreenshotCaptionItem(
            number = "3",
            title = "Continuous & Swipe Reading Modes",
            description = "Seamless vertical scrolling or focused single-verse reading tailored to your pace."
          )
          ScreenshotCaptionItem(
            number = "4",
            title = "Comfortable Dark Theme",
            description = "A soothing deep emerald and obsidian palette for serene nighttime contemplation."
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun FeatureRow(
  icon: ImageVector,
  title: String,
  description: String,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp)
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun ScreenshotCaptionItem(
  number: String,
  title: String,
  description: String,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(RoundedCornerShape(6.dp))
        .background(MaterialTheme.colorScheme.secondary),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = number,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp
      )
    }
  }
}
