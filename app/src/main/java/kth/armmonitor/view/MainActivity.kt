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
            binding.dataTextView.text = "Data exported to: $path"  // This should work now
        }

        sensorViewModel.ewmaDataList.observe(this, Observer { ewmaData ->
            // Display the filtered (EWMA) data
            binding.ewmaTextView.text = ewmaData.joinToString("\n") {
                "Time: ${it.timestamp}, Filtered Angle: ${it.filteredAngle}°"
            }
        })

        sensorViewModel.complDataList.observe(this, Observer { complData ->
            // Display the fused (complementary) data
            binding.complTextView.text = complData.joinToString("\n") {
                "Time: ${it.timestamp}, Fused Angle: ${it.fusedAngle}°"
            }
        })

        sensorViewModel.sensorDataList.observe(this, Observer { allData ->
            // Display combined data (elevation, filtered, and fused angles)
            binding.dataTextView.text = allData.joinToString("\n") {
                "Time: ${it.timestamp}, Elevation Angle: ${it.elevationAngle}°, Filtered: ${it.filteredAngle}°, Fused: ${it.fusedAngle}°"
            }
        })
    }
}
