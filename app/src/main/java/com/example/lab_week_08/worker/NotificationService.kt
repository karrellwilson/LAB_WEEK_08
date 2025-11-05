package com.example.lab_week_08

// Import-import penting
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.lang.IllegalStateException

class NotificationService : Service() {

    // Builder untuk notifikasi
    private lateinit var notificationBuilder: NotificationCompat.Builder

    // Handler untuk menjalankan tugas di thread terpisah
    private lateinit var serviceHandler: Handler

    // Dipanggil saat service di-bind (tidak kita gunakan)
    override fun onBind(intent: Intent?): IBinder? = null

    // Dipanggil saat service pertama kali dibuat
    override fun onCreate() {
        super.onCreate()

        // Membuat notifikasi dan menjalankannya sebagai foreground service
        notificationBuilder = startForegroundService()

        // Membuat thread baru untuk handler
        val handlerThread = HandlerThread("SecondThread").apply { start() }
        serviceHandler = Handler(handlerThread.looper)
    }

    // Dipanggil setiap kali service di-start
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val returnValue = super.onStartCommand(intent, flags, startId)

        // Mendapat ID channel dari MainActivity
        val id = intent?.getStringExtra(EXTRA_ID)
            ?: throw IllegalStateException("Channel ID must be provided")

        // Menjalankan tugas di thread yang berbeda (serviceHandler)
        serviceHandler.post {
            // Menghitung mundur di notifikasi
            countDownFromTenToZero(notificationBuilder)

            // Memberi tahu MainActivity bahwa proses selesai
            notifyCompletion(id)

            // Menghentikan foreground service (notifikasi hilang)
            stopForeground(STOP_FOREGROUND_REMOVE)

            // Menghancurkan service
            stopSelf()
        }

        return returnValue
    }

    // Fungsi untuk membuat dan memulai foreground service
    private fun startForegroundService(): NotificationCompat.Builder {
        val pendingIntent = getPendingIntent()
        val channelId = createNotificationChannel()
        val notificationBuilder = getNotificationBuilder(pendingIntent, channelId)

        // Memulai foreground service
        startForeground(NOTIFICATION_ID, notificationBuilder.build())

        return notificationBuilder
    }

    // Membuat PendingIntent untuk membuka MainActivity saat notifikasi diklik
    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
        return PendingIntent.getActivity(this, 0, intent, flag)
    }

    // Membuat Notification Channel (wajib untuk Android 8.0 Oreo ke atas)
    private fun createNotificationChannel(): String {
        val channelId = "001"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "001 Channel"
            val channelPriority = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, channelPriority)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        return channelId
    }

    // Membangun notifikasi
    private fun getNotificationBuilder(pendingIntent: PendingIntent, channelId: String):
            NotificationCompat.Builder {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Second worker process is done")
            .setContentText("Check it out!")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Pastikan Anda punya drawable ini
            .setContentIntent(pendingIntent)
            .setTicker("Second worker process is done, check it out!")
            .setOngoing(true) // Notifikasi tidak bisa di-swipe
    }

    // Fungsi untuk countdown di notifikasi
    private fun countDownFromTenToZero(notificationBuilder: NotificationCompat.Builder) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        for (i in 10 downTo 0) {
            Thread.sleep(1000L) // Jeda 1 detik
            notificationBuilder.setContentText("$i seconds until last warning")
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    // Mengupdate LiveData agar bisa diobserve oleh MainActivity
    private fun notifyCompletion(id: String) {
        Handler(Looper.getMainLooper()).post {
            mutableID.value = id
        }
    }

    // Companion object untuk menyimpan konstanta dan LiveData
    companion object {
        const val NOTIFICATION_ID = 0xCA7
        const val EXTRA_ID = "Id"

        private val mutableID = MutableLiveData<String>()
        val trackingCompletion: LiveData<String> = mutableID
    }
}