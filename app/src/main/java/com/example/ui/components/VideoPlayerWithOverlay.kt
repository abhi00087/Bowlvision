package com.example.ui.components

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.data.model.BowlingDeliveryAnalysis
import com.example.data.model.TrajectoryPoint
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerWithOverlay(
  analysis: BowlingDeliveryAnalysis,
  currentTimeMs: Long,
  isPlaying: Boolean,
  playbackSpeed: Float,
  onPlaybackPositionChanged: (Long) -> Unit,
  onPlayPauseToggle: (Boolean) -> Unit,
  onPlaybackSpeedChanged: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Overlay visibility toggles
  var showTrajectory by remember { mutableStateOf(true) }
  var showBallReticle by remember { mutableStateOf(true) }
  var showActionSkeleton by remember { mutableStateOf(true) }
  var showPitchMarkers by remember { mutableStateOf(true) }
  var showOverlayControls by remember { mutableStateOf(false) }

  // Media3 ExoPlayer instance
  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      repeatMode = Player.REPEAT_MODE_ALL
    }
  }

  // Update MediaItem when analysis videoUri changes
  var isRealVideoAvailable by remember { mutableStateOf(false) }

  LaunchedEffect(analysis.videoUri) {
    try {
      if (analysis.videoUri.startsWith("content://") || analysis.videoUri.startsWith("file://") || analysis.videoUri.startsWith("http")) {
        val mediaItem = MediaItem.fromUri(Uri.parse(analysis.videoUri))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        isRealVideoAvailable = true
      } else {
        isRealVideoAvailable = false
      }
    } catch (e: Exception) {
      isRealVideoAvailable = false
    }
  }

  // Sync playback speed
  LaunchedEffect(playbackSpeed) {
    exoPlayer.setPlaybackSpeed(playbackSpeed)
  }

  // Sync play/pause state
  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      exoPlayer.play()
    } else {
      exoPlayer.pause()
    }
  }

  // Progress synchronization ticker for real-time tracking
  LaunchedEffect(isPlaying, isRealVideoAvailable) {
    while (true) {
      if (isPlaying) {
        if (isRealVideoAvailable) {
          val pos = exoPlayer.currentPosition
          onPlaybackPositionChanged(pos)
        } else {
          // Synthetic timeline playback loop matching video duration
          val next = currentTimeMs + (25 * playbackSpeed).toLong()
          if (next >= analysis.videoDurationMs) {
            onPlaybackPositionChanged(0L)
          } else {
            onPlaybackPositionChanged(next)
          }
        }
      }
      delay(25)
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      exoPlayer.release()
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
      .clip(RoundedCornerShape(14.dp))
      .testTag("video_player_with_overlay")
  ) {
    // Video viewport container with overlaid analytics
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
        .background(Color(0xFF0F1014))
        .pointerInput(Unit) {
          detectTapGestures(
            onTap = {
              onPlayPauseToggle(!isPlaying)
            }
          )
        }
    ) {
      // 1. Video Player Surface
      if (isRealVideoAvailable) {
        AndroidView(
          factory = { ctx ->
            PlayerView(ctx).apply {
              player = exoPlayer
              useController = false
            }
          },
          modifier = Modifier.fillMaxSize()
        )
      } else {
        // High-fidelity Sports Broadcast Pitch Simulation Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
          drawCricketBroadcastScene(
            analysis = analysis,
            currentTimeMs = currentTimeMs
          )
        }
      }

      // 2. Augmented Analytics Overlay Canvas
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 2A. Complete Ball Trajectory Path Overlay
        if (showTrajectory) {
          drawBallTrajectoryOverlay(
            analysis = analysis,
            currentTimeMs = currentTimeMs,
            w = w,
            h = h
          )
        }

        // 2B. Pitch Markers (Release, Bounce, Reach)
        if (showPitchMarkers) {
          drawPitchEventMarkers(
            analysis = analysis,
            currentTimeMs = currentTimeMs,
            w = w,
            h = h
          )
        }

        // 2C. Bowler Action Skeleton (Visible near release frame)
        if (showActionSkeleton && abs(currentTimeMs - analysis.releaseTimeMs) < 350) {
          drawBowlerActionSkeleton(
            analysis = analysis,
            currentTimeMs = currentTimeMs,
            w = w,
            h = h
          )
        }

        // 2D. Real-time Ball Tracking Reticle (Dynamic Position at current frame)
        if (showBallReticle) {
          drawLiveBallReticle(
            analysis = analysis,
            currentTimeMs = currentTimeMs,
            w = w,
            h = h
          )
        }
      }

      // 3. In-Video HUD (Speed, Timecode, Current Phase, and Original / AI Tracking Badges)
      VideoHudOverlay(
        analysis = analysis,
        currentTimeMs = currentTimeMs,
        isPlaying = isPlaying,
        playbackSpeed = playbackSpeed,
        onToggleOverlayMenu = { showOverlayControls = !showOverlayControls }
      )
    }

    // Secondary Overlay Controls Menu (Collapsible)
    AnimatedVisibility(visible = showOverlayControls) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .border(1.dp, MaterialTheme.colorScheme.outline)
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "OVERLAYS:",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
        FilterChip(
          selected = showTrajectory,
          onClick = { showTrajectory = !showTrajectory },
          label = { Text("Trajectory", fontSize = 10.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          )
        )
        FilterChip(
          selected = showBallReticle,
          onClick = { showBallReticle = !showBallReticle },
          label = { Text("Ball Reticle", fontSize = 10.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          )
        )
        FilterChip(
          selected = showActionSkeleton,
          onClick = { showActionSkeleton = !showActionSkeleton },
          label = { Text("Arm Skeleton", fontSize = 10.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          )
        )
      }
    }

    // 4. Playback Scrubber & Controls
    VideoControlBar(
      currentTimeMs = currentTimeMs,
      totalDurationMs = analysis.videoDurationMs,
      isPlaying = isPlaying,
      playbackSpeed = playbackSpeed,
      onSeek = { targetMs ->
        if (isRealVideoAvailable) {
          exoPlayer.seekTo(targetMs)
        }
        onPlaybackPositionChanged(targetMs)
      },
      onPlayPause = { onPlayPauseToggle(!isPlaying) },
      onSpeedChange = onPlaybackSpeedChanged
    )
  }
}

