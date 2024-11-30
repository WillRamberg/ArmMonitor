package kth.armmonitor.model.service

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter

class CsvService {

    fun exportToCsv(context: Context, data: List<String>, fileName: String): File {
        val csvFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "$fileName.csv")
        val writer = FileWriter(csvFile)
        data.forEach { writer.write(it + "\n") }
        writer.flush()
        writer.close()
        return csvFile
    }
}
