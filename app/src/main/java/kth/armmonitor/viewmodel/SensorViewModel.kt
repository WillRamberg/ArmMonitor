package kth.armmonitor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kth.armmonitor.model.SensorData
import kth.armmonitor.model.service.CsvService
import kth.armmonitor.model.service.SensorService

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorService = SensorService(application)
    private val csvService = CsvService()

    private val _sensorDataList = MutableLiveData<List<SensorData>>()
    val sensorDataList: MutableLiveData<List<SensorData>> get() = _sensorDataList

    private val dataBuffer = mutableListOf<SensorData>()

    fun startMeasurement() {
        sensorService.startMeasurement { timestamp, angle ->
            val data = SensorData(timestamp, angle)
            dataBuffer.add(data)
            _sensorDataList.postValue(dataBuffer)
        }
    }

    fun stopMeasurement() {
        sensorService.stopMeasurement()
    }

    fun exportDataToCsv(context: Application, fileName: String): String {
        val csvData = dataBuffer.map { "${it.timestamp},${it.elevationAngle}" }
        val file = csvService.exportToCsv(context, csvData, fileName)
        return file.absolutePath
    }
}