@Composable
private fun VideoHudOverlay(
  analysis: BowlingDeliveryAnalysis,
  currentTimeMs: Long,
  isPlaying: Boolean,
  playbackSpeed: Float,
  onToggleOverlayMenu: () -> Unit
) {
  Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
    // Top Left: High Density Original & AI Tracking Badges + Speed
    Row(
      modifier = Modifier.align(Alignment.TopStart),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Original badge
      Box(
        modifier = Modifier
          .background(Color(0xB3000000), RoundedCornerShape(4.dp))
          .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = "ORIGINAL",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp
        )
      }

      // AI Tracking badge
      Box(
        modifier = Modifier
          .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = "AI TRACKING",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onPrimary,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp
        )
      }

      // Speed Tag
      Box(
        modifier = Modifier
          .background(Color(0xCC1C1B1F), RoundedCornerShape(4.dp))
          .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .background(MaterialTheme.colorScheme.primary, CircleShape)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${analysis.releaseSpeedKmph.toInt()} KM/H",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 9.sp
          )
        }
      }

      // Current Video Phase badge
      val currentPhase = when {
        currentTimeMs < analysis.releaseTimeMs -> "RUN-UP"
        abs(currentTimeMs - analysis.releaseTimeMs) < 80 -> "RELEASE"
        currentTimeMs < analysis.bounceTimeMs -> "FLIGHT"
        abs(currentTimeMs - analysis.bounceTimeMs) < 80 -> "BOUNCE"
        currentTimeMs <= analysis.reachTimeMs -> "CREASE"
        else -> "FOLLOW-THROUGH"
      }

      Box(
        modifier = Modifier
          .background(Color(0xCC000000), RoundedCornerShape(4.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = currentPhase,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          fontSize = 9.sp
        )
      }
    }

    // Top Right: Overlay Layers button & Speedometer indicator
    Row(
      modifier = Modifier.align(Alignment.TopEnd),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (playbackSpeed < 1f) {
        Box(
          modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "${playbackSpeed}x SLOW-MO",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiary,
            fontSize = 9.sp
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
      }

      IconButton(
        onClick = onToggleOverlayMenu,
        modifier = Modifier
          .size(28.dp)
          .background(Color(0xCC1C1B1F), CircleShape)
          .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
      ) {
        Icon(
          imageVector = Icons.Default.Layers,
          contentDescription = "Toggle Overlays",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(15.dp)
        )
      }
    }
  }
}

