package kth.armmonitor.model.service

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter

class CsvService {

    fun exportToCsv(context: Context, data: List<String>, fileName: String): File {
        // Bestäm platsen som Downloads-mappen
        val csvFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "$fileName.csv")

        // Skapa och skriv till CSV-filen
        val writer = FileWriter(csvFile)
        writer.write("Timestamp,EWMA Angle,Fusion Angle\n") // Lägg till kolumnrubriker
        data.forEach { writer.write(it + "\n") } // Lägg till data
        writer.flush()
        writer.close()

        return csvFile // Returnera filens referens
    }
}
