package com.example.lab_week_08

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class ThirdWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        const val INPUT_DATA_ID = "inId"
        const val OUTPUT_DATA_ID = "outId"
    }

    override fun doWork(): Result {
        try {
            val id = inputData.getString(INPUT_DATA_ID)

            Thread.sleep(3000L)
            Log.d("ThirdWorker", "Process 3 done for ID: $id") // Pesan log diubah

            val outputData = Data.Builder()
                .putString(OUTPUT_DATA_ID, id)
                .build()

            return Result.success(outputData)

        } catch (e: Exception) {
            Log.e("ThirdWorker", "Error in ThirdWorker", e)
            return Result.failure()
        }
    }
}