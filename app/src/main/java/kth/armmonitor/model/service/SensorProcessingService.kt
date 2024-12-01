package kth.armmonitor.model.service

import kth.armmonitor.model.SensorData

class SensorProcessingService {

    private val alphaEWMA = 0.1 // For EWMA filter
    private val alphaCompl = 0.98 // For complementary filter
    private var previousOutput = 0.0 // Store previous EWMA output
    private var previousAngle = 0.0 // Store previous complementary filter output

    fun applyEWMAFilter(currentAngle: Double): Double {
        val filteredAngle = alphaEWMA * currentAngle + (1 - alphaEWMA) * previousOutput
        previousOutput = filteredAngle
        return filteredAngle
    }

    fun applyComplementaryFilter(accelerationAngle: Double, gyroAngle: Double, dt: Long): Double {
        val angleFromGyro = gyroAngle * dt / 1000.0 // Convert milliseconds to seconds
        val fusedAngle = alphaCompl * (previousAngle + angleFromGyro) + (1 - alphaCompl) * accelerationAngle
        previousAngle = fusedAngle
        return fusedAngle
    }
}
