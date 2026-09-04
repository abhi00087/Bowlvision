package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BowlingDeliveryAnalysis

@Composable
fun MilestoneTimelineCard(
  analysis: BowlingDeliveryAnalysis,
  currentTimeMs: Long,
  onSeekToMs: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
      .padding(12.dp)
      .testTag("milestone_timeline_card")
  ) {
    // Header with Auto-detection badge
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "AUTO-DETECTED MILESTONES",
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
          text = "100% AUTOMATIC",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
      }
    }

    Text(
      text = "Release point, bounce, and reach point detected automatically without manual tagging.",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 11.sp
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Milestone Nodes
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // 1. Release Point
      MilestoneItem(
        title = "1. Release",
        timeSec = "${String.format("%.2f", analysis.releaseTimeMs / 1000f)}s",
        desc = "${analysis.armAngleDeg.toInt()}° arm • ${analysis.releaseHeightM}m",
        badge = "RELEASE",
        badgeColor = MaterialTheme.colorScheme.primary,
        isActive = currentTimeMs in (analysis.releaseTimeMs - 100)..(analysis.releaseTimeMs + 100),
        onClick = { onSeekToMs(analysis.releaseTimeMs) },
        modifier = Modifier.weight(1f)
      )

      // 2. Pitch / Bounce Point
      MilestoneItem(
        title = "2. Pitch Bounce",
        timeSec = "${String.format("%.2f", analysis.bounceTimeMs / 1000f)}s",
        desc = "${analysis.pitchLength} • ${analysis.pitchDistanceM}m",
        badge = "PITCH",
        badgeColor = MaterialTheme.colorScheme.tertiary,
        isActive = currentTimeMs in (analysis.bounceTimeMs - 100)..(analysis.bounceTimeMs + 100),
        onClick = { onSeekToMs(analysis.bounceTimeMs) },
        modifier = Modifier.weight(1f)
      )

      // 3. Reach / Crease Point
      MilestoneItem(
        title = "3. Crease",
        timeSec = "${String.format("%.2f", analysis.reachTimeMs / 1000f)}s",
        desc = "${analysis.postBounceSpeedKmph.toInt()} km/h arrival",
        badge = "REACH",
        badgeColor = MaterialTheme.colorScheme.secondary,
        isActive = currentTimeMs in (analysis.reachTimeMs - 100)..(analysis.reachTimeMs + 100),
        onClick = { onSeekToMs(analysis.reachTimeMs) },
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun MilestoneItem(
  title: String,
  timeSec: String,
  desc: String,
  badge: String,
  badgeColor: Color,
  isActive: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val borderColor = if (isActive) badgeColor else MaterialTheme.colorScheme.outline
  val bgColor = if (isActive) badgeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface

  Box(
    modifier = modifier
      .background(bgColor, RoundedCornerShape(8.dp))
      .border(1.dp, borderColor, RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(7.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
          Text(
            text = badge,
            style = MaterialTheme.typography.labelSmall,
            color = badgeColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Seek",
          tint = if (isActive) badgeColor else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(13.dp)
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = timeSec,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = if (isActive) badgeColor else MaterialTheme.colorScheme.onSurface,
        fontSize = 13.sp
      )

      Text(
        text = desc,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 9.sp,
        maxLines = 2,
        lineHeight = 12.sp
      )
    }
  }
}
