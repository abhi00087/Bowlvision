package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BowlingDeliveryAnalysis
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerCard(
  analysis: BowlingDeliveryAnalysis,
  modifier: Modifier = Modifier
) {
  val speedProgress = (analysis.releaseSpeedKmph / 160.0).toFloat().coerceIn(0f, 1f)
  val animatedProgress by animateFloatAsState(
    targetValue = speedProgress,
    animationSpec = tween(durationMillis = 900),
    label = "speed_gauge"
  )

  val speedCategoryColor = when {
    analysis.releaseSpeedKmph >= 140.0 -> MaterialTheme.colorScheme.primary // Express Fast (#D0BCFF)
    analysis.releaseSpeedKmph >= 125.0 -> MaterialTheme.colorScheme.secondary // Fast-Medium (#CCC2DC)
    else -> MaterialTheme.colorScheme.tertiary // Medium / Spin (#FFD8E4)
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
      .padding(12.dp)
      .testTag("speedometer_card")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Speed,
          contentDescription = "Radar Speed",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "BALL SPEED & RADAR",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.6.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Box(
        modifier = Modifier
          .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
          .padding(horizontal = 7.dp, vertical = 3.dp)
      ) {
        Text(
          text = if (analysis.releaseSpeedKmph >= 140) "EXPRESS PACE" else if (analysis.releaseSpeedKmph >= 125) "FAST" else "SPIN / ACCURACY",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Gauge and Big Metrics
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Circular Arc Speed Gauge
      Box(
        modifier = Modifier.size(105.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.size(95.dp)) {
          val strokeWidth = 9.dp.toPx()
          val startAngle = 140f
          val sweepAngle = 260f

          // Background track
          drawArc(
            color = Color(0x3349454F),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )

          // Active gradient gauge arc
          drawArc(
            brush = Brush.sweepGradient(
              listOf(Color(0xFFCCC2DC), Color(0xFFD0BCFF))
            ),
            startAngle = startAngle,
            sweepAngle = sweepAngle * animatedProgress,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )

          // Indicator needle dot
          val needleAngle = Math.toRadians((startAngle + sweepAngle * animatedProgress).toDouble())
          val r = (size.width - strokeWidth) / 2f
          val cx = size.width / 2f + (r * cos(needleAngle)).toFloat()
          val cy = size.height / 2f + (r * sin(needleAngle)).toFloat()
          drawCircle(
            color = Color.White,
            radius = 4.5.dp.toPx(),
            center = Offset(cx, cy)
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "${analysis.releaseSpeedKmph.toInt()}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "KM/H",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 10.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Speed Details Columns
      Column(modifier = Modifier.weight(1f)) {
        // MPH equivalent
        MetricRow(
          label = "Miles / Hour",
          value = "${String.format("%.1f", analysis.releaseSpeedMph)} mph"
        )
        Spacer(modifier = Modifier.height(5.dp))
        // Post-bounce Speed
        MetricRow(
          label = "Post-Bounce",
          value = "${String.format("%.1f", analysis.postBounceSpeedKmph)} km/h"
        )
        Spacer(modifier = Modifier.height(5.dp))
        // Flight time & RPM
        val flightTimeMs = analysis.reachTimeMs - analysis.releaseTimeMs
        MetricRow(
          label = "Flight Time",
          value = "${flightTimeMs} ms"
        )
      }
    }
  }
}

@Composable
private fun MetricRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
