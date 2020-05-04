package edu.towson.cosc435.choi.todos.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import edu.towson.cosc435.choi.todos.interfaces.ITodoController
import edu.towson.cosc435.choi.todos.models.Todo
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URL
import java.util.*


interface ITodoApi {
    suspend fun fetchTodos(): Deferred<List<Todo>>
    suspend fun fetchIcon(iconUrl: String): Deferred<Bitmap>
}

class TodoApi(val controller: ITodoController): ITodoApi {

    private val BASE_URL: String = "https://my-json-server.typicode.com/rvalis-towson/todos_api/todos"
    private val client: OkHttpClient = OkHttpClient()

    override suspend fun fetchTodos(): Deferred<List<Todo>> {
        return controller.async(Dispatchers.IO) {
            val request = Request.Builder()
                .url(BASE_URL)
                .get()
                .build()
            val result: String? = client.newCall(request).execute().body()?.string()
            val todos: List<Todo> = parseJson(result)
            todos
        }
    }

    override suspend fun fetchIcon(iconUrl: String): Deferred<Bitmap> {
        return controller.async(Dispatchers.IO) {
            val filename = getImageFilename(iconUrl)
            val bitmap = controller.checkCache(filename)
            if(bitmap != null) {
                bitmap
            } else {
                val request = Request.Builder()
                    .url(iconUrl)
                    .get()
                    .build()
                val stream = client.newCall(request).execute().body()?.byteStream()
                val bitmap = BitmapFactory.decodeStream(stream)
                if(bitmap != null)
                    controller.cacheIcon(filename, bitmap)
                bitmap
            }
        }
    }

    private fun parseJson(json: String?): List<Todo> {
        val todos = mutableListOf<Todo>()
        if(json == null) return todos
        val jsonArr = JSONArray(json)
        var i = 0
        while(i < jsonArr.length()) {
            val jsonObj = jsonArr.getJSONObject(i)
            val todo = Todo(
                id = jsonObj.getString("id"),
                title = jsonObj.getString("title"),
                content = jsonObj.getString("contents"),
                completed = jsonObj.getBoolean("completed"),
                iconUrl = jsonObj.getString("image_url"),
                create_date = jsonObj.getString("created")
            )
            todos.add(todo)
            i++
        }
        return todos
    }

    private fun getImageFilename(url: String): String {
        val urlObj = URL(url)
        val query : String? = urlObj.toString().drop(32)
        return query.plus(".jpg")
    }
}