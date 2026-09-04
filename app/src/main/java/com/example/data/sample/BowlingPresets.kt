package com.example.data.sample

import com.example.data.model.BowlingDeliveryAnalysis
import com.example.data.model.TrajectoryPoint

object BowlingPresets {

  fun getSampleDeliveries(): List<BowlingDeliveryAnalysis> {
    return listOf(
      createBumrahYorker(),
      createStarcOutswinger(),
      createCumminsTopOff(),
      createSpinLegBreak(),
      createArcherBouncer()
    )
  }

  private fun createBumrahYorker(): BowlingDeliveryAnalysis {
    val releaseMs = 740L
    val bounceMs = 1170L
    val reachMs = 1350L
    val duration = 2200L

    val points = generateTrajectory(
      releaseMs = releaseMs,
      bounceMs = bounceMs,
      reachMs = reachMs,
      releaseX = 0.54f,
      releaseY = 0.28f,
      bounceX = 0.49f,
      bounceY = 0.84f,
      reachX = 0.48f,
      reachY = 0.90f
    )

    return BowlingDeliveryAnalysis(
      id = 1L,
      title = "Jasprit Bumrah - Searing Inswinging Yorker",
      videoUri = "asset://sample_yorker.mp4",
      timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
      bowlerName = "Jasprit Bumrah",
      bowlerType = "Right-Arm Fast",
      bowlingArm = "Right-arm",
      releaseSpeedKmph = 144.8,
      releaseSpeedMph = 90.0,
      postBounceSpeedKmph = 119.5,
      releaseTimeMs = releaseMs,
      bounceTimeMs = bounceMs,
      reachTimeMs = reachMs,
      videoDurationMs = duration,
      releaseX = 0.54f,
      releaseY = 0.28f,
      bounceX = 0.49f,
      bounceY = 0.84f,
      reachX = 0.48f,
      reachY = 0.90f,
      trajectoryPoints = points,
      pitchLength = "Yorker",
      pitchLine = "Middle & Off",
      pitchDistanceM = 18.5,
      pitchLateralM = -0.04,
      actionType = "Front-On (Hyperextension Whip)",
      armAngleDeg = 164.5,
      elbowExtensionDeg = 10.4,
      isElbowLegal = true,
      releaseHeightM = 2.08,
      runUpVelocityKmph = 19.2,
      movementType = "Late In-Swing",
      deviationCm = 16.4,
      deviationDeg = 2.8,
      bounceHeightM = 0.18,
      rpmEstimate = 1920,
      coachingNotes = "Exceptional release leverage with stiff front leg brace. Ball dips rapidly with late reverse curl into the toes.",
      tacticalVerdict = "Deadly execution. Perfect yorker dipping under batsman's toe. High wicket probability."
    )
  }

  private fun createStarcOutswinger(): BowlingDeliveryAnalysis {
    val releaseMs = 820L
    val bounceMs = 1260L
    val reachMs = 1440L
    val duration = 2400L

    val points = generateTrajectory(
      releaseMs = releaseMs,
      bounceMs = bounceMs,
      reachMs = reachMs,
      releaseX = 0.46f,
      releaseY = 0.24f,
      bounceX = 0.53f,
      bounceY = 0.68f,
      reachX = 0.57f,
      reachY = 0.86f
    )

    return BowlingDeliveryAnalysis(
      id = 2L,
      title = "Mitchell Starc - Late Shaping Outswinger",
      videoUri = "asset://sample_outswinger.mp4",
      timestamp = System.currentTimeMillis() - 1000 * 60 * 90,
      bowlerName = "Mitchell Starc",
      bowlerType = "Left-Arm Fast",
      bowlingArm = "Left-arm",
      releaseSpeedKmph = 148.6,
      releaseSpeedMph = 92.3,
      postBounceSpeedKmph = 122.4,
      releaseTimeMs = releaseMs,
      bounceTimeMs = bounceMs,
      reachTimeMs = reachMs,
      videoDurationMs = duration,
      releaseX = 0.46f,
      releaseY = 0.24f,
      bounceX = 0.53f,
      bounceY = 0.68f,
      reachX = 0.57f,
      reachY = 0.86f,
      trajectoryPoints = points,
      pitchLength = "Good Length",
      pitchLine = "Outside Off Stump",
      pitchDistanceM = 14.8,
      pitchLateralM = 0.28,
      actionType = "Side-On (Classic High Arm)",
      armAngleDeg = 177.2,
      elbowExtensionDeg = 7.6,
      isElbowLegal = true,
      releaseHeightM = 2.28,
      runUpVelocityKmph = 21.5,
      movementType = "Conventional Out-Swing",
      deviationCm = 19.2,
      deviationDeg = 3.4,
      bounceHeightM = 0.74,
      rpmEstimate = 2100,
      coachingNotes = "Tremendous high wrist release at 2.28m height. Seam tilted at 20 degrees towards first slip giving late air shape.",
      tacticalVerdict = "Elite cordon attack. Drawing batsman on the front foot before deviating past outside edge."
    )
  }

