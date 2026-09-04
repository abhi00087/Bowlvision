package com.example.ui.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
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
fun ActionBiomechanicsCard(
  analysis: BowlingDeliveryAnalysis,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
      .padding(12.dp)
      .testTag("action_biomechanics_card")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = "Biomechanics",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "ACTION BIOMECHANICS",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.6.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // ICC 15° Legality Badge
      val legalColor = if (analysis.isElbowLegal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
      Box(
        modifier = Modifier
          .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
          .padding(horizontal = 7.dp, vertical = 3.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (analysis.isElbowLegal) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (analysis.isElbowLegal) "ICC LEGAL (${analysis.elbowExtensionDeg}°)" else "EXT. ALERT (${analysis.elbowExtensionDeg}°)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Grid of biomechanic indicators
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      BioTile(
        title = "ACTION TYPE",
        value = analysis.actionType,
        subtitle = "Alignment",
        modifier = Modifier.weight(1f)
      )
      BioTile(
        title = "ARM ELEVATION",
        value = "${analysis.armAngleDeg}°",
        subtitle = "Release angle",
        modifier = Modifier.weight(1f)
      )
      BioTile(
        title = "RELEASE HT",
        value = "${analysis.releaseHeightM}m",
        subtitle = "Above ground",
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Ball movement & deviation details
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      BioTile(
        title = "DEVIATION",
        value = analysis.movementType,
        subtitle = "${analysis.deviationCm} cm deviation",
        highlightColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.weight(1.2f)
      )
      BioTile(
        title = "BOUNCE HT",
        value = "${String.format("%.2f", analysis.bounceHeightM)}m",
        subtitle = "At crease",
        modifier = Modifier.weight(0.8f)
      )
      BioTile(
        title = "SPIN RATE",
        value = "${analysis.rpmEstimate}",
        subtitle = "Est. RPM",
        modifier = Modifier.weight(0.8f)
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // AI Coaching verdict box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
        .padding(8.dp)
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "AUTOMATED BIOMECHANICAL VERDICT",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 0.4.sp,
            fontSize = 10.sp
          )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = analysis.coachingNotes,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 11.sp,
          lineHeight = 15.sp
        )
      }
    }
  }
}

@Composable
private fun BioTile(
  title: String,
  value: String,
  subtitle: String,
  modifier: Modifier = Modifier,
  highlightColor: Color? = null
) {
  Box(
    modifier = modifier
      .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
      .padding(7.dp)
  ) {
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.4.sp
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
        color = highlightColor ?: MaterialTheme.colorScheme.onSurface,
        fontSize = 12.sp,
        maxLines = 1
      )
      Spacer(modifier = Modifier.height(1.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 9.sp,
        maxLines = 1
      )
    }
  }
}
