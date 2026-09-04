package com.example.data.model

data class TrajectoryPoint(
  val timeMs: Long,
  val x: Float, // Normalized 0.0 - 1.0 in video frame
  val y: Float, // Normalized 0.0 - 1.0 in video frame
  val phase: String // "RUN_UP", "RELEASE", "FLIGHT", "BOUNCE", "REACH"
)

data class BowlingDeliveryAnalysis(
  val id: Long = 0,
  val title: String,
  val videoUri: String,
  val timestamp: Long = System.currentTimeMillis(),
  val bowlerName: String = "Bowler",
  val bowlerType: String = "Fast Bowler",
  val bowlingArm: String = "Right-arm",
  // Speed metrics
  val releaseSpeedKmph: Double,
  val releaseSpeedMph: Double = releaseSpeedKmph * 0.621371,
  val postBounceSpeedKmph: Double = releaseSpeedKmph * 0.82,
  // Key automated time points (in milliseconds from video start)
  val releaseTimeMs: Long,
  val bounceTimeMs: Long,
  val reachTimeMs: Long,
  val videoDurationMs: Long,
  // Normalized 2D screen coordinates for overlay
  val releaseX: Float,
  val releaseY: Float,
  val bounceX: Float,
  val bounceY: Float,
  val reachX: Float,
  val reachY: Float,
  // Trajectory points for continuous rendering
  val trajectoryPoints: List<TrajectoryPoint> = emptyList(),
  // Pitch landing mapping
  val pitchLength: String, // "Yorker", "Full", "Good Length", "Back of Length", "Short"
  val pitchLine: String, // "Wide Outside Off", "Outside Off", "Off Stump", "Middle Stump", "Leg Stump", "Down Leg"
  val pitchDistanceM: Double, // Distance from bowling crease (0 - 20.12m)
  val pitchLateralM: Double, // Lateral offset from center line in meters (-1.5m to +1.5m)
  // Biomechanics & action analysis
  val actionType: String, // "Front-On", "Side-On", "Semi-Open", "Mixed Action"
  val armAngleDeg: Double, // Arm release elevation angle from horizontal (e.g. 165°)
  val elbowExtensionDeg: Double, // Elbow straightening angle (ICC limit is 15°)
  val isElbowLegal: Boolean = elbowExtensionDeg <= 15.0,
  val releaseHeightM: Double, // Estimated release height in meters (e.g. 2.15m)
  val runUpVelocityKmph: Double = 18.4,
  // Ball movement
  val movementType: String, // "In-Swing", "Out-Swing", "Reverse Swing", "Leg Break", "Off Break", "Straight Seam"
  val deviationCm: Double, // Lateral deviation in cm
  val deviationDeg: Double, // Angle of movement in degrees
  val bounceHeightM: Double, // Height of ball at batting crease (e.g. 0.72m)
  val rpmEstimate: Int = 1850, // Ball revs per minute
  // AI Coaching commentary
  val coachingNotes: String,
  val tacticalVerdict: String
)