  private fun createCumminsTopOff(): BowlingDeliveryAnalysis {
    val releaseMs = 890L
    val bounceMs = 1330L
    val reachMs = 1510L
    val duration = 2300L

    val points = generateTrajectory(
      releaseMs = releaseMs,
      bounceMs = bounceMs,
      reachMs = reachMs,
      releaseX = 0.52f,
      releaseY = 0.26f,
      bounceX = 0.51f,
      bounceY = 0.65f,
      reachX = 0.50f,
      reachY = 0.84f
    )

    return BowlingDeliveryAnalysis(
      id = 3L,
      title = "Pat Cummins - Seam Nip-Back Top of Off",
      videoUri = "asset://sample_goodlength.mp4",
      timestamp = System.currentTimeMillis() - 1000 * 60 * 180,
      bowlerName = "Pat Cummins",
      bowlerType = "Right-Arm Fast",
      bowlingArm = "Right-arm",
      releaseSpeedKmph = 141.2,
      releaseSpeedMph = 87.7,
      postBounceSpeedKmph = 117.8,
      releaseTimeMs = releaseMs,
      bounceTimeMs = bounceMs,
      reachTimeMs = reachMs,
      videoDurationMs = duration,
      releaseX = 0.52f,
      releaseY = 0.26f,
      bounceX = 0.51f,
      bounceY = 0.65f,
      reachX = 0.50f,
      reachY = 0.84f,
      trajectoryPoints = points,
      pitchLength = "Good Length",
      pitchLine = "Top of Off Stump",
      pitchDistanceM = 15.2,
      pitchLateralM = 0.08,
      actionType = "Semi-Open (Heavy Ball)",
      armAngleDeg = 171.0,
      elbowExtensionDeg = 8.8,
      isElbowLegal = true,
      releaseHeightM = 2.19,
      runUpVelocityKmph = 19.8,
      movementType = "Seam Cut Nip-Back",
      deviationCm = 11.5,
      deviationDeg = 2.1,
      bounceHeightM = 0.78,
      rpmEstimate = 1880,
      coachingNotes = "Upright seam presentation, hitting the deck hard. Scintillating deviation cutting back sharply from 5th stump into off-bail.",
      tacticalVerdict = "Textbook Test match delivery. Answering batsman's leave by pegging back off stump."
    )
  }

  private fun createSpinLegBreak(): BowlingDeliveryAnalysis {
    val releaseMs = 650L
    val bounceMs = 1380L
    val reachMs = 1720L
    val duration = 2500L

    val points = generateTrajectory(
      releaseMs = releaseMs,
      bounceMs = bounceMs,
      reachMs = reachMs,
      releaseX = 0.50f,
      releaseY = 0.32f,
      bounceX = 0.44f,
      bounceY = 0.62f,
      reachX = 0.58f,
      reachY = 0.88f
    )

    return BowlingDeliveryAnalysis(
      id = 4L,
      title = "Shane Warne - Big Turning Leg-Break",
      videoUri = "asset://sample_legspin.mp4",
      timestamp = System.currentTimeMillis() - 1000 * 60 * 360,
      bowlerName = "Shane Warne",
      bowlerType = "Right-Arm Leg Spin",
      bowlingArm = "Right-arm",
      releaseSpeedKmph = 88.4,
      releaseSpeedMph = 54.9,
      postBounceSpeedKmph = 70.8,
      releaseTimeMs = releaseMs,
      bounceTimeMs = bounceMs,
      reachTimeMs = reachMs,
      videoDurationMs = duration,
      releaseX = 0.50f,
      releaseY = 0.32f,
      bounceX = 0.44f,
      bounceY = 0.62f,
      reachX = 0.58f,
      reachY = 0.88f,
      trajectoryPoints = points,
      pitchLength = "Full Good Length",
      pitchLine = "Pitching Outside Leg, Hitting Off",
      pitchDistanceM = 16.0,
      pitchLateralM = -0.35,
      actionType = "Pivot Wrist-Spin",
      armAngleDeg = 158.0,
      elbowExtensionDeg = 6.2,
      isElbowLegal = true,
      releaseHeightM = 1.94,
      runUpVelocityKmph = 11.2,
      movementType = "Massive Leg Break Turn",
      deviationCm = 28.5,
      deviationDeg = 6.2,
      bounceHeightM = 0.82,
      rpmEstimate = 2520,
      coachingNotes = "Tremendous revolutions (2520 RPM) with high dip and drift into the right hander, followed by vicious turn across the face of the bat.",
      tacticalVerdict = "Classic leg spinner's masterclass. Pitches comfortably outside leg stump and turns 28.5 cm to clip off stump."
    )
  }

