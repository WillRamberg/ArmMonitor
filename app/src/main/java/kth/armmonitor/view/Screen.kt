package kth.armmonitor.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kth.armmonitor.view.GraphView
import kth.armmonitor.viewmodel.SensorViewModel

class Screen : Fragment() {

    private lateinit var graphView: GraphView
    private lateinit var sensorViewModel: SensorViewModel
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var exportButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Create the root layout
        val rootView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Add GraphView
        graphView = GraphView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        rootView.addView(graphView)

        // Add Start Button
        startButton = Button(requireContext()).apply {
            text = "Start"
            setOnClickListener {
                sensorViewModel.startMeasurement()
                visibility = View.GONE
                stopButton.visibility = View.VISIBLE
            }
        }
        rootView.addView(startButton)

        // Add Stop Button (initially hidden)
        stopButton = Button(requireContext()).apply {
            text = "Stop"
            visibility = View.GONE
            setOnClickListener {
                sensorViewModel.stopMeasurement()
                visibility = View.GONE
                startButton.visibility = View.VISIBLE
            }
        }
        rootView.addView(stopButton)

        // Add Export Button
        exportButton = Button(requireContext()).apply {
            text = "Export"
            setOnClickListener {
                val path = sensorViewModel.exportDataToCsv(requireActivity().application, "arm_elevation_data")
                Toast.makeText(requireContext(), "Data exported to: $path", Toast.LENGTH_LONG).show()
            }
        }
        rootView.addView(exportButton)

        return rootView
    }

    override fun onStart() {
        super.onStart()
        sensorViewModel = ViewModelProvider(requireActivity()).get(SensorViewModel::class.java)

        // Observe the data and update GraphView
        sensorViewModel.sensorDataList.observe(viewLifecycleOwner) { dataList ->
            if (dataList.isNotEmpty()) {
                val lastData = dataList.last()
                graphView.addPoint(lastData.ewmaAngle, lastData.fusionAngle)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        sensorViewModel.stopMeasurement()
    }
}

