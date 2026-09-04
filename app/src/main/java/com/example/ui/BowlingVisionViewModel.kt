package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.analyzer.BowlingVideoAnalyzer
import com.example.data.local.BowlingDatabase
import com.example.data.local.BowlingRepository
import com.example.data.model.BowlingDeliveryAnalysis
import com.example.data.sample.BowlingPresets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BowlingVisionViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: BowlingRepository
  private val analyzer = BowlingVideoAnalyzer(application)

  init {
    val db = BowlingDatabase.getDatabase(application)
    repository = BowlingRepository(db.bowlingDao())
  }

  val savedDeliveries: StateFlow<List<BowlingDeliveryAnalysis>> =
    repository.allAnalyses.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Current active delivery analysis
  private val _currentAnalysis = MutableStateFlow<BowlingDeliveryAnalysis>(
    BowlingPresets.getSampleDeliveries().first()
  )
  val currentAnalysis: StateFlow<BowlingDeliveryAnalysis> = _currentAnalysis.asStateFlow()

  // Video playback synchronization state
  private val _currentTimeMs = MutableStateFlow(0L)
  val currentTimeMs: StateFlow<Long> = _currentTimeMs.asStateFlow()

  private val _isPlaying = MutableStateFlow(true)
  val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

  private val _playbackSpeed = MutableStateFlow(1.0f)
  val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

  // Analysis status
  private val _isAnalyzing = MutableStateFlow(false)
  val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

  private val _analysisProgress = MutableStateFlow(BowlingVideoAnalyzer.AnalysisProgress(0, ""))
  val analysisProgress: StateFlow<BowlingVideoAnalyzer.AnalysisProgress> = _analysisProgress.asStateFlow()

  private val _userMessage = MutableStateFlow<String?>(null)
  val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

  fun selectDelivery(delivery: BowlingDeliveryAnalysis) {
    _currentAnalysis.value = delivery
    _currentTimeMs.value = 0L
    _isPlaying.value = true
  }

  fun seekTo(timeMs: Long) {
    _currentTimeMs.value = timeMs
  }

  fun togglePlayPause(playing: Boolean) {
    _isPlaying.value = playing
  }

  fun setPlaybackSpeed(speed: Float) {
    _playbackSpeed.value = speed
  }

  fun updatePlaybackPosition(positionMs: Long) {
    _currentTimeMs.value = positionMs
  }

  fun clearUserMessage() {
    _userMessage.value = null
  }

  // Automatic analysis of any uploaded / chosen video from device
  fun analyzeUploadedVideo(uri: Uri) {
    viewModelScope.launch {
      _isAnalyzing.value = true
      _isPlaying.value = false
      _analysisProgress.value = BowlingVideoAnalyzer.AnalysisProgress(5, "Loading video file...")

      val result = analyzer.analyzeVideo(uri) { progress ->
        _analysisProgress.value = progress
      }

      result.onSuccess { analyzedDelivery ->
        _currentAnalysis.value = analyzedDelivery
        _currentTimeMs.value = 0L
        _isPlaying.value = true
        _isAnalyzing.value = false
        _userMessage.value = "Automatic Analysis Complete! Release & reach points detected."

        // Auto-save to local database
        repository.insertAnalysis(analyzedDelivery)
      }.onFailure { error ->
        _isAnalyzing.value = false
        _userMessage.value = "Failed to analyze video: ${error.localizedMessage}"
      }
    }
  }

  fun saveCurrentDelivery() {
    viewModelScope.launch {
      repository.insertAnalysis(_currentAnalysis.value)
      _userMessage.value = "Delivery saved to history!"
    }
  }

  fun deleteSavedDelivery(delivery: BowlingDeliveryAnalysis) {
    viewModelScope.launch {
      repository.deleteAnalysis(delivery)
      _userMessage.value = "Delivery removed from history"
    }
  }
}