  private fun createArcherBouncer(): BowlingDeliveryAnalysis {
    val releaseMs = 790L
    val bounceMs = 1200L
    val reachMs = 1390L
    val duration = 2200L

    val points = generateTrajectory(
      releaseMs = releaseMs,
      bounceMs = bounceMs,
      reachMs = reachMs,
      releaseX = 0.53f,
      releaseY = 0.25f,
      bounceX = 0.51f,
      bounceY = 0.50f,
      reachX = 0.50f,
      reachY = 0.85f
    )

    return BowlingDeliveryAnalysis(
      id = 5L,
      title = "Jofra Archer - Hostile Steep Bouncer",
      videoUri = "asset://sample_bouncer.mp4",
      timestamp = System.currentTimeMillis() - 1000 * 60 * 500,
      bowlerName = "Jofra Archer",
      bowlerType = "Right-Arm Fast",
      bowlingArm = "Right-arm",
      releaseSpeedKmph = 147.2,
      releaseSpeedMph = 91.5,
      postBounceSpeedKmph = 124.5,
      releaseTimeMs = releaseMs,
      bounceTimeMs = bounceMs,
      reachTimeMs = reachMs,
      videoDurationMs = duration,
      releaseX = 0.53f,
      releaseY = 0.25f,
      bounceX = 0.51f,
      bounceY = 0.50f,
      reachX = 0.50f,
      reachY = 0.85f,
      trajectoryPoints = points,
      pitchLength = "Short Pitch",
      pitchLine = "Ribcage / Bodyline",
      pitchDistanceM = 10.4,
      pitchLateralM = 0.02,
      actionType = "Front-On Snap",
      armAngleDeg = 175.4,
      elbowExtensionDeg = 9.1,
      isElbowLegal = true,
      releaseHeightM = 2.22,
      runUpVelocityKmph = 20.4,
      movementType = "Heavy Pitch Lift",
      deviationCm = 7.8,
      deviationDeg = 1.4,
      bounceHeightM = 1.76,
      rpmEstimate = 1960,
      coachingNotes = "Effortless, quick-arm flick. Hits the deck half-way down the pitch and explodes off the turf directly towards helmet height.",
      tacticalVerdict = "Hostile enforcer ball. Puts the batsman deep in defensive evasive posture."
    )
  }

  fun generateTrajectory(
    releaseMs: Long,
    bounceMs: Long,
    reachMs: Long,
    releaseX: Float,
    releaseY: Float,
    bounceX: Float,
    bounceY: Float,
    reachX: Float,
    reachY: Float
  ): List<TrajectoryPoint> {
    val points = mutableListOf<TrajectoryPoint>()
    val stepMs = 25L

    // Phase 1: Pre-release Run-up
    var t = maxOf(0L, releaseMs - 300L)
    while (t < releaseMs) {
      val progress = (t - (releaseMs - 300L)).toFloat() / 300f
      val x = releaseX - 0.05f * (1f - progress)
      val y = releaseY + 0.15f * (1f - progress)
      points.add(TrajectoryPoint(t, x, y, "RUN_UP"))
      t += stepMs
    }

    // Release Point
    points.add(TrajectoryPoint(releaseMs, releaseX, releaseY, "RELEASE"))

    // Phase 2: Flight from Release to Bounce
    t = releaseMs + stepMs
    val flightDuration = (bounceMs - releaseMs).toFloat()
    while (t < bounceMs) {
      val p = (t - releaseMs) / flightDuration
      // Parabolic drop with slight lateral curve
      val x = releaseX + (bounceX - releaseX) * p
      // Gravity curve down: start high, accelerate towards bounce
      val y = releaseY + (bounceY - releaseY) * (p * p * 0.7f + p * 0.3f)
      points.add(TrajectoryPoint(t, x, y, "FLIGHT"))
      t += stepMs
    }

    // Bounce Point
    points.add(TrajectoryPoint(bounceMs, bounceX, bounceY, "BOUNCE"))

    // Phase 3: Post-bounce to Reach
    t = bounceMs + stepMs
    val reachDuration = (reachMs - bounceMs).toFloat()
    while (t <= reachMs) {
      val p = (t - bounceMs) / reachDuration
      // Deviation off pitch
      val x = bounceX + (reachX - bounceX) * p
      // Rise and move towards batsman/stumps
      val y = bounceY + (reachY - bounceY) * p
      points.add(TrajectoryPoint(t, x, y, "REACH"))
      t += stepMs
    }

    return points
  }
}
