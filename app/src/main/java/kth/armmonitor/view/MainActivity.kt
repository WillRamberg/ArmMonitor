package kth.armmonitor.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kth.armmonitor.databinding.ActivityMainBinding
import kth.armmonitor.viewmodel.SensorViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sensorViewModel: SensorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            sensorViewModel.startMeasurement()
            binding.startButton.visibility = android.view.View.GONE
            binding.stopButton.visibility = android.view.View.VISIBLE
        }

        binding.stopButton.setOnClickListener {
            sensorViewModel.stopMeasurement()
            binding.startButton.visibility = android.view.View.VISIBLE
            binding.stopButton.visibility = android.view.View.GONE
        }

        binding.exportButton.setOnClickListener {
            val path = sensorViewModel.exportDataToCsv(application, "arm_elevation_data")
            binding.dataTextView.text = "Data exported to: $path"
        }

        sensorViewModel.sensorDataList.observe(this, Observer { dataList ->
            binding.dataTextView.text = dataList.joinToString("\n") { "Time: ${it.timestamp}, Angle: ${it.elevationAngle}°" }
        })
    }
}