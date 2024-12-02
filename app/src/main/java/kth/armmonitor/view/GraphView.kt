package kth.armmonitor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kth.armmonitor.model.SensorData

class GraphView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paintLine = Paint().apply {
        color = Color.BLUE
        strokeWidth = 5f
        isAntiAlias = true
    }

    private val paintAxis = Paint().apply {
        color = Color.BLACK
        strokeWidth = 3f
    }

    private val dataPoints = mutableListOf<SensorData>()

    fun updateData(newData: List<SensorData>) {
        dataPoints.clear()
        dataPoints.addAll(newData)
        invalidate() // Uppdaterar vyn
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dataPoints.isEmpty()) return

        // Rita axlar
        canvas.drawLine(100f, 100f, 100f, 600f, paintAxis) // Y-axel
        canvas.drawLine(100f, 600f, 700f, 600f, paintAxis) // X-axel

        // Skalning
        val maxTime = dataPoints.last().timestamp
        val minTime = dataPoints.first().timestamp
        val timeRange = maxTime - minTime

        val maxAngle = dataPoints.maxOf { it.elevationAngle }
        val scaleX = 600f / timeRange
        val scaleY = 400f / maxAngle

        // Rita datapunkter och linjer
        var prevX = 100f
        var prevY = 600f - dataPoints[0].elevationAngle * scaleY

        for (data in dataPoints) {
            val x = 100f + (data.timestamp - minTime) * scaleX
            val y = 600f - data.elevationAngle * scaleY
            canvas.drawLine(prevX, prevY, x, y, paintLine)
            prevX = x
            prevY = y
        }
    }
}