@Composable
private fun VideoControlBar(
  currentTimeMs: Long,
  totalDurationMs: Long,
  isPlaying: Boolean,
  playbackSpeed: Float,
  onSeek: (Long) -> Unit,
  onPlayPause: () -> Unit,
  onSpeedChange: (Float) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surface)
      .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp))
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    // Scrubber Slider
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = String.format("%.2fs", currentTimeMs / 1000f),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
      )

      Slider(
        value = currentTimeMs.toFloat().coerceIn(0f, totalDurationMs.toFloat()),
        onValueChange = { onSeek(it.toLong()) },
        valueRange = 0f..totalDurationMs.toFloat().coerceAtLeast(100f),
        colors = SliderDefaults.colors(
          thumbColor = MaterialTheme.colorScheme.primary,
          activeTrackColor = MaterialTheme.colorScheme.primary,
          inactiveTrackColor = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
      )

      Text(
        text = String.format("%.2fs", totalDurationMs / 1000f),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // Playback buttons & Slow-motion speed toggles
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Play / Pause / Replay buttons
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = { onSeek(maxOf(0L, currentTimeMs - 100L)) },
          modifier = Modifier.size(34.dp)
        ) {
          Icon(Icons.Default.FastRewind, contentDescription = "Step Back", tint = MaterialTheme.colorScheme.onSurface)
        }

        IconButton(
          onClick = onPlayPause,
          modifier = Modifier
            .size(38.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(22.dp)
          )
        }

        IconButton(
          onClick = { onSeek(minOf(totalDurationMs, currentTimeMs + 100L)) },
          modifier = Modifier.size(34.dp)
        ) {
          Icon(Icons.Default.FastForward, contentDescription = "Step Forward", tint = MaterialTheme.colorScheme.onSurface)
        }

        IconButton(
          onClick = { onSeek(0L) },
          modifier = Modifier.size(34.dp)
        ) {
          Icon(Icons.Default.Replay, contentDescription = "Restart", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }

      // Speed selectors: 0.25x (Super Slow Mo), 0.5x, 1.0x
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        SpeedButton(speed = 0.25f, current = playbackSpeed, onSelect = onSpeedChange)
        SpeedButton(speed = 0.5f, current = playbackSpeed, onSelect = onSpeedChange)
        SpeedButton(speed = 1.0f, current = playbackSpeed, onSelect = onSpeedChange)
      }
    }
  }
}

@Composable
private fun SpeedButton(
  speed: Float,
  current: Float,
  onSelect: (Float) -> Unit
) {
  val isSelected = abs(speed - current) < 0.05f
  Box(
    modifier = Modifier
      .background(
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        RoundedCornerShape(6.dp)
      )
      .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
      .clickable { onSelect(speed) }
      .padding(horizontal = 7.dp, vertical = 4.dp)
  ) {
    Text(
      text = "${speed}x",
      style = MaterialTheme.typography.labelSmall,
      color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      fontSize = 11.sp
    )
  }
}

