package kth.armmonitor.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kth.armmonitor.viewmodel.SensorViewModel
import kth.armmonitor.view.GraphView

class Screen : Fragment() {

    private val sensorViewModel: SensorViewModel by viewModels()

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var dataTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Root layout (LinearLayout)
        val rootView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(16, 16, 16, 16) // Padding for overall layout
        }

        // GraphView with weight to take up more space
        val graphView = GraphView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, // Use weight to control height dynamically
                1f // This assigns the "weight" to take up remaining space
            )
        }

        // Start Button
        startButton = Button(requireContext()).apply {
            text = "Start"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16 // Add space between GraphView and buttons
            }
            setOnClickListener {
                sensorViewModel.startMeasurement()
                visibility = View.GONE
                stopButton.visibility = View.VISIBLE
            }
        }

        // Stop Button
        stopButton = Button(requireContext()).apply {
            text = "Stop"
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16 // Add space between buttons
            }
            setOnClickListener {
                sensorViewModel.stopMeasurement()
                visibility = View.GONE
                startButton.visibility = View.VISIBLE
            }
        }

        // Export Button
        val exportButton = Button(requireContext()).apply {
            text = "Export"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
            setOnClickListener {
                val path = sensorViewModel.exportDataToCsv(requireActivity().application, "arm_elevation_data")
                dataTextView.text = "Data exported to: $path"
            }
        }

        // Data TextView
        dataTextView = TextView(requireContext()).apply {
            text = "Data path will be shown here"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
        }

        // Add views to root layout
        rootView.addView(graphView)
        rootView.addView(startButton)
        rootView.addView(stopButton)
        rootView.addView(exportButton)
        rootView.addView(dataTextView)

        // Observe sensor data and update the graph
        sensorViewModel.sensorDataList.observe(viewLifecycleOwner, Observer { dataList ->
            graphView.updateData(dataList)
        })

        return rootView
    }
}
