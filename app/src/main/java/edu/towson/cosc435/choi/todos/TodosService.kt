package edu.towson.cosc435.choi.todos

import android.app.*
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import edu.towson.cosc435.choi.todos.MainActivity
import edu.towson.cosc435.choi.todos.MessageQueue
import edu.towson.cosc435.choi.todos.database.TodoDatabaseRepository
import edu.towson.cosc435.choi.todos.interfaces.ITodoController
import edu.towson.cosc435.choi.todos.models.Todo
import edu.towson.cosc435.choi.todos.network.TodoApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.*
import kotlin.coroutines.CoroutineContext

class TodosService : JobService(), CoroutineScope{
    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        // fetch a new song from the web api...
        Log.d(TAG,"Todo Service job started")

        val client = OkHttpClient()
        val repos = TodoDatabaseRepository(this)

        launch {
            for( todo in repos.GetTodos()){
                val iconUrl = todo.iconUrl
                val request = Request.Builder()
                    .url(iconUrl)
                    .get()
                    .build()
                val stream = client.newCall(request).execute().body()?.byteStream()
                val bitmap = BitmapFactory.decodeStream(stream)

                if(bitmap != null) {
                    val filename : String = iconUrl.drop(32).plus(".jpg")
                    val file = File(cacheDir, filename)
                    val output = file.outputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
                    //System.out.println(filename+" Caching completed")
                }
            }
            val notif = createNotification()
            NotificationManagerCompat.from(this@TodosService).notify(NOTIF_ID, notif)
            val success = true
            MessageQueue.Channel.postValue(success)
        }

        return true
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "FetchIconChannel"
            val descriptionTxt = "Notification channel for fetching icons."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionTxt
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText("Icons are available!")
            .setContentTitle("Image download complete!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

    }

    companion object {
        val TAG = TodosService::class.java.simpleName
        val CHANNEL_ID = "FetchIconChannelId"
        val NOTIF_ID = 1
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO


}
