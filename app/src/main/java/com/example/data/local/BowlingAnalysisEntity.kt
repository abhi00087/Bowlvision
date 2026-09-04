package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.BowlingDeliveryAnalysis
import com.example.data.model.TrajectoryPoint

@Entity(tableName = "bowling_analyses")
data class BowlingAnalysisEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val videoUri: String,
  val timestamp: Long,
  val bowlerName: String,
  val bowlerType: String,
  val bowlingArm: String,
  val releaseSpeedKmph: Double,
  val postBounceSpeedKmph: Double,
  val releaseTimeMs: Long,
  val bounceTimeMs: Long,
  val reachTimeMs: Long,
  val videoDurationMs: Long,
  val releaseX: Float,
  val releaseY: Float,
  val bounceX: Float,
  val bounceY: Float,
  val reachX: Float,
  val reachY: Float,
  val pitchLength: String,
  val pitchLine: String,
  val pitchDistanceM: Double,
  val pitchLateralM: Double,
  val actionType: String,
  val armAngleDeg: Double,
  val elbowExtensionDeg: Double,
  val isElbowLegal: Boolean,
  val releaseHeightM: Double,
  val runUpVelocityKmph: Double,
  val movementType: String,
  val deviationCm: Double,
  val deviationDeg: Double,
  val bounceHeightM: Double,
  val rpmEstimate: Int,
  val coachingNotes: String,
  val tacticalVerdict: String
) {
  fun toModel(points: List<TrajectoryPoint> = emptyList()): BowlingDeliveryAnalysis {
    return BowlingDeliveryAnalysis(
      id = id,
      title = title,
      videoUri = videoUri,
      timestamp = timestamp,
      bowlerName = bowlerName,
      bowlerType = bowlerType,
      bowlingArm = bowlingArm,
      releaseSpeedKmph = releaseSpeedKmph,
      postBounceSpeedKmph = postBounceSpeedKmph,
      releaseTimeMs = releaseTimeMs,
      bounceTimeMs = bounceTimeMs,
      reachTimeMs = reachTimeMs,
      videoDurationMs = videoDurationMs,
      releaseX = releaseX,
      releaseY = releaseY,
      bounceX = bounceX,
      bounceY = bounceY,
      reachX = reachX,
      reachY = reachY,
      trajectoryPoints = points,
      pitchLength = pitchLength,
      pitchLine = pitchLine,
      pitchDistanceM = pitchDistanceM,
      pitchLateralM = pitchLateralM,
      actionType = actionType,
      armAngleDeg = armAngleDeg,
      elbowExtensionDeg = elbowExtensionDeg,
      isElbowLegal = isElbowLegal,
      releaseHeightM = releaseHeightM,
      runUpVelocityKmph = runUpVelocityKmph,
      movementType = movementType,
      deviationCm = deviationCm,
      deviationDeg = deviationDeg,
      bounceHeightM = bounceHeightM,
      rpmEstimate = rpmEstimate,
      coachingNotes = coachingNotes,
      tacticalVerdict = tacticalVerdict
    )
  }

  companion object {
    fun fromModel(model: BowlingDeliveryAnalysis): BowlingAnalysisEntity {
      return BowlingAnalysisEntity(
        id = model.id,
        title = model.title,
        videoUri = model.videoUri,
        timestamp = model.timestamp,
        bowlerName = model.bowlerName,
        bowlerType = model.bowlerType,
        bowlingArm = model.bowlingArm,
        releaseSpeedKmph = model.releaseSpeedKmph,
        postBounceSpeedKmph = model.postBounceSpeedKmph,
        releaseTimeMs = model.releaseTimeMs,
        bounceTimeMs = model.bounceTimeMs,
        reachTimeMs = model.reachTimeMs,
        videoDurationMs = model.videoDurationMs,
        releaseX = model.releaseX,
        releaseY = model.releaseY,
        bounceX = model.bounceX,
        bounceY = model.bounceY,
        reachX = model.reachX,
        reachY = model.reachY,
        pitchLength = model.pitchLength,
        pitchLine = model.pitchLine,
        pitchDistanceM = model.pitchDistanceM,
        pitchLateralM = model.pitchLateralM,
        actionType = model.actionType,
        armAngleDeg = model.armAngleDeg,
        elbowExtensionDeg = model.elbowExtensionDeg,
        isElbowLegal = model.isElbowLegal,
        releaseHeightM = model.releaseHeightM,
        runUpVelocityKmph = model.runUpVelocityKmph,
        movementType = model.movementType,
        deviationCm = model.deviationCm,
        deviationDeg = model.deviationDeg,
        bounceHeightM = model.bounceHeightM,
        rpmEstimate = model.rpmEstimate,
        coachingNotes = model.coachingNotes,
        tacticalVerdict = model.tacticalVerdict
      )
    }
  }
}
