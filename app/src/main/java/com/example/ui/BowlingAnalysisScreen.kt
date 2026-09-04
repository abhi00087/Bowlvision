package com.example.ui

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BowlingDeliveryAnalysis
import com.example.data.sample.BowlingPresets
import com.example.ui.components.ActionBiomechanicsCard
import com.example.ui.components.MilestoneTimelineCard
import com.example.ui.components.PitchMapView
import com.example.ui.components.SpeedometerCard
import com.example.ui.components.VideoPlayerWithOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BowlingAnalysisScreen(
  viewModel: BowlingVisionViewModel,
  modifier: Modifier = Modifier
) {
  val currentAnalysis by viewModel.currentAnalysis.collectAsState()
  val currentTimeMs by viewModel.currentTimeMs.collectAsState()
  val isPlaying by viewModel.isPlaying.collectAsState()
  val playbackSpeed by viewModel.playbackSpeed.collectAsState()
  val isAnalyzing by viewModel.isAnalyzing.collectAsState()
  val progress by viewModel.analysisProgress.collectAsState()
  val userMessage by viewModel.userMessage.collectAsState()
  val savedDeliveries by viewModel.savedDeliveries.collectAsState()

  val snackbarHostState = remember { SnackbarHostState() }
  var showHistorySheet by remember { mutableStateOf(false) }
  var showUploadSourceSheet by remember { mutableStateOf(false) }

  // Channel 1: Device File Manager / Downloads / SD Card / Internal Storage
  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri ->
    if (uri != null) {
      viewModel.analyzeUploadedVideo(uri)
    }
  }

  // Channel 2: Android Photo & Video Picker (Gallery / Photos)
  val videoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      viewModel.analyzeUploadedVideo(uri)
    }
  }

  // Channel 3: Storage Access Framework OpenDocument for persistent file access
  val openDocumentLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri ->
    if (uri != null) {
      viewModel.analyzeUploadedVideo(uri)
    }
  }

  LaunchedEffect(userMessage) {
    userMessage?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearUserMessage()
    }
  }

  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.SportsCricket,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Bowling Vision",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Automatic Video & Ball Tracking",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        },
        actions = {
          // Direct Upload Video Button with choice modal
          Button(
            onClick = { showUploadSourceSheet = true },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
            modifier = Modifier.testTag("upload_video_button")
          ) {
            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Upload Video", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }

          Spacer(modifier = Modifier.width(6.dp))

          IconButton(
            onClick = { showHistorySheet = true },
            modifier = Modifier.testTag("saved_history_button")
          ) {
            Icon(
              imageVector = Icons.Default.BookmarkBorder,
              contentDescription = "Saved Deliveries",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      if (isLandscape) {
        // Landscape: True Side-by-Side Layout
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Left Pane: Original Video with Trajectory & Telemetry
          Box(
            modifier = Modifier
              .weight(1.1f)
              .fillMaxHeight()
          ) {
            VideoPlayerWithOverlay(
              analysis = currentAnalysis,
              currentTimeMs = currentTimeMs,
              isPlaying = isPlaying,
              playbackSpeed = playbackSpeed,
              onPlaybackPositionChanged = viewModel::updatePlaybackPosition,
              onPlayPauseToggle = viewModel::togglePlayPause,
              onPlaybackSpeedChanged = viewModel::setPlaybackSpeed,
              modifier = Modifier.fillMaxSize()
            )
          }

          // Right Pane: Synchronized Ball Analysis Alongside
          Column(
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight()
              .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            DirectUploadBar(
              onBrowseFiles = { filePickerLauncher.launch("video/*") },
              onBrowseGallery = {
                videoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                )
              }
            )
            PresetSelectorRow(
              currentId = currentAnalysis.id,
              onSelect = viewModel::selectDelivery
            )
            MilestoneTimelineCard(
              analysis = currentAnalysis,
              currentTimeMs = currentTimeMs,
              onSeekToMs = viewModel::seekTo
            )
            SpeedometerCard(analysis = currentAnalysis)
            PitchMapView(analysis = currentAnalysis)
            ActionBiomechanicsCard(analysis = currentAnalysis)
          }
        }
      } else {
        // Portrait: Synchronized Video on top + Ball Analysis Dashboard Alongside below
        Column(
          modifier = Modifier
            .fillMaxSize()
        ) {
          // Direct Upload quick bar
          DirectUploadBar(
            onBrowseFiles = { filePickerLauncher.launch("video/*") },
            onBrowseGallery = {
              videoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
              )
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
          )

          // Video Presets Carousel (Fast Switcher)
          PresetSelectorRow(
            currentId = currentAnalysis.id,
            onSelect = viewModel::selectDelivery,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
          )

          // Top Half: Original Video Player with Overlay
          VideoPlayerWithOverlay(
            analysis = currentAnalysis,
            currentTimeMs = currentTimeMs,
            isPlaying = isPlaying,
            playbackSpeed = playbackSpeed,
            onPlaybackPositionChanged = viewModel::updatePlaybackPosition,
            onPlayPauseToggle = viewModel::togglePlayPause,
            onPlaybackSpeedChanged = viewModel::setPlaybackSpeed,
            modifier = Modifier.padding(horizontal = 12.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Bottom Half: Ball Analysis Dashboard Alongside
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
              .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
          ) {
            item {
              MilestoneTimelineCard(
                analysis = currentAnalysis,
                currentTimeMs = currentTimeMs,
                onSeekToMs = viewModel::seekTo
              )
            }
            item {
              SpeedometerCard(analysis = currentAnalysis)
            }
            item {
              PitchMapView(analysis = currentAnalysis)
            }
            item {
              ActionBiomechanicsCard(analysis = currentAnalysis)
            }
            item {
              // Action Bar for saving
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                OutlinedButton(
                  onClick = { viewModel.saveCurrentDelivery() },
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Save Delivery Analysis")
                }
              }
            }
          }
        }
      }

      // Automatic Video Analysis Processing Dialog
      if (isAnalyzing) {
        Dialog(
          onDismissRequest = { /* Non-dismissible while processing */ },
          properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
          Card(
            modifier = Modifier
              .fillMaxWidth(0.9f)
              .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = "Automatic Video Processing",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = progress.statusMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(18.dp))
              LinearProgressIndicator(
                progress = { progress.percentage / 100f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "${progress.percentage}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }
      }

      // Saved Deliveries Bottom Sheet
      if (showHistorySheet) {
        ModalBottomSheet(
          onDismissRequest = { showHistorySheet = false },
          sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Text(
              text = "Saved Delivery Analyses",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (savedDeliveries.isEmpty()) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "No saved deliveries yet. Upload a video or save an analysis!",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            } else {
              LazyColumn(
                modifier = Modifier.fillMaxWidth().height(320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                items(savedDeliveries) { delivery ->
                  Card(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable {
                        viewModel.selectDelivery(delivery)
                        showHistorySheet = false
                      },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = delivery.title,
                          style = MaterialTheme.typography.titleSmall,
                          fontWeight = FontWeight.Bold
                        )
                        Text(
                          text = "${delivery.releaseSpeedKmph} km/h • ${delivery.pitchLength} • ${delivery.pitchLine}",
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.primary
                        )
                      }
                      IconButton(onClick = { viewModel.deleteSavedDelivery(delivery) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252))
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Direct Device Upload Source Selector Bottom Sheet
      if (showUploadSourceSheet) {
        ModalBottomSheet(
          onDismissRequest = { showUploadSourceSheet = false },
          sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
          containerColor = MaterialTheme.colorScheme.surface
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 8.dp)
              .padding(bottom = 32.dp)
          ) {
            Text(
              text = "DEVICE VIDEO UPLOAD",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp,
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Choose upload method from device:",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Channel 1: Device File Manager
            UploadOptionCard(
              title = "Browse Device Files",
              description = "Select video from Downloads, Internal Storage, SD Card, or File Manager",
              icon = Icons.Default.FolderOpen,
              onClick = {
                showUploadSourceSheet = false
                filePickerLauncher.launch("video/*")
              },
              testTag = "upload_source_device_files"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Channel 2: Gallery / Photo Picker
            UploadOptionCard(
              title = "Device Gallery & Videos",
              description = "Select recorded bowling action from Camera roll or Google Photos",
              icon = Icons.Default.VideoLibrary,
              onClick = {
                showUploadSourceSheet = false
                videoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                )
              },
              testTag = "upload_source_gallery"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Channel 3: Document Storage Provider
            UploadOptionCard(
              title = "Open Document / Cloud File",
              description = "Access video files stored in Google Drive or external cloud document providers",
              icon = Icons.Default.InsertDriveFile,
              onClick = {
                showUploadSourceSheet = false
                openDocumentLauncher.launch(arrayOf("video/*", "*/*"))
              },
              testTag = "upload_source_documents"
            )
          }
        }
      }
    }
  }
}

@Composable
private fun UploadOptionCard(
  title: String,
  description: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant)
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
      .clickable { onClick() }
      .padding(14.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(40.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.size(22.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 16.sp
      )
    }
  }
}

@Composable
private fun DirectUploadBar(
  onBrowseFiles: () -> Unit,
  onBrowseGallery: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
      .padding(horizontal = 8.dp, vertical = 5.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Default.CloudUpload,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(15.dp)
      )
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = "DIRECT UPLOAD",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        fontSize = 9.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Row(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Direct File Button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(MaterialTheme.colorScheme.primaryContainer)
          .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
          .clickable { onBrowseFiles() }
          .padding(horizontal = 8.dp, vertical = 4.dp)
          .testTag("direct_device_files_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.FolderOpen,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Device Files",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 10.sp
          )
        }
      }

      // Direct Video Gallery Button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(MaterialTheme.colorScheme.surface)
          .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
          .clickable { onBrowseGallery() }
          .padding(horizontal = 8.dp, vertical = 4.dp)
          .testTag("direct_device_gallery_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.VideoLibrary,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Gallery Videos",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}

@Composable
private fun PresetSelectorRow(
  currentId: Long,
  onSelect: (BowlingDeliveryAnalysis) -> Unit,
  modifier: Modifier = Modifier
) {
  val presets = remember { BowlingPresets.getSampleDeliveries() }

  LazyRow(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    contentPadding = PaddingValues(horizontal = 2.dp)
  ) {
    items(presets) { preset ->
      val isSelected = preset.id == currentId
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(
            if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
          )
          .border(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            RoundedCornerShape(8.dp)
          )
          .clickable { onSelect(preset) }
          .padding(horizontal = 9.dp, vertical = 5.dp)
      ) {
        Column {
          Text(
            text = preset.bowlerName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${preset.releaseSpeedKmph.toInt()} km/h • ${preset.pitchLength}",
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp
          )
        }
      }
    }
  }
}
