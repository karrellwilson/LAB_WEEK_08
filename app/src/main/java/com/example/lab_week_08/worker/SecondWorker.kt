package com.example.lab_week_08

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

// Nama kelas diubah menjadi SecondWorker
class SecondWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        // Konstantanya bisa dipakai ulang, tapi untuk kejelasan kita definisikan di sini juga
        const val INPUT_DATA_ID = "inId"
        const val OUTPUT_DATA_ID = "outId"
    }

    override fun doWork(): Result {
        try {
            // Get the parameter input
            val id = inputData.getString(INPUT_DATA_ID)

            // Sleep the process for 3 seconds
            Thread.sleep(3000L)
            Log.d("SecondWorker", "Process 2 done for ID: $id") // Pesan log diubah

            // Build the output based on process result
            val outputData = Data.Builder()
                .putString(OUTPUT_DATA_ID, id)
                .build()

            // Return the output
            return Result.success(outputData)

        } catch (e: Exception) {
            Log.e("SecondWorker", "Error in SecondWorker", e)
            return Result.failure()
        }
    }
}