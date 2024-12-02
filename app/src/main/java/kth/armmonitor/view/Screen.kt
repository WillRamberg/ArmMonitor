package kth.armmonitor.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kth.armmonitor.R
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
        val rootView = inflater.inflate(R.layout.activity_main, container, false)

        graphView = rootView.findViewById(R.id.graphView)
        startButton = rootView.findViewById(R.id.startButton)
        stopButton = rootView.findViewById(R.id.stopButton)
        exportButton = rootView.findViewById(R.id.exportButton)

        startButton.setOnClickListener {
            sensorViewModel.startMeasurement()
            startButton.visibility = View.GONE
            stopButton.visibility = View.VISIBLE
        }

        stopButton.setOnClickListener {
            sensorViewModel.stopMeasurement()
            stopButton.visibility = View.GONE
            startButton.visibility = View.VISIBLE
        }

        exportButton.setOnClickListener {
            val path = sensorViewModel.exportDataToCsv(requireActivity().application, "arm_elevation_data")
            Toast.makeText(requireContext(), "Data exported to: $path", Toast.LENGTH_LONG).show()
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        sensorViewModel = ViewModelProvider(requireActivity()).get(SensorViewModel::class.java)

        sensorViewModel.sensorDataList.observe(viewLifecycleOwner) { dataList ->
            if (dataList.isNotEmpty()) {
                val lastData = dataList.last()
                graphView.addPoint(lastData.ewmaAngle, lastData.fusionAngle)
            }
        }
    }
}
