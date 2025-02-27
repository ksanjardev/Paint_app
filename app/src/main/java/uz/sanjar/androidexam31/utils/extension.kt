package uz.sanjar.androidexam31.utils

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**   Created by Sanjar Karimov 3:59 PM 12/7/2024   */


private val gson = GsonBuilder()
    .create()

fun <T> String.parseTo(): T {
    val type = object : TypeToken<T>() {}.type
    return gson.fromJson<T>(this, type)
}

fun <T> T.toJson(): String {
    return gson.toJson(this)
}


/**
fun OkHttpClient.Builder.addContentHeaderIfNeeded(preference: LocalStorage): OkHttpClient.Builder {
addInterceptor { chain ->
val originalRequest = chain.request()
if (originalRequest.url.toString().contains("todo")) {
val request = originalRequest.newBuilder()
.addHeader("Authorization", "Bearer ${preference.token}")
.build()
chain.proceed(request)
} else {
chain.proceed(originalRequest)
}
}
return this
}*/