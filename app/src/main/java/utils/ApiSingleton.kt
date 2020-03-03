package utils

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http2.Http2Reader
import java.io.IOException

open class ApiSingleton {

    val gson: Gson = Gson()

    companion object {
        internal class LoggingInterceptor : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()

                Http2Reader.logger.info("Sending request ${request.url} with Method - ${request.method}")
                return chain.proceed(request)
            }
        }

        private val client: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .addInterceptor(LoggingInterceptor())
                .build()
        }

        fun executeCall(url: String) = client.newCall(Request.Builder()
            .url(url)
            .get()
            .build())
            .execute()
    }
}