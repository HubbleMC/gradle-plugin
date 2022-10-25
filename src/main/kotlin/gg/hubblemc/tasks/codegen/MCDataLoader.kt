package gg.hubblemc.tasks.codegen

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.gradle.api.Project

private const val version = "1.19"
val gson = Gson()

// TODO: Cleanup
object MCDataLoader {
    private val client = HttpClientBuilder.create().build()

    fun fetch(file: String): String {
        val url = "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/master/data/pc/$version/$file.json"
        val request = HttpGet(url)
        val response = client.execute(request)
        return response.entity.content.readBytes().decodeToString()
    }
}

inline fun <reified T : Any> Project.fetchMCData(file: String): T {
    val cache = buildDir.resolve("mc-data/$version/$file.json")
    val type = object : TypeToken<T>() {}.type

    if (cache.exists()) return gson.fromJson(cache.inputStream().reader(), type)
    return gson.fromJson(MCDataLoader.fetch(file), type)
}
