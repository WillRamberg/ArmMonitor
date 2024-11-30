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
    private var sensorDataListener: ((Long, Float) -> Unit)? = null

    fun startMeasurement(listener: (Long, Float) -> Unit) {
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
            val elevationAngle = computeElevationAngle(event)
            sensorDataListener?.invoke(timestamp, elevationAngle)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun computeElevationAngle(event: SensorEvent): Float {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val z = event.values[2]
            val y = event.values[1]
            return Math.toDegrees(atan2(y.toDouble(), z.toDouble())).toFloat()
        }
        return 0f
    }
}