package kth.armmonitor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kth.armmonitor.model.SensorData
import kth.armmonitor.model.service.CsvService
import kth.armmonitor.model.service.SensorProcessingService
import kth.armmonitor.model.service.SensorService

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorService = SensorService(application)
    private val sensorProcessingService = SensorProcessingService()
    private val csvService = CsvService()

    // LiveData to hold combined sensor data (elevation, filtered, and fused)
    private val _sensorDataList = MutableLiveData<List<SensorData>>()
    val sensorDataList: MutableLiveData<List<SensorData>> get() = _sensorDataList

    // LiveData to hold only filtered (EWMA) data
    private val _ewmaDataList = MutableLiveData<List<SensorData>>()
    val ewmaDataList: MutableLiveData<List<SensorData>> get() = _ewmaDataList

    // LiveData to hold only fused (complementary) data
    private val _complDataList = MutableLiveData<List<SensorData>>()
    val complDataList: MutableLiveData<List<SensorData>> get() = _complDataList

    private val dataBuffer = mutableListOf<SensorData>()

    // Start measurement and process the sensor data
    fun startMeasurement() {
        sensorService.startMeasurement { timestamp, angle, gyroAngle, filteredAngle, dt ->
            // Apply EWMA filter to the linear acceleration angle (already filtered in service)
            val finalFilteredAngle = sensorProcessingService.applyEWMAFilter(filteredAngle.toDouble()).toFloat()

            // Apply complementary filter to fuse linear acceleration and gyroscope data
            val fusedAngle = sensorProcessingService.applyComplementaryFilter(
                finalFilteredAngle.toDouble(), gyroAngle.toDouble(), dt
            ).toFloat()

            // Store the processed data in the buffer (all angles)
            val data = SensorData(timestamp, angle, finalFilteredAngle, fusedAngle)
            dataBuffer.add(data)

            // Update LiveData for combined data
            _sensorDataList.postValue(dataBuffer)

            // Update LiveData for filtered (EWMA) data
            _ewmaDataList.postValue(listOf(SensorData(timestamp, angle, finalFilteredAngle, fusedAngle)))

            // Update LiveData for fused (complementary) data
            _complDataList.postValue(listOf(SensorData(timestamp, angle, finalFilteredAngle, fusedAngle)))
        }
    }

    // Stop measurement
    fun stopMeasurement() {
        sensorService.stopMeasurement()
    }

    // Export data to CSV
    fun exportDataToCsv(context: Application, fileName: String): String {
        val csvData = dataBuffer.map { "${it.timestamp},${it.elevationAngle},${it.filteredAngle},${it.fusedAngle}" }
        val file = csvService.exportToCsv(context, csvData, fileName)
        return file.absolutePath
    }
}
