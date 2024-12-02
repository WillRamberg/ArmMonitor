package kth.armmonitor.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import kth.armmonitor.model.service.SensorService
import kth.armmonitor.view.GraphView

class Screen : Fragment() {

    private lateinit var sensorService: SensorService
    private lateinit var graphView: GraphView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }

        graphView = GraphView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, 1f
            )
        }

        rootView.addView(graphView)
        return rootView
    }

    override fun onStart() {
        super.onStart()
        sensorService = SensorService(requireContext())

        sensorService.startMeasurement { timestamp, ewmaAngle, gyroAngle ->
            val fusionAngle = sensorService.applyFusion(ewmaAngle, gyroAngle)
            graphView.addPoint(ewmaAngle, fusionAngle)
        }
    }

    override fun onStop() {
        super.onStop()
        sensorService.stopMeasurement()
    }
}