// Draw methods for overlays
private fun DrawScope.drawCricketBroadcastScene(
  analysis: BowlingDeliveryAnalysis,
  currentTimeMs: Long
) {
  val w = size.width
  val h = size.height

  // Stadium turf gradient
  drawRect(
    brush = Brush.verticalGradient(
      listOf(Color(0xFF0F2618), Color(0xFF1E3F27), Color(0xFF142B1B))
    )
  )

  // 3D Perspective Cricket Pitch
  val pitchTopLeft = Offset(w * 0.40f, h * 0.22f)
  val pitchTopRight = Offset(w * 0.60f, h * 0.22f)
  val pitchBottomRight = Offset(w * 0.82f, h * 0.96f)
  val pitchBottomLeft = Offset(w * 0.18f, h * 0.96f)

  val pitchPath = Path().apply {
    moveTo(pitchTopLeft.x, pitchTopLeft.y)
    lineTo(pitchTopRight.x, pitchTopRight.y)
    lineTo(pitchBottomRight.x, pitchBottomRight.y)
    lineTo(pitchBottomLeft.x, pitchBottomLeft.y)
    close()
  }

  // Pitch surface (turf clay)
  drawPath(
    path = pitchPath,
    color = Color(0xFFBCA074)
  )

  // Crease markings
  // Bowling crease (top)
  drawLine(
    color = Color.White,
    start = Offset(w * 0.36f, h * 0.24f),
    end = Offset(w * 0.64f, h * 0.24f),
    strokeWidth = 2f
  )
  // Batting crease (bottom)
  drawLine(
    color = Color.White,
    start = Offset(w * 0.14f, h * 0.88f),
    end = Offset(w * 0.86f, h * 0.88f),
    strokeWidth = 3f
  )

  // Stumps at batting end
  val stumpsCenterX = w * 0.5f
  val stumpsBaseY = h * 0.88f
  for (i in -1..1) {
    drawLine(
      color = Color(0xFFFFD54F),
      start = Offset(stumpsCenterX + i * 9f, stumpsBaseY),
      end = Offset(stumpsCenterX + i * 9f, stumpsBaseY - 32f),
      strokeWidth = 3.5f
    )
  }
  // Bails
  drawLine(
    color = Color(0xFFFFD54F),
    start = Offset(stumpsCenterX - 11f, stumpsBaseY - 32f),
    end = Offset(stumpsCenterX + 11f, stumpsBaseY - 32f),
    strokeWidth = 2.5f
  )

  // Stumps at bowling end
  for (i in -1..1) {
    drawLine(
      color = Color(0x99FFD54F),
      start = Offset(w * 0.5f + i * 4f, h * 0.23f),
      end = Offset(w * 0.5f + i * 4f, h * 0.23f - 14f),
      strokeWidth = 1.8f
    )
  }

  // Bowler silhouette figure
  drawBowlerSilhouette(w, h, analysis, currentTimeMs)
}

private fun DrawScope.drawBowlerSilhouette(
  w: Float,
  h: Float,
  analysis: BowlingDeliveryAnalysis,
  currentTimeMs: Long
) {
  val relTime = analysis.releaseTimeMs
  // Animate bowler stride
  val strideProgress = ((currentTimeMs - (relTime - 400L)).toFloat() / 600f).coerceIn(0f, 1.2f)

  val bowlerBaseX = w * analysis.releaseX
  val bowlerBaseY = h * (analysis.releaseY + 0.15f)

  // Head
  drawCircle(
    color = Color(0xFFD4E6F1),
    radius = 7.dp.toPx(),
    center = Offset(bowlerBaseX, h * (analysis.releaseY - 0.05f))
  )

  // Torso
  drawLine(
    color = Color(0xFFEEEEEE),
    start = Offset(bowlerBaseX, h * (analysis.releaseY - 0.02f)),
    end = Offset(bowlerBaseX, bowlerBaseY),
    strokeWidth = 4.dp.toPx(),
    cap = StrokeCap.Round
  )

  // Bowling Arm at Release Angle
  val armAngleRad = Math.toRadians(analysis.armAngleDeg)
  val armLength = 28.dp.toPx()
  val handX = bowlerBaseX + (armLength * cos(armAngleRad)).toFloat()
  val handY = h * (analysis.releaseY - 0.02f) - (armLength * sin(armAngleRad)).toFloat()

  drawLine(
    color = Color(0xFF00E676),
    start = Offset(bowlerBaseX, h * (analysis.releaseY - 0.02f)),
    end = Offset(handX, handY),
    strokeWidth = 3.dp.toPx(),
    cap = StrokeCap.Round
  )

  // Front Leg brace
  drawLine(
    color = Color(0xFFD4E6F1),
    start = Offset(bowlerBaseX, bowlerBaseY),
    end = Offset(bowlerBaseX - 12f, bowlerBaseY + 28f),
    strokeWidth = 3.5.dp.toPx(),
    cap = StrokeCap.Round
  )
}

private fun DrawScope.drawBallTrajectoryOverlay(
  analysis: BowlingDeliveryAnalysis,
  currentTimeMs: Long,
  w: Float,
  h: Float
) {
  val path = Path()
  val points = analysis.trajectoryPoints
  if (points.isEmpty()) return

  // Find points up to the reach point
  var first = true
  for (pt in points) {
    val px = pt.x * w
    val py = pt.y * h
    if (first) {
      path.moveTo(px, py)
      first = false
    } else {
      path.lineTo(px, py)
    }
  }

  // Trajectory Glow Line (Outer)
  drawPath(
    path = path,
    color = Color(0x33D0BCFF),
    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
  )

  // Trajectory Core Line (Inner)
  drawPath(
    path = path,
    color = Color(0xFFD0BCFF),
    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
  )
}

