package kth.armmonitor.model.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.atan2

class SensorService(private val context: Context) : SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private var isRecording = false
    private var sensorDataListener: ((Long, Float, Float, Float, Long) -> Unit)? = null

    private val sensorProcessingService = SensorProcessingService()

    private var lastTimestamp = System.currentTimeMillis()
    private var lastGyroAngle = 0.0

    fun startMeasurement(listener: (Long, Float, Float, Float, Long) -> Unit) {
        if (!isRecording) {
            isRecording = true
            sensorDataListener = listener
            accelerometer?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
            gyroscope?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        }
    }

    fun stopMeasurement() {
        if (isRecording) {
            isRecording = false
            sensorManager.unregisterListener(this)
            sensorDataListener = null
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (isRecording && event != null) {
            val timestamp = System.currentTimeMillis()

            // Define a time difference (dt)
            val dt = timestamp - lastTimestamp

            // Capture sensor data (for example, using accelerometer)
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val elevationAngle = computeElevationAngle(event)
                    val gyroAngle = lastGyroAngle.toFloat()  // Use gyroscope data here if needed
                    val filteredAngle = sensorProcessingService.applyEWMAFilter(elevationAngle.toDouble()) // Apply EWMA filter

                    // Apply complementary filter after both sensor data is available
                    val fusedAngle = sensorProcessingService.applyComplementaryFilter(
                        filteredAngle, gyroAngle.toDouble(), dt
                    )

                    // Pass all the required parameters to the listener
                    sensorDataListener?.invoke(timestamp, elevationAngle.toFloat(), gyroAngle, filteredAngle.toFloat(), dt)

                }
                Sensor.TYPE_GYROSCOPE -> {
                    // Process gyroscope data if needed
                    lastGyroAngle = event.values[0].toDouble()
                }
            }

            lastTimestamp = timestamp
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun computeElevationAngle(event: SensorEvent): Double {
        val z = event.values[2]
        val y = event.values[1]
        return Math.toDegrees(atan2(y.toDouble(), z.toDouble()))
    }
}

