package kth.armmonitor.model


data class SensorData(
    val timestamp: Long,
    val elevationAngle: Float,
    val filteredAngle: Float, // Add new field for filtered angle
    val fusedAngle: Float // Add new field for fused angle
)