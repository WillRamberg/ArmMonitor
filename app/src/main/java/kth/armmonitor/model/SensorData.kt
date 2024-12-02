package kth.armmonitor.model

data class SensorData(
    val timestamp: Long,
    val ewmaAngle: Float,
    val fusionAngle: Float
)
