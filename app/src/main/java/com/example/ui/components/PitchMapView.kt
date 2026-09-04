package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BowlingDeliveryAnalysis
import kotlin.math.roundToInt

@Composable
fun PitchMapView(
  analysis: BowlingDeliveryAnalysis,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pitch_bounce_pulse")
  val pulseRadius by infiniteTransition.animateFloat(
    initialValue = 8f,
    targetValue = 22f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse_radius"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse_alpha"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
      .padding(12.dp)
      .testTag("pitch_map_view")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "2D PITCH MAP & HAWKEYE",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.6.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = "${analysis.pitchLength} • ${analysis.pitchLine}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )
      }

      Box(
        modifier = Modifier
          .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
          .padding(horizontal = 7.dp, vertical = 3.dp)
      ) {
        Text(
          text = "${analysis.pitchDistanceM}m from bowler",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Pitch Canvas
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(230.dp)
        .background(Color(0xFF161E1A), RoundedCornerShape(10.dp))
        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
        .padding(8.dp)
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(214.dp)
      ) {
        drawCricketPitch(
          analysis = analysis,
          pulseRadius = pulseRadius,
          pulseAlpha = pulseAlpha
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Zone Legend
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      LegendItem(color = Color(0xFFFFD8E4), label = "Yorker")
      LegendItem(color = Color(0xFFCCC2DC), label = "Full")
      LegendItem(color = Color(0xFFD0BCFF), label = "Good Length")
      LegendItem(color = Color(0xFF938F99), label = "Short")
    }
  }
}

private fun DrawScope.drawCricketPitch(
  analysis: BowlingDeliveryAnalysis,
  pulseRadius: Float,
  pulseAlpha: Float
) {
  val w = size.width
  val h = size.height

  // Pitch turf rectangle (centered vertically, standard perspective)
  val pitchLeft = w * 0.22f
  val pitchRight = w * 0.78f
  val pitchWidth = pitchRight - pitchLeft
  val pitchTop = h * 0.08f
  val pitchBottom = h * 0.92f
  val pitchHeight = pitchBottom - pitchTop

  // Turf clay base
  drawRoundRect(
    color = Color(0xFFC2A379), // Light clay/turf
    topLeft = Offset(pitchLeft, pitchTop),
    size = Size(pitchWidth, pitchHeight),
    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
  )

  // Subtle turf texture strips
  val stripStep = pitchWidth / 6
  for (i in 0..6) {
    drawLine(
      color = Color(0x18000000),
      start = Offset(pitchLeft + i * stripStep, pitchTop),
      end = Offset(pitchLeft + i * stripStep, pitchBottom),
      strokeWidth = 1f
    )
  }

  // Pitch Length Zones (from bowler at top to batsman at bottom)
  // Short length (0.08 to 0.40)
  val shortY = pitchTop + pitchHeight * 0.35f
  val backLengthY = pitchTop + pitchHeight * 0.52f
  val goodLengthY = pitchTop + pitchHeight * 0.72f
  val fullY = pitchTop + pitchHeight * 0.86f
  val battingCreaseY = pitchTop + pitchHeight * 0.90f

  // Draw transparent colored overlays for zones
  drawRect(
    color = Color(0x35FFB300),
    topLeft = Offset(pitchLeft, pitchTop),
    size = Size(pitchWidth, shortY - pitchTop)
  )
  drawRect(
    color = Color(0x30FFD54F),
    topLeft = Offset(pitchLeft, shortY),
    size = Size(pitchWidth, backLengthY - shortY)
  )
  // Good Length Zone (Target green)
  drawRect(
    color = Color(0x4500E676),
    topLeft = Offset(pitchLeft, backLengthY),
    size = Size(pitchWidth, goodLengthY - backLengthY)
  )
  // Full Zone
  drawRect(
    color = Color(0x3500E5FF),
    topLeft = Offset(pitchLeft, goodLengthY),
    size = Size(pitchWidth, fullY - goodLengthY)
  )
  // Yorker Zone
  drawRect(
    color = Color(0x45FF5252),
    topLeft = Offset(pitchLeft, fullY),
    size = Size(pitchWidth, battingCreaseY - fullY)
  )

  // White Crease Lines
  val creasePaintColor = Color.White
  // Bowling crease (top)
  drawLine(
    color = creasePaintColor,
    start = Offset(pitchLeft - 10f, pitchTop + 14f),
    end = Offset(pitchRight + 10f, pitchTop + 14f),
    strokeWidth = 2.dp.toPx()
  )
  // Batting Popping Crease (bottom)
  drawLine(
    color = creasePaintColor,
    start = Offset(pitchLeft - 15f, battingCreaseY),
    end = Offset(pitchRight + 15f, battingCreaseY),
    strokeWidth = 2.5.dp.toPx()
  )

  // Center stump line (dashed)
  drawLine(
    color = Color(0x55FFFFFF),
    start = Offset(w * 0.5f, pitchTop),
    end = Offset(w * 0.5f, pitchBottom),
    strokeWidth = 1f,
    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
  )

  // Stumps at batting end (bottom)
  val stumpsY = battingCreaseY + 8f
  val stumpSpacing = 8f
  val centerX = w * 0.5f
  // 3 stumps (Leg, Middle, Off)
  for (offset in listOf(-stumpSpacing, 0f, stumpSpacing)) {
    drawCircle(
      color = Color(0xFFFFD54F),
      radius = 3.dp.toPx(),
      center = Offset(centerX + offset, stumpsY)
    )
  }
  // Bails
  drawLine(
    color = Color(0xFFFFD54F),
    start = Offset(centerX - stumpSpacing - 2f, stumpsY),
    end = Offset(centerX + stumpSpacing + 2f, stumpsY),
    strokeWidth = 2f
  )

  // Calculate ball bounce coordinates on 2D pitch
  // pitchLateralM maps [-1.0m, +1.0m] to pitch width
  val normX = (analysis.bounceX - 0.5f) * 1.6f
  val landingX = (centerX + normX * (pitchWidth * 0.45f)).coerceIn(pitchLeft + 10f, pitchRight - 10f)

  // Distance: 0m (bowler) to 20.12m (stumps)
  val distRatio = (analysis.pitchDistanceM / 20.12).toFloat().coerceIn(0.15f, 0.95f)
  val landingY = pitchTop + distRatio * pitchHeight

  // Post-bounce trajectory line towards stumps
  val reachNormX = (analysis.reachX - 0.5f) * 1.6f
  val reachLandingX = (centerX + reachNormX * (pitchWidth * 0.45f)).coerceIn(pitchLeft + 10f, pitchRight - 10f)

  // Draw deviation line
  val path = Path().apply {
    moveTo(landingX, landingY)
    lineTo(reachLandingX, stumpsY)
  }
  drawPath(
    path = path,
    color = Color(0xFF00E676),
    style = Stroke(width = 2.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f)))
  )

  // Animated pulse ring at landing point
  drawCircle(
    color = Color(0xFFFF1744).copy(alpha = pulseAlpha),
    radius = pulseRadius * 1.5f,
    center = Offset(landingX, landingY),
    style = Stroke(width = 2.dp.toPx())
  )

  // Landing Spot marker (Red Cricket Ball)
  drawCircle(
    color = Color(0xFFFF1744),
    radius = 6.dp.toPx(),
    center = Offset(landingX, landingY)
  )
  drawCircle(
    color = Color.White,
    radius = 2.dp.toPx(),
    center = Offset(landingX - 1.5f, landingY - 1.5f)
  )

  // Target impact at stumps
  drawCircle(
    color = Color(0xFF00E5FF),
    radius = 4.dp.toPx(),
    center = Offset(reachLandingX, stumpsY)
  )
}

@Composable
private fun LegendItem(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(10.dp)
        .background(color, CircleShape)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 11.sp
    )
  }
}
