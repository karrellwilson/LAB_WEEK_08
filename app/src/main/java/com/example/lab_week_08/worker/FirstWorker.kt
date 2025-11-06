package com.example.lab_week_08

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class FirstWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        const val INPUT_DATA_ID = "inId"
        const val OUTPUT_DATA_ID = "outId"
    }

    override fun doWork(): Result {
        try {
            val id = inputData.getString(INPUT_DATA_ID)

            Thread.sleep(3000L)
            Log.d("FirstWorker", "Process 1 done for ID: $id")

            val outputData = Data.Builder()
                .putString(OUTPUT_DATA_ID, id)
                .build()

            return Result.success(outputData)

        } catch (e: Exception) {
            Log.e("FirstWorker", "Error in FirstWorker", e)
            return Result.failure()
        }
    }
}