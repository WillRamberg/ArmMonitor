package kth.armmonitor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

data class SensorData(val timestamp: Long, val ewmaAngle: Float, val fusionAngle: Float)

class GraphView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paintEWMA = Paint().apply {
        color = Color.BLUE
        strokeWidth = 5f
        isAntiAlias = true
    }

    private val paintFusion = Paint().apply {
        color = Color.RED
        strokeWidth = 5f
        isAntiAlias = true
    }

    private val paintAxis = Paint().apply {
        color = Color.BLACK
        strokeWidth = 3f
    }

    private val dataPoints = mutableListOf<SensorData>()

    fun addPoint(ewma: Float, fusion: Float) {
        val timestamp = System.currentTimeMillis()
        dataPoints.add(SensorData(timestamp, ewma, fusion))
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dataPoints.isEmpty()) return

        // Draw axes
        canvas.drawLine(100f, 100f, 100f, 600f, paintAxis) // Y-axis
        canvas.drawLine(100f, 600f, 700f, 600f, paintAxis) // X-axis

        // Scaling setup
        val maxTime = dataPoints.last().timestamp
        val minTime = dataPoints.first().timestamp
        val timeRange = maxTime - minTime

        val maxAngle = dataPoints.maxOf { maxOf(it.ewmaAngle, it.fusionAngle) }
        val scaleX = 600f / timeRange
        val scaleY = 400f / maxAngle

        // Draw EWMA line
        var prevEWMAX = 100f
        var prevEWMAY = 600f - dataPoints[0].ewmaAngle * scaleY
        var prevFusionX = prevEWMAX
        var prevFusionY = 600f - dataPoints[0].fusionAngle * scaleY

        for (data in dataPoints) {
            val x = 100f + (data.timestamp - minTime) * scaleX
            val ewmaY = 600f - data.ewmaAngle * scaleY
            val fusionY = 600f - data.fusionAngle * scaleY

            // Draw EWMA line (blue)
            canvas.drawLine(prevEWMAX, prevEWMAY, x, ewmaY, paintEWMA)

            // Draw Fusion line (red)
            canvas.drawLine(prevFusionX, prevFusionY, x, fusionY, paintFusion)

            prevEWMAX = x
            prevEWMAY = ewmaY
            prevFusionX = x
            prevFusionY = fusionY
        }
    }
}