private fun DrawScope.drawPitchEventMarkers(
  analysis: BowlingDeliveryAnalysis,
  currentTimeMs: Long,
  w: Float,
  h: Float
) {
  // 1. Release Point Marker
  val rx = analysis.releaseX * w
  val ry = analysis.releaseY * h
  drawCircle(
    color = Color(0xFFD0BCFF),
    radius = 5.dp.toPx(),
    center = Offset(rx, ry)
  )
  drawCircle(
    color = Color(0x66D0BCFF),
    radius = 9.dp.toPx(),
    center = Offset(rx, ry),
    style = Stroke(width = 1.5.dp.toPx())
  )

  // 2. Bounce Point Marker
  val bx = analysis.bounceX * w
  val by = analysis.bounceY * h
  drawCircle(
    color = Color(0xFFFFD8E4),
    radius = 6.dp.toPx(),
    center = Offset(bx, by)
  )
  drawCircle(
    color = Color(0x88FFD8E4),
    radius = 12.dp.toPx(),
    center = Offset(bx, by),
    style = Stroke(width = 2.dp.toPx())
  )

  // 3. Batting Crease Reach Marker
  val cx = analysis.reachX * w
  val cy = analysis.reachY * h
  drawCircle(
    color = Color(0xFFCCC2DC),
    radius = 5.dp.toPx(),
    center = Offset(cx, cy)
  )
}

private fun DrawScope.drawBowlerActionSkeleton(
  analysis: BowlingDeliveryAnalysis,
  currentTimeMs: Long,
  w: Float,
  h: Float
) {
  val rx = analysis.releaseX * w
  val ry = analysis.releaseY * h

  // Arm angle vector & indicator arc
  val armAngleRad = Math.toRadians(analysis.armAngleDeg)
  val length = 45.dp.toPx()
  val endX = rx + (length * cos(armAngleRad)).toFloat()
  val endY = ry - (length * sin(armAngleRad)).toFloat()

  // Dashed horizontal reference line
  drawLine(
    color = Color(0x66938F99),
    start = Offset(rx - 30f, ry),
    end = Offset(rx + 50f, ry),
    strokeWidth = 1.5f,
    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
  )

  // Arm vector
  drawLine(
    color = Color(0xFFD0BCFF),
    start = Offset(rx, ry),
    end = Offset(endX, endY),
    strokeWidth = 3.dp.toPx()
  )

  // Elbow joint marker
  val elbowX = rx + (endX - rx) * 0.5f
  val elbowY = ry + (endY - ry) * 0.5f
  drawCircle(
    color = Color(0xFFFFD8E4),
    radius = 3.5.dp.toPx(),
    center = Offset(elbowX, elbowY)
  )
}

private fun DrawScope.drawLiveBallReticle(
  analysis: BowlingDeliveryAnalysis,
  currentTimeMs: Long,
  w: Float,
  h: Float
) {
  // Interpolate current ball position from trajectory points
  val points = analysis.trajectoryPoints
  if (points.isEmpty()) return

  // Find closest point
  val currentPoint = points.minByOrNull { abs(it.timeMs - currentTimeMs) } ?: return
  val bx = currentPoint.x * w
  val by = currentPoint.y * h

  // Draw Reticle Target Rings
  drawCircle(
    color = Color(0xFFFF5252),
    radius = 7.dp.toPx(),
    center = Offset(bx, by)
  )
  drawCircle(
    color = Color.White,
    radius = 2.5.dp.toPx(),
    center = Offset(bx - 1f, by - 1f)
  )
  // Crosshairs
  val crosshairLength = 12.dp.toPx()
  drawLine(
    color = Color(0xFFD0BCFF),
    start = Offset(bx - crosshairLength, by),
    end = Offset(bx + crosshairLength, by),
    strokeWidth = 1.5.dp.toPx()
  )
  drawLine(
    color = Color(0xFFD0BCFF),
    start = Offset(bx, by - crosshairLength),
    end = Offset(bx, by + crosshairLength),
    strokeWidth = 1.5.dp.toPx()
  )
}
